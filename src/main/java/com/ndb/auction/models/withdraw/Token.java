package com.ndb.auction.models.withdraw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {
    private long id;
    private String tokenName;
    private String tokenSymbol;
    private String network;
    private String address;
    private boolean withdrawable;
}
