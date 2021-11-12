package com.ndb.auction.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.Coin;
import com.ndb.auction.models.User;
import com.ndb.auction.service.interfaces.IUserService;

import freemarker.template.TemplateException;

@Service
public class UserService extends BaseService implements IUserService {

	@Override
	public String createUser(String email, String password, String country, Boolean tos) {
		Optional<User> tuser = userDao.getUserByEmail(email);
		
		User user;
		if(tuser != null) {
			user = tuser.get();
			if(user.getVerify().get("email")) {
				return "Already verified";
			} else {
				sendEmailCode(user, VERIFY_TEMPLATE);
				return "Already exists, sent verify code";
			}
		} else {
			List<Coin> coins = cryptoDao.getCoins();
			user = new User(email, password, country, tos, coins);			
			userDao.createUser(user);
		}
		sendEmailCode(user, VERIFY_TEMPLATE);
		return "Success";
	}

	@Override
	public Boolean verifyAccount(String email, String code) {
		
		Optional<User> tUser = userDao.getUserByEmail(email);
		if(tUser == null) {
			// throw Not Found Exception
			return false;
		}
		
		if(!totpService.checkVerifyCode(email, code)) {
			// throw Unauthorized Exception
			return false;
		}
		
		User user = tUser.get();
		user.getVerify().replace("email", true);
		userDao.updateUser(user);		
		return true;
	}
	
	@Override
	public String resendVerifyCode(String email) {
		Optional<User> tUser = userDao.getUserByEmail(email);
		if(tUser == null) {
			return "Not Found";
		}
		sendEmailCode(tUser.get(), VERIFY_TEMPLATE);		
		return "Success";
	}
	
	@Override
	public String request2FA(String email, String method, String phone) {
		
		User user = getUserByEmail(email);
		user.setTwoStep(method);
				
		if(!user.getVerify().get("email")) {
			throw new UnauthorizedException("Your account is not verified", "email");
		}
		
		// Generate proper TOTP code
		String code = totpService.get2FACode(email);
		
		switch(method) {
		case "app":
			String secret = totpService.generateSecret();
			user.setGoogleSecret(secret);
			userDao.updateUser(user);
			String qrUri = totpService.getUriForImage(secret);
			return qrUri;
		case "phone":
			try {
				user.setMobile(phone);
				userDao.updateUser(user);
				return smsService.sendSMS(phone, code);
			} catch (IOException | TemplateException e) {
				return "error";
			}
		case "email":
			try {
				mailService.sendVerifyEmail(user, code, _2FA_TEMPLATE);
				userDao.updateUser(user);
				return "sent";
			} catch (MessagingException | IOException | TemplateException e) {
				return "error"; // or exception
			}
		}
		
		return null;
	}
	
	public String confirmRequest2FA(String email, String code) {
		User user = getUserByEmail(email);
		
		if(!user.getVerify().get("email")) {
			throw new UnauthorizedException("Your account is not verified", "email");
		}
		
		boolean status = false;
		String method = user.getTwoStep();
		if(method.equals("app")) {
			status = totpService.verifyCode(code, user.getGoogleSecret());
		} else if(method.equals("phone") || method.equals("email")){
			status = totpService.check2FACode(email, code);
		}
		
		if(status) {
			user.getSecurity().replace("2FA", true);
			userDao.updateUser(user);
			return "Success";
		} else {
			return "Failed";
		}			
		
	}
	
	public String signin2FA(User user) {
		byte[] array = new byte[32]; 
	    new Random().nextBytes(array);
	    String token = new String(array, Charset.forName("UTF-8"));
	    
	    String method = user.getTwoStep();
	    switch(method) {
	    case "app":
	    	if(user.getGoogleSecret() == "") {
	    		return "error";
	    	} else {
	    		return token;
	    	}
	    case "phone":
	    	try {
				String code = totpService.get2FACode(user.getEmail());
	    		String phone = user.getMobile();
				userDao.updateUser(user);
				smsService.sendSMS(phone, code);
				return token;
			} catch (IOException | TemplateException e) {
				return "error";
			}
	    case "email":
	    	try {
	    		String code = totpService.get2FACode(user.getEmail());
	    		mailService.sendVerifyEmail(user, code, _2FA_TEMPLATE);
				userDao.updateUser(user);
				return token;
			} catch (MessagingException | IOException | TemplateException e) {
				return "error"; // or exception
			}
	    default:
	    	return "error";
	    }
	    
	}
	
	@Override
	public boolean verify2FACode(String email, String code) {
		User user = getUserByEmail(email);
		String method = user.getTwoStep();
		if(method.equals("app")) {
			return totpService.verifyCode(code, user.getGoogleSecret());
		} else if (method.equals("email") || method.equals("phone")) {
			return totpService.check2FACode(email, code);
		}
		return false;
	}
	
	public boolean sendResetToken(String email) {
		User user = getUserByEmail(email);		
		String code = totpService.get2FACode(email);
		try {
			mailService.sendVerifyEmail(user, code, RESET_TEMPLATE);
		} catch (MessagingException | IOException | TemplateException e) {
			return false; // or exception
		}
		return true;
	}
	
	public String resetPassword(String email, String code, String newPass) {

		if(totpService.check2FACode(email, code)) {
			User user = getUserByEmail(email);
			user.setPassword(newPass);
			
			userDao.updateUser(user);
		} else {
			return "Failed";
		}
		
		return "Success";
	}
	
	
	
	private boolean sendEmailCode(User user, String template) {
		String code = totpService.getVerifyCode(user.getEmail());
		try {
			mailService.sendVerifyEmail(user, code, VERIFY_TEMPLATE);
		} catch (MessagingException | IOException | TemplateException e) {
			return false; // or exception
		}	
		return true;
	}
	
	@Override
	public User getUserByEmail(String email) {
		Optional<User> tUser = userDao.getUserByEmail(email);
		
		if(tUser == null) {
			throw new UserNotFoundException("We were unable to find a user with the provided credentials", "email");
		}
		
		return tUser.get();
	}	

	public User getUserById(String id) {
		return userDao.getUserById(id);
	}

	public User updateUser(User user) {
		return userDao.updateUser(user);
	}
}
