package com.ndb.auction.service.payment;

import java.util.List;

import com.ndb.auction.models.coinbase.CoinbaseBody;
import com.ndb.auction.models.coinbase.CoinbasePostBody;
import com.ndb.auction.models.coinbase.CoinbaseRes;
import com.ndb.auction.models.transaction.DepositTransaction;
import com.ndb.auction.payload.CryptoPayload;
import com.ndb.auction.service.BaseService;

import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class DepositService extends BaseService {

    public DepositService(WebClient.Builder webClientBuilder) {
        this.coinbaseAPI = webClientBuilder
                .baseUrl("https://api.commerce.coinbase.com")
                .build();
    }

    // make new deposit charge
    public CryptoPayload createNewCharge(int userId) {
        CoinbasePostBody postBody = new CoinbasePostBody(
            "DEPOSIT",
            "Deposit for " + userId,
            "no_price",
            ""
        );

        // API call for create new charge
        String response = coinbaseAPI.post()
                .uri(uriBuilder -> uriBuilder.path("/charges").build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-CC-Api-Key", coinbaseApiKey)
                .header("X-CC-Version", "2018-03-22")
                .body(Mono.just(postBody), CoinbasePostBody.class)
                .retrieve()
                .bodyToMono(String.class).block();

        CoinbaseRes res = gson.fromJson(response, CoinbaseRes.class);

        CoinbaseBody resBody = res.getData();
        String txnId = resBody.getId();
        String code = resBody.getCode();

        DepositTransaction tx = new DepositTransaction(userId, txnId, code);
        if(depositTxnDao.insert(tx) == 1) {
            CryptoPayload payload = new CryptoPayload(resBody.getAddresses(), null);
            return payload;
        }

        return null;
    }

    public List<DepositTransaction> getDepositTxnByUserId(int userId) {
        return depositTxnDao.selectByUser(userId);
    }

}
