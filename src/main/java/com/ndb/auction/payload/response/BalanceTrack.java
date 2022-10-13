package com.ndb.auction.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class tracks all transactions(deposit and withdraw) into(from) wallet
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceTrack {
    public static final int DEPOSIT = 1;
    public static final int PURCHASE = 2;
    public static final int WITHDRAW = 3;
    
    // identifier
    private int id; 
    private int userId;

    // transaction type
    private int txnType; // 1 - deposit, 2 - purchase, 3 - withdrawal
    private String gatewayType; // For purchase, BID or PRESALE
    private int txnId; // deposit transaction id, bid/presale id, withdrawal id

    // details
    private double sourceAmount;
    private String sourceType;
    private double usdAmount;

    // date time
    private long createdAt;
    private long updatedAt;

}
