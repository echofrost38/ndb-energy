package com.ndb.auction.service;

import java.util.List;

import com.google.gson.Gson;
import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.Coin;
import com.ndb.auction.models.CryptoTransaction;
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
    // coin name, symbol, pair
    private List<Coin> coinList;
    private WebClient binanceAPI;
    private WebClient coinbaseAPI;

    public CryptoService(WebClient.Builder webClientBuilder) {
        this.binanceAPI = webClientBuilder
				.baseUrl("https://api.binance.com/api/v3")
				.build();
        this.coinbaseAPI = webClientBuilder
            .baseUrl("https://api.commerce.coinbase.com")
            .build();
    }

    public synchronized void buildCoinCache() {
        clearCoinCache();
        this.coinList = cryptoDao.getCoins();
    }

    public List<Coin> getCoinList() {
        if(this.coinList == null) {
            buildCoinCache();
        }
        return this.coinList;
    }

    private void clearCoinCache() {
        if(this.coinList == null) return;
        this.coinList.clear();
    }

    public Coin addNewCoin(String name, String symbol) {
        Coin coin = new Coin(name, symbol);
        return cryptoDao.addNewCoin(coin);
    }

    public Coin deleteCoin(String name, String symbol) {
        Coin coin = new Coin(name, symbol);
        return cryptoDao.deleteCoin(coin);
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
        if(round == null) {
        	throw new AuctionException("Round doesn't exist.", "roundId");
        }

        CoinbasePostBody data = new CoinbasePostBody(
            "Bid payment",
            "Bid payment for " + userId,
            "fixed_price",
            amount
        );

        // API call for create new charge
        String response = coinbaseAPI.post()
            .uri(uriBuilder -> uriBuilder.path("/charges").build())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("X-CC-Api-Key", coinbaseApiKey)
            .header("X-CC-Version", "2018-03-22")
            .body(Mono.just(data), CoinbasePostBody.class)
            .retrieve()
            .bodyToMono(String.class).block();
        
        CoinbaseRes res = new Gson().fromJson(response, CoinbaseRes.class);
        
        CoinbaseBody resBody = res.getData();
        String txnId = resBody.getId();
        String code = resBody.getCode();
        CryptoTransaction tx = new CryptoTransaction(txnId, roundId, userId, code, amount, "0", null);
        cryptoDao.insert(tx);
        
        CryptoPayload payload = new CryptoPayload(resBody.getAddresses(), resBody.getPricing());
        
        return payload;
    }

    public CryptoTransaction getTransactionByCode(String code) {
        return cryptoDao.selectByCode(code);
    }

    public List<CryptoTransaction> getTransactionByUser(int userId) {
        return cryptoDao.selectByUserId(userId);
    }

    public List<CryptoTransaction> getTransactionByRound(int roundId) {
        return cryptoDao.selectByRoundId(roundId);
    }

    public List<CryptoTransaction> getTransaction(int roundId, int userId) {
        return cryptoDao.getTransaction(roundId, userId);
    }

    public int updateTransaction(String code, int status, String cryptoAmount, String cryptoType) {
        return cryptoDao.updateTransactionStatus(code, status, cryptoAmount, cryptoType);
    }
}
