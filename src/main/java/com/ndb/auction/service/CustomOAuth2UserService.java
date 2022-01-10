package com.ndb.auction.service;

import com.ndb.auction.dao.UserDao;
import com.ndb.auction.exceptions.OAuth2AuthenticationProcessingException;
import com.ndb.auction.models.user.AuthProvider;
import com.ndb.auction.models.user.User;
import com.ndb.auction.security.oauth2.user.OAuth2UserInfo;
import com.ndb.auction.security.oauth2.user.OAuth2UserInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    public final static String RANDOM_PASSWORD = "randomPassword.ftlh";

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private UserService userService;

    @Autowired
    public TotpService totpService;

    @Autowired
    public MailService mailService;

	@Value("${linkedin.email-address-uri}")
	private String linkedInEmailEndpointUri;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        log.info("oAuth2User {}", oAuth2User);

        try {
            Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
            String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
            if (provider.equalsIgnoreCase(AuthProvider.linkedin.toString())) {
                populateEmailAddressFromLinkedIn(oAuth2UserRequest, attributes);
            }
            return processOAuth2User(provider, attributes);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void populateEmailAddressFromLinkedIn(OAuth2UserRequest oAuth2UserRequest, Map<String, Object> attributes) throws OAuth2AuthenticationException {
        Assert.notNull(linkedInEmailEndpointUri, "LinkedIn email address end point required");
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + oAuth2UserRequest.getAccessToken().getTokenValue());
		HttpEntity<?> entity = new HttpEntity<>("", headers);
		ResponseEntity<Map> response = restTemplate.exchange(linkedInEmailEndpointUri, HttpMethod.GET, entity, Map.class);
		List<?> list = (List<?>) response.getBody().get("elements");
		Map map = (Map<?, ?>) ((Map<?, ?>) list.get(0)).get("handle~");
		attributes.putAll(map);
		log.info("populateEmailAddressFromLinkedIn uri : {}, attributes : {}", linkedInEmailEndpointUri, attributes);
	}

    @SuppressWarnings("deprecation")
	private OAuth2User processOAuth2User(String provider, Map<String, Object> attributes) {
        log.info("ProcessOAuth2User {}", attributes);
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userDao.getUserByEmail(oAuth2UserInfo.getEmail());
        User user;
        if(userOptional != null) {
            user = userOptional.get();
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(provider, oAuth2UserInfo);
        }
        
        return UserDetailsImpl.build(user, attributes);
    }

    @SuppressWarnings("deprecation")
    public UserDetailsImpl processUserDetails(String provider, Map<String, Object> attributes) {
        log.info("processUserDetails {}", attributes);
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userDao.getUserByEmail(oAuth2UserInfo.getEmail());
        User user;
        if(userOptional != null) {
            user = userOptional.get();
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(provider, oAuth2UserInfo);
        }
        
        return UserDetailsImpl.build(user, attributes);
    }


    private User registerNewUser(String provider, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setProvider(AuthProvider.valueOf(provider));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setCountry(oAuth2UserInfo.getLocale());
        user.getVerify().replace("email", true);
        String rPassword = userService.getRandomPassword(10);
        String encoded = userService.encodePassword(rPassword);
        user.setPassword(encoded);

        // send Random Password to user
        try {
			mailService.sendVerifyEmail(user, rPassword, RANDOM_PASSWORD);
		} catch (Exception e) {
			
		}	
        // user.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userDao.createUser(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.getVerify().replace("email", true);
        // existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userDao.createUser(existingUser);
    }
}
