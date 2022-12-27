package com.ndb.auction.service.payment.stripe;

import java.text.DecimalFormat;
import java.util.List;

import com.ndb.auction.dao.oracle.transactions.stripe.StripeTransactionDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.models.tier.WalletTask;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.models.transactions.stripe.StripeTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.payload.BalancePayload;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.service.InternalBalanceService;
import com.ndb.auction.utils.ThirdAPIUtils;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeDepositService extends StripeBaseService {

    private final StripeTransactionDao stripeTransactionDao;
    private final InternalBalanceService internalBalanceService;
    private final ThirdAPIUtils apiUtil;

    public PayResponse createDeposit(StripeTransaction m, boolean isSaveCard) {
        int userId = m.getUserId();
        PaymentIntent intent = null;
        PayResponse response = new PayResponse();
        double totalAmount = getTotalAmount(userId, m.getFiatAmount());
        try {
            if(m.getIntentId() == null) {
                PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
                        .setAmount((long) totalAmount)
                        .setCurrency(m.getFiatType())
                        .setConfirm(true)
                        .setPaymentMethod(m.getMethodId())
                        .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL);

                if(isSaveCard) {
                    createParams = saveStripeCustomer(createParams, m);
                }

                intent = PaymentIntent.create(createParams.build());
                if(intent != null) {
                    m.setIntentId(intent.getId());
                }
                m = stripeTransactionDao.insert(m);
                log.info("deposit stripe payment");
                log.info("id: {}, intent: {}", m.getId(), m.getIntentId());
            } else if (m.getIntentId() != null) {
                intent = PaymentIntent.retrieve(m.getIntentId());
                intent = intent.confirm();
                stripeTransactionDao.updatePaymentIntent(m.getId(), m.getIntentId());
            }

            if(intent != null && intent.getStatus().equals("succeeded")) {
                // get real payment!
                handleDepositSuccess(userId, intent, m);
            }
            response = generateResponse(intent, response);
            response.setPaymentId(m.getId());
        } catch (Exception e) {
            response.setError(e.getMessage());
        }
        return response;
    }

    public PayResponse createDepositWithSavedCard(StripeTransaction m, StripeCustomer customer) {
        int userId = m.getUserId();
        PaymentIntent intent = null;
        PayResponse response = new PayResponse();
        double totalAmount = getTotalAmount(userId, m.getFiatAmount());
        try {
            if(m.getIntentId() == null) {
                PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
                        .setAmount((long) totalAmount)
                        .setCurrency(m.getFiatType())
                        .setCustomer(customer.getCustomerId())
                        .setConfirm(true)
                        .setPaymentMethod(customer.getPaymentMethod())
                        .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL);

                intent = PaymentIntent.create(createParams.build());
                if(intent != null) {
                    m.setIntentId(intent.getId());
                }
                m = stripeTransactionDao.insert(m);
            }
            else if (m.getIntentId() != null) {
                intent = PaymentIntent.retrieve(m.getIntentId());
                intent = intent.confirm();
                stripeTransactionDao.updatePaymentIntent(m.getId(), m.getIntentId()); 
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

    private void handleDepositSuccess(int userId, PaymentIntent intent, StripeTransaction m) {

        double fee = getStripeFee(userId, m.getFiatAmount()) / 100.00;
        double amount = m.getUsdAmount() / 100;
        double cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(m.getCryptoType());
    
        double deposited = (amount - fee) / cryptoPrice;

        stripeTransactionDao.updateTransactionStatus(true, deposited, fee, intent.getStatus(), intent.getId());
        
        internalBalanceService.addFreeBalance(userId, m.getCryptoType(), deposited, "Stripe Deposit");

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

        var admins = userDao.selectByRole("ROLE_SUPER");
        try {
            mailService.sendDeposit(
                user.getEmail(),    
                user.getAvatar().getPrefix() + " " + user.getAvatar().getName(), 
                "Stripe", 
                m.getFiatType(), 
                m.getCryptoType(), 
                m.getUsdAmount(), 
                m.getCryptoAmount(), 
                m.getFee(), 
                admins
            );
        } catch (Exception e) {
            log.info("Cannot send deposit confirmation email");
        }

        notificationService.sendNotification(
                userId,
                Notification.DEPOSIT_SUCCESS,
                "DEPOSIT SUCCESS",
                String.format("Your deposit of %s %s was successful.", formattedDeposit, m.getCryptoType())
        );
    }

    public StripeTransaction insert(StripeTransaction m) {
        return stripeTransactionDao.insert(m);
    }

    public List<StripeTransaction> selectAll(int status, int showStatus, Integer offset, Integer limit, String orderBy) {
        return stripeTransactionDao.selectPage(status, showStatus, offset, limit, "DEPOSIT", orderBy);
    }

    public List<StripeTransaction> selectByUser(int userId, int showStatus, String orderBy) {
        return stripeTransactionDao.selectByUser(userId, showStatus, orderBy);
    }

    public StripeTransaction selectById(int id) {
        return stripeTransactionDao.selectById(id);
    }

    public StripeTransaction selectByIntentId(String intentId) {
        return stripeTransactionDao.selectByIntentId(intentId);
    }

    public int changeShowStatus(int id, int showStatus) {
        return stripeTransactionDao.changeShowStatus(id, showStatus);
    }
}
