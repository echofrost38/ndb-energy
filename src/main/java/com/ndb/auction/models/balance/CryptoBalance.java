package com.ndb.auction.models.balance;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class CryptoBalance extends BaseBalance {
    
    private int tokenId;
    public CryptoBalance(int userId, int tokenId) {
        this.userId = userId;
        this.tokenId = tokenId;
        this.free = 0.0;
        this.hold = 0.0;
    }

}
