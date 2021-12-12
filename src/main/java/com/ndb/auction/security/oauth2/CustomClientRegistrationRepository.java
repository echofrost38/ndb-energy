package com.ndb.auction.security.oauth2;

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

    @Autowired
    OAuth2RegistrationService oAuth2RegistrationService;
    
    @SuppressWarnings("deprecation")
	@Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        log.info(registrationId, "registrationId cannot be empty");

        OAuth2Registration r = oAuth2RegistrationService.getByRegistrationId(registrationId);

        return ClientRegistration.withRegistrationId(registrationId)
            .clientId(r.getClientId())
            .clientSecret(r.getClientSecret())
            .clientAuthenticationMethod(new ClientAuthenticationMethod(r.getClientAuthenticationMethod()))
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUriTemplate("{baseUrl}/oauth2/callback/{registrationId}")
            .scope(r.getScope())
            .authorizationUri(r.getAuthorizationUri())
            .tokenUri(r.getTokenUri())
            .userInfoUri(r.getUserInfoUri())
            .userNameAttributeName(r.getUserNameAttributeName())
            .jwkSetUri(r.getJwkSetUri())
            .clientName(r.getClientName())
            .build();
    }
    
}
