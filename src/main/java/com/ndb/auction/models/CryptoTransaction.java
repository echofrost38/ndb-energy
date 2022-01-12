package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class CryptoTransaction {
    public static final int INITIATED = 0;
    public static final int CONFIRMED = 1;
    public static final int CANCELED = 2;

    private String txnId;
    private int roundId;
    private int userId;
    private String code;

    private String amount; // usd
    private String cryptoAmount;
    private String cryptoType;

    private int status;

    private String createdAt;
    private String updatedAt;

    public CryptoTransaction(String txnId, int roundId, int userId, String code, String amount, String cryptoAmount,
            String cryptoType, String createdAt) {
        this.code = code;
        this.txnId = txnId;
        this.roundId = roundId;
        this.userId = userId;
        this.amount = amount;
        this.cryptoAmount = cryptoAmount;
        this.cryptoType = cryptoType;
        this.createdAt = createdAt;
        this.status = INITIATED;
    }

}
