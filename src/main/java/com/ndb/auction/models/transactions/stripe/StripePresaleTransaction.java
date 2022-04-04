package com.ndb.auction.models.transactions.stripe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StripePresaleTransaction extends StripeDepositTransaction {
    private int orderId;
    private int presaleId;
    private Double fee;

    public StripePresaleTransaction(int userId, int presaleId, int orderId, Double amount, String intentId, String methodId) {
        this.userId = userId;
        this.presaleId = presaleId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentIntentId = intentId;
        this.paymentMethodId = methodId;
        this.status = false;
        this.fiatAmount = amount;
        this.fiatType = "USD";
    }

    public StripePresaleTransaction(int userId, int presaleId, int orderId, Double amount, String intentId) {
        this(userId, presaleId, orderId, amount,intentId,null);
    }
}
