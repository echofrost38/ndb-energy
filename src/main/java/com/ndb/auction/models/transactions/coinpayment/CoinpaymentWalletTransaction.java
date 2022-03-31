package com.ndb.auction.models.transactions.coinpayment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoinpaymentWalletTransaction extends CoinpaymentTransaction {
    
    public CoinpaymentWalletTransaction(int userId, Double amount, Double fee, String coin, String network, Double cryptoAmount, String cryptoType) {
        this.userId = userId;
        this.network = network;
        this.amount = 0.0;
        this.cryptoType = cryptoType;
        this.cryptoAmount = cryptoAmount;
        this.coin = coin;
        this.status = false;
        this.depositAddress = "";
        this.fee = fee;
    }

}
