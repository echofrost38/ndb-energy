package com.ndb.auction.service;

import java.io.IOException;

import com.ndb.auction.exceptions.PreSaleException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.transaction.CryptoTransaction;
import com.ndb.auction.payload.request.CoinPaymentsGetCallbackRequest;
import com.ndb.auction.payload.response.AddressResponse;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PresaleOrderService extends BaseService {
    
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
    
    public String payOrderWithCrypto(int orderId, Double amount, String currency) throws ParseException, IOException {

        // insert dataabase
        PreSaleOrder order = presaleOrderDao.selectById(orderId);
        if(order == null || order.getStatus() == 1) {
            throw new PreSaleException("no_presale_order", "orderId");
        }

        // check presale
        PreSale presale = presaleDao.selectById(order.getPresaleId());
        if(presale == null || presale.getStatus() != PreSale.STARTED) {
            throw new PreSaleException("no_started_presale", "presaleId");
        }

        Long usdPrice = order.getNdbAmount() * order.getNdbPrice();

        HttpPost post = new HttpPost(COINS_API_URL);
        post.addHeader("Connection", "close");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.addHeader("Cookie2", "$Version=1");
        post.addHeader("Accept-Language", "en-US");


        CryptoTransaction txn = new CryptoTransaction(order.getUserId(), 0, order.getId(), Double.valueOf(usdPrice), currency, CryptoTransaction.PRESALE);
        txn = cryptoTransactionDao.insert(txn);

        // get address
        String ipnUrl = COINSPAYMENT_IPN_URL + "/presale/" + txn.getId();
        CoinPaymentsGetCallbackRequest request = new CoinPaymentsGetCallbackRequest(currency, ipnUrl);
        
        String payload = request.toString();
        payload += "&version=1&key=" + COINSPAYMENT_PUB_KEY + "&format-json";
        String hmac = buildHmacSignature(payload, COINSPAYMENT_PRIV_KEY);
        
        post.addHeader("HMAC", hmac);
        post.setEntity(new StringEntity(payload));
        CloseableHttpResponse response = client.execute(post);
        
        String content = EntityUtils.toString(response.getEntity());
        
        System.out.println(content);
        
        AddressResponse addressResponse = gson.fromJson(content, AddressResponse.class);
        if(!addressResponse.getError().equals("ok")) return "error";
        
        return addressResponse.getResult().getAddress();
    }

    public PreSaleOrder getPresaleById(int orderId) {
        return presaleOrderDao.selectById(orderId);
    }

    public int updateStatus(int orderId) {
        return presaleOrderDao.updateStatus(orderId);
    }

}
