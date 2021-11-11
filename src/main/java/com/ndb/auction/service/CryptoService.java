package com.ndb.auction.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, String> coinList;
    private WebClient binanceAPI;
    private WebClient coinbaseAPI;

    public CryptoService(WebClient.Builder webClientBuilder) {
        coinList = new HashMap<String, String>();
        this.binanceAPI = webClientBuilder
				.baseUrl("https://api.binance.com/api/v3")
				.build();
        this.coinbaseAPI = webClientBuilder
            .baseUrl("https://api.commerce.coinbase.com")
            .build();
    }

    public synchronized void buildCoinCache() {
        clearCoinCache();
        List<Coin> list = cryptoDao.getCoins();
        for (Coin coin : list) {
            coinList.put(coin.getSymbol(), coin.getName());
        }
    }

    public Map<String, String> getCoinList() {
        return this.coinList;
    }

    private void clearCoinCache() {
        this.coinList.clear();
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
