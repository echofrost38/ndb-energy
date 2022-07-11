package com.ndb.auction.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.ndb.auction.models.OAuth2Setting;
import com.ndb.auction.models.user.TwoFAEntry;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserSecurity;
import com.ndb.auction.models.user.UserVerify;
import com.ndb.auction.payload.Credentials;
import com.ndb.auction.service.user.UserAuthService;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;

@Component
public class AuthResolver extends BaseResolver
		implements GraphQLMutationResolver {
	
	@Autowired
	private UserAuthService userAuthService;
	
	private String lowerEmail(String email) {
		return email.toLowerCase();
	}
	
	public String signup(String email, String password, String country,String referredByCode) {
		return userService.createUser(lowerEmail(email), password, country,referredByCode);
	}

	public String verifyAccount(String email, String code) {
		if (userAuthService.verifyAccount(lowerEmail(email), code)) {
			return "Success";
		}
		return "Failed";
	}

	public String resendVerifyCode(String email) {
		return userAuthService.resendVerifyCode(lowerEmail(email));
	}

	public String request2FA(String email, String method, String phone) {
		return userAuthService.request2FA(lowerEmail(email), method, phone);
	}

	@PreAuthorize("isAuthenticated()")
	public String disable2FA(String method) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int id = userDetails.getId();
		return userAuthService.disable2FA(id, method);
	}

	public Credentials confirmRequest2FA(String email, String method, String code) {
		String result = userAuthService.confirmRequest2FA(lowerEmail(email), method, code);
		if(result.equals("Success")) {
			String jwt = jwtUtils.generateJwtToken(email);
			return new Credentials("Success", jwt);
		} else {
			return new Credentials("Failed", "Cannot generate access token.");
		}
	}

	public Credentials signin(String email, String password) {
		email = lowerEmail(email);
		// get user ( Not found exception is threw in service)
		User user = userService.getUserByEmail(email);
		if (user == null) {
			String msg = messageSource.getMessage("unregistered_email", null, Locale.ENGLISH);
			return new Credentials("Failed", msg);
		}

		if (!userService.checkMatchPassword(password, user.getPassword())) {
			String msg = messageSource.getMessage("wrong_password", null, Locale.ENGLISH);
			return new Credentials("Failed", msg);
		}
		UserVerify userVerify = userVerifyService.selectById(user.getId());
		if (userVerify == null || !userVerify.isEmailVerified()) {
			// send verify code again
			resendVerifyCode(email);
			String msg = messageSource.getMessage("not_verified", null, Locale.ENGLISH);
			return new Credentials("Failed", msg);
		}

		List<UserSecurity> userSecurities = userSecurityService.selectByUserId(user.getId());
		List<String> twoStep = new ArrayList<>();
		
		for (UserSecurity userSecurity : userSecurities) {
			if (userSecurity.isTfaEnabled()) {
				twoStep.add(userSecurity.getAuthType());
			} 
		}
		if (twoStep.isEmpty()) {
			String msg = messageSource.getMessage("no_2fa", null, Locale.ENGLISH);
			return new Credentials("Failed", msg);
		}
		
		String token = userService.signin2FA(user);
		if (token.equals("error")) {
			String msg = messageSource.getMessage("invalid_twostep", null, Locale.ENGLISH);
			return new Credentials("Failed", msg);
		}

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, password));

		totpService.setTokenAuthCache(token, authentication);

		return new Credentials("Success", token, twoStep);
	}

	public Credentials confirm2FA(String email, String token, List<TwoFAEntry> code) {
		email = lowerEmail(email);
		Map<String, String> codeMap = new HashMap<>();
		for (TwoFAEntry entry : code) {
			codeMap.put(entry.getKey(), entry.getValue());
		}
		Authentication authentication = totpService.getAuthfromToken(token);
		if (authentication == null) {
			String msg = messageSource.getMessage("expired_2fa", null, Locale.ENGLISH);
			return new Credentials("Failed", msg);
		}

		if (!userService.verify2FACode(email, codeMap)) {
			String msg = messageSource.getMessage("invalid_twostep", null, Locale.ENGLISH);
			return new Credentials("Failed", msg);
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		return new Credentials("Success", jwt);
	}

	public String forgotPassword(String email) {
		if (userService.sendResetToken(lowerEmail(email))) {
			return "Success";
		} else {
			return "Failed";
		}
	}

	public String resetPassword(String email, String code, String newPassword) {
		return userAuthService.resetPassword(lowerEmail(email), code, newPassword);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<OAuth2Setting> getAllOAuth2Settings() {
		return oAuth2RegistrationService.getAllOAuth2Settings();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String updateOAuth2Client(String clientName, String clientId, String clientSecret) {
		return oAuth2RegistrationService.updateOAuth2Client(clientName, clientId, clientSecret);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public OAuth2Setting addOAuth2Registration(
			int registrationId,
			String clientId,
			String clientSecret,
			String clientAuthenticationMethod,
			String authorizationGrantType,
			String redirectUriTemplate,
			Set<String> scope,
			String authorizationUri,
			String tokenUri,
			String userInfoUri,
			String userNameAttributeName,
			String jwkSetUri,
			String clientName) {
		OAuth2Setting registration = new OAuth2Setting(
				registrationId,
				clientId,
				clientSecret,
				clientAuthenticationMethod,
				authorizationGrantType,
				redirectUriTemplate,
				scope,
				authorizationUri,
				tokenUri,
				userInfoUri,
				userNameAttributeName,
				jwkSetUri,
				clientName);
		return oAuth2RegistrationService.createRegistration(registration);
	}

	// For Zendesk SSO
	@PreAuthorize("isAuthenticated()")
	public Credentials getZendeskJwt() {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int id = userDetails.getId();

		// get user 
		User user = userService.getUserById(id);

		// Generate jwt token
		String token = jwtUtils.generateZendeskJwtToken(user);

		return new Credentials("success", token);
	}
}
