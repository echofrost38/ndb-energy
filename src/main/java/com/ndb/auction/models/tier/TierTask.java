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

    public TierTask(int userId) {
        this.userId = userId;
        this.verification = false;
        this.auctions = new ArrayList<>();
        this.staking = new ArrayList<>();
    }

    @DynamoDBHashKey(attributeName="user_id")
    private int userId;
    
    @DynamoDBAttribute(attributeName="verification")
    private Boolean verification;
    
    @DynamoDBAttribute(attributeName="wallet")
    private long wallet;
    
    @DynamoDBAttribute(attributeName="auctions")
    private List<Integer> auctions;
   
    @DynamoDBAttribute(attributeName="direct")
    private long direct;
    
    @DynamoDBAttribute(attributeName="staking")
    private List<StakeHist> staking;
}
