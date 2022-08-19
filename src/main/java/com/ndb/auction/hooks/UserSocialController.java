package com.ndb.auction.hooks;

import com.google.gson.Gson;
import com.ndb.auction.service.user.UserSocialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    UserSocialService socialService;

    @PostMapping("/social/discord")
    @ResponseBody
    public Object NyyuPayCallbackHandler(HttpServletRequest request) {
        try {
            String reqQuery =  getBody(request);
            Map<String, String> reqHeader = getHeadersInfo(request);
            Discord response = new Gson().fromJson(reqQuery, Discord.class);

            //socialDao.selectByDiscordUsername();
            return socialService.getTier(response.getUsername());
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
