package com.ndb.auction.hooks;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.ndb.auction.config.PaypalConfig;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.withdraw.PaypalWithdraw;
import com.ndb.auction.payload.response.paypal.WebhookEvent;
import com.ndb.auction.service.withdraw.PaypalWithdrawService;
import com.paypal.api.payments.Event;
import com.paypal.base.Constants;
import com.paypal.base.rest.APIContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
@Slf4j
public class PaypalController extends BaseController {

    @Autowired
    private PaypalWithdrawService papalWithdrawService;

    private final PaypalConfig paypalConfig;
    public static final String WEBHOOK_ID = "0X538017S25613946";

    @Autowired
    public PaypalController(PaypalConfig paypalConfig) {
        this.paypalConfig = paypalConfig;
    }

	@PostMapping("/paypal")
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
            log.info("PayPal webhook: {}", reqBody);

            Boolean result = Event.validateReceivedEvent(apiContext, headerMap, reqBody);

            if(!result) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            WebhookEvent hookEvent = new Gson().fromJson(reqBody, WebhookEvent.class);
        
            switch (hookEvent.getEvent_type()) {
                case "PAYMENT.PAYOUTSBATCH.SUCCESS":
                    String payoutId = hookEvent.getResource().getBatch_header().getPayout_batch_id();
                    PaypalWithdraw m = papalWithdrawService.getWithdrawByPayoutId(payoutId);

                    // check pending status
                    double tokenAmount = m.getTokenAmount();
                    balanceService.deductFree(m.getUserId(), m.getSourceToken(), tokenAmount);
                    notificationService.sendNotification(
                        m.getUserId(),
                        Notification.WITHDRAW_SUCCESS,
                        "PAYMENT CONFIRMED",
                        String.format("Your %f %s withdarwal request has been approved", tokenAmount, m.getSourceToken())
                    );
                    /// 3 means success
                    papalWithdrawService.updateWithdrawRequest(m.getId(), 3, "");
                    break;
                case "PAYMENT.PAYOUTSBATCH.DENIED":
                    String _payoutId = hookEvent.getResource().getBatch_header().getPayout_batch_id();
                    PaypalWithdraw _m = papalWithdrawService.getWithdrawByPayoutId(_payoutId);

                    // check pending status
                    notificationService.sendNotification(
                        _m.getUserId(),
                        Notification.WITHDRAW_SUCCESS,
                        "PAYMENT CONFIRMED",
                    "Your PayPal withdarwal request has been denied.");  
                    papalWithdrawService.updateWithdrawRequest(_m.getId(), 2, "Denied by PayPal");
                    break;
            }
            return ResponseEntity.ok().body("Payment success");
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}