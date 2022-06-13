package com.ndb.auction.service;

import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ndb.auction.dao.oracle.transactions.coinpayment.CoinpaymentTransactionDao;
import com.ndb.auction.dao.oracle.transactions.paypal.PaypalPresaleDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripePresaleDao;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.presale.PresaleOrderPayments;

@Service
public class PresaleOrderService extends BaseService {
    
    @Autowired
    private CoinpaymentTransactionDao coinpaymentTransactionDao;

    @Autowired
    private StripePresaleDao stripePresaleDao;

    @Autowired
    private PaypalPresaleDao paypalPresaleDao;

    protected CloseableHttpClient client;

    public PresaleOrderService(WebClient.Builder webClientBuilder) {
        client = HttpClients.createDefault();
        this.coinPaymentAPI = webClientBuilder
                .baseUrl(COINS_API_URL)
                .build();
    }
    
    // create new presale order
    public PreSaleOrder placePresaleOrder(PreSaleOrder order) {
        return presaleOrderDao.insert(order);
    }

    public PreSaleOrder getPresaleById(int orderId) {
        return presaleOrderDao.selectById(orderId);
    }

    public int updateStatus(int orderId) {
        return presaleOrderDao.updateStatus(orderId);
    }

    public List<PreSaleOrder> getPresaleOrders(int presaleId) {
        return presaleOrderDao.selectByPresaleId(presaleId);
    }

    public List<PreSaleOrder> getPresaleOrdersByUserId(int userId) {
        return presaleOrderDao.selectAllByUserId(userId);
    }

    public PresaleOrderPayments getPaymentsByOrder(int userId, int orderId) {
        var coinpayments = coinpaymentTransactionDao.selectByOrderIdByUser(userId, orderId, "PRESALE");
        var stripe = stripePresaleDao.selectByOrderId(userId, orderId);
        var paypal = paypalPresaleDao.selectByOrderId(userId, orderId);

        return PresaleOrderPayments.builder()
            .coinpaymentTxns(coinpayments)
            .stripeTxns(stripe)
            .paypalTxns(paypal)
            .build();
    }

}
