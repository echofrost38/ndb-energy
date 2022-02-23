package com.ndb.auction.models.transactions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CryptoTransaction extends Transaction {

    public static final int INITIATED = 0;
    public static final int CONFIRMED = 1;
    public static final int CANCELED = 2;

    protected String cryptoType;
    protected String network;
    protected Double cryptoAmount;
}
