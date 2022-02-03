package com.ndb.auction.models.transaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoinsPayments extends BaseTransaction{
    private int id;
    private String cryptoType;
    private Double cryptoAmount;
    private String network;
    private int status;
    private Long updatedAt;
    
    public static final int AUCTION = 1;
    public static final int PRESALE = 2;

    protected int userId;
    protected Integer roundId;

    protected int transactionType;
    protected Integer presaleOrderId;
    
    protected Double amount; // usd value
    protected Long createdAt;
}
