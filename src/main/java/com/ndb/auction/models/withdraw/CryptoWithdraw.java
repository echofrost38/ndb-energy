package com.ndb.auction.models.withdraw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CryptoWithdraw extends BaseWithdraw {
    private String network;
    private String destination;

    public CryptoWithdraw (
        int userId, 
        double withdrawAmount,
        double fee,
        String sourceToken, 
        double tokenPrice, 
        double tokenAmount,
        String network,
        String destination
    ) {
        this.userId = userId;
        this.withdrawAmount = withdrawAmount;
        this.fee = fee;
        this.sourceToken = sourceToken;
        this.tokenPrice = tokenPrice;
        this.tokenAmount = tokenAmount;

        this.network = network;
        this.destination = destination;

        this.status = BaseWithdraw.PENDING;
    }
}
