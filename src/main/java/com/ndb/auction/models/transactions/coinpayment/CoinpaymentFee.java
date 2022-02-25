package com.ndb.auction.models.transactions.coinpayment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoinpaymentFee {
    private int id;
    private int tierLevel;
    private Double fee;
}
