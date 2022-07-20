package com.ndb.auction.config;

import com.ndb.auction.security.jwt.JwtUtils;
import com.ndb.auction.stomp.StompPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketStompConfig.class);

    @Autowired
    protected JwtUtils jwtUtils;

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        config.enableSimpleBroker("/ws");
        config.setApplicationDestinationPrefixes("/ws");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/ndbcoin").setAllowedOriginPatterns("*").withSockJS();
        registry.addEndpoint("/ws/presale_orders/{jwt}").addInterceptors(new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                String path = request.getURI().getPath();
                int start = path.indexOf("/presale_orders/") + "/presale_orders/".length();
                int end = path.indexOf("/", start);
                String jwt = path.substring(start, end);
                attributes.put("jwt", jwt);
                System.out.println("jwt = " + jwt);
                try {
                    String email = jwtUtils.getEmailFromJwtToken(jwt);
                    logger.info("Websocket connected: /presale_orders/{} by {}", jwt, email);
                    return true;
                } catch (Exception e) {
                    logger.error("Invalid JWT on /presale_orders: {}", jwt);
                }
                return false;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
            }
        }).setAllowedOriginPatterns("*").withSockJS();
        registry.addEndpoint("/ws/notify/{jwt}").addInterceptors(new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                String path = request.getURI().getPath();
                int start = path.indexOf("/notify/") + "/notify/".length();
                int end = path.indexOf("/", start);
                String jwt = path.substring(start, end);
                attributes.put("jwt", jwt);
                try {
                    String email = jwtUtils.getEmailFromJwtToken(jwt);
                    attributes.put("email", email);
                    logger.info("Websocket connected: /notify/{} by {}", jwt, email);
                    return true;
                } catch (Exception e) {
                    logger.error("Invalid JWT on /notify: {}", jwt);
                }
                return false;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
            }
        }).setHandshakeHandler(new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                String jwt = (String) attributes.get("jwt");
                String email = jwtUtils.getEmailFromJwtToken(jwt);
                return new StompPrincipal(jwt, email);
            }
        }).setAllowedOriginPatterns("*").withSockJS();
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    List tokenList = accessor.getNativeHeader("authorization");
//                    String token = null;
//                    if (tokenList == null || tokenList.size() < 1) {
//                        return message;
//                    } else {
//                        token = (String) tokenList.get(0);
//                        if (token == null) {
//                            return message;
//                        }
//                        String[] array = token.split(" ");
//                        if (array.length == 2) token = array[1];
//                    }
//
//                    // validate and convert to a Principal based on your own requirements e.g.
//                    // authenticationManager.authenticate(JwtAuthentication(token))
//                    StompPrincipal principal = new StompPrincipal(token);
//                    accessor.setUser(principal);
//                    try {
//                        String email = jwtUtils.getEmailFromJwtToken(token);
//                        principal.getAttributes().put("email", email);
//                        logger.info("Websocket connected: /notify/{} by {}", token, email);
//                    } catch (Exception e) {
//                        logger.error("Invalid JWT on /notify: {}", token);
//                    }
//
//                    // not documented anywhere but necessary otherwise NPE in StompSubProtocolHandler!
//                    accessor.setLeaveMutable(true);
//                }
//                return message;
//            }
//        });
//    }

}