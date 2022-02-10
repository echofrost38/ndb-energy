package com.ndb.auction.service.payment;

import java.util.List;

import javax.annotation.PostConstruct;

import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.StripeTransaction;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.service.BaseService;
import com.ndb.auction.service.BidService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
	public PayResponse createStripeForAuction(
		int roundId, 
		int userId, 
		Long amount, 
		String paymentIntentId, 
		String paymentMethodId
	) {
		
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
                
                StripeTransaction tx = new StripeTransaction(roundId, userId, amount, StripeTransaction.AUCTION, intent.getId());
                tx = stripeDao.createNewPayment(tx);
                
				Bid bid = bidService.getBid(roundId, userId);
				double usdAmount = ((double)amount)/100;
				if(bid.isPendingIncrease()) {
                    double pendingPrice = bid.getDelta();
                    if(pendingPrice > usdAmount) {
                        response.setError("Insufficient funds");
						return response;
                    }
                    
                    bidService.updateBid(userId, roundId, bid.getTempTokenAmount(), bid.getTempTokenPrice());
                    
                } else {
                    long totalPrice = bid.getTotalPrice();
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

	public PayResponse payStripeForPresale(
		int orderId, 
		int userId, 
		Long amount, 
		String paymentIntentId,
		String paymentMethodId
	) {
		PaymentIntent intent = null;
		PayResponse response = new PayResponse();
		
		PreSaleOrder presaleOrder = presaleOrderDao.selectById(orderId);
		if(presaleOrder == null) {
			throw new UserNotFoundException("no_presale_order", "orderId");
		}

		// check amount 
		Long orderAmount = presaleOrder.getNdbPrice() * presaleOrder.getNdbAmount();
		if(orderAmount * 100 > amount) {
			throw new UserNotFoundException("no_enough_funds", "amount");
		}

		try {
			if(paymentMethodId != null) {
				PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
					.setAmount(amount)
					.setCurrency("USD")	
					.setConfirm(true)
					.setPaymentMethod(paymentIntentId)
					.setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
					.build();
				intent = PaymentIntent.create(createParams);
				
			} else if (paymentIntentId != null) {
				intent = PaymentIntent.retrieve(paymentIntentId);
				intent = intent.confirm();
			}
			
			if(intent != null && intent.getStatus().equals("succeeded")) {
				handlePresaleOrder(userId, presaleOrder);
			}
			response = generateResponse(intent, response);

		} catch (Exception e) {
			response.setError(e.getMessage());
		}

		return response;
	}

	// update payments - called by closeBid
	public boolean UpdateTransaction(int id, Integer status) {
		
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
	public List<StripeTransaction> getTransactionsByRound(int roundId) {
		return stripeDao.getTransactionsByRound(roundId);
	}
	
	public List<StripeTransaction> getTransactionByUser(int userId) {
		
		return stripeDao.getTransactionsByUser(userId);
	}
	
	public List<StripeTransaction> getTransactions(int roundId, int userId) {
		return stripeDao.getTransactions(roundId, userId);
	}
	
	private PayResponse generateResponse(PaymentIntent intent, PayResponse response) {
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

	private void handlePresaleOrder(int userId, PreSaleOrder order) {
		User user = userDao.selectById(userId);

		// processing order
		Long ndb = order.getNdbAmount();
		Double fiatAmount = Double.valueOf(ndb * order.getNdbPrice());
		if(order.getDestination() == PreSaleOrder.INTERNAL) {
			int tokenId = tokenAssetService.getTokenIdBySymbol("NDB");
			balanceDao.addFreeBalance(userId, tokenId, ndb);
		} else if (order.getDestination() == PreSaleOrder.EXTERNAL) {
			ndbCoinService.transferNDB(userId, order.getExtAddr(), Double.valueOf(ndb));
		}

		// update user tier points
		List<Tier> tierList = tierService.getUserTiers();
		TaskSetting taskSetting = taskSettingService.getTaskSetting();
		TierTask tierTask = tierTaskService.getTierTask(user.getId());
		double presalePoint = tierTask.getDirect();
		presalePoint += taskSetting.getDirect() * fiatAmount;
		tierTask.setDirect(presalePoint);

		double newPoint = user.getTierPoint() + taskSetting.getDirect() * fiatAmount;
		int tierLevel = 0;

		// check change in level
		for (Tier tier : tierList) {
			if(tier.getPoint() <= newPoint) {
				tierLevel = tier.getLevel();
			}
		}

		userDao.updateTier(userId, tierLevel, newPoint);
		tierTaskService.updateTierTask(tierTask);
		presaleOrderDao.updateStatus(order.getId());
		presaleDao.udpateSold(order.getId(), ndb);

		// send notification to user for payment result!!
		notificationService.sendNotification(
			userId,
			Notification.PAYMENT_RESULT,
			"PAYMENT CONFIRMED",
			"You have successfully purchased " + ndb + "NDB" + " in Presale Round.");
	}
	
}
