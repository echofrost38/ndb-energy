package com.ndb.auction.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ndb.auction.models.Bid;
import com.ndb.auction.models.StripeTransaction;
import com.ndb.auction.payload.PayResponse;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class StripeService extends BaseService {
	
	@Value("${stripe.secret.key}")
	private String stripeSecretKey;
	
	@Value("${stripe.public.key}")
	private String stripePublicKey;
	
	@Autowired 
	private BidService bidService;
	
    @PostConstruct
    public void init() {
    	Stripe.apiKey = stripeSecretKey;
    }
	
	
	public String getPublicKey( ) {
		return stripePublicKey;
	}
	
	/**
	 * create new payment and check confirm status
	 * @param roundId : String Round ID that user places new bid
	 * @param userId : String user who makes new payment
	 * @param amount
	 * @param paymentIntentId
	 * @param paymentMethodId
	 * @return
	 */
	public PayResponse createNewPayment(String roundId, String userId, Long amount, String paymentIntentId, String paymentMethodId) {
		
		PaymentIntent intent;
		PayResponse response = new PayResponse();
		try {
            if (paymentIntentId == null) {
            	
            	// Create new PaymentIntent for the order
                PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                        .setCurrency("usd")
                        .setAmount(amount)
                        .setPaymentMethod(paymentMethodId)
                        .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                        .setConfirm(true)
                        .build();
                
                
                // Create a PaymentIntent with the order amount and currency
                intent = PaymentIntent.create(createParams);
                
                StripeTransaction tx = new StripeTransaction(roundId, userId, amount, intent.getId());
                tx = stripeDao.createNewPayment(tx);
                
//                intent = PaymentIntent.retrieve(paymentIntentId);
//                intent = intent.confirm();
                
//                stripeDao.updatePaymentStatus(paymentIntentId, StripeTransaction.AUTHORIZED);
				Bid bid = bidService.getBid(roundId, userId);
				double usdAmount = (double)amount;
				if(bid.getPendingIncrease()) {
                    double pendingPrice = bid.getDelta();
                    if(pendingPrice > usdAmount) {
                        response.setError("Insufficient funds");
						return response;
                    }
                    
                    bidService.updateBid(userId, roundId, bid.getTempTokenAmount(), bid.getTempTokenPrice());
                    
                } else {
                    double totalPrice = bid.getTotalPrice();
                    if(totalPrice > usdAmount) {
                        response.setError("Insufficient funds");
						return response;
                    }
					bidService.updateBidRanking(userId, roundId);
                }

                // Call update bid ranking!
                // it must be called only when payment is confirmed.....

				response = generateResponse(intent, response);
            }
            
        } catch (Exception e) {
            // Handle "hard declines" e.g. insufficient funds, expired card, etc
            // See https://stripe.com/docs/declines/codes for more
        	response.setError(e.getMessage());
        }
		return response;
	}
	
	// update payments - called by closeBid
	public boolean UpdateTransaction(String id, Integer status) {
		
		PaymentIntent intent;
		
		StripeTransaction tx = stripeDao.getTransactionById(id);
		if(tx == null) {
			return false;
		}
		
		String paymentIntentId = tx.getPaymentIntentId();
		try {
			intent = PaymentIntent.retrieve(paymentIntentId);
			if(status == Bid.WINNER) {
				intent.capture();		
				stripeDao.updatePaymentStatus(paymentIntentId, StripeTransaction.CAPTURED);
			} else {
				intent.cancel();
				stripeDao.updatePaymentStatus(paymentIntentId, StripeTransaction.CANCELED);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}	
		return true;
	}
	
	// get transactions
	public List<StripeTransaction> getTransactionsByRound(String roundId) {
		return stripeDao.getTransactionsByRound(roundId);
	}
	
	public List<StripeTransaction> getTransactionByUser(String userId) {
		
		return stripeDao.getTransactionsByUser(userId);
	}
	
	public List<StripeTransaction> getTransactions(String roundId, String userId) {
		return stripeDao.getTransactions(roundId, userId);
	}
	
	private PayResponse generateResponse(PaymentIntent intent, PayResponse response) {
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
