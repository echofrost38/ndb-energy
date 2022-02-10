package com.ndb.auction.hooks;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.ndb.auction.dao.oracle.ShuftiDao;
import com.ndb.auction.dao.oracle.user.UserDao;
import com.ndb.auction.dao.oracle.user.UserVerifyDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.Shufti.ShuftiReference;
import com.ndb.auction.models.Shufti.Response.ShuftiResponse;
import com.ndb.auction.models.Shufti.Response.VerificationResult;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.user.User;

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

    @Autowired
    ShuftiDao shuftiDao;

    @Autowired
    UserDao userDao;

    @Autowired
    UserVerifyDao userVerifyDao;
    
    @PostMapping("/shufti")
    @ResponseBody
    public Object ShuftiCallbackHandler(HttpServletRequest request) {

   		String reqQuery = "";
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

        System.out.println(reqQuery);

        ShuftiResponse response = new Gson().fromJson(reqQuery, ShuftiResponse.class);
        String reference = response.getReference();
        ShuftiReference ref = shuftiDao.selectByReference(reference);

        if(ref == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);;
        int userId = ref.getUserId();
        
        shuftiDao.updatePendingStatus(userId, false);

        if(response.getEvent().equals("verification.accepted")) {
            
            shuftiDao.passed(userId);
            
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
                if(tier.getPoint() <= tierPoint) {
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
        } else if (response.getEvent().equals("request.pending")){

        } else if (response.getEvent().equals("request.invalid")) {
            // invalid
            notificationService.sendNotification(
                userId,
                Notification.KYC_VERIFIED,
                "KYC VERIFICATION FAILED",
                String.format(
                    "KYC Verification failed.\n%s \nPlease try again.", 
                    response.getError().getMessage())
            );
        } else if (response.getEvent().equals("verification.declined")) {
            // check declined reason
            VerificationResult result = response.getVerification_result();
            
            // check one by one
            if(result.getDocument().getDocument() != 1) {
                shuftiDao.updateDocStatus(userId, false);
            } else {
                shuftiDao.updateDocStatus(userId, true);
            }

            if(result.getAddress().getAddress_document() != 1) {
                shuftiDao.updateAddrStatus(userId, false);
            } else {
                shuftiDao.updateAddrStatus(userId, true);
            }

            if(result.getConsent().getConsent() != 1) {
                shuftiDao.updateConStatus(userId, false);
            } else {
                shuftiDao.updateConStatus(userId, true);
            }

            if(result.getFace() != 1) {
                shuftiDao.updateSelfieStatus(userId, false);
            } else {
                shuftiDao.updateSelfieStatus(userId, true);
            }
            
            // send notification
            notificationService.sendNotification(
                userId,
                Notification.KYC_VERIFIED,
                "KYC VERIFICATION FAILED",
                String.format("KYC Verification failed.\n%s. \nPlease try again.", 
                    response.getDeclined_reason()));
            System.out.println("Verification failed");
            System.out.println(response.getEvent());
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
