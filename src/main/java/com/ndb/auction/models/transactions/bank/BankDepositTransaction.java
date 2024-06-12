package com.ndb.auction.models.transactions.bank;

import com.ndb.auction.models.transactions.FiatDepositTransaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BankDepositTransaction extends FiatDepositTransaction{
    
    // unique identifier 9 digits 
    private String uid;
    private int bankDetailId;

    private String cryptoType;
    private double cryptoPrice;

    private double usdAmount;

    private double fee;
    private double deposited;

    public BankDepositTransaction (
        int userId,
        long amount,
        String uid,
        int bankDetailId,
        String cryptoType,
        double cryptoPrice,
        String fiatType,
        double usdAmount,
        double fee, 
        double deposited
    ) {
        this.userId = userId;
        this.uid = uid;
        this.bankDetailId = bankDetailId;
        this.amount = amount;
        this.cryptoPrice = cryptoPrice;
        this.cryptoType = cryptoType;
        this.fiatType = fiatType;
        this.usdAmount = usdAmount; // based on USD
        this.fee = fee;
        this.deposited = deposited;
    }


}
