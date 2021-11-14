package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Coin")
public class Coin {
    
    // full name: Bitcoin
    private String name;

    // short name: BTC
    private String symbol;

    public Coin () {
        
    }
    
    public Coin(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    @DynamoDBHashKey(attributeName = "coin_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBRangeKey(attributeName = "coin_symbol")
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
