package com.ndb.auction.models;

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
public class TokenAsset {
    
    private int id;
    private String tokenName;
    private String tokenSymbol;
    private String network;
    private String address;
    private String symbol;

    public TokenAsset(String tokenName, String tokenSymbol, String network, String address, String symbol) {
        this.tokenName = tokenName;
        this.tokenSymbol = tokenSymbol;
        this.network = network;
        this.address = address;
        this.symbol = symbol;
    }

}
