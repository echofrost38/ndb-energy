package com.ndb.auction.models.transaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepositTransaction {
    
    public DepositTransaction(int userId, String cryptoType) {
        this.userId = userId;
        this.cryptoType = cryptoType;
        this.status = 0;
        this.amount = 0.0;
        this.cryptoAmount = 0.0;
    }

    private int id;
    private int userId;
    private Double amount; // USD?
    private Double cryptoAmount;
    private String cryptoType;

    private int status;
    private Long createdAt;
    private Long updatedAt;
}
