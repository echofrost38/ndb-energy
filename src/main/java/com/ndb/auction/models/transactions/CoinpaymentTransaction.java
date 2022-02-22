package com.ndb.auction.models.transactions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoinpaymentTransaction extends CryptoDepositTransaction {
    protected String coin;
}
