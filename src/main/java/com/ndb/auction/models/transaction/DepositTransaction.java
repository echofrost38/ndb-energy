package com.ndb.auction.models.transaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepositTransaction {
    
    public DepositTransaction(int userId, String txnId, String code) {
        this.userId = userId;
        this.txnId = txnId;
        this.code = code;
    }
    private int userId;
    private String txnId;
    private String code;
    private Double amount;
    private Double cryptoAmount;
    private String cryptoType;

    private int status;
    private Long createdAt;
    private Long updatedAt;
}
