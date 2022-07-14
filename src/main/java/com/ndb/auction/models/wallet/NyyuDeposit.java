package com.ndb.auction.models.wallet;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class NyyuDeposit {
    protected int id;
    protected int userId;
    protected String walletAddress;
    protected double amount;
    protected String txnHash;
}