package com.ndb.auction.service.payment.stripe;

import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.models.transactions.stripe.StripePresaleTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.ndb.auction.service.payment.ITransactionService;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;

import org.springframework.stereotype.Service;

@Service
public class StripePresaleService extends StripeBaseService implements ITransactionService, IStripeDepositService {

    @Override
    public PayResponse createNewTransaction(StripeDepositTransaction _m, boolean isSaveCard) {
        StripePresaleTransaction m = (StripePresaleTransaction) _m;
        PaymentIntent intent;
        PayResponse response = new PayResponse();

        int userId = m.getUserId();
        int orderId = m.getOrderId();
        double totalAmount = getTotalAmount(userId, m.getAmount());
        m.setFee(getStripeFee(userId, m.getAmount()));
        PreSaleOrder presaleOrder = presaleOrderDao.selectById(orderId);
        if (presaleOrder == null) {
            String msg = messageSource.getMessage("no_order", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "order");
        }

        try {
            if (m.getPaymentIntentId() == null) {
                PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder().setAmount((long) totalAmount).setCurrency("USD").setConfirm(true).setPaymentMethod(m.getPaymentMethodId()).setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL).setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC).setConfirm(true);

                // check save card
                if (isSaveCard) {
                    createParams = saveStripeCustomer(createParams, m);
                }

                intent = PaymentIntent.create(createParams.build());
                stripePresaleDao.insert(m);
            } else {
                intent = PaymentIntent.retrieve(m.getPaymentIntentId());
                intent = intent.confirm();
                stripePresaleDao.insert(m);
            }

            if (intent != null && intent.getStatus().equals("succeeded")) {
                handleSuccessPresaleOrder(m, presaleOrder, intent);
            }
            response = generateResponse(intent, response);

        } catch (Exception e) {
            response.setError(e.getMessage());
        }

        return response;
    }

    public PayResponse createNewTransactionWithSavedCard(StripePresaleTransaction m, StripeCustomer customer) {

        PaymentIntent intent;
        PayResponse response = new PayResponse();

        int userId = m.getUserId();
        int orderId = m.getOrderId();
        double totalAmount = getTotalAmount(userId, m.getAmount());
        m.setFee(getStripeFee(userId, m.getAmount()));
        PreSaleOrder presaleOrder = presaleOrderDao.selectById(orderId);
        if (presaleOrder == null) {
            String msg = messageSource.getMessage("no_order", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "order");
        }

        try {

            if(m.getPaymentIntentId() == null) {
                PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
                        .setAmount((long) totalAmount)
                        .setCurrency("USD")
                        .setCustomer(customer.getCustomerId())
                        .setConfirm(true)
                        .setPaymentMethod(customer.getPaymentMethod())
                        .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                        .setConfirm(true);

                intent = PaymentIntent.create(createParams.build());
                stripePresaleDao.insert(m);
            }
            else {
                intent = PaymentIntent.retrieve(m.getPaymentIntentId());
                intent = intent.confirm();
                stripePresaleDao.insert(m);
            }

            if (intent != null && intent.getStatus().equals("succeeded")) {

                handleSuccessPresaleOrder(m, presaleOrder, intent);
            }
            response = generateResponse(intent, response);

        } catch (Exception e) {
            response.setError(e.getMessage());
        }

        return response;
    }

    private void handleSuccessPresaleOrder(StripePresaleTransaction m, PreSaleOrder presaleOrder, PaymentIntent intent) {
        int userId = m.getUserId();
        long paidAmount = intent.getAmount();
        double orderAmount = presaleOrder.getNdbPrice() * presaleOrder.getNdbAmount() * 100;
        double totalOrder = getTotalAmount(userId, orderAmount);
        if (totalOrder > paidAmount) {
            String msg = messageSource.getMessage("insufficient", null, Locale.ENGLISH);
            throw new BalanceException(msg, "balance");
        }

        handlePresaleOrder(userId, presaleOrder);
        stripePresaleDao.update(m.getId(), 1);
    }

    @Override
    public List<? extends StripeDepositTransaction> selectByIntentId(String intentId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        return stripePresaleDao.selectAll(orderBy);
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        return stripePresaleDao.selectByUser(userId, orderBy);
    }

    @Override
    public Transaction selectById(int id) {
        return stripePresaleDao.selectById(id);
    }

    @Override
    public int update(int id, int status) {
        return stripePresaleDao.update(id, status);
    }

    public List<? extends Transaction> selectByPresale(int userId, int presaleId, String orderBy) {
        return stripePresaleDao.selectByPresale(userId, presaleId, orderBy);
    }
}
