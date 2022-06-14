package com.ndb.auction.service.user;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.mail.MessagingException;

import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.GeoLocation;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserSecurity;
import com.ndb.auction.models.user.UserVerify;
import com.ndb.auction.models.user.Whitelist;
import com.ndb.auction.service.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import freemarker.template.TemplateException;

@Service
public class UserService extends BaseService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PasswordEncoder encoder;

	public String createUser(String email, String password, String country,String referredByCode) {
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
			user = new User(email, encoder.encode(password), country);
			Set<String> roles = new HashSet<String>();
			roles.add("ROLE_USER");
			user.setRole(roles);
			user.setProvider("email");
			userDao.insert(user);
			userReferralService.createNewReferrer(user.getId(),"",referredByCode);
			// create Tier Task
			TierTask tierTask = new TierTask(user.getId());
			tierTaskService.updateTierTask(tierTask);
		}
		sendEmailCode(user, VERIFY_TEMPLATE);
		return "Success";
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
			String msg = messageSource.getMessage("unregistered_email", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "email");
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
			String msg = messageSource.getMessage("unregistered_email", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "email");
		}
		String code = totpService.getVerifyCode(email);
		try {
			mailService.sendVerifyEmail(user, code, RESET_TEMPLATE);
		} catch (MessagingException | IOException | TemplateException e) {
			return false; // or exception
		}
		return true;
	}

	public String requestEmailChange(int id) {
        User user = userDao.selectById(id);
        if(sendEmailCode(user,CONFIRM_EMAIL_CHANGE_TEMPLATE)) return "Sent";
        else return "Error";
	}

	public String confirmEmailChange(int id,  String code, String newEmail) {
        User user = userDao.selectById(id);
        boolean status = totpService.checkVerifyCode(user.getEmail(),code);
        if(status) {
            try {
				totpService.clearOTP(user.getEmail());
                userDao.updateEmail(id,newEmail);
                return "Success";
            } catch (Exception e){
                return "Error";
            }
        }
        else{
            return "Invalid Code";
        }
	}

	public int updatePhone(int userId, String phone) {
		return userDao.updatePhone(userId, phone);
	}

	public String changeName(int id, String newName) {
		if(userAvatarDao.changeName(id, newName) > 0)
			return "Success";
		return "Failed";
	}

	private boolean sendEmailCode(User user, String template) {
		String code = totpService.getVerifyCode(user.getEmail());
		try {
			mailService.sendVerifyEmail(user, code, template);
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

	public List<User> getUsersByRole(String role) {
		return userDao.selectByRole(role);
	}

	public int getUserCount() {
		return userDao.countAll();
	}

	public List<User> getPaginatedUser(int offset, int limit) {
		var users = userDao.selectList(null, offset, limit, null);
		for (User user : users) {
			user.setVerify(userVerifyDao.selectById(user.getId()));
			user.setAvatar(userAvatarDao.selectById(user.getId()));
		}
		return users;
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

	public List<User> getAllUsers() {
		var users = userDao.selectAll(null);
		for (var user : users) {
			user.setAvatar(userAvatarDao.selectById(user.getId()));
		}
		return users;
	}

	///////////////////////// Geo Location /////////
	public GeoLocation addDisallowed(String country, String countryCode) {
		GeoLocation geoLocation = geoLocationDao.getGeoLocation(countryCode);
		if(geoLocation != null) {
			geoLocationDao.makeDisallow(geoLocation.getId());
			geoLocation.setAllowed(false);
			return geoLocation;
		}
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
			String msg = messageSource.getMessage("unregistered_email", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "email");
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

	@Transactional
	public String createNewUser(User user, String rPassword) {

		// create new user!
		user = userDao.insert(user);
		user.getAvatar().setId(user.getId());
		user.getVerify().setId(user.getId());
		userAvatarDao.insertOrUpdate(user.getAvatar());
		userVerifyDao.insertOrUpdate(user.getVerify());

		// add internal balance
		int ndbId = tokenAssetService.getTokenIdBySymbol("NDB");
		balanceDao.addFreeBalance(user.getId(), ndbId, 0);

		int voltId = tokenAssetService.getTokenIdBySymbol("VOLT");
		balanceDao.addFreeBalance(user.getId(), voltId, 0);

		// send email!
		try {
			mailService.sendVerifyEmail(user, rPassword, "new_user.ftlh");
		} catch (Exception e) {
		}
		return "Success";
	}

	public String changeRole(String email, String role) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			String msg = messageSource.getMessage("unregistered_email", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "email");
		}

		if (role.equals("ROLE_ADMIN")) {
			Set<String> roles = new HashSet<>();
			roles.add("ROLE_USER");
			roles.add("ROLE_ADMIN");
			user.setRole(roles);
		} else if (role.equals("ROLE_USER")) {
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
			String msg = messageSource.getMessage("unregistered_email", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "email");
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

	public int updateTier(int id, int tierLevel, Double tierPoint) {
		// If tierLevel is Diamond automatically added into Whitelist
		var tier = tierDao.selectByLevel(tierLevel);
		if(tier.getName().equals("Diamond")) {
			var m = whitelistDao.selectByUserId(id);
            if(m != null) {
                m = new Whitelist(id, "Diamond Level");
                whitelistDao.insert(m);
            }
		}
		return userDao.updateTier(id, tierLevel, tierPoint);
	}
}
