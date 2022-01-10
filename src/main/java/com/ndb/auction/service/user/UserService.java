package com.ndb.auction.service.user;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
		User user = userDao.selectByEmail(email);
		if (user != null) {
			UserVerify userVerify = userVerifyDao.selectById(user.getId());
			if (userVerify != null && userVerify.isEmailVerified()) {
				return "Already verified";
			} else {
				sendEmailCode(user, VERIFY_TEMPLATE);
				return "Already exists, sent verify code";
			}
		} else {
			user = new User();
			user.setEmail(email);
			user.setPassword(encoder.encode(password));
			user.setCountry(country);
			userDao.insert(user);

			// create user wallet in contract!
			userWalletService.addNewUser(user.getId(), email, "");

			// create Tier Task
			TierTask tierTask = new TierTask(user.getId());
			tierService.createNewTierTask(tierTask);
		}
		sendEmailCode(user, VERIFY_TEMPLATE);
		return "Success";
	}

	public boolean verifyAccount(String email, String code) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			// throw Not Found Exception
			return false;
		}

		if (!totpService.checkVerifyCode(email, code)) {
			// throw Unauthorized Exception
			return false;
		}

		if (userVerifyDao.updateEmailVerified(user.getId(), true) < 1) {
			UserVerify userVerify = new UserVerify();
			userVerify.setId(user.getId());
			userVerify.setEmailVerified(true);
			userVerifyDao.insertOrUpdate(userVerify);
		}
		return true;
	}

	public String resendVerifyCode(String email) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			return "Not Found";
		}
		sendEmailCode(user, VERIFY_TEMPLATE);
		return "Success";
	}

	public String request2FA(String email, String method, String phone) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			// throw Not Found Exception
		}

		UserSecurity userSecurity = userSecurityDao.selectById(user.getId());
		userSecurity.setAuthType(method);
		UserVerify userVerify = userVerifyDao.selectById(user.getId());

		if (userVerify == null || !userVerify.isEmailVerified()) {
			throw new UnauthorizedException("Your account is not verified", "email");
		}

		// Generate proper TOTP code
		String code = totpService.get2FACode(email);

		switch (method) {
			case "app":
				String tfaSecret = totpService.generateSecret();
				userSecurityDao.updateTfaSecret(user.getId(), tfaSecret);
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

	public String confirmRequest2FA(String email, String code) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			// throw Not Found Exception
		}
		UserVerify userVerify = userVerifyDao.selectById(user.getId());

		if (userVerify == null || !userVerify.isEmailVerified()) {
			throw new UnauthorizedException("Your account is not verified", "email");
		}

		UserSecurity userSecurity = userSecurityDao.selectById(user.getId());

		boolean status = false;
		String method;
		if (userSecurity != null && (method = userSecurity.getAuthType()) != null) {
			if (method.equals("app")) {
				status = totpService.verifyCode(code, userSecurity.getTfaSecret());
			} else if (method.equals("phone") || method.equals("email")) {
				status = totpService.check2FACode(email, code);
			}
		}

		if (status) {
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

		UserSecurity userSecurity = userSecurityDao.selectById(user.getId());
		String method;
		if (userSecurity == null || (method = userSecurity.getAuthType()) == null)
			return "error";
		switch (method) {
			case "app":
				if (userSecurity.getTfaSecret() == null || userSecurity.getTfaSecret().isEmpty()) {
					return "error";
				} else {
					return token;
				}
			case "phone":
				try {
					String code = totpService.get2FACode(user.getEmail());
					String phone = user.getPhone();
					// userDao.updateUser(user); // TODO: why update?
					smsService.sendSMS(phone, code);
					return token;
				} catch (IOException | TemplateException e) {
					return "error";
				}
			case "email":
				try {
					String code = totpService.get2FACode(user.getEmail());
					mailService.sendVerifyEmail(user, code, _2FA_TEMPLATE);
					// userDao.updateUser(user); // TODO: why update?
					return token;
				} catch (MessagingException | IOException | TemplateException e) {
					return "error"; // or exception
				}
			default:
				return "error";
		}

	}

	public boolean verify2FACode(String email, String code) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			// throw
		}
		UserSecurity userSecurity = userSecurityDao.selectById(user.getId());
		String method;
		if (userSecurity == null || (method = userSecurity.getAuthType()) == null)
			return false;
		if (method.equals("app")) {
			return totpService.verifyCode(code, userSecurity.getTfaSecret());
		} else if (method.equals("email") || method.equals("phone")) {
			return totpService.check2FACode(email, code);
		}
		return false;
	}

	public boolean sendResetToken(String email) {
		User user = userDao.selectByEmail(email);

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
				// throw
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

	public User getUserByEmail(String email, boolean includeAvatar, boolean includeSecurity, boolean includeVerify) {
		User user = userDao.selectByEmail(email);
		if (user == null)
			throw new UserNotFoundException("We were unable to find a user with the provided credentials", "email");
		if (includeAvatar)
			user.setAvatar(userAvatarDao.selectById(user.getId()));
		if (includeSecurity)
			user.setSecurity(userSecurityDao.selectById(user.getId()));
		if (includeVerify)
			user.setVerify(userVerifyDao.selectById(user.getId()));
		return user;
	}

	public User getUserById(int id) {
		return userDao.selectById(id);
	}

	public int getUserCount() {
		return userDao.countAll();
	}

	public List<User> getPaginatedUser(int offset, int limit) {
		return userDao.selectList(null, offset, limit, null);
	}

	public String deleteUser(int id) {
		if (userDao.updateDeleted(id) > 0)
			return "Success";
		return "Failed";
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
			// throw
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
			// throw
		}

		if (role.equals("admin")) {
			user.addRole("ROLE_ADMIN");
		} else if (role.equals("user")) {
			user.setRole("ROLE_USER");
		} else {
			return "Failed";
		}
		userDao.updateRole(user.getId(), role);
		return "Success";
	}

	///////// change user notificatin setting ///////
	public int changeNotifySetting(int userId, int nType, boolean status) {
		User user = userDao.selectById(userId);
		if (user == null) {
			// throw
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
