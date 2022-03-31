package com.ndb.auction.service.payment.stripe;

import java.util.List;

import com.ndb.auction.dao.oracle.transactions.stripe.StripeDepositDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.tier.WalletTask;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.BalancePayload;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.service.InternalBalanceService;
import com.ndb.auction.service.payment.ITransactionService;
import com.ndb.auction.utils.ThirdAPIUtils;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StripeDepositService extends StripeBaseService implements ITransactionService {

    private final StripeDepositDao stripeDepositDao;
    private final InternalBalanceService internalBalanceService;

    @Autowired
    private ThirdAPIUtils apiUtil;

    @Autowired
    public StripeDepositService(StripeDepositDao stripeDepositDao,
                                InternalBalanceService internalBalanceService) {
        this.stripeDepositDao = stripeDepositDao;
        this.internalBalanceService = internalBalanceService;
    }

    public PayResponse createDeposit(StripeDepositTransaction m, boolean isSaveCard) {
        int userId = m.getUserId();
        PaymentIntent intent = null;
        PayResponse response = new PayResponse();
        Double totalAmount = getTotalAmount(userId,m.getAmount().doubleValue());
        try {
            if(m.getPaymentIntentId() == null) {
                PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
                        .setAmount(totalAmount.longValue())
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
                            m.getUserId(), customer.getId(), m.getPaymentMethodId(), card.getBrand(),
                            card.getCountry(), card.getExpMonth(), card.getExpYear(), card.getLast4()
                    );

                    stripeCustomerDao.insert(stripeCustomer);
                }

                intent = PaymentIntent.create(createParams.build());
            } else if (m.getPaymentIntentId() != null) {
                intent = PaymentIntent.retrieve(m.getPaymentIntentId());
                intent = intent.confirm();
                insert(m);
            }

            if(intent != null && intent.getStatus().equals("succeeded")) {

                // get real payment!
                handleDepositSuccess(userId, intent, m);
            }
            response = generateResponse(intent, response);
        } catch (Exception e) {
            response.setError(e.getMessage());
        }
        return response;
    }

    public PayResponse createDepositWithSavedCard(StripeDepositTransaction m, StripeCustomer customer) {
        int userId = m.getUserId();
        PaymentIntent intent = null;
        PayResponse response = new PayResponse();
        Double totalAmount = getTotalAmount(userId, m.getAmount());
        try {
            if(m.getPaymentIntentId() == null) {

            PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
                    .setAmount(totalAmount.longValue())
                    .setCurrency("USD")
                    .setCustomer(customer.getCustomerId())
                    .setConfirm(true)
                    .setPaymentMethod(customer.getPaymentMethod())
                    .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL);

            intent = PaymentIntent.create(createParams.build());
            }
            else if (m.getPaymentIntentId() != null) {
                intent = PaymentIntent.retrieve(m.getPaymentIntentId());
                if(intent.getStatus().equals("requires_confirmation")){
                    intent = intent.confirm();
                    insert(m);
                }
            }

            if(intent != null && intent.getStatus().equals("succeeded")) {
                handleDepositSuccess(userId, intent, m);
            }
            response = generateResponse(intent, response);
        } catch (Exception e) {
            response.setError(e.getMessage());
        }
        return response;
    }

    private void handleDepositSuccess(int userId, PaymentIntent intent, StripeDepositTransaction m) {

        double fee = getStripeFee(userId, m.getAmount()) / 100.00;
        double amount = m.getAmount() / 100;
        double cryptoPrice = 1.0;
        if(!m.getCryptoType().equals("USDT")) {
            cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(m.getCryptoType());
        }

        double deposited = amount / cryptoPrice;
        var depositTransaction = new StripeDepositTransaction(
                userId, m.getAmount() ,m.getCryptoType(),cryptoPrice, intent.getId(),
                intent.getPaymentMethod(),fee,deposited);

        depositTransaction.setStatus(true);
        insert(depositTransaction);

        internalBalanceService.addFreeBalance(userId, m.getCryptoType(), deposited);

        List<BalancePayload> balances = internalBalanceService.getInternalBalances(userId);
        double totalBalance = 0.0;
        for (BalancePayload balance : balances) {
            // get price and total balance
            double _price = apiUtil.getCryptoPriceBySymbol(balance.getTokenSymbol());
            double _balance = _price * (balance.getFree() + balance.getHold());
            totalBalance += _balance;
        }

        User user = userDao.selectById(userId);
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
            userDao.updateTier(user.getId(), tierLevel, newPoint);
            tierTaskService.updateTierTask(tierTask);
        }

        notificationService.sendNotification(
                userId,
                Notification.DEPOSIT_SUCCESS,
                "Deposit Successful",
                String.format("You have successfully deposited %f %s", deposited , m.getCryptoType())
        );
    }

    public int insert(StripeDepositTransaction m) {
        return stripeDepositDao.insert(m);
    }

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        return stripeDepositDao.selectAll(orderBy);
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        return stripeDepositDao.selectByUser(userId, orderBy);
    }

    @Override
    public Transaction selectById(int id) {
        return stripeDepositDao.selectById(id);
    }

    @Override
    public int update(int id, int status) {
        return 0;
    }

    public StripeDepositTransaction selectByIntentId(String intentId) {
        return stripeDepositDao.selectByStripeIntentId(intentId);
    }
}
