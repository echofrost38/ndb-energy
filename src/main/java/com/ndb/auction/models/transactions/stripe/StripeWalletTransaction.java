package com.ndb.auction.models.transactions.stripe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StripeWalletTransaction extends StripeDepositTransaction {
    
    public StripeWalletTransaction(int userId, Long amount, String intentId, String methodId) {
        this.userId = userId;
        this.amount = amount;
        this.paymentIntentId = intentId;
        this.paymentMethodId = methodId;
        this.status = false;
        this.fiatAmount = amount;
        this.fiatType = "USD";
    }

    public StripeWalletTransaction(int userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
        this.paymentIntentId = null;
        this.paymentMethodId = null;
        this.status = false;
        this.fiatAmount = amount;
        this.fiatType = "USD";
    }

}
