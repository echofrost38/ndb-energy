package com.ndb.auction.hooks;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.ndb.auction.models.Shufti.Response.ShuftiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ShuftiController extends BaseController {
    @PostMapping("/shufti")
    @ResponseBody
    public ResponseEntity<?> ShuftiWebhooks(HttpServletRequest request) {
        String reqQuery = "";
        try {
            reqQuery = getBody(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ShuftiResponse response = new Gson().fromJson(reqQuery, ShuftiResponse.class);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
