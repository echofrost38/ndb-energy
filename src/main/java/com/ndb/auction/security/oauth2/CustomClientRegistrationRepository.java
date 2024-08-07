package com.ndb.auction.security.oauth2;

import com.ndb.auction.models.OAuth2Setting;
import com.ndb.auction.service.OAuth2RegistrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomClientRegistrationRepository implements ClientRegistrationRepository{

    @Autowired
    OAuth2RegistrationService oAuth2RegistrationService;
    
	@Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        
        OAuth2Setting r = oAuth2RegistrationService.getByRegistrationId(registrationId);
        log.info("OAuth2 Registration : {}", r);

        return ClientRegistration.withRegistrationId(registrationId)
            .clientId(r.getClientId())
            .clientSecret(r.getClientSecret())
            .clientAuthenticationMethod(new ClientAuthenticationMethod(r.getClientAuthMethod()))
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri(r.getRedirectUriTemplate())
            .scope(r.getScope())
            .authorizationUri(r.getAuthUri())
            .tokenUri(r.getTokenUri())
            .userInfoUri(r.getUserInfoUri())
            .userNameAttributeName(r.getUserAttributeName())
            .jwkSetUri(r.getJwkSetUri())
            .clientName(r.getClientName())
            .build();
    }
    
}
