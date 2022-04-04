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
        Double amount,
        Double fee,
        String paypalOrderid,
        String payaplOrderStatus
    ) {
        this.userId = userId;
        this.auctionId = auctionId;
        this.amount = amount;
        this.fiatAmount = amount;
        this.fee = fee;
        this.fiatType = "USD";
        this.paypalOrderId = paypalOrderid;
        this.paypalOrderStatus = payaplOrderStatus;
    }
}
