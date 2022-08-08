package com.ndb.auction.service;

import com.ndb.auction.models.nyyupay.NyyuPayRequest;
import com.ndb.auction.payload.response.NyyuWalletResponse;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NyyuPayService extends BaseService{
    // NyyuPay URL
    @Value("${nyyupay.base}")
    private String NYYU_PAY_BASE;

    @Value("${nyyupay.pubKey}")
    private String PUBLIC_KEY;

    @Value("${nyyupay.privKey}")
    private String PRIVATE_KEY;

    private WebClient nyyuPayAPI;
    protected WebClient.Builder client;
        
    public NyyuPayService(WebClient.Builder webClientBuilder) {
        this.client = webClientBuilder;                
    }

    @PostConstruct
    public void init() {
        this.nyyuPayAPI = this.client
            .baseUrl(NYYU_PAY_BASE)
            .build();
    }

    //// WebClient
    public boolean sendNyyuPayRequest(String network, String walletAddress) {
        long ts = System.currentTimeMillis() / 1000L;
        var request = new NyyuPayRequest(walletAddress);
        String payload = String.valueOf(ts) +"POST"+"{\"address\":\""+request.getAddress()+"\"}";
        String hmac = buildHmacSignature(payload, PRIVATE_KEY);
        var response = nyyuPayAPI.post()
            .uri(uriBuilder -> uriBuilder.path(network).build())
            .header("Content-Type", "application/json; charset=utf-8")
            .header("X-Auth-Token",  hmac)
            .header("X-Auth-Key",  PUBLIC_KEY)
            .header("X-Auth-Ts",  String.valueOf(ts))
            .body(Mono.just(request), NyyuPayRequest.class)
            .retrieve()
            .bodyToMono(NyyuWalletResponse.class)
            .block();
        if(response.getError() == null) return true;
        else return false;
    }
}
