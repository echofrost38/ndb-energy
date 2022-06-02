package com.ndb.auction.service.payment.coinpayment;

import java.io.IOException;

import com.ndb.auction.dao.oracle.transactions.coinpayment.CoinpaymentAuctionDao;
import com.ndb.auction.dao.oracle.transactions.coinpayment.CoinpaymentPresaleDao;
import com.ndb.auction.dao.oracle.transactions.coinpayment.CoinpaymentWalletDao;
import com.ndb.auction.payload.request.CoinPaymentsRateRequest;
import com.ndb.auction.service.BaseService;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class CoinpaymentBaseService extends BaseService {
    
    @Autowired
    protected CoinpaymentAuctionDao coinpaymentAuctionDao;

    @Autowired
    protected CoinpaymentPresaleDao coinpaymentPresaleDao;

    @Autowired
    protected CoinpaymentWalletDao coinpaymentWalletDao;

    protected CloseableHttpClient client;

    public CoinpaymentBaseService() {
        client = HttpClients.createDefault();
    }
    
    public String getExchangeRate() throws ParseException, IOException {
        HttpPost post = new HttpPost(COINS_API_URL);
        post.addHeader("Connection", "close");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.addHeader("Cookie2", "$Version=1");
        post.addHeader("Accept-Language", "en-US");

        CoinPaymentsRateRequest request = new CoinPaymentsRateRequest();
        String payload = request.toString();
        payload += "&version=1&key=" + COINSPAYMENT_PUB_KEY + "&format-json";
        String hmac = buildHmacSignature(payload, COINSPAYMENT_PRIV_KEY);
        
        post.addHeader("HMAC", hmac);
        post.setEntity(new StringEntity(payload));
        CloseableHttpResponse response = client.execute(post);
        
        return EntityUtils.toString(response.getEntity());
    }
}
