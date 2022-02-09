package com.ndb.auction.service;

import java.io.IOException;
import java.util.List;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.transaction.CryptoTransaction;
import com.ndb.auction.payload.CoinPrice;
import com.ndb.auction.payload.request.CoinPaymentsGetCallbackRequest;
import com.ndb.auction.payload.response.AddressResponse;

import org.apache.http.HttpHeaders;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CryptoService extends BaseService {

    // get crypto price from binance API
    private WebClient binanceAPI;

    protected CloseableHttpClient client;
    
    public CryptoService(WebClient.Builder webClientBuilder) {
        client = HttpClients.createDefault();
        this.binanceAPI = webClientBuilder
                .baseUrl("https://api.binance.com/api/v3")
                .build();
        this.coinPaymentAPI = webClientBuilder
                .baseUrl(COINS_API_URL)
                .build();
    }

    public double getCryptoPriceBySymbol(String symbol) {
        try {
            String symbolPair = symbol + "USDT";
            CoinPrice objs = binanceAPI.get()
                    .uri(uriBuilder -> uriBuilder.path("/ticker/price")
                            .queryParam("symbol", symbolPair.toUpperCase())
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .onStatus(org.springframework.http.HttpStatus::is4xxClientError, response -> {
                        return null;
                    })
                    .bodyToMono(CoinPrice.class)
                    .onErrorMap(throwable -> {
                        return null;
                    })
                    .block();
            return Double.valueOf(objs.getPrice());
        } catch (Exception e) {
        }
        return 0.0;
    }

    public String createNewPayment(int roundId, int userId, Double amount, String currency) throws ParseException, IOException {

        // round existing
        Auction round = auctionDao.getAuctionById(roundId);
        if (round == null) {
            throw new AuctionException("Round doesn't exist.", "roundId");
        }

        // check bid
        Bid bid = bidDao.getBid(userId, roundId);
        if (bid == null) {
            throw new AuctionException("No bid.", "roundId");
        }

        HttpPost post = new HttpPost(COINS_API_URL);
        post.addHeader("Connection", "close");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.addHeader("Cookie2", "$Version=1");
        post.addHeader("Accept-Language", "en-US");

        // create new crypto transaction for BID!
        CryptoTransaction txn = new CryptoTransaction(userId, roundId, 0, amount, currency, CryptoTransaction.AUCTION);
        txn = cryptoTransactionDao.insert(txn);
        
        // get address
        String ipnUrl = COINSPAYMENT_IPN_URL + "/bid/" + txn.getId();
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

    public CryptoTransaction getTransactionById(int id) {
        return cryptoTransactionDao.selectById(id);
    }

    public List<CryptoTransaction> getTransactionByUser(int userId) {
        return cryptoTransactionDao.selectByUserId(userId);
    }

    public List<CryptoTransaction> getTransactionByRound(int roundId) {
        return cryptoTransactionDao.selectByRoundId(roundId);
    }

    public List<CryptoTransaction> getTransaction(int roundId, int userId) {
        return cryptoTransactionDao.getTransaction(roundId, userId);
    }

    public int updateTransaction(int id, int status, Double cryptoAmount, String cryptoType) {
        return cryptoTransactionDao.updateTransactionStatus(id, status, cryptoAmount, cryptoType);
    }

    public int deleteExpired() {
        return cryptoTransactionDao.deleteExpired(1);
    }

}
