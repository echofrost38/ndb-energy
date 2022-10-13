package com.ndb.auction.models.balance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceChange {
    
    private int id;
    private int userId;
    private int tokenId;
    private double amount;
    private String reason;
    private long updatedAt;

}
