package com.ndb.auction.service.payment.stripe;

import java.util.List;

import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.balance.FiatBalance;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.tier.WalletTask;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.models.transactions.stripe.StripeWalletTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.service.payment.ITransactionService;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
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
					.setAmount(m.getAmount())
					.setCurrency("USD")	
					.setConfirm(true)
					.setPaymentMethod(m.getPaymentMethodId())
					.setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL);

				if(isSaveCard) {
					var customer = Customer.create(new CustomerCreateParams.Builder().setPaymentMethod(m.getPaymentMethodId()).build());
					createParams.setCustomer(customer.getId());
					createParams.setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION);
					
					// save customer
					var method = PaymentMethod.retrieve(m.getPaymentMethodId());
					
					var card = method.getCard();
					var stripeCustomer = new StripeCustomer(
						m.getUserId(), customer.getId(), card.getBrand(), card.getCountry(), card.getExpMonth(), card.getExpYear(), card.getLast4()
					);
					
					stripeCustomerDao.insert(stripeCustomer);
				}

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

	public PayResponse createTransactionWithSavedCard(StripeDepositTransaction _m, String customerId) {
		StripeWalletTransaction m = (StripeWalletTransaction)_m;
		int userId = m.getUserId();
		PaymentIntent intent = null;
		PayResponse response = new PayResponse();
		try {
			if(m.getPaymentIntentId() == null) {
				PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
						.setAmount(m.getAmount())
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
			userDao.updateTier(user.getId(), tierLevel, newPoint);
			tierTaskService.updateTierTask(tierTask);
		}

		notificationService.sendNotification(
			userId,
			Notification.DEPOSIT_SUCCESS, 
			"Deposit Successful", 
			String.format("You have successfully deposited %f %s", Double.valueOf(amount) / 100, currency)
		);


	}
    
}
