package com.ndb.auction.security;

import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Map;

import com.ndb.auction.security.jwt.JwtUtils;
import com.ndb.auction.service.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

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

    @Override
    public void onConnect(SubscriptionSession session, OperationMessage message) {
        log.info("onConnect with payload {}", message.getPayload());

        var payload = (Map<String, String>) message.getPayload();

        // Get the JWT, perform authentication / rejection
        // here
        var jwt = payload.get("Authorization").substring(7);
        log.info("payload : {}", jwt);

        String email = jwtUtils.getEmailFromJwtToken(jwt);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // var userRoles = payload.get(GraphQLSecurityConfig.USER_ROLES_PRE_AUTH_HEADER);
        // var grantedAuthorities = GrantedAuthorityFactory.getAuthoritiesFrom(userRoles);
        // List<GrantedAuthority> grantedAuthorities = user.getRole().stream()
		// 		.map(role -> new SimpleGrantedAuthority(role))
		// 		.collect(Collectors.toList());

        /**
         * 
         * Q: Why do not set the token/Authentication inside Spring Security
         * SecurityContextHolder here?
         * 
         * If the start frame is not sent directly with the connection_init then the two
         * frames may be serviced on different threads. The thread servicing the
         * connection_init frame will check the websocket for any further inbound
         * frames, if false the thread will move onto another websocket. Another thread
         * is then free to service the following start frame. In this case, that thread
         * not have the security context of the correct session/thread.
         * 
         * Same scenario happens for onStop. (Message can be executed on different
         * thread).
         * 
         * This seems to be why some users are reporting intermittent failures with
         * spring security. E.g.
         * https://github.com/graphql-java-kickstart/graphql-java-servlet/discussions/134#discussioncomment-225980
         * 
         * With the NIO connector, a small number of threads will check sessions for new
         * frames. If a session has a frame available, the session will be passed to
         * another thread pool which will read frame, execute it, check for another
         * frame, execute it (loop). The session will be released when there are no
         * further frames available. With this, we know that at most one thread will
         * concurrently access one socket, therefore frames will be read sequentially.
         * We can therefore extract the auth credentials from onConnect and add them to
         * the session.getUserProperties(). These properties are available in the
         * onStart and onStop callbacks. Inside these callbacks, we can add the token to
         * the SecurityContextHolder if we decide to use method level security, or
         * simply access the credentials inside the subscription resolver via
         * DataFetchingEnvironment.
         * 
         */
      
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
    }

    @Override
    public void onTerminate(SubscriptionSession session, OperationMessage message) {
        log.info("onTerminate with payload {}", message.getPayload());
    }
}
