package com.ndb.auction.service.user;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.mail.MessagingException;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.GeoLocation;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserSecurity;
import com.ndb.auction.models.user.UserVerify;
import com.ndb.auction.service.BaseService;
import com.ndb.auction.service.CryptoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import freemarker.template.TemplateException;

@Service
public class UserService extends BaseService {

	@Autowired
	CryptoService cryptoService;

	@Autowired
	PasswordEncoder encoder;

	public String createUser(String email, String password, String country) {
		User user = userDao.selectEntireByEmail(email);
		if (user != null) {
			if(user.getDeleted() == 0){
				UserVerify userVerify = userVerifyDao.selectById(user.getId());
				if (userVerify != null && userVerify.isEmailVerified()) {
					return "Already verified";
				} else {
					sendEmailCode(user, VERIFY_TEMPLATE);
					return "Already exists, sent verify code";
				}
			} else {}
		} else {
			user = new User();
			user.setEmail(email);
			user.setPassword(encoder.encode(password));
			user.setCountry(country);
			Set<String> roles = new HashSet<String>();
			roles.add("ROLE_USER");
			user.setRole(roles);
			user.setProvider("email");
			userDao.insert(user);

			// create user wallet in contract!
			// userWalletService.addNewUser(user.getId(), email, "");

			// create Tier Task
			TierTask tierTask = new TierTask(user.getId());
			tierTaskService.updateTierTask(tierTask);
		}
		sendEmailCode(user, VERIFY_TEMPLATE);
		return "Success";
	}

