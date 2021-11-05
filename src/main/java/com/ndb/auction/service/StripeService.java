package com.ndb.auction.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ndb.auction.models.Bid;
import com.ndb.auction.models.FiatTransaction;
import com.ndb.auction.payload.PayResponse;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class StripeService extends BaseService {
	
	@Autowired 
	private BidService bidService;
	
	public StripeService() {
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
	 * @return
	 */
	public PayResponse createNewPayment(String roundId, String userId, Long amount, String paymentIntentId) {
		
		PaymentIntent intent;
		PayResponse response = new PayResponse();
		try {
            if (paymentIntentId == null) {

            	// check existing
        		FiatTransaction fTx = stripeDao.getTransaction(roundId, userId);
        		if(fTx != null) {
        			response.setError("Already exists");
        			return response;
        		}
            	
            	// Create new PaymentIntent for the order
                PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                        .setCurrency("usd")
                        .setAmount(amount)
                        .setPaymentMethod("card")
                        .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                        .setConfirm(true)
                        .build();
                
                
                // Create a PaymentIntent with the order amount and currency
                intent = PaymentIntent.create(createParams);
                
                fTx = new FiatTransaction(roundId, userId, amount, intent.getId());
                fTx = stripeDao.createNewPayment(fTx);
                
            } else {
                // Confirm the PaymentIntent to collect the money
            	FiatTransaction tx = stripeDao.getTransactionById(paymentIntentId);
            	if(tx == null) {
            		response.setError("Not found paymentIntent");
            		return response;
            	}
            	
                intent = PaymentIntent.retrieve(paymentIntentId);
                intent = intent.confirm();
                
                stripeDao.updatePaymentStatus(paymentIntentId, FiatTransaction.AUTHORIZED);
                
                // Call update bid ranking!
                // it must be called only when payment is confirmed.....
                bidService.updateBidRanking(userId, roundId);
            }
            
            response = generateResponse(intent, response);
        } catch (Exception e) {
            // Handle "hard declines" e.g. insufficient funds, expired card, etc
            // See https://stripe.com/docs/declines/codes for more
        	response.setError(e.getMessage());
        }
		return response;
	}
	
	// update payments - called by closeBid
	public boolean UpdateTransaction(String roundId, String userId, Integer status) {
		
		PaymentIntent intent;
		
		FiatTransaction tx = stripeDao.getTransaction(roundId, userId);
		if(tx == null) {
			return false;
		}
		
		String paymentIntentId = tx.getPaymentIntentId();
		try {
			intent = PaymentIntent.retrieve(paymentIntentId);
			if(status == Bid.WINNER) {
				intent.capture();		
				stripeDao.updatePaymentStatus(paymentIntentId, FiatTransaction.CAPTURED);
			} else {
				intent.cancel();
				stripeDao.updatePaymentStatus(paymentIntentId, FiatTransaction.CANCELED);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}	
		return true;
	}
	
	// get transactions
	public List<FiatTransaction> getTransactionsByRound(String roundId) {
		return stripeDao.getTransactionsByRound(roundId);
	}
	
	public List<FiatTransaction> getTransactionByUser(String userId) {
		
		return stripeDao.getTransactionsByUser(userId);
	}
	
	public FiatTransaction getTransaction(String roundId, String userId) {
		return stripeDao.getTransaction(roundId, userId);
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
