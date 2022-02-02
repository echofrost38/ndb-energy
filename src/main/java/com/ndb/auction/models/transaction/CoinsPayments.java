package com.ndb.auction.models.transaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoinsPayments extends BaseTransaction{
    private int id;
    private String cryptoType;
    private Double cryptoAmount;
    private String network;
    private int status;
    private Long updatedAt;
}
