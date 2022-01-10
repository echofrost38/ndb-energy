package com.ndb.auction.service;

import java.util.UUID;

import javax.annotation.PostConstruct;

import com.google.gson.Gson;
import com.ndb.auction.models.DirectSale;
import com.ndb.auction.models.coinbase.CoinbaseBody;
import com.ndb.auction.models.coinbase.CoinbasePostBody;
import com.ndb.auction.models.coinbase.CoinbaseRes;
import com.ndb.auction.payload.CryptoPayload;
import com.ndb.auction.payload.PayResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class DirectSaleService extends BaseService {
    
    @Value("${stripe.secret.key}")
	private String stripeSecretKey;

	@Value("${stripe.public.key}")
	private String stripePublicKey;

    private WebClient coinbaseAPI;

    public DirectSaleService(WebClient.Builder webClientBuilder) {
        this.coinbaseAPI = webClientBuilder
            .baseUrl("https://api.commerce.coinbase.com")
            .build();
    }

    @PostConstruct
    public void init() {
    	Stripe.apiKey = stripeSecretKey;
    }

    // create empty direct sale
    public DirectSale createNewDirectSale(int userId, long ndbPrice, long ndbAmount, int whereTo, String extAddr) {
        String txnId = UUID.randomUUID().toString();
        DirectSale directSale = new DirectSale(userId, txnId, ndbPrice, ndbAmount, whereTo, extAddr);
        return directSaleDao.createEmptyDirectSale(directSale);
    }

    // stripe pay for Direct Sale
    public PayResponse stripePayment(int userId, String txnId) throws StripeException {
        // get Direct Sale object
        DirectSale directSale = directSaleDao.getDirectSale(userId, txnId);
        long amount = directSale.getNdbPrice() * directSale.getNdbAmount();
        Long lAmount = Double.valueOf(amount * 100).longValue();
        PayResponse response = new PayResponse();
        PaymentIntentCreateParams params =
        PaymentIntentCreateParams.builder()
          .setAmount(lAmount)
          .setCurrency("usd")
          .setAutomaticPaymentMethods(
            PaymentIntentCreateParams.AutomaticPaymentMethods
              .builder()
              .setEnabled(true)
              .build()
          )
          .build();

        // Create a PaymentIntent with the order amount and currency
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        directSale.setPaymentIntentId(paymentIntent.getId());
        directSaleDao.updateDirectSale(directSale);

        response.setClientSecret(paymentIntent.getClientSecret());
        return response;
    }

    public CryptoPayload cryptoPayment(int userId, String txnId) {
        
        DirectSale directSale = directSaleDao.getDirectSale(userId, txnId);
        if(directSale == null) {
            return null;
        }

        long amount = directSale.getNdbAmount() * directSale.getNdbPrice();
        /// Amount means the total USD price for NDB Token
        CoinbasePostBody data = new CoinbasePostBody(
            "Direct Sale",
            "Direct Sale for " + userId,
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
        String code = resBody.getCode();
        directSale.setCode(code);
        directSaleDao.updateDirectSale(directSale);
        return new CryptoPayload(resBody.getAddresses(), resBody.getPricing());
    }

    public DirectSale getDirectSaleByPayment(String paymentId) {
        return directSaleDao.getDirectSaleByIntent(paymentId);
    }

    public DirectSale getDirectSaleByCode(String code) {
        return directSaleDao.getDirectSaleByCode(code);
    }

    public DirectSale updateDirectSale(DirectSale directSale) {
        return directSaleDao.updateDirectSale(directSale);
    }

}
