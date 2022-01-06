package com.ndb.auction.service.interfaces;

import java.util.Map;

import com.ndb.auction.models.User;

public interface IUserService {
	
	// sign up user
	String createUser(String email, String password, String country, Boolean tos);
	
	Boolean verifyAccount(String email, String code);
	
	User getUserByEmail(String email);
	
	String resendVerifyCode(String email);
	
	String request2FA(String email, String method, String phone);
	
	boolean verify2FACode(String email, Map<String, String> codeMap);
}
