package com.ndb.auction.hooks;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.ndb.auction.dao.oracle.ShuftiDao;
import com.ndb.auction.dao.oracle.user.UserDao;
import com.ndb.auction.dao.oracle.user.UserDetailDao;
import com.ndb.auction.dao.oracle.user.UserVerifyDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.Shufti.ShuftiReference;
import com.ndb.auction.models.Shufti.Response.Address;
import com.ndb.auction.models.Shufti.Response.Document;
import com.ndb.auction.models.Shufti.Response.Proof;
import com.ndb.auction.models.Shufti.Response.ShuftiResponse;
import com.ndb.auction.models.Shufti.Response.VerificationResult;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserDetail;
import com.ndb.auction.service.ShuftiService;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ShuftiController extends BaseController {
    
    @Value("${shufti.secret.key}")
    private String SECRET_KEY;

    UserVerifyDao userVerifyDao;
    UserDetailDao userDetailDao;
    ShuftiDao shuftiDao;
    ShuftiService shuftiService;
    UserDao userDao;

    @Autowired
    public ShuftiController(ShuftiService shuftiService,
                            ShuftiDao shuftiDao,
                            UserDao userDao,
                            UserVerifyDao userVerifyDao,
                            UserDetailDao userDetailDao) {
        this.userVerifyDao = userVerifyDao;
        this.userDetailDao = userDetailDao;
        this.shuftiService = shuftiService;
        this.shuftiDao = shuftiDao;
        this.userDao = userDao;
    }
    
    @PostMapping("/shufti")
    @ResponseBody
    public Object ShuftiCallbackHandler(HttpServletRequest request) {

   		String reqQuery;
		try {
			reqQuery = getBody(request);
            System.out.println(reqQuery);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

        String original = reqQuery + SECRET_KEY;
        String sha256hex = DigestUtils.sha256Hex(original);
        String signature = request.getHeader("Signature");
        if(!sha256hex.equals(signature)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        ShuftiResponse response = new Gson().fromJson(reqQuery, ShuftiResponse.class);
        String reference = response.getReference();
        ShuftiReference ref = shuftiDao.selectByReference(reference);

        if(ref == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        int userId = ref.getUserId();
        
        shuftiDao.updatePendingStatus(userId, false);

        switch (response.getEvent()) {
            case "review.pending":
                // invalid
                notificationService.sendNotification(
                        userId,
                        Notification.KYC_VERIFIED,
                        "KYC VERIFICATION PENDING",
                        "Identity verification is pending."
                );
                break;
            case "verification.status.changed": 
                // verification status!
                ShuftiResponse statusResponse = shuftiService.checkShuftiStatus(reference);
                if(statusResponse == null) {
                    System.out.println("Error for getting status: " + reference);
                }

                if(statusResponse.getEvent().equals("verification.accepted")) {
                    System.out.println("accepted case: ");
                    System.out.println(reqQuery);
                    handleAccepted(userId, statusResponse);
                } else if(statusResponse.getEvent().equals("verification.declined")) {
                    System.out.println("declined case: ");
                    System.out.println(reqQuery);
                    handleDeclined(userId, statusResponse);
                } else {
                    notificationService.sendNotification(
                        userId,
                        Notification.KYC_VERIFIED,
                        "KYC VERIFICATION INVALID",
                        "Identity verification is invalid."
                    );
                }
                break;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void handleDeclined(int userId, ShuftiResponse response) {
        VerificationResult result = response.getVerification_result();

        // check one by one
        shuftiDao.updateDocStatus(userId, result.getDocument().getDocument() == 1);
        shuftiDao.updateAddrStatus(userId, result.getAddress().getAddress_document() == 1);
        shuftiDao.updateConStatus(userId, result.getConsent().getConsent() == 1);
        shuftiDao.updateSelfieStatus(userId, result.getFace() == 1);

        // send notification
        notificationService.sendNotification(
                userId,
                Notification.KYC_VERIFIED,
                "KYC VERIFICATION FAILED",
                String.format("Identity verification failed.\n%s. \nPlease try again.",
                        response.getDeclined_reason()));
        System.out.println("Verification failed");
        System.out.println(response.getEvent());
    }

    private void handleAccepted(int userId, ShuftiResponse response) {
        shuftiDao.passed(userId);
        //Insert user details after verification
        UserDetail userDetail = generateUserDetailEntity(response);
        userDetail.setUserId(userId);
        userDetailDao.insert(userDetail);
        // update user tier!
        List<Tier> tierList = tierService.getUserTiers();
        TaskSetting taskSetting = taskSettingService.getTaskSetting();
        TierTask tierTask = tierTaskService.getTierTask(userId);

        if(tierTask == null) {
            tierTask = new TierTask(userId);
            tierTaskService.updateTierTask(tierTask);
        }

        tierTask.setVerification(true);

        User user = userDao.selectById(userId);
        double tierPoint = user.getTierPoint();
        tierPoint += taskSetting.getVerification();
        int tierLevel = 0;
        for (Tier tier : tierList) {
            if (tier.getPoint() <= tierPoint) {
                tierLevel = tier.getLevel();
            }
        }
        userDao.updateTier(userId, tierLevel, tierPoint);
        tierTaskService.updateTierTask(tierTask);

        userVerifyDao.updateKYCVerified(userId, true);

        // send notification
        notificationService.sendNotification(
                userId,
                Notification.KYC_VERIFIED,
                "KYC VERIFIED",
                "Your identity has been successfully verified.");
        System.out.println("Verification success.");
        System.out.println(response.getEvent());
    }

    private UserDetail generateUserDetailEntity(ShuftiResponse response) {
        Document userDocument = response.getVerification_data().getDocument();
        Proof userProof = response.getAdditional_data().getDocument().getProof();
        Address userAddress = response.getVerification_data().getAddress();

        return UserDetail.builder()
                .firstName(userDocument.getName().getFirst_name())
                .lastName(userDocument.getName().getLast_name())
                .issueDate(userDocument.getIssue_date())
                .expiryDate(userDocument.getExpiry_date())
                .nationality(userProof.getNationality())
                .countryCode(userProof.getCountry_code())
                .documentType(userProof.getDocument_type())
                .placeOfBirth(userProof.getPlace_of_birth())
                .documentNumber(userProof.getDocument_number())
                .personalNumber(userProof.getPersonal_number())
                .height(userProof.getHeight())
                .country(userProof.getCountry())
                .authority(userProof.getAuthority())
                .dob(userDocument.getDob())
                .age(userDocument.getAge())
                .gender(userDocument.getGender())
                .address(userAddress.getFull_address())
                .build();
    }
}
