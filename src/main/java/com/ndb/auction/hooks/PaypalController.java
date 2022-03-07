package com.ndb.auction.hooks;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.ndb.auction.config.PaypalConfig;
import com.ndb.auction.payload.response.paypal.WebhookEvent;
import com.paypal.api.payments.Event;
import com.paypal.base.Constants;
import com.paypal.base.rest.APIContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paypal")
public class PaypalController extends BaseController {

    private final PaypalConfig paypalConfig;
    public static final String WEBHOOK_ID = "6SP16862L7635611T";

    @Autowired
    public PaypalController(PaypalConfig paypalConfig) {
        this.paypalConfig = paypalConfig;
    }

	@PostMapping(value = "/auction")
    public ResponseEntity<String> paymentSuccess(HttpServletRequest request) {
        
        try {
            APIContext apiContext = new APIContext(
                paypalConfig.getClientId(),
                paypalConfig.getClientSecret(),
                paypalConfig.getMode()
            );
            apiContext.addConfiguration(Constants.PAYPAL_WEBHOOK_ID, WEBHOOK_ID);
            var headerMap = getHeadersInfo(request);
            var reqBody = getBody(request);
            Boolean result = Event.validateReceivedEvent(apiContext, headerMap, reqBody);

            if(!result) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            WebhookEvent hookEvent = new Gson().fromJson(reqBody, WebhookEvent.class);
        
            switch (hookEvent.getEvent_type()) {
                case "PAYMENT.PAYOUTSBATCH.SUCCESS":
                    
                    break;
                case "PAYMENT.PAYOUTSBATCH.DENIED":
                    
                    break;
            }
            return ResponseEntity.ok().body("Payment success");
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}