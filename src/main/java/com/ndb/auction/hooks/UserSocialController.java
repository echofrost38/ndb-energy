package com.ndb.auction.hooks;

import com.google.gson.Gson;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.user.UserSocial;
import com.ndb.auction.service.user.UserSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/")
public class UserSocialController extends BaseController {
    @Value("${social.auth.pubKey}")
    private String PUBLIC_KEY;

    @Value("${social.auth.privKey}")
    private String PRIVATE_KEY;
    @Autowired
    UserSocialService socialService;

    @PostMapping("/social/discord")
    @ResponseBody
    public Object DiscordBotCallbackHandler(HttpServletRequest request) {
        try {
            String reqQuery =  getBody(request);
            UserSocial response = new Gson().fromJson(reqQuery, UserSocial.class);
            //Map<String, String> token = getHeadersInfo(request);
            String token = request.getHeader("x-auth-token");
            String key = request.getHeader("x-auth-key");
            String ts = request.getHeader("x-auth-ts");
            String payload = ts + "." + response.getDiscord();
            String hmac = BaseController.buildHmacSignature(payload, PRIVATE_KEY);
            if (!key.equals(PUBLIC_KEY) || !token.equals(hmac))
                throw new UnauthorizedException("something went wrong", "signature");

            String tierName = socialService.getTier(response.getDiscord());
            System.out.println(tierName);
            return tierName;
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
