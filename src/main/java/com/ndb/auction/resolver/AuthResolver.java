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

	public String signup(String email, String password, String country) {
		return userService.createUser(email, password, country);
	}

	public String verifyAccount(String email, String code) {
		if (userService.verifyAccount(email, code)) {
			return "Success";
		}
		return "Failed";
	}

	public String resendVerifyCode(String email) {
		return userService.resendVerifyCode(email);
	}

	public String request2FA(String email, String method, String phone) {
		return userService.request2FA(email, method, phone);
	}

	public String confirmRequest2FA(String email, String code) {
		return userService.confirmRequest2FA(email, code);
	}

	public Credentials signin(String email, String password) {

		// get user ( Not found exception is threw in service)
		User user = userService.getUserByEmail(email);
		if(user == null) {
			return new Credentials("Failed", "You are not registered");
		} 

		if (!userService.checkMatchPassword(password, user.getPassword())) {
			return new Credentials("Failed", "Your email and password do not match!");
		}
		UserVerify userVerify = userVerifyService.selectById(user.getId());
		if (!userVerify.isEmailVerified()) {
			return new Credentials("Failed", "Please verify your email");
		}

		List<UserSecurity> userSecurities = userSecurityService.selectByUserId(user.getId());
		if(userSecurities.size() == 0) {
			return new Credentials("Failed", "Please set 2FA");
		}

		List<String> twoStep = new ArrayList<>();

		for (UserSecurity userSecurity : userSecurities) {
			if(userSecurity.isTfaEnabled()) {
				twoStep.add(userSecurity.getAuthType());
			} else {
				return new Credentials("Failed", "Please set 2FA");
			}
		}

		String token = userService.signin2FA(user);
		if (token.equals("error")) {
			return new Credentials("Failed", "2FA Error");
		}

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, password));

		totpService.setTokenAuthCache(token, authentication);

		return new Credentials("Success", token, twoStep);
	}

	public Credentials confirm2FA(String email, String token, List<TwoFAEntry> code) {
		Map<String, String> codeMap = new HashMap<String, String>();
		for(TwoFAEntry entry : code) {
			codeMap.put(entry.getKey(), entry.getValue());
		}
		Authentication authentication = totpService.getAuthfromToken(token);
		if (authentication == null) {
			return new Credentials("Failed", "Password expired");
		}

		if (!userService.verify2FACode(email, codeMap)) {
			return new Credentials("Failed", "2FA code mismatch");
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = jwtUtils.generateJwtToken(authentication);

		return new Credentials("Success", jwt);
	}

	public String forgotPassword(String email) {
		if (userService.sendResetToken(email)) {
			return "Success";
		} else {
			return "Failed";
		}
	}

	public String resetPassword(String email, String code, String newPassword) {
		return userService.resetPassword(email, code, newPassword);
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
		TransactionReceipt receipt = userWalletService.addNewUser(id, email, name);
		return receipt.getLogs().get(0).getData();
	}

	public String addHoldAmount(int id, String crypto, long amount) {
		TransactionReceipt receipt = userWalletService.addHoldAmount(id, crypto, amount);
		return receipt.getLogs().get(0).getData();
	}

}
