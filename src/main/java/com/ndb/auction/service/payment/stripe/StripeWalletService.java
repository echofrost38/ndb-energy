package com.ndb.auction.service.payment.stripe;

import java.text.DecimalFormat;
import java.util.List;

import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.balance.FiatBalance;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.tier.WalletTask;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.models.transactions.stripe.StripeWalletTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.Whitelist;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.service.payment.ITransactionService;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import org.springframework.stereotype.Service;

@Service
public class StripeWalletService extends StripeBaseService implements ITransactionService, IStripeDepositService {

	@Override
	public PayResponse createNewTransaction(StripeDepositTransaction _m, boolean isSaveCard) {
		StripeWalletTransaction m = (StripeWalletTransaction)_m;
		int userId = m.getUserId();
		PaymentIntent intent = null;
		PayResponse response = new PayResponse();
		try {
			if(m.getPaymentIntentId() == null) {
				PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
					.setAmount(m.getAmount().longValue())
					.setCurrency("USD")	
					.setConfirm(true)
					.setPaymentMethod(m.getPaymentMethodId())
					.setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL);

				if(isSaveCard) {
					createParams = saveStripeCustomer(createParams, m);
				}

				intent = PaymentIntent.create(createParams.build());
			} else if (m.getPaymentIntentId() != null) {
				intent = PaymentIntent.retrieve(m.getPaymentIntentId());
				intent = intent.confirm();
				m = (StripeWalletTransaction) stripeWalletDao.insert(m);
			}

			if(intent != null && intent.getStatus().equals("succeeded")) {
				handleDepositSuccess(userId, intent.getAmount(), intent.getCurrency());
				stripeWalletDao.update(m.getId(), 1);
			}
			response = generateResponse(intent, response);
		} catch (Exception e) {
			response.setError(e.getMessage());
		}
		return response;
	}

	public PayResponse createTransactionWithSavedCard(StripeDepositTransaction _m, String customerId) {
		StripeWalletTransaction m = (StripeWalletTransaction)_m;
		int userId = m.getUserId();
		PaymentIntent intent = null;
		PayResponse response = new PayResponse();
		try {
			if(m.getPaymentIntentId() == null) {
				PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
						.setAmount(m.getAmount().longValue())
						.setCurrency("USD")
						.setCustomer(customerId)
						.setConfirm(true)
						.setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL);

				intent = PaymentIntent.create(createParams.build());
			} else if (m.getPaymentIntentId() != null) {
				intent = PaymentIntent.retrieve(m.getPaymentIntentId());
				intent = intent.confirm();
				m = (StripeWalletTransaction) stripeWalletDao.insert(m);
			}

			if(intent != null && intent.getStatus().equals("succeeded")) {

				// get real payment!

				handleDepositSuccess(userId, intent.getAmount(), intent.getCurrency());
				stripeWalletDao.update(m.getId(), 1);
			}
			response = generateResponse(intent, response);
		} catch (Exception e) {
			response.setError(e.getMessage());
		}
		return response;
	}

	@Override
	public List<? extends StripeDepositTransaction> selectByIntentId(String intentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends Transaction> selectAll(String orderBy) {
		return stripeWalletDao.selectAll(orderBy);
	}

	@Override
	public List<? extends Transaction> selectByUser(int userId, String orderBy) {
		return stripeWalletDao.selectByUser(userId, orderBy);
	}

	@Override
	public Transaction selectById(int id) {
		return stripeWalletDao.selectById(id);
	}

	@Override
	public int update(int id, int status) {
		return stripeWalletDao.update(id, status);
	}

	private void handleDepositSuccess(int userId, Long amount, String currency) {
		User user = userDao.selectById(userId);
		int fiatId = fiatAssetService.getFiatIdByName(currency);

		fiatBalanceDao.addFreeBalance(userId, fiatId, Double.valueOf(amount) / 100);
		List<FiatBalance> fiatBalances = fiatBalanceDao.selectByUserId(userId, null);

		double totalBalance = 0.0;
		for (FiatBalance balance : fiatBalances) {
			String _currency = fiatAssetService.getFiatAssetById(balance.getFiatId()).getName();
			double _totalBalance = balance.getFree() + balance.getHold();
			double _usdBalance = apiUtils.currencyConvert(_currency, "usd", _totalBalance);
			totalBalance += _usdBalance;
		}

		// update user tier points
		List<Tier> tierList = tierService.getUserTiers();
		TaskSetting taskSetting = taskSettingService.getTaskSetting();
		TierTask tierTask = tierTaskService.getTierTask(userId);

		if(tierTask == null) {
			tierTask = new TierTask(userId);
			tierTaskService.updateTierTask(tierTask);
		}

		if(tierTask.getWallet() < totalBalance) {

			tierTask.setWallet(totalBalance);
			// get point
			double gainedPoint = 0.0;
			for (WalletTask task : taskSetting.getWallet()) {
				if(tierTask.getWallet() > task.getAmount()) {
					continue;
				}                    
				if(totalBalance < task.getAmount()) {
					break;
				}
				gainedPoint += task.getPoint();
			}

			double newPoint = user.getTierPoint() + gainedPoint;
			int tierLevel = 0;
			// check change in level
			for (Tier tier : tierList) {
				if(tier.getPoint() <= newPoint) {
					tierLevel = tier.getLevel();
				}
			}
			var tier = tierDao.selectByLevel(tierLevel);
			if(tier.getName().equals("Diamond")) {
				var m = whitelistDao.selectByUserId(userId);
				if(m == null) {
					m = new Whitelist(userId, "Diamond Level");
					whitelistDao.insert(m);
				}
			}
			userDao.updateTier(user.getId(), tierLevel, newPoint);
			tierTaskService.updateTierTask(tierTask);
		}

		String formattedDeposit;
		DecimalFormat df;
		if(currency.equals("USDT") || currency.equals("USDC")) {
			df = new DecimalFormat("#.00");
		} else {
			df = new DecimalFormat("#.00000000");
		}
		formattedDeposit = df.format(Double.valueOf(amount) / 100);
		notificationService.sendNotification(
			userId,
			Notification.DEPOSIT_SUCCESS, 
			"DEPOSIT SUCCESS", 
			String.format("Your deposit of 20 %s %s was successful.", formattedDeposit, currency)
		);


	}

}
