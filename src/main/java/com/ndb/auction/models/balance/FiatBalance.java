package com.ndb.auction.models.balance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiatBalance extends BaseBalance {
    int fiatId;
    public FiatBalance(int userId, int fiatId) {
        this.userId = userId;
        this.fiatId = fiatId;
        this.free = 0.0;
        this.hold = 0.0;
    }
}
