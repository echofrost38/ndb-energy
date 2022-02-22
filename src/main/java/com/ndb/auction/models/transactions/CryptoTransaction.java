package com.ndb.auction.models.transactions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CryptoTransaction extends Transaction {
    protected String cryptoType;
    protected String network;
    protected Double cryptoAmount;
    protected Long confirmedAt;
}
