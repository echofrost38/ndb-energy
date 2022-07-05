package com.ndb.auction.service;

import com.ndb.auction.models.nyyupay.NyyuPayRequest;
import com.ndb.auction.models.wallet.NyyuWallet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NyyuPayService extends BaseService{
    // NyyuPay URL
    private static final String BASE_URL = "https://api.dev.pay.nyyu.io/bep20";

    @Value("${nyyupay.pubKey}")
    private String PUBLIC_KEY;

    @Value("${nyyupay.privKey}")
    private String PRIVATE_KEY;

    private WebClient nyyuPayAPI;
    protected CloseableHttpClient client;
    public NyyuPayService(WebClient.Builder webClientBuilder) {
        client = HttpClients.createDefault();
        this.nyyuPayAPI = webClientBuilder
                .baseUrl(BASE_URL)
                .build();
    }

    // This function need improve ==> async
    public String sendAddressRequest(NyyuWallet nyyuWallet){
        try {
            NyyuPayRequest request = new NyyuPayRequest();
            request.setAddress(nyyuWallet.getPublicKey());
            sendNyyuPayRequest(request).subscribe(
                    i -> {
                        nyyuWallet.setNyyuPayRegistered(true);
                        nyyuWalletDao.insertOrUpdate(nyyuWallet);
                        System.out.println("Nyyu Pay response: " + i);
                    },
                    error -> {
                        nyyuWallet.setNyyuPayRegistered(false);
                        nyyuWalletDao.insertOrUpdate(nyyuWallet);
                        System.out.println("Request to Nyyu pay has error :" + error +" from wallet " + nyyuWallet.getPublicKey()) ;
                    },
                    () -> System.out.println("Done")
            );
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
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
