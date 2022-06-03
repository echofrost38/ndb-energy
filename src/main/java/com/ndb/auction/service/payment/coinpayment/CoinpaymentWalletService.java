package com.ndb.auction.service.payment.coinpayment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.ndb.auction.models.transactions.CryptoDepositTransaction;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentWalletTransaction;
import com.ndb.auction.payload.request.CoinPaymentsGetCallbackRequest;
import com.ndb.auction.payload.response.AddressResponse;
import com.ndb.auction.service.payment.ICryptoDepositService;
import com.ndb.auction.service.payment.ITransactionService;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class CoinpaymentWalletService extends CoinpaymentBaseService implements ITransactionService, ICryptoDepositService {

    @Override
    public Transaction createNewTransaction(Transaction _m)
            throws UnsupportedEncodingException, ClientProtocolException, IOException {
        CoinpaymentWalletTransaction m = (CoinpaymentWalletTransaction)_m;

        HttpPost post = new HttpPost(COINS_API_URL);
        post.addHeader("Connection", "close");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.addHeader("Cookie2", "$Version=1");
        post.addHeader("Accept-Language", "en-US");
        
        m = (CoinpaymentWalletTransaction) coinpaymentWalletDao.insert(m);
        
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
        coinpaymentWalletDao.insertDepositAddress(m.getId(), address);
        m.setDepositAddress(address);
        return m;
    }

    @Override
    public List<CryptoDepositTransaction> selectByDepositAddress(String depositAddress) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        return coinpaymentWalletDao.selectAll(orderBy);
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        return coinpaymentWalletDao.selectByUser(userId, orderBy);
    }

    @Override
    public Transaction selectById(int id) {
        return coinpaymentWalletDao.selectById(id);
    }

    @Override
    public int update(int id, int status) {
        return coinpaymentWalletDao.update(id, status);
    }

    public int updateStatus(int id, int status, double amount, double fee, String type) {
        return coinpaymentWalletDao.updateStatus(id, status, amount, fee, type);
    }

    public int deleteExpired(double days) {
        return coinpaymentWalletDao.deleteExpired(days);
    }
    
}
