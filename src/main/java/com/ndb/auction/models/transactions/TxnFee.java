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

    public TxnFee(int tierLevel, double fee) {
        this.fee = fee;
        this.tierLevel = tierLevel;
    }

    private int id;
    private int tierLevel;
    private Double fee;
}
