package com.ndb.auction.resolver;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ndb.auction.models.User;
import com.ndb.auction.payload.Credentials;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import reactor.core.publisher.Mono;

@Component
public class AuthResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLSubscriptionResolver {

	public String signup(String email, String password, String country) {
		password = encoder.encode(password);
		return userService.createUser(email, password, country, true);
	}
	
	public String verifyAccount(String email, String code) {
		if(userService.verifyAccount(email, code)) {
			return "Success";
		} 
		return "Failed";
	}
	
	public String resendVerifyCode(String email) {
		return resendVerifyCode(email);
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
		
		if(!encoder.matches(password, user.getPassword())) {
			return new Credentials("Failed", "password mismatch");
		}
		
		if(!user.getVerify().get("email")) {
			return new Credentials("Failed", "please verify");
		}
		
		if(!user.getSecurity().get("2FA")) {
			return new Credentials("Failed", "Please set 2FA");
		}
		
		String token = userService.signin2FA(user);
		if(token.equals("error")) {
			return new Credentials("Failed", "2FA Error");
		}
		
		totpService.setToken(token, password);
		return new Credentials("Success", token);
	}
	
	public Credentials confirm2FA(String email, String token, String code) {
		String password = totpService.getPassword(token);
		if(password.equals("")) {
			return new Credentials("Failed", "Password expired");
		}
		
		if(!userService.verify2FACode(email, code)) {
			return new Credentials("Failed", "2FA code mismatch");
		}
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, password));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

//		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
//				.collect(Collectors.toList());
		//return new Credentials(email, jwt);
		return new Credentials("Success", jwt);
	}
	
	public String forgotPassword(String email) {
		if(userService.sendResetToken(email)) {
			return "Success";
		} else {
			return "Failed";
		}
		
	}
	
	public String resetPassword(String email, String code, String newPassword) {
		newPassword = encoder.encode(newPassword);
		return userService.resetPassword(email, code, newPassword);
	}
	
	public Mono<String> fluxTest(String param) {
		return Mono.just("flux test: " + param);
	}
	
}
