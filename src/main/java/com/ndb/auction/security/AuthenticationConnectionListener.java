package com.ndb.auction.security;

import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Map;

import com.ndb.auction.dao.UserDao;
import com.ndb.auction.models.User;
import com.ndb.auction.security.jwt.JwtUtils;
import com.ndb.auction.service.MailService;
import com.ndb.auction.service.SMSService;
import com.ndb.auction.service.UserDetailsImpl;
import com.ndb.auction.service.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionConnectionListener;
import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthenticationConnectionListener implements ApolloSubscriptionConnectionListener {

    public static final String AUTHENTICATION = "AUTHENTICATION";

    @Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SMSService smsService;

    @Autowired
    private MailService mailService;

    @Override
    public void onConnect(SubscriptionSession session, OperationMessage message) {
        log.info("onConnect with payload {}", message.getPayload());

		@SuppressWarnings("unchecked")
		var payload = (Map<String, String>) message.getPayload();

        // Get the JWT, perform authentication / rejection
        var jwt = payload.get("Authorization").substring(7);
        log.info("payload : {}", jwt);

        String email = jwtUtils.getEmailFromJwtToken(jwt);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        var token = new PreAuthenticatedAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        session.getUserProperties().put(AUTHENTICATION, token);
    }

    @Override
    public void onStart(SubscriptionSession session, OperationMessage message) {
        log.info("onStart with payload {}", message.getPayload());
        var authentication = (Authentication) session.getUserProperties().get(AUTHENTICATION);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public void onStop(SubscriptionSession session, OperationMessage message) {
        log.info("onStop with payload {}", message.getPayload());

        PreAuthenticatedAuthenticationToken token = (PreAuthenticatedAuthenticationToken)session.getUserProperties().get(AUTHENTICATION);
        UserDetailsImpl userDetails = (UserDetailsImpl)token.getPrincipal();
        String userId = userDetails.getId();
        User user = userDao.getUserById(userId);
        String phone = user.getMobile();
        String smsContent = "You are offline now!";
        SecurityContextHolder.clearContext();
        
        // Send SMS about offline
        try {
            smsService.sendNormalSMS(phone, smsContent);
        } catch (Exception e) {
            log.info("SMS Error {}", e);
        }

        // Send Mail about offline
        try {
			mailService.sendNormalEmail(user, "You are offline from NDB", smsContent);
		} catch (Exception e) {
            log.info("SMS Error {}", e);
		}
    }   

    @Override
    public void onTerminate(SubscriptionSession session, OperationMessage message) {
        log.info("onTerminate with payload {}", message.getPayload());
    }
}
