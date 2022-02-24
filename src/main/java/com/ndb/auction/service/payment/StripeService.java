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
	

// 	public PayResponse payStripeForDeposit(		
// 		int userId, 
// 		Long amount, 
// 		String currencyName,
// 		String paymentIntentId,
// 		String paymentMethodId
// 	) {
// 		PaymentIntent intent = null;
// 		PayResponse response = new PayResponse();
// 		try {
// 			if(paymentMethodId != null) {
// 				PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
// 					.setAmount(amount)
// 					.setCurrency(currencyName)	
// 					.setConfirm(true)
// 					.setPaymentMethod(paymentIntentId)
// 					.setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
// 					.build();
// 				intent = PaymentIntent.create(createParams);
// 			} else if (paymentIntentId != null) {
// 				intent = PaymentIntent.retrieve(paymentIntentId);
// 				intent = intent.confirm();
// 			}

// 			if(intent != null && intent.getStatus().equals("succeeded")) {
// 				handleDepositSuccess(userId, intent.getAmount(), intent.getCurrency());
// 			}
// 			response = generateResponse(intent, response);
// 		} catch (Exception e) {
// 			response.setError(e.getMessage());
// 		}
// 		return response;
// 	}

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
	
	


	
// 	private void handleDepositSuccess(int userId, Long amount, String currency) {
// 		User user = userDao.selectById(userId);
// 		int fiatId = fiatAssetService.getFiatIdByName(currency);

// 		fiatBalanceDao.addFreeBalance(userId, fiatId, Double.valueOf(amount) / 100);
// 		List<FiatBalance> fiatBalances = fiatBalanceDao.selectByUserId(userId, null);

// 		double totalBalance = 0.0;
// 		for (FiatBalance balance : fiatBalances) {
// 			String _currency = fiatAssetService.getFiatAssetById(balance.getFiatId()).getName();
// 			double _totalBalance = balance.getFree() + balance.getHold();
// 			double _usdBalance = apiUtils.currencyConvert(_currency, "usd", _totalBalance);
// 			totalBalance += _usdBalance;
// 		}

// 		// update user tier points
// 		List<Tier> tierList = tierService.getUserTiers();
// 		TaskSetting taskSetting = taskSettingService.getTaskSetting();
// 		TierTask tierTask = tierTaskService.getTierTask(userId);

// 		if(tierTask.getWallet() < totalBalance) {

// 			tierTask.setWallet(totalBalance);
// 			// get point
// 			double gainedPoint = 0.0;
// 			for (WalletTask task : taskSetting.getWallet()) {
// 				if(tierTask.getWallet() > task.getAmount()) {
// 					continue;
// 				}                    
// 				if(totalBalance < task.getAmount()) {
// 					break;
// 				}
// 				gainedPoint += task.getPoint();
// 			}

// 			double newPoint = user.getTierPoint() + gainedPoint;
// 			int tierLevel = 0;
// 			// check change in level
// 			for (Tier tier : tierList) {
// 				if(tier.getPoint() <= newPoint) {
// 					tierLevel = tier.getLevel();
// 				}
// 			}
// 			userDao.updateTier(user.getId(), tierLevel, newPoint);
// 			tierTaskService.updateTierTask(tierTask);
// 		}

// 		notificationService.sendNotification(
// 			userId,
// 			Notification.DEPOSIT_SUCCESS, 
// 			"Deposit Successful", 
// 			String.format("You have successfully deposited %f %s", Double.valueOf(amount) / 100, currency)
// 		);


// 	}

// }
