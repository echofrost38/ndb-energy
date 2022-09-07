package com.ndb.auction.models.transactions.wallet;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class NyyuWalletTransaction {
    private int id; // transaction id
    private int userId; // user id
    private int orderId; // presale or bid id
    private int orderType; // 1 - presale, 2 - bid
    private String assetType; // coin type
    private double amount; // amount to pay in crypto
    private double usdAmount; // current price * amount
    private long createdAt;
}
