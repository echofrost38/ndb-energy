package com.ndb.auction.service.payment.coinpayment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.transactions.CoinpaymentPresaleTransaction;
import com.ndb.auction.models.transactions.CryptoDepositTransaction;
import com.ndb.auction.models.transactions.Transaction;
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
public class CoinpaymentPresaleService  extends CoinpaymentBaseService implements ITransactionService, ICryptoDepositService {

    @Override
    public Transaction createNewTransaction(Transaction _m)
            throws UnsupportedEncodingException, ClientProtocolException, IOException {
        CoinpaymentPresaleTransaction m = (CoinpaymentPresaleTransaction)_m;
        
        // round existing
        PreSale presale = presaleDao.selectById(m.getPresaleId());
        if (presale == null) {
            throw new AuctionException("Round doesn't exist.", "roundId");
        }

        // check bid
        PreSaleOrder presaleOrder = presaleOrderDao.selectById(m.getOrderId());
        if(presaleOrder == null || presaleOrder.getStatus() == 1) {
            throw new AuctionException("Order doesn't exist.", "roundId");
        }

        HttpPost post = new HttpPost(COINS_API_URL);
        post.addHeader("Connection", "close");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.addHeader("Cookie2", "$Version=1");
        post.addHeader("Accept-Language", "en-US");
        
        m = (CoinpaymentPresaleTransaction) coinpaymentPresaleDao.insert(m);

        // get address
        String ipnUrl = COINSPAYMENT_IPN_URL + "/presale/" + m.getId();
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
        coinpaymentPresaleDao.insertDepositAddress(m.getId(), address);
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
        return coinpaymentPresaleDao.selectAll(orderBy);
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        return coinpaymentPresaleDao.selectByUser(userId, orderBy);
    }

    @Override
    public Transaction selectById(int id) {
        return coinpaymentPresaleDao.selectById(id);
    }

    @Override
    public int update(int id, int status) {
        return coinpaymentPresaleDao.update(id, status);
    }

    public List<? extends Transaction> selectByPresaleId(int presaleId) {
        return coinpaymentPresaleDao.selectByPresaleId(presaleId);
    }

    public List<? extends Transaction> select(int userId, int presaleId) {
        return coinpaymentPresaleDao.select(userId, presaleId);
    }

    public int updateTransaction(int id, int status, Double cryptoAmount, String cryptoType) {
        return coinpaymentPresaleDao.updateStatus(id, status, cryptoAmount, cryptoType);
    }

    public int deleteExpired(double days) {
        return coinpaymentPresaleDao.deleteExpired(days);
    }
        
}
