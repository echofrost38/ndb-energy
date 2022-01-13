package com.ndb.auction.hooks;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.Shufti.ShuftiReference;
import com.ndb.auction.models.Shufti.Response.ShuftiResponse;
import com.ndb.auction.models.Shufti.Response.VerificationResult;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ShuftiController extends BaseController {

    private static final String KYC = "kyc";
    private static final String BACKGROUND_CHECKS = "background_checks";
    private static final String KYB = "kyb";
    private static final String AML_FOR_BUSINESSES = "aml_for_businesses";

    @PostMapping("/shufti")
    @ResponseBody
    public ResponseEntity<?> ShuftiWebhooks(HttpServletRequest request) {
        String reqQuery = "";
        try {
            reqQuery = getBody(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ShuftiResponse response = new Gson().fromJson(reqQuery, ShuftiResponse.class);
        if(!response.getEvent().equals("verification.accepted")) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        String reference = response.getReference();
        ShuftiReference shuftiResponse = shuftiDao.selectByReference(reference);
        VerificationResult result = response.getVerification_result();

        boolean status = false;
        String vType = "";

        switch (shuftiResponse.getVerificationType()) {
            case KYC:
                if(result.getKyc() == 1) { status = true; }
                vType = "KYC";
                break;
            case BACKGROUND_CHECKS:
                if(result.getBackground_checks() == 1) { status = true; }
                vType = "Background Checks";
                break;
            case KYB:
                if(result.getKyb() == 1) { status = true; } 
                vType = "KYB";
                break;
            case "aml_for_businesses":
                if(result.getAml_for_businesses() == 1) { status = true; }
                vType = "AML for Businesses";
                break;
        }
        
        if(status) {
            // send notification 
            notificationService.sendNotification(
                "user id",//shuftiResponse.getUserId(), 
                Notification.N_VERIFICATION, 
                "Verification Accepted", 
                "Your " + vType + " has been successfully verified."
            );
        } else {
            // failed notification
            notificationService.sendNotification(
                "user id",//shuftiResponse.getUserId(), 
                Notification.N_VERIFICATION, 
                "Verification Declined", 
                "Your " + vType + " has been failed. Please try again."
            );
            // delete request! from Shufti and database!
            
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
