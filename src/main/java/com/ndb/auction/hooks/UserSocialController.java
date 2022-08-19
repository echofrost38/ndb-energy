package com.ndb.auction.hooks;

import com.google.gson.Gson;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.service.user.UserSocialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import com.ndb.auction.models.social.Discord;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/")
@Slf4j
public class UserSocialController extends BaseController {
    @Value("${social.auth.pubKey}")
    private String PUBLIC_KEY;

    @Value("${social.auth.privKey}")
    private String PRIVATE_KEY;
    @Autowired
    UserSocialService socialService;

    @PostMapping("/social/discord")
    @ResponseBody
    public Object NyyuPayCallbackHandler(HttpServletRequest request) {
        try {
            String reqQuery =  getBody(request);
            Discord response = new Gson().fromJson(reqQuery, Discord.class);
            //Map<String, String> token = getHeadersInfo(request);
            String token = request.getHeader("x-auth-token");
            String key = request.getHeader("x-auth-key");
            String ts = request.getHeader("x-auth-ts");
            String payload = ts + "." + response.getUsername();
            String hmac = BaseController.buildHmacSignature(payload, PRIVATE_KEY);
            if (!key.equals(PUBLIC_KEY) || !token.equals(hmac))
                throw new UnauthorizedException("something went wrong", "signature");

            String tierName = socialService.getTier(response.getUsername());
            System.out.println(tierName);
            //socialDao.selectByDiscordUsername();
            return tierName;
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
