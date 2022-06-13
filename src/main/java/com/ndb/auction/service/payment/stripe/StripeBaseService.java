package com.ndb.auction.service.payment.stripe;

import javax.annotation.PostConstruct;

import com.ndb.auction.dao.oracle.transactions.stripe.StripeAuctionDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripePresaleDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripeWalletDao;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.service.BaseService;
import com.ndb.auction.service.BidService;
import com.ndb.auction.utils.ThirdAPIUtils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;

import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethod.Card;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.stripe.param.PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION;

@Service
public class StripeBaseService extends BaseService {

    @Getter
    private final double STRIPE_FEE = 2.9;

    @Value("${stripe.secret.key}")
	private String stripeSecretKey;

	@Value("${stripe.public.key}")
	private String stripePublicKey;

	@Autowired 
	protected BidService bidService;

    @Autowired
    protected StripeAuctionDao stripeAuctionDao;

    @Autowired 
    protected StripePresaleDao stripePresaleDao;

    @Autowired
    protected StripeWalletDao stripeWalletDao;

    @Autowired
    protected ThirdAPIUtils thirdAPIUtils;

    @PostConstruct
    public void init() {
    	Stripe.apiKey = stripeSecretKey;
    }
	
	public String getPublicKey( ) {
		return stripePublicKey;
	}

    // total order! 
    // total = 100 / (100 - gateway fee - tier fee) * (amount + fixed fee)
    public Double getTotalOrder(int userId, double amount) {
        User user = userDao.selectById(userId);
        double tierFeeRate = txnFeeService.getFee(user.getTierLevel());
        var white = whitelistDao.selectByUserId(userId);
		if(white != null) tierFeeRate = 0.0;
        return 100 * (amount + 0.30) / (100 - STRIPE_FEE - tierFeeRate);
    }

    public double getStripeFee(int userId, double amount) {
        User user = userDao.selectById(userId);
        double tierFeeRate = txnFeeService.getFee(user.getTierLevel());
        var white = whitelistDao.selectByUserId(userId);
        if(white != null) tierFeeRate = 0.0;
        return  (amount * (STRIPE_FEE + tierFeeRate) / 100) + 0.30;
    }

    public double getTotalAmount(int userId, double amount) {
        double fee = getStripeFee(userId, amount);
        return amount + fee;
    }

    protected PayResponse generateResponse(PaymentIntent intent, PayResponse response) {
		if(intent == null) {
			response.setError("Unrecognized status");
			return response;
		}
		switch (intent.getStatus()) {
        case "requires_action":
            response.setClientSecret(intent.getClientSecret());
            response.setRequiresAction(true);
            break;
        case "requires_source_action":
            // Card requires authentication
            response.setClientSecret(intent.getClientSecret());
            response.setPaymentIntentId(intent.getId());
            response.setRequiresAction(true);
            break;
        case "requires_payment_method":
            response.setError("requires_payment_method");
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
    public Builder saveStripeCustomer(Builder createParams, StripeDepositTransaction m) throws StripeException {
        Customer customer = Customer.create(new CustomerCreateParams.Builder().setPaymentMethod(m.getPaymentMethodId()).build());
        createParams.setCustomer(customer.getId());
        createParams.setSetupFutureUsage(OFF_SESSION);

        // save customer
        PaymentMethod method = PaymentMethod.retrieve(m.getPaymentMethodId());

        Card card = method.getCard();
        StripeCustomer stripeCustomer = new StripeCustomer(
                m.getUserId(), customer.getId(), m.getPaymentMethodId(), card.getBrand(),
                card.getCountry(), card.getExpMonth(), card.getExpYear(), card.getLast4()
        );

        stripeCustomerDao.insert(stripeCustomer);
        return createParams;
    }
}
