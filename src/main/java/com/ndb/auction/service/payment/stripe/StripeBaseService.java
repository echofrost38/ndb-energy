package com.ndb.auction.service.payment.stripe;

import javax.annotation.PostConstruct;

import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.service.BaseService;
import com.ndb.auction.service.BidService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeBaseService extends BaseService {
    @Value("${stripe.secret.key}")
	private String stripeSecretKey;

	@Value("${stripe.public.key}")
	private String stripePublicKey;

	@Autowired 
	protected BidService bidService;

    @PostConstruct
    public void init() {
    	Stripe.apiKey = stripeSecretKey;
    }
	
	public String getPublicKey( ) {
		return stripePublicKey;
	}

    protected PayResponse generateResponse(PaymentIntent intent, PayResponse response) {
		if(intent == null) {
			response.setError("Unrecognized status");
			return response;
		}
		switch (intent.getStatus()) {
        case "requires_action":
        case "requires_source_action":
            // Card requires authentication
            response.setClientSecret(intent.getClientSecret());
            response.setPaymentIntentId(intent.getId());
            response.setRequiresAction(true);
            break;
        case "requires_payment_method":
        	break;
        case "requires_capture":
        	response.setRequiresAction(false);
        	response.setClientSecret(intent.getClientSecret());
        	break;
        case "requires_source":
            // Card was not properly authenticated, suggest a new payment method
            response.setError("Your card was denied, please provide a new payment method");
            break;
        case "succeeded":
            System.out.println("💰 Payment received!");
            // Payment is complete, authentication not required
            // To cancel the payment after capture you will need to issue a Refund
            // (https://stripe.com/docs/api/refunds)
            response.setClientSecret(intent.getClientSecret());
            break;
        default:
            response.setError("Unrecognized status");
        }
        return response;
	}
}
