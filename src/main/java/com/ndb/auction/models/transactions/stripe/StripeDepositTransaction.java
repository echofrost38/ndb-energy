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

    public StripeDepositTransaction(int userId, Double usdAmount, Double fiatAmount, String fiatType, String cryptoType, String paymentIntentId, String paymentMethodId) {
        if (fiatType == null) {
            fiatType = "USD";
            fiatAmount = usdAmount;
        }

        this.userId = userId;
        this.amount = usdAmount;
        this.cryptoType = cryptoType;
        this.paymentIntentId = paymentIntentId;
        this.paymentMethodId = paymentMethodId;
        this.status = false;
        this.fiatType = fiatType;
        this.fiatAmount = fiatAmount;
    }

    public StripeDepositTransaction(int userId, Double usdAmount, Double fiatAmount, String fiatType, String cryptoType, Double cryptoPrice, String paymentIntentId, String paymentMethodId, Double fee, Double deposited) {
        this.userId = userId;
        this.amount = usdAmount;
        this.fiatAmount = fiatAmount;
        this.fiatType = fiatType;
        this.cryptoType = cryptoType;
        this.cryptoPrice = cryptoPrice;
        this.paymentIntentId = paymentIntentId;
        this.paymentMethodId = paymentMethodId;
        this.fee = fee;
        this.deposited = deposited;
        this.status = false;
    }

    public StripeDepositTransaction(int userId, Double amount, String cryptoType) {
        this(userId, amount, amount,"USD", cryptoType, null, null);
    }

    public StripeDepositTransaction(int userId, Double amount, Double fiatAmount, String fiatType, String cryptoType, String paymentIntentId) {
        this(userId, amount, fiatAmount, fiatType, cryptoType, paymentIntentId, null);

    }
}
