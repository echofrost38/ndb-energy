package com.ndb.auction.service;

import com.ndb.auction.models.coinbase.CoinbaseBody;
import com.ndb.auction.models.coinbase.CoinbasePostBody;
import com.ndb.auction.models.coinbase.CoinbaseRes;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.transaction.CryptoTransaction;
import com.ndb.auction.payload.CryptoPayload;

import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class PresaleOrderService extends BaseService {
    
    public PresaleOrderService(WebClient.Builder webClientBuilder) {
        this.coinbaseAPI = webClientBuilder
                .baseUrl("https://api.commerce.coinbase.com")
                .build();
    }
    
    // create new presale order
    public CryptoPayload createNewPresaleOrder(PreSaleOrder order) {

        // insert dataabase
        order = presaleOrderDao.insert(order);

        Long _amount = order.getNdbAmount() * order.getNdbPrice();
        String amount = _amount.toString();
        
        CoinbasePostBody data = new CoinbasePostBody(
                "Bid payment",
                "Bid payment for " + order.getUserId(),
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

        CryptoTransaction tx = new CryptoTransaction(order.getUserId(), null, order.getId(), txnId, code, Double.valueOf(amount), CryptoTransaction.PRESALE);
        cryptoTransactionDao.insert(tx);

        CryptoPayload payload = new CryptoPayload(resBody.getAddresses(), resBody.getPricing());
        return payload;
    }

    public PreSaleOrder getPresaleById(int orderId) {
        return presaleOrderDao.selectById(orderId);
    }

    public int updateStatus(int orderId) {
        return presaleOrderDao.updateStatus(orderId);
    }

}
