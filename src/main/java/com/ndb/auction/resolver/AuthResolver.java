package com.ndb.auction.resolver;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.OAuth2Registration;
import com.ndb.auction.models.User;
import com.ndb.auction.models.user.TwoFAEntry;
import com.ndb.auction.models.user.Wallet;
import com.ndb.auction.payload.Credentials;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import reactor.core.publisher.Mono;

@Component
public class AuthResolver extends BaseResolver
		implements GraphQLMutationResolver, GraphQLSubscriptionResolver, GraphQLQueryResolver {

	public String signup(String email, String password, String country) {
		// Geo IP checking
		// HttpServletRequest request =
		// ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		// String ipAddress = RemoteIpHelper.getRemoteIpFrom(request);
		// if(!ipChecking.isAllowed(ipAddress)) {
		// return "Not Allowed Location";
		// }
		return userService.createUser(email, password, country, true);
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

	public String confirmRequest2FA(String email, String method, String code) {
		return userService.confirmRequest2FA(email, method, code);
	}

	public Credentials signin(String email, String password) {

		// Geo IP checking
		// HttpServletRequest request =
		// ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		// String ipAddress = RemoteIpHelper.getRemoteIpFrom(request);
		// if(!ipChecking.isAllowed(ipAddress)) {
		// return new Credentials("Failed", "Not Allowed Location");
		// }

		// get user ( Not found exception is threw in service)
		User user = null;
		try {
			user = userService.getUserByEmail(email);
		} catch (UserNotFoundException e) {
			return new Credentials("Failed", "You are not registered");
		}

		if (!userService.checkMatchPassword(password, user.getPassword())) {
			return new Credentials("Failed", "Your email and password do not match!");
		}

		if (!user.getVerify().get("email")) {
			return new Credentials("Failed", "Please verify your email");
		}

		if (!user.getSecurity().get("2FA")) {
			return new Credentials("Failed", "Please set 2FA");
		}

		String token = userService.signin2FA(user);
		if (token.equals("error")) {
			return new Credentials("Failed", "2FA Error");
		}

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, password));

		totpService.setTokenAuthCache(token, authentication);

		return new Credentials("Success", token, user.getTwoStep());
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

		// UserDetailsImpl userDetails = (UserDetailsImpl)
		// authentication.getPrincipal();
		// List<String> roles = userDetails.getAuthorities().stream().map(item ->
		// item.getAuthority())
		// .collect(Collectors.toList());
		// return new Credentials(email, jwt);
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
	public OAuth2Registration addOAuth2Registration(
			String registrationId,
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
		OAuth2Registration registration = new OAuth2Registration(
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

	public String addNewUser(String id, String email, String name) {
		TransactionReceipt receipt = userWalletService.addNewUser(id, email, name);
		return receipt.getLogs().get(0).getData();
	}

	public String addHoldAmount(String id, String crypto, int amount) {
		double _amount = (double) amount;
		TransactionReceipt receipt = userWalletService.addHoldAmount(id, crypto, _amount);
		return receipt.getLogs().get(0).getData();
	}

	public Wallet getWalletById(String id, String crypto) {
		return userWalletService.getWalletById(id, crypto);
	}
}
