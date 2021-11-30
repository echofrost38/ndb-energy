package com.ndb.auction.security.oauth2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ndb.auction.dao.OAuth2Dao;
import com.ndb.auction.models.OAuth2Registration;
import com.ndb.auction.service.OAuth2RegistrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomClientRegistrationRepository implements ClientRegistrationRepository{

    // @Autowired
    // OAuth2RegistrationService oAuth2RegistrationService;
    
    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        log.info(registrationId, "registrationId cannot be empty");

        // OAuth2Registration r = oAuth2RegistrationService.getByRegistrationId(registrationId);
        OAuth2Registration r = new OAuth2Registration();

        return ClientRegistration.withRegistrationId(registrationId)
            .clientId(r.getClientId())
            .clientSecret(r.getClientSecret())
            .clientAuthenticationMethod(new ClientAuthenticationMethod(r.getClientAuthenticationMethod()))
            .authorizationGrantType(new AuthorizationGrantType(r.getAuthorizationGrantType()))
            .redirectUriTemplate("{baseUrl}/oauth2/callback/{registrationId}")
            .scope(r.getScope())
            .authorizationUri(r.getAuthorizationUri())
            .tokenUri(r.getTokenUri())
            .userInfoUri(r.getUserInfoUri())
            .userNameAttributeName(r.getUserNameAttributeName())
            .jwkSetUri(r.getJwkSetUri())
            .clientName(r.getClientName())
            // .clientId("217015743019-arfgls5skjg3tehl67gf8sitbf0rq9k9.apps.googleusercontent.com")
            // .clientSecret("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")
            // .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
            // .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            // .redirectUriTemplate("{baseUrl}/oauth2/callback/{registrationId}")
            // .scope("openid", "profile", "email", "address", "phone")
            // .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
            // .tokenUri("https://www.googleapis.com/oauth2/v4/token")
            // .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
            // .userNameAttributeName(IdTokenClaimNames.SUB)
            // .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
            // .clientName("Google")
            .build();
    }
    
}
