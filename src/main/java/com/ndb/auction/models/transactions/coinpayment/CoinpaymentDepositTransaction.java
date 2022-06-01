package com.ndb.auction.models.transactions.coinpayment;

import com.ndb.auction.models.transactions.CryptoDepositTransaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoinpaymentDepositTransaction extends CryptoDepositTransaction {
    public static final int PENDING = 0;
    public static final int CONFIRMED = 1;
    public static final int EXPIRED = 2;

    protected String coin;
    protected String orderType;
    protected int orderId;
    protected int depositStatus;
    protected String txHash;

    public CoinpaymentDepositTransaction(int orderId, int userId, double amount, double fee, String orderType, String cryptoType, String network, String coin) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderType = orderType;
        this.amount = amount;
        this.fee = fee;
        this.cryptoType = cryptoType;
        this.network = network;
        this.coin = coin;
        this.depositAddress = null;
        this.depositStatus = 0; // pending
    }
}
