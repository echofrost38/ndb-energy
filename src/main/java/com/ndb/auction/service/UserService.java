package com.ndb.auction.service;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.GeoLocation;
import com.ndb.auction.models.User;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.service.interfaces.IUserService;

import freemarker.template.TemplateException;

@Service
public class UserService extends BaseService implements IUserService {

	@Autowired
	CryptoService cryptoService;
  
	@Autowired
	PasswordEncoder encoder;

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
			user = new User(email, encoder.encode(password), country, tos);			
			userDao.createUser(user);
			user = userDao.getUserByEmail(email).get();

			// create user wallet in contract!
			userWalletService.addNewUser(user.getId(), email, "");

			// create Tier Task
			TierTask tierTask = new TierTask(user.getId());
			tierService.createNewTierTask(tierTask);
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
			String qrUri = totpService.getUriForImage(secret, user.getEmail());
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
	    String token = UUID.randomUUID().toString();
	    
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
