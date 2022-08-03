package com.ndb.auction.service.payment.coinpayment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.ndb.auction.models.transactions.coinpayment.CoinpaymentDepositTransaction;
import com.ndb.auction.payload.request.CoinPaymentsGetCallbackRequest;
import com.ndb.auction.payload.response.AddressResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class CoinpaymentWalletService extends CoinpaymentBaseService {

    public CoinpaymentDepositTransaction createNewTransaction(CoinpaymentDepositTransaction m)
            throws UnsupportedEncodingException, ClientProtocolException, IOException {

        HttpPost post = new HttpPost(COINS_API_URL);
        post.addHeader("Connection", "close");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.addHeader("Cookie2", "$Version=1");
        post.addHeader("Accept-Language", "en-US");
        
        m = coinpaymentTransactionDao.insert(m);
        
        // get address
        String ipnUrl = COINSPAYMENT_IPN_URL + "/deposit/" + m.getId();
        CoinPaymentsGetCallbackRequest request = new CoinPaymentsGetCallbackRequest(m.getCoin(), ipnUrl);
        
        String payload = request.toString();
        payload += "&version=1&key=" + COINSPAYMENT_PUB_KEY + "&format-json";
        String hmac = buildHmacSignature(payload, COINSPAYMENT_PRIV_KEY);
        
        post.addHeader("HMAC", hmac);
        post.setEntity(new StringEntity(payload));
        CloseableHttpResponse response = client.execute(post);
        
        String content = EntityUtils.toString(response.getEntity());
        
        AddressResponse addressResponse = gson.fromJson(content, AddressResponse.class);
        if(!addressResponse.getError().equals("ok")) return null;
        String address = addressResponse.getResult().getAddress();
        coinpaymentTransactionDao.updateDepositAddress(m.getId(), address);
        m.setDepositAddress(address);
        return m;
    }

    public CoinpaymentDepositTransaction createNewDepositTxn(CoinpaymentDepositTransaction m) {
        return coinpaymentTransactionDao.insert(m);
    }

    public int updateStatus(int id, double amount, double deposited, double fee, String cryptoType) {
        return coinpaymentTransactionDao.updateStatus(id, 1, amount, deposited, fee, cryptoType);
    }
    
}
