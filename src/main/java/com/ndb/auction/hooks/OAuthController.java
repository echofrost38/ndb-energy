package com.ndb.auction.hooks;

import com.google.gson.Gson;
import com.ndb.auction.security.oauth2.user.AppleOAuth2UserInfo;
import com.ndb.auction.service.AppleLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@Slf4j
public class OAuthController {

    private static final String twitterClientId = "RS14MW9FbWx1YjI4WDRYTDJkOW46MTpjaQ";
    private static final String twitterClientSecret = "Yudj_KRcmRQG6BLsvhnN--0FOGMcrXJGbufao9S70JWS9BkcEp";
    private static final String twitterCallbackUrl = "https://api.dev.nyyu.io/oauth2/callback/twitter";

    @GetMapping("/oauth2/authorize/manual/twitter")
    public void twitterOauthLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {

        TwitterConnectionFactory connectionFactory = new TwitterConnectionFactory(twitterClientId, twitterClientSecret);
        OAuth1Operations oauthOperations = connectionFactory.getOAuthOperations();

        OAuthToken requestToken = oauthOperations.fetchRequestToken(twitterCallbackUrl, null);
        String authorizeUrl = oauthOperations.buildAuthorizeUrl(String.valueOf(requestToken), OAuth1Parameters.NONE);
        response.sendRedirect(authorizeUrl);
    }

    @GetMapping("/oauth2/callback/twitter")
    public void getTwitter() {

        TwitterConnectionFactory connectionFactory =
                new TwitterConnectionFactory(twitterClientId, twitterClientSecret);
        OAuth1Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuthToken requestToken = oauthOperations.fetchRequestToken("https://api.twitter.com/oauth/request_token", null);

        OAuthToken accessToken = oauthOperations.exchangeForAccessToken(
                new AuthorizedRequestToken(requestToken, ""), null);

        Twitter twitter = new TwitterTemplate(twitterClientId,
                twitterClientSecret,
                accessToken.getValue(),
                accessToken.getSecret());
        TwitterProfile profile = twitter.userOperations().getUserProfile();
        log.info("Twitter profile callback");
        log.info("profile" + profile.getName());

    }

    @GetMapping("/oauth2/authorize/manual/apple")
    public void appleOauthLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String sub = AppleLoginService.appleAuth("");
        log.info("sub " + sub);
    }

    @PostMapping("/oauth2/callback/apple")
    public void appleLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String authorizationCode = request.getParameter("code");
        String userInfo = request.getParameter("user");
        log.info("Apple callback");
        log.info("code " + authorizationCode);
        log.info("userInfo " + userInfo);
        log.info("Retrieving apple User " + userInfo);
        AppleOAuth2UserInfo appleUser = new Gson().fromJson(userInfo, AppleOAuth2UserInfo.class);
        log.info("appleUser email" + appleUser.getEmail());
        log.info("appleUser name" + appleUser.getName());

        String sub = AppleLoginService.appleAuth(authorizationCode);
        log.info("sub " + sub);
    }


}
