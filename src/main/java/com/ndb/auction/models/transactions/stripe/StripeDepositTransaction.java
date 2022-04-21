package com.ndb.auction.models.transactions.stripe;

import com.ndb.auction.models.transactions.FiatDepositTransaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StripeDepositTransaction extends FiatDepositTransaction {

    public static final Integer INITIATED = 0;
    public static final Integer AUTHORIZED = 1;
    public static final Integer CAPTURED = 2;
    public static final Integer CANCELED = 3;

    protected String paymentMethodId;
    protected String paymentIntentId;

    private String cryptoType;
    private Double cryptoPrice;
    private Double fee;
    private Double deposited;

    public StripeDepositTransaction(int userId, Double amount, String cryptoType, String paymentIntentId, String paymentMethodId) {
        this.userId = userId;
        this.amount = amount;
        this.cryptoType = cryptoType;
        this.paymentIntentId = paymentIntentId;
        this.paymentMethodId = paymentMethodId;
        this.status = false;
    }

    public StripeDepositTransaction(int userId, Double amount, String cryptoType, Double cryptoPrice, String paymentIntentId, String paymentMethodId, Double fee, Double deposited) {
        this.userId = userId;
        this.amount = amount;
        this.fiatAmount = amount;
        this.fiatType = "USD";
        this.cryptoType = cryptoType;
        this.cryptoPrice = cryptoPrice;
        this.paymentIntentId = paymentIntentId;
        this.paymentMethodId = paymentMethodId;
        this.fee = fee;
        this.deposited = deposited;
        this.status = false;
    }

    public StripeDepositTransaction(int userId, Double amount, String cryptoType) {
        this(userId, amount, cryptoType, null, null);
    }

    public StripeDepositTransaction(int userId, Double amount, String cryptoType, String paymentIntentId) {
        this(userId, amount, cryptoType, paymentIntentId, null);

    }
}
