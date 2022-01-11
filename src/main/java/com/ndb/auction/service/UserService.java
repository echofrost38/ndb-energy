package com.ndb.auction.service;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.GeoLocation;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserSecurity;
import com.ndb.auction.models.user.UserVerify;

import freemarker.template.TemplateException;

@Service
public class UserService extends BaseService {

	@Autowired
	CryptoService cryptoService;
  
	@Autowired
	PasswordEncoder encoder;

	public String createUser(String email, String password, String country, Boolean tos) {
		User user = userDao.selectByEmail(email);
		
		if(user != null) {
			UserVerify userVerify = userVerifyDao.selectById(user.getId());
			if(userVerify.isEmailVerified()) {
				return "Already verified";
			} else {
				sendEmailCode(user, VERIFY_TEMPLATE);
				return "Already exists, sent verify code";
			}
		} else {
			user = new User(email, encoder.encode(password), country);	
			userDao.insert(user);
			user = userDao.selectByEmail(email);

			// create security and verify and store to database!
			UserSecurity userSecurity = new UserSecurity();
			userSecurity.setId(user.getId());
			userSecurityDao.insert(userSecurity);

			UserVerify userVerify = new UserVerify();
			userVerify.setId(user.getId());
			userVerifyDao.insert(userVerify);

			// create user wallet in contract! not in this phase
			// userWalletService.addNewUser(user.getId(), email, "");

			// create Tier Task
			TierTask tierTask = new TierTask(user.getId());
			tierService.createNewTierTask(tierTask);
		}
		sendEmailCode(user, VERIFY_TEMPLATE);
		return "Success";
	}

	// Email verify function!
	public Boolean verifyAccount(String email, String code) {
		
		User user = userDao.selectByEmail(email);
		if(user == null) {
			throw new UserNotFoundException("Cannot find user", "email");
		}
		
		if(!totpService.checkVerifyCode(email, code)) {
			throw new UnauthorizedException("Your verification code is not matched", "code");
		}
		userVerifyDao.updateEmailVerified(user.getId(), true);
		return true;
	}
	
	// Resend code
	public String resendVerifyCode(String email) {
		User user = userDao.selectByEmail(email);
		if(user == null) {
			throw new UserNotFoundException("Cannot find user", "email");
		}
		sendEmailCode(user, VERIFY_TEMPLATE);		
		return "Success";
	}
	
	// Request 2FA method 
	public String request2FA(String email, String method, String phone) {
		
		User user = userDao.selectByEmail(email);
		if(user == null) {
			throw new UserNotFoundException("Cannot find user", "email");
		}

		UserVerify userVerify = userVerifyDao.selectById(user.getId());
		if(!userVerify.isEmailVerified()) {
			throw new UnauthorizedException("Your account is not verified", "email");
		}
		
		// Generate proper TOTP code
		String code = totpService.get2FACode(email);
		
		switch(method) {
		case "app":
			String secret = totpService.generateSecret();
			userSecurityDao.updateSecretAndAuthType(user.getId(), secret, "app");
			String qrUri = totpService.getUriForImage(secret, user.getEmail());
			return qrUri;
		case "phone":
			try {
				userDao.updatePhone(user.getId(), phone);
				userSecurityDao.updateSecretAndAuthType(user.getId(), phone, "phone");
				return smsService.sendSMS(phone, code);
			} catch (IOException | TemplateException e) {
				return "error";
			}
		case "email":
			try {
				mailService.sendVerifyEmail(user, code, _2FA_TEMPLATE);
				userSecurityDao.updateSecretAndAuthType(user.getId(), method, method);
				return "sent";
			} catch (MessagingException | IOException | TemplateException e) {
				return "error"; // or exception
			}
		}
		
		return null;
	}
	
	public String confirmRequest2FA(String email, String method, String code) {
		User user = getUserByEmail(email);
		UserVerify userVerify = userVerifyDao.selectById(user.getId());
		if(!userVerify.isEmailVerified()) {
			throw new UnauthorizedException("Your account is not verified", "email");
		}
		
		boolean status = false;

		if(method.equals("app")) {
			UserSecurity userSecurity = userSecurityDao.selectById(user.getId());
			status = totpService.verifyCode(code, userSecurity.getTfaSecret());
		} else if(method.equals("phone") || method.equals("email")){
			status = totpService.check2FACode(email, code);
		}
		
		if(status) {
			userSecurityDao.updateTfaEnabled(user.getId(), true);
			return "Success";
		} else {
			return "Failed";
		}			
		
	}
	
	public String signin2FA(User user) {
		byte[] array = new byte[32]; 
	    new Random().nextBytes(array);
	    String token = UUID.randomUUID().toString();
	    
	    Map<String, Boolean> methods = user.getTwoStep();

		for (Map.Entry<String, Boolean> method : methods.entrySet()) {
			String key = method.getKey();
			Boolean value = method.getValue();

			if (!value) continue;

			switch(key) {
			case "app":
				if(user.getGoogleSecret() == "") {
					return "error";
				} 
				break;
			case "phone":
				try {
					String code = totpService.get2FACode(user.getEmail() + key);//example@gmail.comphone
					String phone = user.getMobile();
					userDao.updateUser(user);
					smsService.sendSMS(phone, code);
				} catch (IOException | TemplateException e) {
					return "error";
				}
				break;
			case "email":
				try {
					String code = totpService.get2FACode(user.getEmail() + key);//example@gmail.comemail
					mailService.sendVerifyEmail(user, code, _2FA_TEMPLATE);
					userDao.updateUser(user);
				} catch (MessagingException | IOException | TemplateException e) {
					return "error"; // or exception
				}
				break;
			}
		}
		return token;
	}
	
