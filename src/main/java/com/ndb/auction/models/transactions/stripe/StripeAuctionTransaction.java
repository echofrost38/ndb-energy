package com.ndb.auction.models.transactions.stripe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StripeAuctionTransaction extends StripeDepositTransaction {

    public StripeAuctionTransaction(int userId, int auctionId, Double amount, String paymentIntentId, String paymentMethodId) {
        this.userId = userId;
        this.auctionId = auctionId;
        this.paymentIntentId = paymentIntentId;
        this.paymentMethodId = paymentMethodId;
        this.fiatType = "USD";
        this.fiatAmount = amount;
        this.amount = amount;
        this.bidId = 0;
    }

    public StripeAuctionTransaction(int userId, int auctionId, Double amount) {
       this(userId, auctionId, amount,null,null);
    }

    public StripeAuctionTransaction(int userId, int auctionId, Double amount, String paymentIntentId ) {
     this(userId, auctionId, amount, paymentIntentId, null);
    }

    private int auctionId;
    private int bidId;
}
