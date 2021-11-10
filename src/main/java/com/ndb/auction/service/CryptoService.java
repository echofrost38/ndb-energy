package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.models.CryptoTransaction;
import com.ndb.auction.models.coinbase.CoinbaseBody;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;



@Service
public class CryptoService extends BaseService {
    

    public CryptoTransaction createNewPayment(String roundId, String userId, double amount) {
        
        // API call for create new charge
        CoinbaseBody resBody = WebClient
            .create()
            .post()
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

    public CryptoTransaction getTransaction(String roundId, String userId) {
        return cryptoDao.getTransaction(roundId, userId);
    }

    public CryptoTransaction updateTransaction(CryptoTransaction tx) {
        cryptoDao.updateCryptoTransaction(tx);
        return tx;
    }
}