	public boolean verifyAccount(String email, String code) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("Cannot find user by " + email, "email");
		}

		if (!totpService.checkVerifyCode(email, code)) {
			throw new UnauthorizedException("Your account is not verified", "email");
		}

		if (userVerifyDao.updateEmailVerified(user.getId(), true) < 1) {
			UserVerify userVerify = new UserVerify();
			userVerify.setId(user.getId());
			userVerify.setEmailVerified(true);
			userVerifyDao.insert(userVerify);
		}
		return true;
	}

	public String resendVerifyCode(String email) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("Cannot find user by " + email, "email");
		}

		UserVerify userVerify = userVerifyDao.selectById(user.getId());
		if (userVerify != null && userVerify.isEmailVerified()) {
			return "Already verified";
		} else {
			sendEmailCode(user, VERIFY_TEMPLATE);
			return "Already exists, sent verify code";
		}
	}


	
	public String request2FA(String email, String method, String phone) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("Cannot find user by " + email, "email");
		}

		List<UserSecurity> userSecurities = userSecurityDao.selectByUserId(user.getId());
		UserSecurity currentSecurity = null;
		// check there is any 2FA.
		if (userSecurities.size() == 0) {
			// add new security
			currentSecurity = new UserSecurity(user.getId(), method, false, "");
		} else {
			// check already exists
			for (UserSecurity userSecurity : userSecurities) {
				if (userSecurity.getAuthType().equals(method)) {
					currentSecurity = userSecurity;
					break;
				}
			}
			if (currentSecurity == null) {
				// there is no security with that method
				currentSecurity = new UserSecurity(user.getId(), method, false, "");
			} else {
				// Generate proper TOTP code
				String code = totpService.get2FACode(email);
				
				switch (method) {
					case "app":
					String tfaSecret = totpService.generateSecret();
					userSecurityDao.updateTfaSecret(currentSecurity.getId(), tfaSecret);
					String qrUri = totpService.getUriForImage(tfaSecret, user.getEmail());
					return qrUri;
					case "phone":
					try {
						userDao.updatePhone(user.getId(), phone);
						return smsService.sendSMS(phone, code);
					} catch (IOException | TemplateException e) {
						return "error";
					}
					case "email":
					try {
						mailService.sendVerifyEmail(user, code, _2FA_TEMPLATE);
						return "sent";
					} catch (MessagingException | IOException | TemplateException e) {
						return "error"; // or exception
					}
					default: 
					return String.format("There is no %s", method);
				}
			}
		}
		currentSecurity = userSecurityDao.insert(currentSecurity);
		
		UserVerify userVerify = userVerifyDao.selectById(user.getId());
		
		if (userVerify == null || !userVerify.isEmailVerified()) {
			throw new UnauthorizedException("Your account is not verified", "email");
		}
		
		// Generate proper TOTP code
		String code = totpService.get2FACode(email);
		
		switch (method) {
			case "app":
			String tfaSecret = totpService.generateSecret();
			userSecurityDao.updateTfaSecret(currentSecurity.getId(), tfaSecret);
			String qrUri = totpService.getUriForImage(tfaSecret, user.getEmail());
			return qrUri;
			case "phone":
			try {
				userDao.updatePhone(user.getId(), phone);
				return smsService.sendSMS(phone, code);
			} catch (IOException | TemplateException e) {
				return "error";
				}
			case "email":
			try {
				mailService.sendVerifyEmail(user, code, _2FA_TEMPLATE);
				return "sent";
			} catch (MessagingException | IOException | TemplateException e) {
				return "error"; // or exception
			}
		}
		
		return null;
	}
	
	public String disable2FA(int userId, String method) {
		try{
			userSecurityDao.updateTfaDisabled(userId, method, false);
		}catch(Exception e) {
			return "Failed";
		}
		return "Success";
	}
	
	public String confirmRequest2FA(String email, String method, String code) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("Cannot find user by " + email, "email");
		}
		UserVerify userVerify = userVerifyDao.selectById(user.getId());
		
		if (userVerify == null || !userVerify.isEmailVerified()) {
			throw new UnauthorizedException("Your account is not verified", "email");
		}

		List<UserSecurity> userSecurities = userSecurityDao.selectByUserId(user.getId());
		if (userSecurities.size() == 0) {
			throw new UnauthorizedException("There is no proper 2FA setting.", "code");
		}

		boolean status = false;
		int userSecurityId = 0;
		
		for(UserSecurity userSecurity : userSecurities) {
			if (userSecurity.getAuthType().equals(method)) {
				if (method.equals("app")) {
					status = totpService.verifyCode(code, userSecurity.getTfaSecret());
					userSecurityId = userSecurity.getId();
				} else if (method.equals("phone") || method.equals("email")) {
					status = totpService.check2FACode(email, code);
					userSecurityId = userSecurity.getId();
				}
			}
		}

		if (status && userSecurityId != 0) {
			userSecurityDao.updateTfaEnabled(userSecurityId, true);
			return "Success";
		} else {
			return "Failed";
		}

	}

	public String signin2FA(User user) {
		byte[] array = new byte[32];
		new Random().nextBytes(array);
		String token = UUID.randomUUID().toString();

		List<UserSecurity> userSecurities = userSecurityDao.selectByUserId(user.getId());
		boolean mfaEnabled = false;
		for (UserSecurity userSecurity : userSecurities) {
			String method;
			if (userSecurity == null || (method = userSecurity.getAuthType()) == null)
				return "error";
			
			if(userSecurity.isTfaEnabled()) {
				mfaEnabled = true;
			} else {
				continue;
			}

			switch (method) {
				case "app":
					if (userSecurity.getTfaSecret() == null || userSecurity.getTfaSecret().isEmpty()) {
						return "error";
					}
					break;
				case "phone":
					try {
						String code = totpService.get2FACode(user.getEmail() + method);
						String phone = user.getPhone();
						smsService.sendSMS(phone, code);
					} catch (Exception e) {
						return "error";
					}
					break;
				case "email":
					try {
						String code = totpService.get2FACode(user.getEmail() + method);
						mailService.sendVerifyEmail(user, code, _2FA_TEMPLATE);
					} catch (Exception e) {
						return "error"; // or exception
					}
					break;
				default:
					return "error";
			}
		}

		if(!mfaEnabled) return "Please set 2FA.";

		return token;
	}

	public boolean verify2FACode(String email, Map<String, String> codeMap) {
		boolean result = false;
		User user = userDao.selectByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("Cannot find user by " + email, "email");
		}
		List<UserSecurity> userSecurities = userSecurityDao.selectByUserId(user.getId());
		for (UserSecurity userSecurity : userSecurities) {
			String method;
			if(!userSecurity.isTfaEnabled()) continue;
			if (userSecurity == null || (method = userSecurity.getAuthType()) == null)
				return false;
			if (method.equals("app")) {
				result = totpService.verifyCode(codeMap.get(method), userSecurity.getTfaSecret());
			} else if (method.equals("email") || method.equals("phone")) {
				result = totpService.check2FACode(email + method, codeMap.get(method));
			}
			if (!result)
				return false;
		}

		return true;
	}

	public boolean sendResetToken(String email) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("Cannot find user by " + email, "email");
		}
		String code = totpService.get2FACode(email);
		try {
			mailService.sendVerifyEmail(user, code, RESET_TEMPLATE);
		} catch (MessagingException | IOException | TemplateException e) {
			return false; // or exception
		}
		return true;
	}

	public String resetPassword(String email, String code, String newPass) {

		if (totpService.check2FACode(email, code)) {
			User user = userDao.selectByEmail(email);
			if (user == null) {
				throw new UserNotFoundException("Cannot find user by " + email, "email");
			}
			userDao.updatePassword(user.getId(), encoder.encode(newPass));
		} else {
			return "Failed";
		}

		return "Success";
	}

	public String changePassword(int id, String newPassword) {
		if (userDao.updatePassword(id, encoder.encode(newPassword)) > 0)
			return "Success";
		return "Failed";
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

	public User getUserById(int id) {
		User user = userDao.selectById(id);
		
		user.setAvatar(userAvatarDao.selectById(id));
		user.setSecurity(userSecurityDao.selectByUserId(id));
		user.setVerify(userVerifyDao.selectById(id));

		return user;
	}

	public int getUserCount() {
		return userDao.countAll();
	}

	public List<User> getPaginatedUser(int offset, int limit) {
		return userDao.selectList(null, offset, limit, null);
	}

	public String deleteUser(int id) {
		// if (userDao.updateDeleted(id) > 0)
		if (userDao.deleteById(id) > 0)
			return "Success";
		return "Failed";
	}

	public User getUserByEmail(String email) {
		return userDao.selectByEmail(email);
	}

	///////////////////////// Geo Location /////////
	public GeoLocation addDisallowed(String country, String countryCode) {
		return geoLocationDao.addDisallowedCountry(country, countryCode);
	}

	public List<GeoLocation> getDisallowed() {
		return geoLocationDao.getGeoLocations();
	}

	public int makeAllow(int locationId) {
		return geoLocationDao.makeAllow(locationId);
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

		for (int i = 1; i < len; i++) {
			randomIndex = random.nextInt(lowers.length());
			sb.append(lowers.charAt(randomIndex));
		}

		randomIndex = random.nextInt(numbers.length());
		sb.append(numbers.charAt(randomIndex));

		randomIndex = random.nextInt(symbols.length());
		sb.append(symbols.charAt(randomIndex));

		return sb.toString();
	}

	public String resetPasswordByAdmin(String email) {

		User user = userDao.selectByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("Cannot find user by " + email, "email");
		}
		String rPassword = getRandomPassword(10);
		String encoded = encodePassword(rPassword);
		userDao.updatePassword(user.getId(), encoded);

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
		userDao.insert(user);
		userAvatarDao.insertOrUpdate(user.getAvatar());
		userVerifyDao.insertOrUpdate(user.getVerify());

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
		User user = userDao.selectByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("Cannot find user by " + email, "email");
		}

		if (role.equals("admin")) {
			user.addRole("ROLE_ADMIN");
		} else if (role.equals("user")) {
			Set<String> roles = new HashSet<>();
			roles.add("ROLE_USER");
			user.setRole(roles);
		} else {
			return "Failed";
		}
		userDao.updateRole(user.getId(), user.getRoleString());
		return "Success";
	}

	///////// change user notificatin setting ///////
	public int changeNotifySetting(int userId, int nType, boolean status) {
		User user = userDao.selectById(userId);
		if (user == null) {
			throw new UserNotFoundException("Cannot find user", "userId");
		}

		int notifySetting = user.getNotifySetting();
		if (status) {
			notifySetting = notifySetting | (0x01 << nType);
		} else {
			notifySetting = notifySetting & ~(0x01 << nType);
		}
		userDao.updateNotifySetting(userId, notifySetting);
		return nType;
	}

	public int updateTier(int id, int tierLevel, long tierPoint) {
		return userDao.updateTier(id, tierLevel, tierPoint);
	}

}
