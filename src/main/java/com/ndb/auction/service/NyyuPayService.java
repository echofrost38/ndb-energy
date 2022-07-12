package com.ndb.auction.service;

import com.ndb.auction.exceptions.ReferralException;
import com.ndb.auction.models.nyyupay.NyyuPayRequest;
import com.ndb.auction.models.wallet.NyyuWallet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NyyuPayService extends BaseService{
    // NyyuPay URL
    @Value("${nyyupay.base}")
    private String BASE_URL;

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
            .baseUrl(BASE_URL)
            .build();
    }

    // This function need improve ==> async
    public String sendAddressRequest(String wallet){
        try {
            NyyuPayRequest request = new NyyuPayRequest();
            request.setAddress(wallet);
            NyyuWallet nyyuWallet = nyyuWalletDao.selectByAddress(wallet);
            var response = sendNyyuPayRequest(request); 
            if(response == null) {
                nyyuWallet.setNyyuPayRegistered(false);
                nyyuWalletDao.insertOrUpdate(nyyuWallet);
                System.out.println("Request to Nyyu pay has error");
                throw new ReferralException("Cannot register wallet address");
            } else {
                nyyuWallet.setNyyuPayRegistered(true);
                nyyuWalletDao.insertOrUpdate(nyyuWallet);
                System.out.println("Nyyu Pay response: " + response);
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new ReferralException("Cannot register wallet address");
        }
        return "sent request";
    }

    //// WebClient
    private String sendNyyuPayRequest(NyyuPayRequest request) {
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
                .bodyToMono(String.class)
                .block();
    }
}
