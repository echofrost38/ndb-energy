package com.ndb.auction.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.Shufti.ShuftiReference;
import com.ndb.auction.models.Shufti.Request.ShuftiRequest;
import com.ndb.auction.models.Shufti.Response.ShuftiResponse;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserVerify;
import com.ndb.auction.payload.ShuftiStatusRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class ShuftiService extends BaseService{
    
    // Shuftipro URL
    private static final String BASE_URL = "https://api.shuftipro.com/";

    @Value("${shufti.client.id}")
    private String CLIENT_ID;

    @Value("${shufti.secret.key}")
    private String SECRET_KEY;

    private WebClient shuftiAPI;

    public ShuftiService(WebClient.Builder webClientBuilder) {
        this.shuftiAPI = webClientBuilder
            .baseUrl(BASE_URL)
            .build();
    }

    // Create new application
    public String createShuftiReference(int userId, String verifyType) {
        // check existing 
        ShuftiReference sRef = shuftiDao.selectById(userId);
        if(sRef != null) {
            return sRef.getReference();
        }
        String reference = UUID.randomUUID().toString();
        sRef = new ShuftiReference(userId, reference, verifyType);
        shuftiDao.insert(sRef);
        return reference;
    }

    public String updateShuftiReference(int userId, String reference) {
        shuftiDao.update(userId, reference);
        return reference;
    }

    public ShuftiReference getShuftiReference(int userId) {
        return shuftiDao.selectById(userId);
    }

    // kyc verification
    public int kycRequest(int userId, ShuftiRequest request) throws JsonProcessingException, IOException {
        
        // add supported types
        List<String> docSupportedTypes = new ArrayList<>();
        docSupportedTypes.add("id_card");
        docSupportedTypes.add("driving_license");
        docSupportedTypes.add("passport");
        request.getDocument().setSupported_types(docSupportedTypes);

        List<String> addrSupportedTypes = new ArrayList<>();
        addrSupportedTypes.add("id_card");
        addrSupportedTypes.add("bank_statement");
        addrSupportedTypes.add("utility_bill");
        request.getAddress().setSupported_types(addrSupportedTypes);

        List<String> consentTypes = new ArrayList<>();
        consentTypes.add("printed");
        request.getConsent().setSupported_types(consentTypes);
        request.getConsent().setText("I & NDB");    

        sendShuftiRequest(request)
            .subscribe(response -> handleRequestResponse(response));

        return 1;
    }

    public int kycStatusRequestAsync(String reference) {
        ShuftiStatusRequest request = new ShuftiStatusRequest(reference);
        ShuftiResponse response = sendShuftiStatusRequest(request).block();
        if(response.getEvent().equals("verification.accepted")) {
            return 1;
        }
        return 0;
    }

    private void handleRequestResponse(ShuftiResponse response) {
        String reference = response.getReference();
        ShuftiReference ref = shuftiDao.selectByReference(reference);

        if(ref == null) return;
        int userId = ref.getUserId();
        
        if(response.getEvent().equals("verification.accepted")) {
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

            UserVerify userVerify = userVerifyDao.selectById(userId);
            userVerify.setKycVerified(true);

            // send notification
            notificationService.sendNotification(
                userId,
                Notification.KYC_VERIFIED,
                "KYC VERIFIED",
                "Your identity has been successfully verified.");
        } else {
            // send notification
            notificationService.sendNotification(
                userId,
                Notification.KYC_VERIFIED,
                "KYC VERIFICATION FAILED",
                "Verification failed.");
        }
    }

    // private routines
    private String generateToken() {
        String combination = CLIENT_ID + ":" + SECRET_KEY;
        return Base64.getEncoder().encodeToString(combination.getBytes());
    }

    //// WebClient
    private Mono<ShuftiResponse> sendShuftiRequest(ShuftiRequest request) {
        String token = generateToken();
        return shuftiAPI.post()
            .uri(uriBuilder -> uriBuilder.path("").build())
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("Authorization", "Basic " + token)
            .body(Mono.just(request), ShuftiRequest.class)
            .retrieve()
            .bodyToMono(ShuftiResponse.class);
    }

    private Mono<ShuftiResponse> sendShuftiStatusRequest(ShuftiStatusRequest request) {
        String token = generateToken();
        return shuftiAPI.post()
            .uri(uriBuilder -> uriBuilder.path("status").build())
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("Authorization", "Basic " + token)
            .body(Mono.just(request), ShuftiStatusRequest.class)
            .retrieve()
            .bodyToMono(ShuftiResponse.class);
    }


}
