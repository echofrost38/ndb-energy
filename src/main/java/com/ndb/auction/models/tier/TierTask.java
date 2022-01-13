package com.ndb.auction.models.tier;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DynamoDBTable(tableName = "TierTasks")
public class TierTask {

    public TierTask(String userId) {
        this.userId = userId;
        this.verification = false;
        this.wallet = 0.0;
        this.auctions = new ArrayList<Integer>();
        this.direct = 0.0;
        this.staking = new ArrayList<StakeHist>();
    }

    @DynamoDBHashKey(attributeName="user_id")
    private String userId;
    
    @DynamoDBAttribute(attributeName="verification")
    private Boolean verification;
    
    @DynamoDBAttribute(attributeName="wallet")
    private double wallet;
    
    @DynamoDBAttribute(attributeName="auctions")
    private List<Integer> auctions;
   
    @DynamoDBAttribute(attributeName="direct")
    private double direct;
    
    @DynamoDBAttribute(attributeName="staking")
    private List<StakeHist> staking;
}
