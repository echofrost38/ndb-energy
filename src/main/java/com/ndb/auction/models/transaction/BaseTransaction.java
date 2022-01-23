package com.ndb.auction.models.transaction;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class BaseTransaction {
    
    public static final int AUCTION = 1;
    public static final int PRESALE = 2;

    protected int userId;
    protected int roundId;

    protected int transactionType;
    protected int presaleId;
    
    protected Double amount; // usd value
    protected Long createdAt;

}
