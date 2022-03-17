package com.ndb.auction.service.payment.stripe;

import com.ndb.auction.dao.oracle.transactions.stripe.StripeDepositDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.service.InternalBalanceService;
import com.ndb.auction.service.payment.ITransactionService;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StripeDepositService extends StripeBaseService implements ITransactionService {

    private final StripeDepositDao stripeDepositDao;
    private final InternalBalanceService internalBalanceService;

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
                            m.getUserId(), customer.getId(), m.getPaymentMethodId(), card.getBrand(), card.getCountry(), card.getExpMonth(), card.getExpYear(), card.getLast4()
                    );

                    stripeCustomerDao.insert(stripeCustomer);
                }

                intent = PaymentIntent.create(createParams.build());
            } else if (m.getPaymentIntentId() != null) {
                intent = PaymentIntent.retrieve(m.getPaymentIntentId());
                intent = intent.confirm();
                m = insert(m);
            }

            if(intent != null && intent.getStatus().equals("succeeded")) {

                // get real payment!
                handleDepositSuccess(userId, intent, m.getCryptoType());
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
        try {
            if(m.getPaymentIntentId() == null) {

            PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
                    .setAmount(m.getAmount())
                    .setCurrency("USD")
                    .setCustomer(customer.getCustomerId())
                    .setConfirm(true)
                    .setPaymentMethod(customer.getPaymentMethod())
                    .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC);

            intent = PaymentIntent.create(createParams.build());
            }
            else if (m.getPaymentIntentId() != null) {
                intent = PaymentIntent.retrieve(m.getPaymentIntentId());
                intent = intent.confirm();
                m = insert(m);
            }

            if(intent != null && intent.getStatus().equals("succeeded")) {
                handleDepositSuccess(userId, intent, m.getCryptoType());
            }
            response = generateResponse(intent, response);
        } catch (Exception e) {
            response.setError(e.getMessage());
        }
        return response;
    }

    private void handleDepositSuccess(int userId, PaymentIntent intent, String cryptoType) {

        double amount = intent.getAmount() / 100.00;
        double fee = getStripeFee(userId, amount);
        double cryptoPrice = 1.0;
        if(!cryptoType.equals("USDT")) {
            cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(cryptoType);
        }

        double deposited = (amount - fee) / cryptoPrice;
        var m = new StripeDepositTransaction(
                userId,(long) amount,cryptoType,cryptoPrice, intent.getId(),
                intent.getPaymentMethod(),fee,deposited);

        insert(m);

        internalBalanceService.addFreeBalance(userId, cryptoType, deposited);

        notificationService.sendNotification(
                userId,
                Notification.DEPOSIT_SUCCESS,
                "Deposit Successful",
                String.format("You have successfully deposited %f %s", deposited , cryptoType)
        );
    }

    public StripeDepositTransaction insert(StripeDepositTransaction m) {
        return (StripeDepositTransaction) stripeDepositDao.insert(m);
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
