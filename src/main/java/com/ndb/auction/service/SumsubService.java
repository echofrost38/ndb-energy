package com.ndb.auction.service;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndb.auction.models.sumsub.Applicant;
import com.ndb.auction.models.sumsub.DocType;
import com.ndb.auction.models.sumsub.HttpMethod;
import com.ndb.auction.models.sumsub.Metadata;

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
		
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	// create applicant
	public String createApplicant(String userId, String docType, String levelName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // https://developers.sumsub.com/api-reference/#creating-an-applicant

        Applicant applicant = new Applicant(userId);

        Response response = sendPost(
                "/resources/applicants?levelName=" + levelName,
                RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        objectMapper.writeValueAsString(applicant)));

        ResponseBody responseBody = response.body();

        return responseBody != null ? 
        		objectMapper.readValue(responseBody.string(), Applicant.class).getId() : null;
    }
	
	// uploading document
	public String addDocument(String applicantId, File doc) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        // https://developers.sumsub.com/api-reference/#adding-an-id-document

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("metadata", objectMapper.writeValueAsString(new Metadata(DocType.PASSPORT, "DEU")))
                .addFormDataPart("content", doc.getName(), RequestBody.create(MediaType.parse("image/*"), doc))
                .build();

        Response response = sendPost("/resources/applicants/" + applicantId + "/info/idDoc", requestBody);
        return response.headers().get("X-Image-Id");
    }
	
	// Request check for an applicant
	public String getApplicantStatus(String applicantId) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        // https://developers.sumsub.com/api-reference/#getting-applicant-status-api

        Response response = sendGet("/resources/applicants/" + applicantId + "/requiredIdDocsStatus");

        ResponseBody responseBody = response.body();
        return responseBody != null ? responseBody.string() : null;
    }
	
	// resubmitting problematic documents
	
	
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

        Request request = new Request.Builder()
                .url(SUMSUB_TEST_BASE_URL + url)
                .header("X-App-Token", SUMSUB_APP_TOKEN)
                .header("X-App-Access-Sig", createSignature(ts, HttpMethod.GET, url, null))
                .header("X-App-Access-Ts", String.valueOf(ts))
                .get()
                .build();

        Response response = new OkHttpClient().newCall(request).execute();

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
}
