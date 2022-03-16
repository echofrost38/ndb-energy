package com.ndb.auction.service.payment.stripe;

import java.util.List;

import com.ndb.auction.exceptions.UserNotFoundException;
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

        // getting ready 
        int userId = m.getUserId();
        int orderId = m.getOrderId();
        Long amount = m.getAmount();
        PreSaleOrder presaleOrder = presaleOrderDao.selectById(orderId);
        if (presaleOrder == null) {
            throw new UserNotFoundException("no_presale_order", "orderId");
        }

        try {
            if (m.getPaymentIntentId() == null) {
                PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder().setAmount(amount).setCurrency("USD").setConfirm(true).setPaymentMethod(m.getPaymentMethodId()).setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL).setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC).setConfirm(true);

                // check save card
                if (isSaveCard) {
                    var customer = Customer.create(new CustomerCreateParams.Builder().setPaymentMethod(m.getPaymentMethodId()).build());
                    createParams.setCustomer(customer.getId());
                    createParams.setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION);

                    // save customer
                    var method = PaymentMethod.retrieve(m.getPaymentMethodId());

                    var card = method.getCard();
                    var stripeCustomer = new StripeCustomer(m.getUserId(), customer.getId(), m.getPaymentMethodId(), card.getBrand(), card.getCountry(), card.getExpMonth(), card.getExpYear(), card.getLast4());

                    stripeCustomerDao.insert(stripeCustomer);
                }

                intent = PaymentIntent.create(createParams.build());

            } else {
                intent = PaymentIntent.retrieve(m.getPaymentIntentId());
                intent = intent.confirm();
                m = (StripePresaleTransaction) stripePresaleDao.insert(m);
            }

            if (intent != null && intent.getStatus().equals("succeeded")) {

                // check amount
                long paidAmount = intent.getAmount();
                Long orderAmount = presaleOrder.getNdbPrice() * presaleOrder.getNdbAmount();
                double totalOrder = getTotalOrder(userId, orderAmount.doubleValue());
                if (totalOrder * 100 > paidAmount) {
                    throw new UserNotFoundException("no_enough_funds", "amount");
                }

                handlePresaleOrder(userId, presaleOrder);
                stripePresaleDao.update(m.getId(), 1);
            }
            response = generateResponse(intent, response);

        } catch (Exception e) {
            response.setError(e.getMessage());
        }

        return response;
    }

    public PayResponse createNewTransactionWithSavedCard(StripeDepositTransaction _m, StripeCustomer customer) {
        StripePresaleTransaction m = (StripePresaleTransaction) _m;
        PaymentIntent intent;
        PayResponse response = new PayResponse();

        // getting ready
        int userId = m.getUserId();
        int orderId = m.getOrderId();
        Long amount = m.getAmount();
        PreSaleOrder presaleOrder = presaleOrderDao.selectById(orderId);
        if (presaleOrder == null) {
            throw new UserNotFoundException("no_presale_order", "orderId");
        }

        try {
            if (m.getPaymentIntentId() == null) {
                PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder().setAmount(amount).setCurrency("USD").setCustomer(customer.getCustomerId()).setConfirm(true).setPaymentMethod(customer.getPaymentMethod()).setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL).setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC).setConfirm(true);

                intent = PaymentIntent.create(createParams.build());

            } else {
                intent = PaymentIntent.retrieve(m.getPaymentIntentId());
                intent = intent.confirm();
                m = (StripePresaleTransaction) stripePresaleDao.insert(m);
            }

            if (intent != null && intent.getStatus().equals("succeeded")) {

                // check amount
                long paidAmount = intent.getAmount();
                Long orderAmount = presaleOrder.getNdbPrice() * presaleOrder.getNdbAmount();
                double totalOrder = getTotalOrder(userId, orderAmount.doubleValue());
                if (totalOrder * 100 > paidAmount) {
                    throw new UserNotFoundException("no_enough_funds", "amount");
                }

                handlePresaleOrder(userId, presaleOrder);
                stripePresaleDao.update(m.getId(), 1);
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
