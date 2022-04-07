package com.ndb.auction.models.withdraw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BankWithdrawRequest extends BaseWithdraw {
    private int mode; // international/domestic
    private String country; // 2 letter code / null
    private String nameOfHolder;
    private String bankName;
    private String accountNumber;
    
    // json string
    private String metadata;
}
