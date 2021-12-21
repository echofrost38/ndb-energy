package com.ndb.auction.service;

import com.ndb.auction.dao.UserDao;
import com.ndb.auction.exceptions.OAuth2AuthenticationProcessingException;
import com.ndb.auction.models.user.AuthProvider;
import com.ndb.auction.models.User;
import com.ndb.auction.security.UserPrincipal;
import com.ndb.auction.security.oauth2.user.OAuth2UserInfo;
import com.ndb.auction.security.oauth2.user.OAuth2UserInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

import javax.mail.MessagingException;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    public final static String VERIFY_TEMPLATE = "verify.ftlh";

    @Autowired
    private UserDao userDao;
    
    @Autowired
    public TotpService totpService;

    @Autowired
    public MailService mailService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
         log.info("oAuth2UserReguest {}", oAuth2UserRequest);
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    @SuppressWarnings("deprecation")
	private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userDao.getUserByEmail(oAuth2UserInfo.getEmail());
        User user;
        if(userOptional != null) {
            user = userOptional.get();
            if(!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setCountry(oAuth2UserInfo.getLocale());
        // user.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userDao.createUser(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        // existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userDao.createUser(existingUser);
    }

    @SuppressWarnings("unused")
	private boolean sendEmailCode(User user) {
		String code = totpService.getVerifyCode(user.getEmail());
		try {
			mailService.sendVerifyEmail(user, code, VERIFY_TEMPLATE);
		} catch (MessagingException | IOException | TemplateException e) {
			return false; // or exception
		}	
		return true;
	}
}
