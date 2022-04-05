package com.ndb.auction.models.withdraw;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseBankWithdraw extends BaseWithdraw {
    private String nameOfHolder;
    private String bankName;
    private String accountNumber;
    private Map<String, String> metadata;
}
