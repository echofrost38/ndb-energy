package com.ndb.auction.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FiatAsset {
    private int id;
    private int fiatId;
    private String fiatSymbol;
    private String symbol;
}
