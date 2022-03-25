package com.ndb.auction.hooks;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.ndb.auction.dao.oracle.ShuftiDao;
import com.ndb.auction.dao.oracle.user.UserDao;
import com.ndb.auction.dao.oracle.user.UserDetailDao;
import com.ndb.auction.dao.oracle.user.UserVerifyDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.Shufti.Response.*;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.Shufti.ShuftiReference;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.user.User;

import com.ndb.auction.models.user.UserDetail;
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
    UserDao userDao;

    @Autowired
    public ShuftiController(ShuftiDao shuftiDao,
                            UserDao userDao,
                            UserVerifyDao userVerifyDao,
                            UserDetailDao userDetailDao) {
        this.userVerifyDao = userVerifyDao;
        this.userDetailDao = userDetailDao;
        this.shuftiDao = shuftiDao;
        this.userDao = userDao;
    }
    
    @PostMapping("/shufti")
    @ResponseBody
    public Object ShuftiCallbackHandler(HttpServletRequest request) {

   		String reqQuery;
		try {
			reqQuery = getBody(request);
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
            case "verification.accepted":

                shuftiDao.passed(userId);
                //Insert user details after verification
                UserDetail userDetail = generateUserDetailEntity(response);
                userDetail.setUserId(userId);
                userDetailDao.insert(userDetail);
                // update user tier!
                List<Tier> tierList = tierService.getUserTiers();
                TaskSetting taskSetting = taskSettingService.getTaskSetting();
                TierTask tierTask = tierTaskService.getTierTask(userId);
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
                break;
            case "request.pending":
                // invalid
                notificationService.sendNotification(
                        userId,
                        Notification.KYC_VERIFIED,
                        "KYC VERIFICATION PENDING",
                        "KYC Verification is pending."
                );
                break;
            case "request.invalid":
                // invalid
                notificationService.sendNotification(
                        userId,
                        Notification.KYC_VERIFIED,
                        "KYC VERIFICATION FAILED",
                        String.format(
                                "KYC Verification failed.\n%s \nPlease try again.",
                                response.getError().getMessage())
                );
                break;
            case "verification.declined":
                // check declined reason
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
                        String.format("KYC Verification failed.\n%s. \nPlease try again.",
                                response.getDeclined_reason()));
                System.out.println("Verification failed");
                System.out.println(response.getEvent());
                break;
        }
        return new ResponseEntity<>(HttpStatus.OK);
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
