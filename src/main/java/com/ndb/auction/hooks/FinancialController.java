package com.ndb.auction.hooks;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ndb.auction.models.DirectSale;
import com.ndb.auction.models.coinbase.CoinbaseEvent;
import com.ndb.auction.models.coinbase.CoinbaseEventBody;
import com.ndb.auction.models.coinbase.CoinbaseEventData;
import com.ndb.auction.models.coinbase.CoinbasePayments;
import com.ndb.auction.models.coinbase.CoinbasePricing;
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
                payload, sigHeader, stripeWebhookKey
            );
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
            double amount = directSale.getNdbAmount() * directSale.getNdbPrice();
            Long lAmount = Double.valueOf(amount).longValue();
            if(received < lAmount) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            directSale.setPayType(DirectSale.STRIPE);
            directSale.setConfirmed(true);
            directSale.setConfirmedAt(System.currentTimeMillis());

            directSaleService.updateDirectSale(directSale);
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
    
        if(!hmac.equals(_hmac)) {
            return null;
        }
        CoinbaseEvent event = new Gson().fromJson(reqQuery, CoinbaseEvent.class);
        CoinbaseEventBody body = event.getEvent();

        if(body.getType().equals("charge.confirmed")) {
            CoinbaseEventData data = body.getData();

            if(data.getPayments().size() == 0) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            CoinbasePayments payments = data.getPayments().get(0);
            
            if(payments.getStatus().equals("CONFIRMED")) {
                String code = data.getCode();

                DirectSale tx = directSaleService.getDirectSaleByCode(code);
                if(tx == null) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);            
                }
                double payAmount = tx.getNdbAmount() * tx.getNdbPrice();
                
                Map<String, CoinbasePricing> paymentValues = payments.getValue();
                CoinbasePricing cryptoPricing = paymentValues.get("crypto");
                CoinbasePricing usdPricing = paymentValues.get("local");

                double usdAmount = Double.valueOf(usdPricing.getAmount());
                if(payAmount > usdAmount) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                tx.setConfirmedAt(System.currentTimeMillis());
                tx.setConfirmed(true);
                tx.setCryptoType(cryptoPricing.getCurrency());
                tx.setCryptoAmount(Double.valueOf(cryptoPricing.getAmount()));
                directSaleService.updateDirectSale(tx);
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
