package com.ndb.auction.service.user;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserSecurity;
import com.ndb.auction.models.user.UserVerify;
import com.ndb.auction.service.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import freemarker.template.TemplateException;

@Service
public class UserAuthService extends BaseService {
	
	@Autowired
	PasswordEncoder encoder;
	
    public boolean verifyAccount(String email, String code) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			String msg = messageSource.getMessage("unregistered_email", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "email");
		}

		if (!totpService.checkVerifyCode(email, code)) {
			String msg = messageSource.getMessage("invalid_twostep", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "email");
		}

		if (userVerifyDao.updateEmailVerified(user.getId(), true) < 1) {
			UserVerify userVerify = new UserVerify();
			userVerify.setId(user.getId());
			userVerify.setEmailVerified(true);
			userVerifyDao.insert(userVerify);

			// add internal balance
			int ndbId = tokenAssetService.getTokenIdBySymbol("NDB");
			balanceDao.addFreeBalance(user.getId(), ndbId, 0);

			int voltId = tokenAssetService.getTokenIdBySymbol("WATT");
			balanceDao.addFreeBalance(user.getId(), voltId, 0);

		}
		return true;
	}

    public String resendVerifyCode(String email) {
		User user = userDao.selectByEmail(email);
		if (user == null) {
			String msg = messageSource.getMessage("unregistered_email", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "email");
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
			String msg = messageSource.getMessage("unregistered_email", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "email");
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
						var result = smsService.sendSMS(phone, code);
						userDao.updatePhone(user.getId(), phone);
						return result;
					} catch (Exception e) {
						String msg = messageSource.getMessage("error_phone", null, Locale.ENGLISH);
						throw new UserNotFoundException(msg, "phone");
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
			String msg = messageSource.getMessage("not_verified", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "email");
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

    public String confirmGoogleAuthUpdate(int userId, String newSecret) {
		var securities = userSecurityDao.selectByUserId(userId);
		for (var security : securities) {
			if(security.getAuthType().equals("app")) {
				userSecurityDao.updateTfaSecret(security.getId(), newSecret);
				userSecurityDao.updateTfaEnabled(security.getId(), true);
				return "Success";
			}
		}
		// didn't set google auth before
		var security = UserSecurity.builder()
			.userId(userId)
			.authType("app")
			.tfaEnabled(true)
			.tfaSecret(newSecret)
			.build();
		userSecurityDao.insert(security);
		return "Success";
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
			String msg = messageSource.getMessage("unregistered_email", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "email");
		}
		UserVerify userVerify = userVerifyDao.selectById(user.getId());

		if (userVerify == null || !userVerify.isEmailVerified()) {
			String msg = messageSource.getMessage("not_verified", null, Locale.ENGLISH);
			throw new UnauthorizedException(msg, "email");
		}

		List<UserSecurity> userSecurities = userSecurityDao.selectByUserId(user.getId());
		if (userSecurities.size() == 0) {
			String msg = messageSource.getMessage("no_two_step", null, Locale.ENGLISH);
			throw new UnauthorizedException(msg, "code");
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
   
	public String resetPassword(String email, String code, String newPass) {

		if (totpService.checkVerifyCode(email, code)) {
			User user = userDao.selectByEmail(email);
			if (user == null) {
				String msg = messageSource.getMessage("unregistered_email", null, Locale.ENGLISH);
				throw new UserNotFoundException(msg, "email");
			}
			userDao.updatePassword(user.getId(), encoder.encode(newPass));
		} else {
			String msg = messageSource.getMessage("invalid_twostep", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "code");
		}

		return "Success";
	}

	public String changePassword(int id, String newPassword) {
		if (userDao.updatePassword(id, encoder.encode(newPassword)) > 0)
			return "Success";
		return "Failed";
	}

    public String confirmPhone2FA(int userId, String phone) {
        var securities = userSecurityDao.selectByUserId(userId);
		for (var security : securities) {
			if(security.getAuthType().equals("phone")) {
				userSecurityDao.updateTfaDisabled(security.getId(), "phone", true);
				return "Success";
			}
		}
		// didn't set google auth before
		var security = UserSecurity.builder()
			.userId(userId)
			.authType("phone")
			.tfaEnabled(true)
			.build();
		userSecurityDao.insert(security);
		return "Success";
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
}
