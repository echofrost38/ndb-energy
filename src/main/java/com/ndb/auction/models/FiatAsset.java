package com.ndb.auction.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FiatAsset {
    private int id;
    private String name; // EUR
    private String symbol; 
}
