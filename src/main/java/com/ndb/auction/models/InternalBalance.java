package com.ndb.auction.models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class InternalBalance {
    
    private int userId;
    private int tokenId;
    private double free;
    private double hold; 

    public InternalBalance(int userId, int tokenId) {
        this.userId = userId;
        this.tokenId = tokenId;
        this.free = 0.0;
        this.hold = 0.0;
    }

}
