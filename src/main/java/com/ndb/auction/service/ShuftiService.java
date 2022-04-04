package com.ndb.auction.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.Part;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.Shufti.ShuftiReference;
import com.ndb.auction.models.Shufti.Request.Names;
import com.ndb.auction.models.Shufti.Request.ShuftiRequest;
import com.ndb.auction.models.Shufti.Response.ShuftiResponse;
import com.ndb.auction.payload.ShuftiStatusRequest;
import com.ndb.auction.payload.response.ShuftiRefPayload;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

    @Value("${shufti.callback_url}")
    private String CALLBACK_URL;

    private WebClient shuftiAPI;

	private static final ObjectMapper objectMapper = new ObjectMapper();

    protected CloseableHttpClient client;
    
    private final AmazonS3 s3;

    private static final String bucketName = "ndb-sale-dev";

    public ShuftiService(WebClient.Builder webClientBuilder, AmazonS3 s3) {
        client = HttpClients.createDefault();
        this.s3 = s3;
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
        sRef = new ShuftiReference(userId, reference);
        shuftiDao.insert(sRef);
        return reference;
    }

    public String updateShuftiReference(int userId, String reference) {
        shuftiDao.updateReference(userId, reference);
        return reference;
    }

    public ShuftiReference getShuftiReference(int userId) {
        return shuftiDao.selectById(userId);
    }

    // kyc verification
    public int kycRequest(int userId, ShuftiRequest request) throws JsonProcessingException, IOException {
        
        // add supported types
        List<String> docSupportedTypes = new ArrayList<>();
        docSupportedTypes.add("passport");
        request.getDocument().setSupported_types(docSupportedTypes);

        List<String> addrSupportedTypes = new ArrayList<>();
        addrSupportedTypes.add("id_card");
        request.getAddress().setSupported_types(addrSupportedTypes);

        List<String> consentTypes = new ArrayList<>();
        consentTypes.add("handwritten");
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
        ShuftiResponse response = gson.fromJson(_responseString, ShuftiResponse.class);
        if(response.getEvent() != null && response.getEvent().equals("verification.accepted")) {
            return 1;
        }
        return 0;
    }

    public boolean kycStatusCkeck(int userId) {
        ShuftiReference _reference = shuftiDao.selectById(userId);

        return true;
        // if(_reference == null) {
        //     return false;
        // }

        // ShuftiStatusRequest request = new ShuftiStatusRequest(_reference.getReference());
        // try {
        //     @SuppressWarnings("deprecation")
        //     Response _response = sendPost("status", 
        //         RequestBody.create(
        //             MediaType.parse("application/json; charset=utf-8"),
        //             objectMapper.writeValueAsString(request))
        //     );

        //     String _responseString = _response.body().string();
        //     ShuftiResponse response = gson.fromJson(_responseString, ShuftiResponse.class);
        //     if(response.getEvent().equals("verification.accepted")) {
        //         return true;
        //     }
        //     return false;
        // } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
        //     e.printStackTrace();
        // }

        // return false;
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

    // private String sendPost(ShuftiRequest request) throws ClientProtocolException, IOException {
    //     String token = generateToken();
    //     HttpPost post = new HttpPost(BASE_URL);
    //     String body = gson.toJson(request, ShuftiRequest.class);
    //     StringEntity entity = new StringEntity(body);
    //     post.setEntity(entity);
    //     post.addHeader("Accept", "application/json");
    //     post.addHeader("Content-type", "application/json"); 
    //     post.addHeader("Authorization","Basic " + token);

    //     CloseableHttpResponse response = client.execute(post);
        
    //     return EntityUtils.toString(response.getEntity());
    // }

    // ====================== upload into s3 ===============
    public Boolean uploadDocument(int userId, Part document) {
        // get reference obj
        ShuftiReference refObj = shuftiDao.selectById(userId);
        if(refObj == null) {
            // no reference, create new one.
            createShuftiReference(userId, "KYC");
        }
        String docUrl = String.format("%d-passport", userId);

        try {
            InputStream input = document.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(document.getSize());
            s3.putObject(bucketName, docUrl, input, metadata); 
            // shuftiDao.updateDocUrl(userId, docUrl);
        } catch (Exception e) {
            return false;
        }
        return true;
    }  

    public Boolean uploadAddress(int userId, Part addr) {
        // get reference obj
        ShuftiReference refObj = shuftiDao.selectById(userId);
        if(refObj == null) {
            String msg = messageSource.getMessage("no_ref", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "reference");
        }
        String addrUrl = String.format("%d-address", userId);

        try {
            InputStream input = addr.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(addr.getSize());
            s3.putObject(bucketName, addrUrl, input, metadata); 
            // shuftiDao.updateAddrUrl(userId, addrUrl);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Boolean uploadConsent(int userId, Part consent) {
        // get reference obj
        ShuftiReference refObj = shuftiDao.selectById(userId);
        if(refObj == null) {
            String msg = messageSource.getMessage("no_ref", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "reference");
        }
        String conUrl = String.format("%d-consent", userId);

        try {
            InputStream input = consent.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(consent.getSize());
            s3.putObject(bucketName, conUrl, input, metadata); 
            // shuftiDao.updateConUrl(userId, conUrl);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Boolean uploadSelfie(int userId, Part selfie) {
        // get reference obj
        ShuftiReference refObj = shuftiDao.selectById(userId);
        if(refObj == null) {
            String msg = messageSource.getMessage("no_ref", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "reference");
        }
        String selfieUrl = String.format("%d-selfie", userId);

        try {
            InputStream input = selfie.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(selfie.getSize());
            s3.putObject(bucketName, selfieUrl, input, metadata); 
            // shuftiDao.updateAddrUrl(userId, selfieUrl);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public ShuftiRefPayload getShuftiRefPayload(int userId) {
        ShuftiReference ref = shuftiDao.selectById(userId);
        ShuftiRefPayload refPayload = new ShuftiRefPayload(ref);

        // download files
        refPayload.setDocument(downloadImage(userId, "passport"));
        refPayload.setAddr(downloadImage(userId, "address"));
        refPayload.setConsent(downloadImage(userId, "consent"));
        refPayload.setSelfie(downloadImage(userId, "selfie"));

        return refPayload;
    }

    public String sendVerifyRequest(int userId, String country, String fullAddr, Names names) throws ClientProtocolException, IOException {
        ShuftiReference refObj = shuftiDao.selectById(userId);
        
        // check refObj
        if(refObj == null) {
            String msg = messageSource.getMessage("no_ref", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "reference");
        }

        String reference = refObj.getReference();
        
        String doc64 = downloadImage(userId, "passport");
        String addr64 = downloadImage(userId, "address");
        String con64 = downloadImage(userId, "consent");
        String sel64 = downloadImage(userId, "selfie");

        // build ShuftiRequest
        ShuftiRequest request = new ShuftiRequest(reference, country, doc64, addr64, fullAddr, con64, sel64, names);
        request.setCallback_url(CALLBACK_URL);
        
        sendShuftiRequest(request).subscribe();

        shuftiDao.updatePendingStatus(userId, true);

        return "sent request";
    }

    private String downloadImage(int userId, String type) {
        try {
            String url = String.format("%d-%s", userId, type);
            S3Object docS3 = s3.getObject(bucketName, url);
            InputStream stream = docS3.getObjectContent();
            
            // get byte array from image stream
            int bufLength = 2048;
            byte[] buffer = new byte[2048];
            byte[] data;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int readLength;
            while ((readLength = stream.read(buffer, 0, bufLength)) != -1) {
                out.write(buffer, 0, readLength);
            }

            data = out.toByteArray();
            String imageString = Base64.getEncoder().withoutPadding().encodeToString(data);
            
            out.close();
            stream.close();

            return imageString;
        } catch (Exception e) {
            return "";
        } 
    }

    // frontend version
    public int insertOrUpdateReference(int userId, String reference) {
        // check exists
        ShuftiReference ref = shuftiDao.selectById(userId);
        if(ref != null) {
            return shuftiDao.updateReference(userId, reference);
        }
        ref = new ShuftiReference(userId, reference);
        return shuftiDao.insert(ref);
    }

}
