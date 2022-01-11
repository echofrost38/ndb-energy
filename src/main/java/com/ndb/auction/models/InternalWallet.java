package com.ndb.auction.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Storing deposit addresses and Favourite tokens
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InternalWallet {
    private int userId;
    private String bitcoin;
    private String erc20;
    private String bep20;
    private String favouriteTokens;
}
