package com.ndb.auction.models.transactions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CryptoTransaction extends Transaction {
    private String cryptoType;
    private String network;
    private Double cryptoAmount;
    private Long confirmedAt;
}
