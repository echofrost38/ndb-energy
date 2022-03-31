package com.ndb.auction.models.transactions.coinpayment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoinpaymentAuctionTransaction extends CoinpaymentTransaction {
    
    public CoinpaymentAuctionTransaction(int auctionId, int userId, Double amount, Double fee, String cryptoType, String network, String coin) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.amount = amount;
        this.coin = coin;
        this.status = false;
        this.cryptoType = cryptoType;
        this.cryptoAmount = 0.0;
        this.network = network;
        this.depositAddress = "";
        this.bidId = 0;
        this.fee = fee;
    }
    
    private int auctionId;
    private int bidId;
}
