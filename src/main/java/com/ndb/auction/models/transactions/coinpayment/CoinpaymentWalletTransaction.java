package com.ndb.auction.models.transactions.coinpayment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoinpaymentWalletTransaction extends CoinpaymentTransaction {
    
    public CoinpaymentWalletTransaction(int userId, Long amount, String coin, String network, Double cryptoAmount, String cryptoType) {
        this.userId = userId;
        this.network = network;
        this.cryptoType = cryptoType;
        this.cryptoAmount = cryptoAmount;
        this.coin = coin;
        this.status = false;
        this.depositAddress = "";
    }

}