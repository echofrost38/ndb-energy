package com.ndb.auction.models.transactions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StripePresaleTransaction extends StripeDepositTransaction {
    private int orderId;
    private int presaleId;

    public StripePresaleTransaction(int userId, int presaleId, int orderId, Long amount, String intentId, String methodId) {
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
}
