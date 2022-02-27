package com.ndb.auction.models.transactions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TxnFee {
    private int id;
    private int tierLevel;
    private Double fee;
}
