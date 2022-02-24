// package com.ndb.auction.service.payment;

// import java.util.List;

// import javax.annotation.PostConstruct;

// import com.ndb.auction.exceptions.UserNotFoundException;
// import com.ndb.auction.models.Bid;
// import com.ndb.auction.models.Notification;
// import com.ndb.auction.models.StripeTransaction;
// import com.ndb.auction.models.TaskSetting;
// import com.ndb.auction.models.balance.FiatBalance;
// import com.ndb.auction.models.presale.PreSaleOrder;
// import com.ndb.auction.models.tier.Tier;
// import com.ndb.auction.models.tier.TierTask;
// import com.ndb.auction.models.tier.WalletTask;
// import com.ndb.auction.models.user.User;
// import com.ndb.auction.payload.response.PayResponse;
// import com.ndb.auction.service.BaseService;
// import com.ndb.auction.service.BidService;
// import com.stripe.Stripe;
// import com.stripe.model.PaymentIntent;
// import com.stripe.param.PaymentIntentCreateParams;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;

// @Service
// public class StripeService extends BaseService {
	
// 	// update payments - called by closeBid
// 	private boolean UpdateTransaction(int id, Integer status) {
		
// 		PaymentIntent intent;
		
// 		StripeTransaction tx = stripeDao.getTransactionById(id);
// 		if(tx == null) {
// 			return false;
// 		}
		
// 		String paymentIntentId = tx.getPaymentIntentId();
// 		try {
// 			intent = PaymentIntent.retrieve(paymentIntentId);
// 			if(status == Bid.WINNER) {
// 				intent.capture();		
// 				stripeDao.updatePaymentStatus(paymentIntentId, StripeTransaction.CAPTURED);
// 			} else {
// 				intent.cancel();
// 				stripeDao.updatePaymentStatus(paymentIntentId, StripeTransaction.CANCELED);
// 			}
// 		} catch (Exception e) {
// 			System.out.println(e.getMessage());
// 			return false;
// 		}	
// 		return true;
// 	}
	
// 	// get transactions
// 	public List<StripeTransaction> getTransactionsByRound(int roundId) {
// 		return stripeDao.getTransactionsByRound(roundId);
// 	}
	
// 	public List<StripeTransaction> getTransactionByUser(int userId) {
// 		return stripeDao.getTransactionsByUser(userId);
// 	}
	
// 	public List<StripeTransaction> getTransactions(int roundId, int userId) {
// 		return stripeDao.getTransactions(roundId, userId);
// 	}


// }
