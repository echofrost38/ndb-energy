package com.ndb.auction.models.transaction;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class CryptoTransaction extends BaseTransaction {
    
    public static final int INITIATED = 0;
    public static final int CONFIRMED = 1;
    public static final int CANCELED = 2;

    private String txnId;
    private String code;

    private String cryptoAmount;
    private String cryptoType;

    private int status;

    private Long updatedAt;

    public CryptoTransaction(int userId, int roundId, String txnId, String code, Double amount, int transactionType) {
        this.userId = userId;
        this.roundId = roundId;
        this.txnId = txnId;
        this.code = code;
        this.amount = amount;
        this.transactionType = transactionType;
        this.status = 0;
    }

}
