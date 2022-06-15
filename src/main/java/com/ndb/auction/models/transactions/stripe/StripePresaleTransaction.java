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

    public StripePresaleTransaction(int userId, int presaleId, int orderId, Double amount, Double fiatAmount, String fiatType, String intentId, String methodId) {
        if(fiatType == null) {
            fiatType = "USD";
            fiatAmount = amount;
        }
        this.userId = userId;
        this.presaleId = presaleId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentIntentId = intentId;
        this.paymentMethodId = methodId;
        this.status = false;
        this.fiatType = fiatType;
        this.fiatAmount = fiatAmount;
    }

    public StripePresaleTransaction(int userId, int presaleId, int orderId, Double amount, Double fiatAmount, String fiatType, String intentId) {
        this(userId, presaleId, orderId, amount,fiatAmount,fiatType,intentId,null);
    }
}
