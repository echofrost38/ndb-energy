package com.ndb.auction.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Part;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndb.auction.models.KYCSetting;
import com.ndb.auction.models.sumsub.Applicant;
import com.ndb.auction.models.sumsub.ApplicantResponse;
import com.ndb.auction.models.sumsub.HttpMethod;
import com.ndb.auction.models.sumsub.Metadata;
import com.ndb.auction.models.sumsub.Review;
import com.ndb.auction.payload.ReviewResult;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

@Service
public class SumsubService extends BaseService {
	
	public static final String KYC = "basic-kyc-level";
	public static final String AML = "basic-aml-level";
    public static final String path = "/tmp/sumsub/";
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	// create applicant
	public String createApplicant(String userId, String levelName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        
        // 
        List<Applicant> applicants = sumsubDao.getApplicantByUserId(userId);
        if(applicants.size() != 0) {
            return applicants.get(0).getId();
        }

        Applicant applicant = new Applicant(userId);

        @SuppressWarnings("deprecation")
		Response response = sendPost(
                "/resources/applicants?levelName=" + levelName,
                RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        objectMapper.writeValueAsString(applicant)));
        
        ResponseBody responseBody = response.body();
        String applicantId = responseBody != null ? 
        		objectMapper.readValue(responseBody.string(), Applicant.class).getId() : null;
        applicant.setId(applicantId);
        sumsubDao.createNewApplicant(applicant);
        return applicantId;
    }

    public String upgradeApplicant(String userId, String levelName) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        List<Applicant> applicants = sumsubDao.getApplicantByUserId(userId);
        if(applicants.size() == 0) {
            return null;
        }
        Applicant applicant = applicants.get(0);

        Response response = sendPost(
            "/resources/applicants/" + applicant.getId() + "/moveToLevel?" + levelName, 
            null);
        
        ResponseBody responseBody = response.body();
        ApplicantResponse applicantResponse = objectMapper.readValue(responseBody.string(), ApplicantResponse.class);

        return applicantResponse.getId();
    }
	
	// uploading document
	public String addDocument(String applicantId, String country, String docType, File doc) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        // https://developers.sumsub.com/api-reference/#adding-an-id-document
		
        @SuppressWarnings("deprecation")
		RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("metadata", objectMapper.writeValueAsString(new Metadata(docType, country)))
                .addFormDataPart("content", doc.getName(), RequestBody.create(MediaType.parse("image/*"), doc))
                .build();

        Response response = sendPost("/resources/applicants/" + applicantId + "/info/idDoc", requestBody);
        return response.headers().get("X-Image-Id");
    }
	
	public String addDocument(String applicantId, String country, String docType, Part part) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        
        final String fileName = part.getName();

        OutputStream out = null;
        InputStream filecontent = null;
        File file = new File(path + fileName);
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        try {
            out = new FileOutputStream(file);
            filecontent = part.getInputStream();

            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
        } catch (FileNotFoundException fne) {
            System.out.println(fne.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
        }
		
		@SuppressWarnings("deprecation")
		RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("metadata", objectMapper.writeValueAsString(new Metadata(docType, country)))
                .addFormDataPart("content", part.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                .build();
        Response response = sendPost("/resources/applicants/" + applicantId + "/info/idDoc", requestBody);
        
        System.out.println(response.body().string());
        file.delete();
		return response.headers().get("X-Image-Id");
	}
	
    public String requestChecking(String userId) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        List<Applicant> applicants = sumsubDao.getApplicantByUserId(userId);
        if(applicants.size() == 0) {
            return null;
        }
        Applicant applicant = applicants.get(0);
        
        @SuppressWarnings("deprecation")
		Response response = sendPost(
            "/resources/applicants/" + applicant.getId() + "/status/pending", 
            RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), "")
        );
        
        return response.body().string();
    }

	public ApplicantResponse gettingApplicantData(String applicantId) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
		Response response = sendGet("/resources/applicants/" + applicantId + "/one");
		if(response.code() == 401) {
			return null;
		}
		
		String body = response.body().string();
		
		System.out.println(body);
		
		ApplicantResponse applicant = objectMapper.readValue(body, ApplicantResponse.class);
		return applicant;
	}
	
	// Request check for an applicant
	public String getApplicantStatus(String applicantId) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        // https://developers.sumsub.com/api-reference/#getting-applicant-status-api

        Response response = sendGet("/resources/applicants/" + applicantId + "/requiredIdDocsStatus");

        ResponseBody responseBody = response.body();
        return responseBody != null ? responseBody.string() : null;
    }
	
	// private routines
	private Response sendPost(String url, RequestBody requestBody) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        long ts = Instant.now().getEpochSecond();
        
        Request request = new Request.Builder()
                .url(SUMSUB_TEST_BASE_URL + url)
                .header("X-App-Token", SUMSUB_APP_TOKEN)
                .header("X-App-Access-Sig", createSignature(ts, HttpMethod.POST, url, requestBodyToBytes(requestBody)))
                .header("X-App-Access-Ts", String.valueOf(ts))
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
	
	private Response sendGet(String url) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        long ts = Instant.now().getEpochSecond();
        
        System.out.println(Instant.now().toString());
        
        Request request = new Request.Builder()
                .url(SUMSUB_TEST_BASE_URL + url)
                .header("X-App-Token", SUMSUB_APP_TOKEN)
                .header("X-App-Access-Sig", createSignature(ts, HttpMethod.GET, url, null))
                .header("X-App-Access-Ts", String.valueOf(ts))
                .get()
                .build();

        Response response = new OkHttpClient().newCall(request).execute();
        
        System.out.println(response.message());
        
        if (response.code() != 200 && response.code() != 201) {
            // https://developers.sumsub.com/api-reference/#errors
            // If an unsuccessful answer is received, please log the value of the "correlationId" parameter.
            // Then perhaps you should throw the exception. (depends on the logic of your code)
        }
        return response;
    }
	
	private String createSignature(long ts, HttpMethod httpMethod, String path, byte[] body) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(new SecretKeySpec(SUMSUB_SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        hmacSha256.update((ts + httpMethod.name() + path).getBytes(StandardCharsets.UTF_8));
        byte[] bytes = body == null ? hmacSha256.doFinal() : hmacSha256.doFinal(body);
        return Hex.encodeHexString(bytes);
    }
	
    private byte[] requestBodyToBytes(RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return buffer.readByteArray();
    }
    
    public Applicant getApplicant(String applicantId) {
    	return sumsubDao.getApplicantById(applicantId);
    }
    
    public boolean checkVerificationStatus(String userId, String level) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        List<Applicant> applicants = sumsubDao.getApplicantByUserId(userId);
        if(applicants.size() == 0) return false;

        String applicantId = applicants.get(0).getId();
        ApplicantResponse applicantResponse = gettingApplicantData(applicantId);

        Review review = applicantResponse.getReview();
        if(review.getLevelName().equals(level)) {
            ReviewResult result = review.getReviewResult();
            if(result == null) return false;
            if(result.getReviewAnswer().equals("GREEN")) {
                return true;
            }
        }
        return false;
    }

    public List<Applicant> getApplicantsByUserId(String userId) {
    	return sumsubDao.getApplicantByUserId(userId);
    }

    public KYCSetting updateKYCSetting(String kind, double withdraw, double deposit, double bid, double direct) {
        KYCSetting setting = new KYCSetting(kind, withdraw, deposit, bid, direct);
        return sumsubDao.updateKYCSetting(setting);
    }

    public List<KYCSetting> getKYCSettings() {
        return sumsubDao.getKYCSettings();
    }

    public boolean checkThreshold(String userId, String kind, double amount) {
        List<KYCSetting> settings = sumsubDao.getKYCSettings();
        double kycThreshold = 0.0, amlThreshold = 0.0;
        for (KYCSetting kycSetting : settings) {
            if(kycSetting.getKind().equals("kyc")) {
                switch (kind) {
                    case "deposit":
                        kycThreshold = kycSetting.getDeposit();
                        break;
                    case "withdraw":
                        kycThreshold = kycSetting.getWithdraw();
                        break;
                    case "bid":
                        kycThreshold = kycSetting.getBid();
                        break;
                    case "direct_sale":
                        kycThreshold = kycSetting.getDirect();
                        break;
                    default:
                        return false;
                }
            } else if (kycSetting.getKind().equals("aml")) {
                switch (kind) {
                    case "deposit":
                        amlThreshold = kycSetting.getDeposit();
                        break;
                    case "withdraw":
                        amlThreshold = kycSetting.getWithdraw();
                        break;
                    case "bid":
                        amlThreshold = kycSetting.getBid();
                        break;
                    case "direct_sale":
                        amlThreshold = kycSetting.getDirect();
                        break;
                    default:
                        return false;
                }
            } else {
                return false;
            }
        }

        if(amount > kycThreshold) {
            if( amount < amlThreshold) {
                try {
                    if(!checkVerificationStatus(userId, SumsubService.KYC)) {
                        return false;
                    }
                } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
                    e.printStackTrace();
                    return false;
                }   
            } else if( amount >= amlThreshold) {
                try {
                    if(!checkVerificationStatus(userId, SumsubService.AML)) {
                        return false;
                    }
                } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
                    e.printStackTrace();
                    return false;
                } 
            }
        } 

        return true;
    }
}
