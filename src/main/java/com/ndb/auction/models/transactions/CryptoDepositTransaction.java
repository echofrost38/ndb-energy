package com.ndb.auction.models.transactions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CryptoDepositTransaction extends CryptoTransaction {
    protected String depositAddress;
}
