package com.ndb.auction.models.transactions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoinpaymentAuctionTransaction extends CoinpaymentTransaction {
    private int auctionId;
    private int bidId;
}
