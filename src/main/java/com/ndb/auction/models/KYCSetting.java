package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Verify")
public class KYCSetting {
    
    private String kind;
    private double withdraw;
    private double deposit;
    private double bid;
    private double direct;

    public KYCSetting() {

    }

    public KYCSetting(String kind, double withdraw, double deposit, double bid, double direct){
        this.kind = kind;
        this.withdraw = withdraw;
        this.deposit = deposit;
        this.bid = bid;
        this.direct = direct;
    }

    @DynamoDBHashKey(attributeName = "kind_name")
    public String getKind() {
        return kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }

    @DynamoDBAttribute(attributeName = "withdraw")
    public double getWithdraw() {
        return withdraw;
    }
    public void setWithdraw(double withdraw) {
        this.withdraw = withdraw;
    }

    @DynamoDBAttribute(attributeName = "deposit")
    public double getDeposit() {
        return deposit;
    }
    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    @DynamoDBAttribute(attributeName = "bid")
    public double getBid() {
        return bid;
    }
    public void setBid(double bid) {
        this.bid = bid;
    }

    @DynamoDBAttribute(attributeName = "direct_sale")
    public double getDirect() {
        return direct;
    }
    public void setDirect(double direct) {
        this.direct = direct;
    }

}
