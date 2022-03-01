package com.ndb.auction.models.transactions.stripe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StripeAuctionTransaction extends StripeDepositTransaction {
    
    public StripeAuctionTransaction(
        int userId,
        int auctionId,
        Long amount,
        String paymentIntentId,
        String paymentMethodId,
        boolean isSaveCard
    ) {
        this.userId = userId;
        this.auctionId = auctionId;
        this.paymentIntentId = paymentIntentId;
        this.paymentMethodId = paymentMethodId;
        this.fiatType = "USD";
        this.fiatAmount = amount;
        this.amount = amount;
        this.bidId = 0;
        this.isSaveCard = isSaveCard;
    }
    
    private int auctionId;
    private int bidId;
}
