package com.ndb.auction.service.payment;

import java.io.IOException;
import java.util.List;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.transactions.CoinpaymentAuctionTransaction;
import com.ndb.auction.models.transactions.CryptoDepositTransaction;
import com.ndb.auction.models.transactions.Transaction;
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

@Service
public class CoinpaymentAuctionService extends BaseService implements ITransactionService, ICryptoDepositService {

    protected CloseableHttpClient client;

    public CoinpaymentAuctionService() {
        client = HttpClients.createDefault();
    }

    @Override
    public List<CryptoDepositTransaction> selectByDepositAddress(String depositAddress) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String createNewTransaction(Transaction _m) throws ClientProtocolException, IOException {
        CoinpaymentAuctionTransaction m = (CoinpaymentAuctionTransaction)_m;
        
        // round existing
        Auction round = auctionDao.getAuctionById(m.getAuctionId());
        if (round == null) {
            throw new AuctionException("Round doesn't exist.", "roundId");
        }

        // check bid
        Bid bid = bidDao.getBid(m.getUserId(), m.getAuctionId());
        if (bid == null) {
            throw new AuctionException("No bid.", "roundId");
        }

        HttpPost post = new HttpPost(COINS_API_URL);
        post.addHeader("Connection", "close");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.addHeader("Cookie2", "$Version=1");
        post.addHeader("Accept-Language", "en-US");
        
        m = (CoinpaymentAuctionTransaction) coinpaymentAuctionDao.insert(m);

        // get address
        String ipnUrl = COINSPAYMENT_IPN_URL + "/bid/" + m.getId();
        CoinPaymentsGetCallbackRequest request = new CoinPaymentsGetCallbackRequest(m.getCoin(), ipnUrl);
        
        String payload = request.toString();
        payload += "&version=1&key=" + COINSPAYMENT_PUB_KEY + "&format-json";
        String hmac = buildHmacSignature(payload, COINSPAYMENT_PRIV_KEY);
        
        post.addHeader("HMAC", hmac);
        post.setEntity(new StringEntity(payload));
        CloseableHttpResponse response = client.execute(post);
        
        String content = EntityUtils.toString(response.getEntity());
        
        AddressResponse addressResponse = gson.fromJson(content, AddressResponse.class);
        if(!addressResponse.getError().equals("ok")) return "error";
        String address = addressResponse.getResult().getAddress();
        coinpaymentAuctionDao.insertDepositAddress(m.getId(), address);

        return address;
    }

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Transaction selectById(int id) {
        return coinpaymentAuctionDao.selectById(id);
    }

    @Override
    public int update(int id, int status) {
        // TODO Auto-generated method stub
        return 0;
    }

}
