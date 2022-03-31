package com.ndb.auction.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BuyNamePrice {

    public BuyNamePrice (int chars, double price) {
        this.numOfChars = chars;
        this.price = price;
    }   
    private int id;
    private int numOfChars;
    private double price;
}
