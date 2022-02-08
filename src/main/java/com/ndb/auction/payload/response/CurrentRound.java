package com.ndb.auction.payload.response;

import com.ndb.auction.models.Auction;
import com.ndb.auction.models.presale.PreSale;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CurrentRound {
    // AUCTION, PRESALE and NO
    private String status;
    private Auction auction;
    private PreSale presale;
}
