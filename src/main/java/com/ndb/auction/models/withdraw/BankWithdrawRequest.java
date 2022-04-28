package com.ndb.auction.models.withdraw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BankWithdrawRequest extends BaseWithdraw {
    
    public BankWithdrawRequest(
        int userId,
        String tarCurrency,
        double withdrawAmount,
        double fee,
        String sourceToken,
        double tokenPrice,
        double tokenAmount,
        int mode,
        String country,
        String nameOfHolder,
        String bankName,
        String accNumber,
        String metadata, 
        String address, 
        String postCode
    ) {
        this.userId = userId;
        this.targetCurrency = tarCurrency;
        this.withdrawAmount = withdrawAmount;
        this.fee = fee;
        this.sourceToken = sourceToken;
        this.tokenPrice = tokenPrice;
        this.tokenAmount = tokenAmount;
        this.mode = mode;
        this.country = country;
        this.nameOfHolder = nameOfHolder;
        this.bankName = bankName;
        this.accountNumber = accNumber;
        this.metadata = metadata;
        this.address = address;
        this.postCode = postCode;
        this.status = 0;
    }
    
    private int mode; // international/domestic
    private String country; // 2 letter code / null
    private String nameOfHolder;
    private String bankName;
    private String accountNumber;
    
    // json string
    private String metadata;

    // string
    private String address;
    private String postCode;
}
