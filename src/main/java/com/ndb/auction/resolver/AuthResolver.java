package com.ndb.auction.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ndb.auction.models.OAuth2Setting;
import com.ndb.auction.models.user.TwoFAEntry;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserSecurity;
import com.ndb.auction.models.user.UserVerify;
import com.ndb.auction.payload.Credentials;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import reactor.core.publisher.Mono;

@Component
public class AuthResolver extends BaseResolver
		implements GraphQLMutationResolver, GraphQLSubscriptionResolver, GraphQLQueryResolver {
		
	private String lowerEmail(String email) {
		return email.toLowerCase();
	}
	
	public String signup(String email, String password, String country) {
		return userService.createUser(lowerEmail(email), password, country);
	}

	public String verifyAccount(String email, String code) {
		if (userService.verifyAccount(lowerEmail(email), code)) {
			return "Success";
		}
		return "Failed";
	}

	public String resendVerifyCode(String email) {
		return userService.resendVerifyCode(lowerEmail(email));
	}

	public String request2FA(String email, String method, String phone) {
		return userService.request2FA(lowerEmail(email), method, phone);
	}

	public String confirmRequest2FA(String email, String method, String code) {
		return userService.confirmRequest2FA(lowerEmail(email), method, code);
	}

	public Credentials signin(String email, String password) {
		email = lowerEmail(email);
		// get user ( Not found exception is threw in service)
		User user = userService.getUserByEmail(email);
		if (user == null) {
			return new Credentials("Failed", "Unregistered email.");
		}

		if (!userService.checkMatchPassword(password, user.getPassword())) {
			return new Credentials("Failed", "Email or password is invalid.");
		}
		UserVerify userVerify = userVerifyService.selectById(user.getId());
		if (userVerify == null || !userVerify.isEmailVerified()) {
			return new Credentials("Failed", "Please verify your email.");
		}

		List<UserSecurity> userSecurities = userSecurityService.selectByUserId(user.getId());
		
		List<String> twoStep = new ArrayList<>();
		
		for (UserSecurity userSecurity : userSecurities) {
			if (userSecurity.isTfaEnabled()) {
				twoStep.add(userSecurity.getAuthType());
			} 
		}
		if (twoStep.isEmpty()) {
			return new Credentials("Failed", "Please set 2FA.");
		}
		
		String token = userService.signin2FA(user);
		if (token.equals("error")) {
			return new Credentials("Failed", "2FA failed.");
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
			return new Credentials("Failed", "Password expired.");
		}

		if (!userService.verify2FACode(email, codeMap)) {
			return new Credentials("Failed", "Wrong 2FA code.");
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
		return userService.resetPassword(lowerEmail(email), code, newPassword);
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

	/// testing purpose
	public Mono<String> fluxTest(String param) {
		return Mono.just("flux test: " + param);
	}

	public String addNewUser(int id, String email, String name) {
		TransactionReceipt receipt = userWalletService.addNewUser(id, lowerEmail(email), name);
		return receipt.getLogs().get(0).getData();
	}

	public String addHoldAmount(int id, String crypto, Long amount) {
		TransactionReceipt receipt = userWalletService.addHoldAmount(id, crypto, amount);
		return receipt.getLogs().get(0).getData();
	}

}
