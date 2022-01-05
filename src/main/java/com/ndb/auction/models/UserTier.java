package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName="Tiers")
public class UserTier {
    
    @DynamoDBHashKey(attributeName = "tier_level")
    private int level;

    @DynamoDBAttribute(attributeName = "tier_name")
    private String name;

    @DynamoDBAttribute(attributeName = "tier_points")
    private double points;
    
}
