package com.ndb.auction.service;

import java.util.UUID;

import javax.annotation.PostConstruct;

import com.ndb.auction.models.DirectSale;
import com.ndb.auction.payload.CryptoPayload;
import com.ndb.auction.payload.PayResponse;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import org.springframework.beans.factory.annotation.Value;

public class DirectSaleService extends BaseService {
    
	@Value("${stripe.secret.key}")
	private String stripeSecretKey;
	
	@Value("${stripe.public.key}")
	private String stripePublicKey;

    @PostConstruct
    public void init() {
    	Stripe.apiKey = stripeSecretKey;
    }

    // create empty direct sale
    public DirectSale createNewDirectSale(String userId, double ndbPrice, double ndbAmount) {
        String txnId = UUID.randomUUID().toString();
        DirectSale directSale = new DirectSale(userId, txnId, ndbPrice, ndbAmount);
        return directSaleDao.createEmptyDirectSale(directSale);
    }

    // stripe pay for Direct Sale
    // public PayResponse stripePayment(String userId, String txnId, Long amount, String paymentItentId, String paymentMethodId) {
    //     PaymentIntent intent;
    //     PayResponse response = new PayResponse();
    //     PaymentIntentCreateParams params =
    //     PaymentIntentCreateParams.builder()
    //       .setAmount(new Long(calculateOrderAmount(postBody.getItems())))
    //       .setCurrency("eur")
    //       .setAutomaticPaymentMethods(
    //         PaymentIntentCreateParams.AutomaticPaymentMethods
    //           .builder()
    //           .setEnabled(true)
    //           .build()
    //       )
    //       .build();

    //     // Create a PaymentIntent with the order amount and currency
    //     PaymentIntent paymentIntent = PaymentIntent.create(params);
    //     response.setClientSecret(paymentIntent.getClientSecret());
    //     CreatePaymentResponse paymentResponse = new CreatePaymentResponse(paymentIntent.getClientSecret());

    // }

    // public CryptoPayload cryptoPayment(String userId, String txnId, double amount) {

    // }

}
