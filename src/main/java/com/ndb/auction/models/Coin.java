package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Coins")
public class Coin {
    
    // full name: Bitcoin
    private String name;

    // short name: BTC
    private String symbol;
    
    public Coin(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    @DynamoDBAttribute(attributeName = "coin_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "coin_symbol")
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
