package com.ndb.auction.models.transactions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoinpaymentPresaleTransaction extends CoinpaymentTransaction {
    private int presaleId;
    private int orderId;

    public CoinpaymentPresaleTransaction(int userId, int presaleId, int orderId, Long amount, String coin, String network, Double cryptoAmount, String cryptoType) {
        this.userId = userId;
        this.presaleId = presaleId;
        this.orderId = orderId;
        this.network = network;
        this.cryptoType = cryptoType;
        this.cryptoAmount = cryptoAmount;
        this.coin = coin;
        this.status = false;
        this.depositAddress = "";
    }
}
