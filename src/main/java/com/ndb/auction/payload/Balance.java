package com.ndb.auction.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Balance {
    
    private String tokenName;
    private String tokenSymbol;
    private String symbol;
    private Double free;
    private Double hold;
    
}
