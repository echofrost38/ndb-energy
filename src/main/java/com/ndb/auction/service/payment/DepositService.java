package com.ndb.auction.service.payment;

import java.io.IOException;

import com.ndb.auction.models.transaction.DepositTransaction;
import com.ndb.auction.payload.request.CoinPaymentsGetCallbackRequest;
import com.ndb.auction.payload.response.AddressResponse;
import com.ndb.auction.service.BaseService;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DepositService extends BaseService {

    protected CloseableHttpClient client;

    public DepositService(WebClient.Builder webClientBuilder) {
        client = HttpClients.createDefault();
        this.coinPaymentAPI = webClientBuilder
                .baseUrl(COINS_API_URL)
                .build();
    }

    public String getDepositAddress(int userId, String currency) throws ClientProtocolException, IOException {

        HttpPost post = new HttpPost(COINS_API_URL);
        post.addHeader("Connection", "close");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.addHeader("Cookie2", "$Version=1");
        post.addHeader("Accept-Language", "en-US");

        // create new deposit transaction 
        DepositTransaction txn = new DepositTransaction(userId, currency);
        txn = depositTxnDao.insert(txn);

        // get address 
        String ipnUrl = COINSPAYMENT_IPN_URL + "/deposit/" + txn.getId();
        System.out.println(String.format("Transactio ID: %d", txn.getId()));
        
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

    

}
