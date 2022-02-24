package com.ndb.auction.models.transactions.coinpayment;

import com.ndb.auction.models.transactions.CryptoDepositTransaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoinpaymentTransaction extends CryptoDepositTransaction {
    protected String coin;
}
