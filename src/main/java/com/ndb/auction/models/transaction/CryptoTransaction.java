package com.ndb.auction.models.transaction;

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

    public static final int AUCTION = 1;
    public static final int PRESALE = 2;
    
    private int id;

    private Double cryptoAmount;
    private String cryptoType;

    private int status;

    private Long updatedAt;

    private int userId;
    private Integer roundId;

    private int transactionType;
    private Integer presaleOrderId;
    
    private Double amount; // usd value
    private Long createdAt;

    public CryptoTransaction(
        int userId, 
        Integer roundId, 
        Integer presaleId, 
        Double amount, // usd
        String cryptoType,
        int transactionType
    ) {
        this.userId = userId;
        this.roundId = roundId;
        this.presaleOrderId = presaleId;
        this.amount = amount;
        this.cryptoType = cryptoType;
        this.transactionType = transactionType;
        this.cryptoAmount = 0.0;
        this.status = 0;
    }

}
