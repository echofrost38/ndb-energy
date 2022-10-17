package com.ndb.auction.service.payment.stripe;

import java.util.List;

import com.ndb.auction.dao.oracle.transactions.stripe.StripeTransactionDao;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;
import com.ndb.auction.models.transactions.stripe.StripeTransaction;
import com.ndb.auction.payload.response.PayResponse;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class StripePresaleService extends StripeBaseService {

    private final StripeTransactionDao stripeTransactionDao;

    public PayResponse createNewTransaction(StripeTransaction m, boolean isSaveCard) {
        PaymentIntent intent;
        PayResponse response = new PayResponse();

        int userId = m.getUserId();
        int orderId = m.getTxnId();
        double totalAmount = getTotalAmount(userId, m.getFiatAmount()) * 100;
        m.setFee(getStripeFee(userId, m.getFiatAmount()));
        var presaleOrder = presaleOrderDao.selectById(orderId);

        try {
            if (m.getIntentId() == null) {
                PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
                    .setAmount((long) totalAmount)
                    .setCurrency(m.getFiatType())
                    .setConfirm(true)
                    .setPaymentMethod(m.getMethodId())
                    .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                    .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC);

                // check save card
                if (isSaveCard) {
                    createParams = saveStripeCustomer(createParams, m);
                }

                intent = PaymentIntent.create(createParams.build());
                if(intent != null)
                    m.setIntentId(intent.getId());
                m = stripeTransactionDao.insert(m);
                log.info("presale stripe payment");
                log.info("id: {}, intent: {}", m.getId(), m.getIntentId());
            } else {
                intent = PaymentIntent.retrieve(m.getIntentId());
                intent = intent.confirm();
                stripeTransactionDao.updatePaymentIntent(m.getId(), m.getIntentId()); 
            }

            if (intent != null && intent.getStatus().equals("succeeded")) {
                handleSuccessPresaleOrder(m, presaleOrder);
            }
            response = generateResponse(intent, response);
            response.setPaymentId(m.getId());
        } catch (Exception e) {
            response.setError(e.getMessage());
        }

        return response;
    }

    public PayResponse createNewTransactionWithSavedCard(StripeTransaction m, StripeCustomer customer) {

        PaymentIntent intent;
        PayResponse response = new PayResponse();

        int userId = m.getUserId();
        int orderId = m.getTxnId();
        double totalAmount = getTotalAmount(userId, m.getFiatAmount()) * 100;
        m.setFee(getStripeFee(userId, m.getFiatAmount()));
        PreSaleOrder presaleOrder = presaleOrderDao.selectById(orderId);
        
        try {

            if(m.getIntentId() == null) {
                PaymentIntentCreateParams.Builder createParams = PaymentIntentCreateParams.builder()
                        .setAmount((long) totalAmount)
                        .setCurrency(m.getFiatType())
                        .setCustomer(customer.getCustomerId())
                        .setConfirm(true)
                        .setPaymentMethod(customer.getPaymentMethod())
                        .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC);

                intent = PaymentIntent.create(createParams.build());
                m = stripeTransactionDao.insert(m);
            }
            else {
                intent = PaymentIntent.retrieve(m.getIntentId());
                intent = intent.confirm();
                stripeTransactionDao.updatePaymentIntent(m.getId(), m.getIntentId()); 
            }

            if (intent != null && intent.getStatus().equals("succeeded")) {
                handleSuccessPresaleOrder(m, presaleOrder);
            }
            response = generateResponse(intent, response);

        } catch (Exception e) {
            response.setError(e.getMessage());
        }

        return response;
    }

    private void handleSuccessPresaleOrder(StripeTransaction m, PreSaleOrder presaleOrder) {
        int userId = m.getUserId();
        
        handlePresaleOrder(userId, m.getId(), m.getUsdAmount(), "STRIPE", presaleOrder);
        stripeTransactionDao.update(m.getId(), 1);
    }

    public List<StripeTransaction> selectAll(int status, int showStatus, Integer offset, Integer limit, String orderBy) {
        return stripeTransactionDao.selectPage(status, showStatus, offset, limit, "PRESALE", orderBy);
    }

    public List<StripeTransaction> selectByUser(int userId, int showStatus, String orderBy) {
        return stripeTransactionDao.selectByUser(userId, showStatus, orderBy);
    }

    public StripeTransaction selectById(int id) {
        return stripeTransactionDao.selectById(id);
    }

    public int update(int id, int status) {
        return stripeTransactionDao.update(id, status);
    }

    public int changeShowStatus(int id, int showStatus) {
        return stripeTransactionDao.changeShowStatus(id, showStatus);
    }
}
