package com.ndb.auction.service;

import com.ndb.auction.models.nyyupay.NyyuPayRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Service
public class NyyuPayService extends BaseService{
    // NyyuPay URL
    @Value("${nyyupay.baseUrl}")
    private String BASE_URL;

    @Value("${nyyupay.pubKey}")
    private String PUBLIC_KEY;

    @Value("${nyyupay.privKey}")
    private String PRIVATE_KEY;

    @Value("${nyyupay.callbackUrl}")
    private String CALLBACK_URL;

    private WebClient nyyuPayAPI;
    protected CloseableHttpClient client;
    public NyyuPayService(WebClient.Builder webClientBuilder) {
        client = HttpClients.createDefault();
        this.nyyuPayAPI = webClientBuilder
                .baseUrl(BASE_URL)
                .build();
    }

    public String sendAddressRequest(String address){
        NyyuPayRequest request = new NyyuPayRequest();
        request.setAddress(address);
        sendNyyuPayRequest(request).subscribe();
        return "sent request";
    }

    //// WebClient
    private Mono<String> sendNyyuPayRequest(NyyuPayRequest request) {
        long ts = System.currentTimeMillis() / 1000L;
        String payload = String.valueOf(ts) +"POST"+"{\"address\":\""+request.getAddress()+"\"}";
        String hmac = buildHmacSignature(payload, PRIVATE_KEY);
        return nyyuPayAPI.post()
                .uri(uriBuilder -> uriBuilder.path("").build())
                .header("Content-Type", "application/json; charset=utf-8")
                .header("X-Auth-Token",  hmac)
                .header("X-Auth-Key",  PUBLIC_KEY)
                .header("X-Auth-Ts",  String.valueOf(ts))
                .body(Mono.just(request), NyyuPayRequest.class)
                .retrieve()
                .bodyToMono(String.class);
    }
}
