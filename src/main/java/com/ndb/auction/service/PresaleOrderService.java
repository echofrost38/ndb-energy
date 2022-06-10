package com.ndb.auction.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ndb.auction.dao.oracle.transactions.coinpayment.CoinpaymentTransactionDao;
import com.ndb.auction.dao.oracle.transactions.paypal.PaypalPresaleDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripePresaleDao;
import com.ndb.auction.dao.oracle.user.WhitelistDao;
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

    @Autowired
    private WhitelistDao whitelistDao;

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

    public int updateStatus(int orderId, int paymentId, double paidAmount, String paymentType) {
        return presaleOrderDao.updateStatus(orderId, paymentId, paidAmount, paymentType);
    }

    public List<PreSaleOrder> getPresaleOrders(int presaleId) {
        var list = presaleOrderDao.selectByPresaleId(presaleId);

        for (var order : list) {
            if(order.getPaidAmount() > 0) continue;
            var userId = order.getUserId(); var orderId = order.getId();
            var coinpayments = coinpaymentTransactionDao.selectByOrderIdByUser(userId, orderId, "PRESALE");
            var confirmedCrypto = coinpayments.stream()
                .filter(c -> c.getDepositStatus() == 1)
                .collect(Collectors.toList());
            if(confirmedCrypto.size() > 0) {
                var payment = confirmedCrypto.get(0);
                var cryptoAmount = payment.getAmount();
                var price = apiUtils.getCryptoPriceBySymbol(payment.getCryptoType());
                var paid = cryptoAmount * price;
                order.setPaidAmount(paid);
                updateStatus(orderId, payment.getId(), paid, "CRYPTO");
                continue;
            }

            var stripe = stripePresaleDao.selectByOrderId(userId, orderId);
            var confirmedStripe = stripe.stream()
                .filter(c -> c.getStatus())
                .collect(Collectors.toList());
            if(confirmedStripe.size() > 0) {
                var payment = confirmedStripe.get(0);
                order.setPaidAmount(payment.getAmount());
                updateStatus(orderId, payment.getId(), payment.getAmount(), "STRIPE");
                continue;
            }
            
            var paypal = paypalPresaleDao.selectByOrderId(userId, orderId);
            var confirmedPaypal = paypal.stream()
                .filter(c -> c.getStatus())
                .collect(Collectors.toList());
            if(confirmedPaypal.size() > 0) {
                var payment = confirmedPaypal.get(0);
                order.setPaidAmount(payment.getAmount());
                updateStatus(orderId, payment.getId(), payment.getAmount(), "PAYPAL");
                continue;
            }

            // assume internal wallet
            var tierFeeRate = 0.5;
            var white = whitelistDao.selectByUserId(userId);
            if(white != null) tierFeeRate = 0.0;

            var totalOrder = 100 * (order.getNdbAmount() * order.getNdbPrice()) / (100 - tierFeeRate);
            order.setPaidAmount(totalOrder);
            updateStatus(orderId, 0, totalOrder, "NYYU");
        }

        return list;
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
