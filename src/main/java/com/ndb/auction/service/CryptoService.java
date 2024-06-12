package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.models.Coin;
import com.ndb.auction.models.CryptoTransaction;
import com.ndb.auction.models.coinbase.CoinbaseBody;
import com.ndb.auction.payload.CoinPrice;

import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;



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
        buildCoinCache();
    }

    public synchronized void buildCoinCache() {
        clearCoinCache();
        this.coinList = cryptoDao.getCoins();
    }

    public List<Coin> getCoinList() {
        return this.coinList;
    }

    private void clearCoinCache() {
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

    public double getCryptoPriceBySymbol(String symbol) {
        String symbolPair = symbol + "USDT";
        CoinPrice objs = binanceAPI.get()
				.uri(uriBuilder -> uriBuilder.path("/ticker/price")
                    .queryParam("symbol", symbolPair.toUpperCase())
                    .build())
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
		        .retrieve()
		        .bodyToMono(CoinPrice.class).block();
        return Double.valueOf(objs.getPrice());
    }

    public CryptoTransaction createNewPayment(String roundId, String userId, double amount) {
        
        // API call for create new charge
        CoinbaseBody resBody = coinbaseAPI.post()
            .uri(uriBuilder -> uriBuilder.path("/charges").build())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("X-CC-Api-Key", coinbaseApiKey)
            .header("X-CC-Version", "2018-03-22")
            .retrieve()
            .bodyToMono(CoinbaseBody.class).block();

        String txnId = resBody.getId();
        String code = resBody.getCode();
        String createdAt = resBody.getCreated_at();
        CryptoTransaction tx = new CryptoTransaction(txnId, roundId, userId, code, amount, 0.0, null, createdAt);
        cryptoDao.createNewPayment(tx);

        return tx;
    }

    public CryptoTransaction getTransactionById(String id) {
        return cryptoDao.getTransactionById(id);
    }

    public List<CryptoTransaction> getTransactionByUser(String userId) {
        return cryptoDao.getTransactionByUser(userId);
    }

    public List<CryptoTransaction> getTransactionByRound(String roundId) {
        return cryptoDao.getTransactionByRound(roundId);
    }

    public List<CryptoTransaction> getTransaction(String roundId, String userId) {
        return cryptoDao.getTransaction(roundId, userId);
    }

    public CryptoTransaction updateTransaction(CryptoTransaction tx) {
        cryptoDao.updateCryptoTransaction(tx);
        return tx;
    }
}
