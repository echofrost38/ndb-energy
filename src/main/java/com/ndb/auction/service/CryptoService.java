package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.transaction.CryptoTransaction;
import com.ndb.auction.models.coinbase.CoinbaseBody;
import com.ndb.auction.models.coinbase.CoinbasePostBody;
import com.ndb.auction.models.coinbase.CoinbaseRes;
import com.ndb.auction.payload.CoinPrice;
import com.ndb.auction.payload.CryptoPayload;

import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class CryptoService extends BaseService {

    // get crypto price from binance API
    private WebClient binanceAPI;
    
    public CryptoService(WebClient.Builder webClientBuilder) {
        this.binanceAPI = webClientBuilder
                .baseUrl("https://api.binance.com/api/v3")
                .build();
        this.coinbaseAPI = webClientBuilder
                .baseUrl("https://api.commerce.coinbase.com")
                .build();
    }

    public int getCryptoPriceBySymbol(String symbol) {
        String symbolPair = symbol + "USDT";
        CoinPrice objs = binanceAPI.get()
                .uri(uriBuilder -> uriBuilder.path("/ticker/price")
                        .queryParam("symbol", symbolPair.toUpperCase())
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(CoinPrice.class).block();
        return Integer.parseInt(objs.getPrice());
    }

    public CryptoPayload createNewPayment(int roundId, int userId, String amount) {

        // round existing
        Auction round = auctionDao.getAuctionById(roundId);
        if (round == null) {
            throw new AuctionException("Round doesn't exist.", "roundId");
        }

        CoinbasePostBody data = new CoinbasePostBody(
                "Bid payment",
                "Bid payment for " + userId,
                "fixed_price",
                amount);

        // API call for create new charge
        String response = coinbaseAPI.post()
                .uri(uriBuilder -> uriBuilder.path("/charges").build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-CC-Api-Key", coinbaseApiKey)
                .header("X-CC-Version", "2018-03-22")
                .body(Mono.just(data), CoinbasePostBody.class)
                .retrieve()
                .bodyToMono(String.class).block();

        CoinbaseRes res = gson.fromJson(response, CoinbaseRes.class);

        CoinbaseBody resBody = res.getData();
        String txnId = resBody.getId();
        String code = resBody.getCode();
        CryptoTransaction tx = new CryptoTransaction(userId, roundId, txnId, code, Double.valueOf(amount), 1);
        cryptoTransactionDao.insert(tx);

        CryptoPayload payload = new CryptoPayload(resBody.getAddresses(), resBody.getPricing());

        return payload;
    }

    public CryptoTransaction getTransactionByCode(String code) {
        return cryptoTransactionDao.selectByCode(code);
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

    public int updateTransaction(String code, int status, String cryptoAmount, String cryptoType) {
        return cryptoTransactionDao.updateTransactionStatus(code, status, cryptoAmount, cryptoType);
    }

    public int deleteExpired() {
        return cryptoTransactionDao.deleteExpired(1);
    }

}
