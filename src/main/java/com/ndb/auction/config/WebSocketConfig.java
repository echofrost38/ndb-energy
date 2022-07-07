package com.ndb.auction.config;

import com.ndb.auction.security.jwt.JwtUtils;
import com.ndb.auction.websocket.StompPrincipal;
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
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

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
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                String path = request.getURI().getPath();
                int start = path.indexOf("/presale_orders/") + "/presale_orders/".length();
                int end = path.indexOf("/", start);
                String jwt = path.substring(start, end);
                attributes.put("jwt", jwt);
                System.out.println("jwt = " + jwt);
                try {
                    String email = jwtUtils.getEmailFromJwtToken(jwt);
                    System.out.println("email = " + email);
                    return true;
                } catch (Exception e) {
                    logger.error("Invalid JWT on /presale_orders: {}", jwt);
                }
//                return false;
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
            }
        }).setHandshakeHandler(new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                String jwt = (String) attributes.get("jwt");
                return new StompPrincipal(jwt);
            }
        }).setAllowedOriginPatterns("*").withSockJS();
    }

}