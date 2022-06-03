package com.ndb.auction.models.withdraw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseWithdraw {
    public static final int PENDING = 0;
    public static final int APPROVE = 1;
    public static final int DENIED = 2;

    // id
    protected int id;
    protected int userId;
    protected String email;
    
    // withdraw details
    protected String targetCurrency;
    protected double withdrawAmount; // usd
    protected double fee; // usd

    protected String sourceToken;
    protected double tokenPrice;
    protected double tokenAmount; // requested crypto amount to withdraw

    // pending flag
    // pending  : 1
    // approved : 2
    protected int status; 
    protected boolean isShow;

    protected String deniedReason;

    protected Long requestedAt;
    protected Long confirmedAt;
}
