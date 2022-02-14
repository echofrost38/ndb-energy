package com.ndb.auction.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FiatAsset {

    public FiatAsset(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    private int id;
    private String name; // EUR
    private String symbol; 
}
