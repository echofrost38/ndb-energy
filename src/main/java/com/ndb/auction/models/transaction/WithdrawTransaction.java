package com.ndb.auction.models.transaction;

import com.ndb.auction.models.BaseModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WithdrawTransaction extends BaseModel {
    
    public WithdrawTransaction(int userId, String transactionHash) {
        this.userId = userId;
        this.transactionHash = transactionHash;
    }
    
    private int userId;
    private String transactionHash;
    private String from;
    private String to;
    private Double value;
    private String blockNumber;
    private Boolean status;
}
