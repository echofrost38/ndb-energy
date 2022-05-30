package com.ndb.auction.models.transactions.coinpayment;

import com.ndb.auction.models.transactions.CryptoDepositTransaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoinpaymentTransaction extends CryptoDepositTransaction {
    protected String coin;
}
