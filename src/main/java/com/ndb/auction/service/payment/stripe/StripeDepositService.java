package com.ndb.auction.service.payment.stripe;

import java.text.DecimalFormat;
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
import com.stripe.model.PaymentIntent;
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
        double totalAmount = getTotalAmount(userId, m.getFiatAmount());
        try {
            if(m.getPaymentIntentId() == null) {
                PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
                        .setAmount((long) totalAmount)
                        .setCurrency(m.getFiatType())
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
        double totalAmount = getTotalAmount(userId, m.getFiatAmount());
        try {
            if(m.getPaymentIntentId() == null) {

            PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
                    .setAmount((long) totalAmount)
                    .setCurrency(m.getFiatType())
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

        double fee = getStripeFee(userId, m.getFiatAmount()) / 100.00;
        double amount = m.getAmount() / 100;
        double cryptoPrice = 1.0;
        if(!m.getCryptoType().equals("USDT")) {
            cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(m.getCryptoType());
        }

        double deposited = (amount - fee) / cryptoPrice;
        var depositTransaction = new StripeDepositTransaction(
                userId, m.getAmount(), m.getFiatAmount(), m.getFiatType() ,m.getCryptoType(),cryptoPrice, intent.getId(),
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

            // get point
            double gainedPoint = 0.0;
            for (WalletTask task : taskSetting.getWallet()) {
                if(tierTask.getWallet() > task.getAmount()) continue;
                if(totalBalance > task.getAmount()) {
                    // add point
                    gainedPoint += task.getPoint();
                } else {
                    break;
                }
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
            tierTask.setWallet(totalBalance);
            tierTaskService.updateTierTask(tierTask);
        }
        String formattedDeposit;
        DecimalFormat df;
        if(m.getCryptoType().equals("USDT") || m.getCryptoType().equals("USDC")) {
            df = new DecimalFormat("#.00");
        } else {
            df = new DecimalFormat("#.00000000");
        }
        formattedDeposit = df.format(deposited);
        notificationService.sendNotification(
                userId,
                Notification.DEPOSIT_SUCCESS,
                "DEPOSIT SUCCESS",
                String.format("Your deposit of %s %s was successful.", formattedDeposit, m.getCryptoType())
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
