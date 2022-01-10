package com.ndb.auction.service.interfaces;

import com.ndb.auction.models.user.User;

public interface IUserService {
	
	// sign up user
	String createUser(String email, String password, String country, Boolean tos);
	
	Boolean verifyAccount(String email, String code);
	
	User getUserByEmail(String email);
	
	String resendVerifyCode(String email);
	
	String request2FA(String email, String method, String phone);
	
	boolean verify2FACode(String email, String code);
}
