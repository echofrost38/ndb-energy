package com.ndb.auction.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import static dev.samstevens.totp.util.Utils.getDataUriForImage;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class TotpService {
	
 	 //cache based on username and OPT MAX 8 
	 private static final Integer EXPIRE_MINS = 10;

	 private LoadingCache<String, String> otpCache;
	 private LoadingCache<String, String> _2FACache;
	 private LoadingCache<String, Authentication> tokenCache;
	 
	 public TotpService() {
		 otpCache = CacheBuilder.newBuilder().
				 expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES).build(new CacheLoader<String, String>() {
					 public String load(String key) {
						 return "";
					 }
				 });
		 _2FACache = CacheBuilder.newBuilder().
				 expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES).build(new CacheLoader<String, String>() {
					 public String load(String key) {
						 return "";
					 }
				 });
		 
		 tokenCache = CacheBuilder.newBuilder().
				 expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES).build(new CacheLoader<String, Authentication>() {
					 public Authentication load(String key) {
						 return null;
					 }
				 });
	 }
	 

	public void setTokenAuthCache(String token, Authentication auth) {
		tokenCache.put(token, auth);
	}
	 
	public Authentication getAuthfromToken(String token) {
		Authentication auth;
		try {
			auth = tokenCache.get(token);
			tokenCache.invalidate(token);
			return auth;
		} catch (ExecutionException e) {
			return null;
		}
	}
	 
	 public String getVerifyCode(String key) {
		 String code = generateOTP(key);
		 otpCache.put(key, code);
		 return code;
	 }
	 
	 public String get2FACode(String key) {
		 String code = generateOTP(key);
		 _2FACache.put(key, code);
		 return code;
	 }
	 
	 //This method is used to push the opt number against Key. Rewrite the OTP if it exists
	 //Using user id  as key
	 private String generateOTP(String key){
		 Random random = new Random();
		 Integer otp = 100000 + random.nextInt(900000);
		 return otp.toString();
	}

	 //This method is used to return the OPT number against Key->Key values is username
	 public Boolean checkVerifyCode(String key, String code){ 
		try{
			String existing = otpCache.get(key);
			if(existing.equals(code)) {
				return true;
			} else {
				return false;
			}
		}catch (Exception e){
			return false; 
		}
	 }
	 
	 public Boolean check2FACode(String key, String code) {
		 try{
			String existing = _2FACache.get(key);
			if(existing.equals(code)) {
				return true;
			} else {
				return false;
			}
		 }catch (Exception e){
			return false; 
		 }
	 }

	//This method is used to clear the OTP cached already
	public void clearOTP(String key){ 
		otpCache.invalidate(key);
	}
	
	public String generateSecret() {
        SecretGenerator generator = new DefaultSecretGenerator();
        return generator.generate();
    }
	
    public String getUriForImage(String secret, String email) {
        QrData data = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer("sale.ndb.money")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = new byte[0];

        try {
            imageData = generator.generate(data);
        } catch (QrGenerationException e) {
        	//
        }

        String mimeType = generator.getImageMimeType();

        return getDataUriForImage(imageData, mimeType);
    }

    public boolean verifyCode(String code, String secret) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return verifier.isValidCode(secret, code);
    }
}
