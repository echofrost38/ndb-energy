package com.ndb.auction.models.transactions.paypal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaypalAuctionTransaction extends PaypalDepositTransaction {
    private int auctionId;
    private int bidId;

    public PaypalAuctionTransaction (
        int userId, 
        int auctionId, 
        Long amount,
        String paypalOrderid,
        String payaplOrderStatus
    ) {
        this.userId = userId;
        this.auctionId = auctionId;
        this.amount = amount;
        this.fiatAmount = amount;
        this.fiatType = "USD";
        this.paypalOrderId = paypalOrderid;
        this.paypalOrderStatus = payaplOrderStatus;
    }
}
