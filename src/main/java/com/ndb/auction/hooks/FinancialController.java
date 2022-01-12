package com.ndb.auction.hooks;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ndb.auction.models.DirectSale;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.coinbase.CoinbaseEvent;
import com.ndb.auction.models.coinbase.CoinbaseEventBody;
import com.ndb.auction.models.coinbase.CoinbaseEventData;
import com.ndb.auction.models.coinbase.CoinbasePayments;
import com.ndb.auction.models.coinbase.CoinbasePricing;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.user.User;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class FinancialController extends BaseController {

    @Value("${stripe.webhook.key}")
    private String stripeWebhookKey;

    @PostMapping("/stripe/direct")
    @ResponseBody
    public ResponseEntity<?> StripeWebhooks(HttpServletRequest request) {
        Event event = null;
        String sigHeader = request.getHeader("Stripe-Signature");
        String payload = "";
        try {
            payload = getBody(request);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, stripeWebhookKey);
        } catch (JsonSyntaxException e) {
            // Invalid payload
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (SignatureVerificationException e) {
            // Invalid signature
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        switch (event.getType()) {
            case "payment_intent.succeeded": {
                // Then define and call a function to handle the event payment_intent.succeeded
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                Long received = paymentIntent.getAmountReceived();
                DirectSale directSale = directSaleService.getDirectSaleByPayment(paymentIntent.getId());
                double ndbPrice = Double.valueOf(directSale.getNdbPrice());
                double ndbAmount = Double.valueOf(directSale.getNdbAmount());
                double amount = ndbPrice * ndbAmount; 
                
                // decimals
                Long lAmount = Double.valueOf(amount * 100).longValue();
                if (received < lAmount) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                directSale.setPayType(DirectSale.STRIPE);
                directSale.setConfirmed(true);
                directSale.setConfirmedAt(System.currentTimeMillis());

                directSaleService.updateDirectSale(directSale);

                // real moving of NDB
                if (directSale.getWhereTo() == DirectSale.INTERNAL) {
                    // userWalletService.addFreeAmount(directSale.getUserId(), "NDB", directSale.getNdbAmount());
                } else if (directSale.getWhereTo() == DirectSale.EXTERNAL) {

                } else {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }

                // User Tier!!
                // addDirectSalepoint(directSale.getUserId(), amount);

                break;
            }
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/crypto/direct")
    @ResponseBody
    public ResponseEntity<?> CoinbaseWebhooks(HttpServletRequest request) {
        String hmac = request.getHeader("X-CC-Webhook-Signature");

        String reqQuery = "";
        try {
            reqQuery = getBody(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String _hmac = buildHmacSignature(reqQuery, SHARED_SECRET);

        if (!hmac.equals(_hmac)) {
            return null;
        }
        CoinbaseEvent event = new Gson().fromJson(reqQuery, CoinbaseEvent.class);
        CoinbaseEventBody body = event.getEvent();

        if (body.getType().equals("charge.confirmed")) {
            CoinbaseEventData data = body.getData();

            if (data.getPayments().size() == 0) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            CoinbasePayments payments = data.getPayments().get(0);

            if (payments.getStatus().equals("CONFIRMED")) {
                String code = data.getCode();

                DirectSale tx = directSaleService.getDirectSaleByCode(code);
                if (tx == null) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                double ndbPrice = Double.valueOf(tx.getNdbPrice());
                double ndbAmount = Double.valueOf(tx.getNdbAmount());
                double payAmount = ndbPrice * ndbAmount; 
                
                Map<String, CoinbasePricing> paymentValues = payments.getValue();
                CoinbasePricing cryptoPricing = paymentValues.get("crypto");
                CoinbasePricing usdPricing = paymentValues.get("local");

                double usdAmount = Double.valueOf(usdPricing.getAmount());
                if (payAmount > usdAmount) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                tx.setConfirmedAt(System.currentTimeMillis());
                tx.setConfirmed(true);
                tx.setCryptoType(cryptoPricing.getCurrency());
                tx.setCryptoAmount(cryptoPricing.getAmount());
                directSaleService.updateDirectSale(tx);

                // update Tier Setting!!!!
                // addDirectSalepoint(tx.getUserId(), payAmount);

                // Real moving of NDB
                if (tx.getWhereTo() == DirectSale.INTERNAL) {
                    // add free ndb into ndb wallet
                    // userWalletService.addFreeAmount(tx.getUserId(), "NDB", tx.getNdbAmount());
                } else if (tx.getWhereTo() == DirectSale.EXTERNAL) {
                    //
                } else {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }

            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void addDirectSalepoint(int userId, long amount) {
        TierTask tierTask = tierTaskService.getTierTask(userId);
        TaskSetting taskSetting = taskSettingService.getTaskSetting();
        List<Tier> tiers = tierService.getUserTiers();

        long prevDirect = tierTask.getDirect();
        tierTask.setDirect(prevDirect + amount);

        User user = userService.getUserById(userId);

        long point = user.getTierPoint();
        point += (taskSetting.getDirect() * amount);
        long _point = point;
        int level = user.getTierLevel();
        for (Tier tier : tiers) {
            if (tier.getPoint() >= point && tier.getPoint() > _point) {
                _point = tier.getPoint();
                level = tier.getLevel();
            }
        }
        tierTaskService.updateTierTask(tierTask);
        userService.updateTier(userId, level, point);
    }

}
