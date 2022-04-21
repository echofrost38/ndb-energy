package com.ndb.auction.models.transactions.stripe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StripeAuctionTransaction extends StripeDepositTransaction {

    private Double fee;

    public StripeAuctionTransaction(int userId, int auctionId, Double amount, Double fiatAmount, String fiatType, String paymentIntentId, String paymentMethodId) {
        if(fiatType == null) {
            fiatType = "USD";
            fiatAmount = amount;
        }

        this.userId = userId;
        this.auctionId = auctionId;
        this.paymentIntentId = paymentIntentId;
        this.paymentMethodId = paymentMethodId;
        this.amount = amount;
        this.bidId = 0;
        this.fiatAmount = fiatAmount;
        this.fiatType = fiatType;
    }

    public StripeAuctionTransaction(int userId, int auctionId, Double amount, Double fiatAmount, String fiatType, String paymentIntentId ) {
        this(userId, auctionId, amount, fiatAmount, fiatType, paymentIntentId, null);
    }

    private int auctionId;
    private int bidId;
}
