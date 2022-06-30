package com.ndb.auction.hooks;

import com.google.gson.Gson;
import com.ndb.auction.models.nyyupay.NyyuPayResponse;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/")
public class NyyuPayController extends BaseController{

    @Value("${nyyupay.pubKey}")
    private String PUBLIC_KEY;

    @Value("${nyyupay.privKey}")
    private String PRIVATE_KEY;
    @Autowired
    public NyyuPayController(){}
    @PostMapping("/nyyupay")
    @ResponseBody
    public Object NyyuPayCallbackHandler(HttpServletRequest request) {
        String reqQuery;
        Map<String,String> reqHeader;
        try {
            reqQuery = getBody(request);
            reqHeader= getHeadersInfo(request);
            NyyuPayResponse response = new Gson().fromJson(reqQuery, NyyuPayResponse.class);
            String payload = reqHeader.get("x-auth-ts") +"POST"+"{\"address\":\""+response.getAddress()+"\"}";
            String hmac = buildHmacSignature(payload, PRIVATE_KEY);
            if (reqHeader.get("x-auth-key").equals(PUBLIC_KEY) && reqHeader.get("x-auth-token").equals(hmac)){
                System.out.println("NYYU PAY CALLBACK BODY: " + reqQuery);
                System.out.println("x-auth-key: " + reqHeader.get("x-auth-key"));
                System.out.println("x-auth-token: " + reqHeader.get("x-auth-token"));
                System.out.println("x-auth-token: " + reqHeader.get("x-auth-ts"));
                //NewDeposit

                System.out.println("Deposit detection : " + response.getAddress());
                return response;
            } else
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
