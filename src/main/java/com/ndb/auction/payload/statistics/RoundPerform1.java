package com.ndb.auction.payload.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoundPerform1 {
    
    private int roundNumber;
    private double tokenPrice;
    private double soldAmount;

    public RoundPerform1 (int number, double tokenPrice, double soldAmount) {
        this.roundNumber = number;
        this.tokenPrice = tokenPrice;
        this.soldAmount = soldAmount;
    }
}
