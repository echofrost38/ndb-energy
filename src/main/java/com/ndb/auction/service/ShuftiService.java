package com.ndb.auction.service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ndb.auction.models.Shufti.ShuftiReference;
import com.ndb.auction.models.Shufti.Request.ShuftiRequest;
import com.ndb.auction.models.Shufti.Response.ShuftiResponse;
import com.ndb.auction.payload.ShuftiStatusRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import reactor.core.publisher.Mono;

@Service
public class ShuftiService extends BaseService{
    
    // Shuftipro URL
    private static final String BASE_URL = "https://api.shuftipro.com/";

    @Value("${shufti.client.id}")
    private String CLIENT_ID;

    @Value("${shufti.secret.key}")
    private String SECRET_KEY;

    @Value("${shufti.callback_url")
    private String CALLBACK_URL;

    private WebClient shuftiAPI;

	private static final ObjectMapper objectMapper = new ObjectMapper();

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

        request.setCallback_url(CALLBACK_URL);  

        sendShuftiRequest(request)
            .subscribe(response -> System.out.println(response));
        return 1;
    }

    public int kycStatusRequestAsync(String reference) throws InvalidKeyException, JsonProcessingException, NoSuchAlgorithmException, IOException {
        ShuftiStatusRequest request = new ShuftiStatusRequest(reference);

        @SuppressWarnings("deprecation")
		Response _response = sendPost("status", 
            RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                objectMapper.writeValueAsString(request))
        );

        String _responseString = _response.body().string();
        ShuftiResponse response = new Gson().fromJson(_responseString, ShuftiResponse.class);
        if(response.getEvent().equals("verification.accepted")) {
            return 1;
        }
        return 0;
    }

    // private routines
    private String generateToken() {
        String combination = CLIENT_ID + ":" + SECRET_KEY;
        return Base64.getEncoder().encodeToString(combination.getBytes());
    }

    //// WebClient
    private Mono<String> sendShuftiRequest(ShuftiRequest request) {
        String token = generateToken();
        return shuftiAPI.post()
            .uri(uriBuilder -> uriBuilder.path("").build())
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Authorization", "Basic " + token)
            .body(Mono.just(request), ShuftiRequest.class)
            .retrieve()
            .bodyToMono(String.class);
    }

    private Response sendPost(String url, RequestBody requestBody) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        String token = generateToken();
        Request request = new Request.Builder()
                .url(BASE_URL + url)
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
