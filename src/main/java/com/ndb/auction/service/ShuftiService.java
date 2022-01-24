package com.ndb.auction.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndb.auction.models.Shufti.ShuftiReference;
import com.ndb.auction.models.Shufti.Request.ShuftiRequest;
import com.ndb.auction.models.Shufti.Response.ShuftiResponse;
import com.ndb.auction.payload.ShuftiStatusRequest;
import com.ndb.auction.payload.ShuftiStatusResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Service
public class ShuftiService extends BaseService{
    
    // Shuftipro URL
    private static final String BASE_URL = "https://api.shuftipro.com/";

    @Value("${shufti.client.id}")
    private String CLIENT_ID;

    @Value("${shufti.secret.key}")
    private String SECRET_KEY;

    private static final ObjectMapper objectMapper = new ObjectMapper();

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
    @SuppressWarnings("deprecation")
    public int kycRequest(ShuftiRequest request) throws JsonProcessingException, IOException {
        
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

        Response response = sendPost(RequestBody.create(
            MediaType.parse(
                "application/json; charset=utf-8"), 
                objectMapper.writeValueAsString(request)), BASE_URL
        );
        String responseBody = response.body().string();
        ShuftiResponse shuftiResponse = objectMapper.readValue(responseBody, ShuftiResponse.class);
        if(shuftiResponse.getEvent().equals("verification.accepted")) {
            return 1;
        } 
        return 0;
    }
    
    @SuppressWarnings("deprecation")
    public int kycStatusRequest(String reference) throws JsonProcessingException, IOException {
        ShuftiStatusRequest request = new ShuftiStatusRequest(reference);
        Response response = sendPost(RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"), 
            objectMapper.writeValueAsString(request)), BASE_URL + "status");
        ResponseBody responseBody = response.body();
        ShuftiStatusResponse shuftiResponse = objectMapper.readValue(responseBody.string(), ShuftiStatusResponse.class);
        if(shuftiResponse.getEvent().equals("verification.accepted")) {
            return 1;
        }
        return 0;
    }

    // private routines
    private String generateToken() {
        String combination = CLIENT_ID + ":" + SECRET_KEY;
        return Base64.getEncoder().encodeToString(combination.getBytes());
    }

    private Response sendPost(RequestBody requestBody, String url) throws IOException {
        String token = generateToken();
        Request request = new Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("Authorization", "Basic " + token)
            .post(requestBody)
            .build();
        
        OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.DAYS)
            .writeTimeout(0, TimeUnit.DAYS)
            .callTimeout(0, TimeUnit.DAYS)
            .build();

        Response response = client.newCall(request).execute();
        if (response.code() != 200 && response.code() != 201) {
            // https://developers.sumsub.com/api-reference/#errors
            // If an unsuccessful answer is received, please log the value of the "correlationId" parameter.
            // Then perhaps you should throw the exception. (depends on the logic of your code)
        }
        return response;
    }

}
