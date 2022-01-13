package com.ndb.auction.service;

import java.io.IOException;
import java.util.Base64;

import com.ndb.auction.models.Shufti.ShuftiReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class ShuftiService extends BaseService{
    
    // Shuftipro URL
    private static final String BASE_URL = "https://api.shuftipro.com/";

    @Value("${shufti.client.id}")
    private String CLIENT_ID;

    @Value("${shufti.secret.key}")
    private String SECRET_KEY;

    // Create new application
    public String createShuftiReference(int userId, String verifyType) {
        // check existing 
        ShuftiReference sRef = shuftiDao.selectById(userId);
        if(sRef != null) {
            return sRef.getReference();
        }
        String reference = "SHUFTI-" + verifyType + "-" + String.valueOf(userId);
        sRef = new ShuftiReference(userId, reference, verifyType);
        shuftiDao.insert(sRef);
        return reference;
    }

    // kyc verification
    public void kycRequest() {
        
    }

    // private routines
    private String generateToken() {
        String combination = CLIENT_ID + ":" + SECRET_KEY;
        return Base64.getEncoder().encodeToString(combination.getBytes());
    }

    private Response sendPost(RequestBody requestBody) throws IOException {
        String token = generateToken();
        Request request = new Request.Builder()
            .url(BASE_URL)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("Authorization", "Basic " + token)
            .post(requestBody)
            .build();
        
        Response response = new OkHttpClient().newCall(request).execute();
        if (response.code() != 200 && response.code() != 201) {
            // https://developers.sumsub.com/api-reference/#errors
            // If an unsuccessful answer is received, please log the value of the "correlationId" parameter.
            // Then perhaps you should throw the exception. (depends on the logic of your code)
        }
        return response;
    }

}