	public boolean verify2FACode(String email, Map<String, String> codeMap) {
		boolean result = false;
		User user = getUserByEmail(email);
		Map<String, Boolean> methods = user.getTwoStep();

		for (Map.Entry<String, Boolean> method : methods.entrySet()) {
			String key = method.getKey();
			Boolean value = method.getValue();

			if (!value) continue;

			String code = codeMap.get(key);
			if(key.equals("app")) {
				return totpService.verifyCode(code, user.getGoogleSecret());
			} else if (key.equals("email") || key.equals("phone")) {
				return totpService.check2FACode(email + key, code);
			}
		}

		return result;
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
			user.setPassword(encoder.encode(newPass));
			
			userDao.updateUser(user);
		} else {
			return "Failed";
		}
		
		return "Success";
	}
	
	public String changePassword(String id, String newPassword) {
		User user = getUserById(id);
		user.setPassword(encoder.encode(newPassword));
		userDao.updateUser(user);
		return "Success";
	}
	
	private boolean sendEmailCode(User user, String template) {
		String code = totpService.getVerifyCode(user.getEmail());
		try {
			mailService.sendVerifyEmail(user, code, VERIFY_TEMPLATE);
		} catch (Exception e) {
			return false; // or exception
		}	
		return true;
	}
	
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

	public int getUserCount() {
		return userDao.getUserCount();
	}

	public List<User> getPaginatedUser(String key, int limit) {
		if(key == null) {
			return userDao.getFirstPageOfUser(limit);
		}
		return userDao.getPaginatedUser(key, limit);
	}

	public String deleteUser(String id) {
		return userDao.deleteUser(id).getEmail();
	}

	///////////////////////// Geo Location /////////
	public GeoLocation addDisallowed(String countryCode) {
		return geoLocationDao.addDisallowedCountry(countryCode);
	}

	public List<GeoLocation> getDisallowed() {
		return geoLocationDao.getGeoLocations();
	}

	public GeoLocation makeAllow(String countryCode) {
		return geoLocationDao.makeAllow(countryCode);
	}

	public String encodePassword(String pass) {
		return encoder.encode(pass);
	}

	public boolean checkMatchPassword(String pass, String encPass) {
		return encoder.matches(pass, encPass);
	}

	///////////////////// user operation ///////////
	public String getRandomPassword(int len) {
		// ASCII range – alphanumeric (0-9, a-z, A-Z)
		
		final String uppers = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final String lowers = "abcdefghijklmnopqrstuvwxyz";
		final String numbers = "0123456789";
		final String symbols = ",./<>?!@#$%^&*()_+-=";

		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder();

		// each iteration of the loop randomly chooses a character from the given
		// ASCII range and appends it to the `StringBuilder` instance

		int randomIndex = random.nextInt(uppers.length());
		sb.append(uppers.charAt(randomIndex));

		for (int i = 1; i < len; i++)
		{
			randomIndex = random.nextInt(lowers.length());
			sb.append(lowers.charAt(randomIndex));
		}

		randomIndex = random.nextInt(numbers.length());
		sb.append(numbers.charAt(randomIndex));

		randomIndex = random.nextInt(symbols.length());
		sb.append(symbols.charAt(randomIndex));

		return sb.toString();
	}

	public String resetPassword(User user, String rPassword) {
		
		userDao.updateUser(user);

		// emailing resetted password	
		try {
			mailService.sendVerifyEmail(user, rPassword, "newPassword.ftlh");
		} catch (Exception e) {
			e.printStackTrace();
			return "Sending email failed.";
		}
		return "Success";
	}

	public String createNewUser(User user, String rPassword) {

		// create new user!
		userDao.createUser(user);
		user = userDao.getUserByEmail(user.getEmail()).get();
		
		// send email!	
		try {
			mailService.sendVerifyEmail(user, rPassword, "NE");
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed";
		}
		
		// ndbWalletService.createAccount(user.getId(), user.getEmail());

		return "Success";
	}

	public String changeRole(String email, String role) {
		User user = getUserByEmail(email);
		if(role.equals("admin")) {
			user.getRole().add("ROLE_ADMIN");
		} else if (role.equals("user")) {
			Set<String> roles = new HashSet<String>();
			roles.add("ROLE_USER");
			user.setRole(roles);
		} else {
			return "Failed";
		}
		userDao.updateUser(user);
		return "Success";
	}

	///////// change user notificatin setting ///////
	public int changeNotifySetting(String userId, int nType, boolean status) {
		User user = userDao.getUserById(userId);
		int notifySetting = user.getNotifySetting();
		if(status) {
			notifySetting = notifySetting | (0x01 << nType);
		} else {
			notifySetting = notifySetting & ~(0x01 << nType);
		}
		user.setNotifySetting(notifySetting);
		userDao.updateUser(user);
		return nType;
	}
	
}
