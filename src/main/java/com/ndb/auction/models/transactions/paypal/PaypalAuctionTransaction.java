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
        double fiatAmount,
        String fiatType,
        Double amount, // usd amount
        Double fee, // usd fee
        String paypalOrderid,
        String payaplOrderStatus
    ) {
        this.userId = userId;
        this.auctionId = auctionId;
        this.amount = amount;
        this.fee = fee;
        this.fiatAmount = fiatAmount;
        this.fiatType = fiatType;
        this.paypalOrderId = paypalOrderid;
        this.paypalOrderStatus = payaplOrderStatus;
    }
}
