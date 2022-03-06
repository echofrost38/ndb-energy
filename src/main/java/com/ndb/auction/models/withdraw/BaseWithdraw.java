package com.ndb.auction.models.withdraw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseWithdraw {
    public static final int PENDING = 1;
    public static final int APPROVE = 2;
    public static final int COMPLETED = 3;
    public static final int DENIED = 4;

    // id
    protected int id;
    protected int userId;
    
    // withdraw details
    protected String sourceToken;
    protected double withdrawAmount;

    // pending flag
    // pending  : 1
    // approved : 2
    protected int status; 

    protected String deniedReason;

    protected Long requestedAt;
    protected Long confirmedAt;
}
