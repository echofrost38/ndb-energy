package com.ndb.auction.security.oauth2;

import com.ndb.auction.config.AppProperties;
import com.ndb.auction.security.TokenProvider;
import com.ndb.auction.service.CustomOAuth2UserService;
import com.ndb.auction.service.TotpService;
import com.ndb.auction.service.UserDetailsImpl;
import com.ndb.auction.service.UserService;
import com.ndb.auction.exceptions.BadRequestException;
import com.ndb.auction.models.User;
import com.ndb.auction.models.user.AuthProvider;
import com.ndb.auction.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.ndb.auction.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private TokenProvider tokenProvider;

    private AppProperties appProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private TotpService totpService;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
	AuthenticationManager authenticationManager;

    @Autowired
    OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider, AppProperties appProperties,
                                       HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.tokenProvider = tokenProvider;
        this.appProperties = appProperties;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        log.info("targetURI : {} registrationID : {}, UserPrincipal {},", targetUrl, registrationId, authentication.getPrincipal());

        UserDetailsImpl userPrincipal = new UserDetailsImpl();
        
        try {
            userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        } catch (Exception e) {
            if(registrationId.equalsIgnoreCase(AuthProvider.apple.toString())) { //In case of Apple
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
                userPrincipal = customOAuth2UserService.processUserDetails(registrationId, attributes);
            } else {
                return UriComponentsBuilder.fromUriString(targetUrl + "/error/unknown/registrationId").build().toUriString();
            }
        }

        User user = userService.getUserByEmail(userPrincipal.getEmail());

        String type = "success";
        String dataType;
        String data;
        
        if(!user.getProvider().equals(AuthProvider.valueOf(registrationId))) {
            type = "error";
            dataType = "InvalidProvider";
            data = user.getProvider().toString();
        } else if (!user.getSecurity().get("2FA")) {
            type = "error";
            dataType = "No2FA";
			data = user.getEmail();
		} else {
            dataType = userService.signin2FA(user);
            data = user.getEmail();
            if (dataType.equals("error")) {
                return UriComponentsBuilder.fromUriString(targetUrl + "/error/Failed/2FA Error").build().toUriString();
            }
            // Save token on cache
            totpService.setTokenAuthCache(dataType, authentication);
        }
        
        return UriComponentsBuilder.fromUriString(targetUrl + "/" + type + "/" + dataType + "/" + data)
                // .queryParam("token", token)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
            .stream()
            .anyMatch(authorizedRedirectUri -> {
                // Only validate host and port. Let the clients use different paths if they want to
                URI authorizedURI = URI.create(authorizedRedirectUri);
                if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                        && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                    return true;
                }
                return false;
            });
    }

}
