package com.ndb.auction.models.tier;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DynamoDBDocument
public class StakeTask {
    @DynamoDBAttribute(attributeName = "expired_time")
    private int expiredTime;
    @DynamoDBAttribute(attributeName = "ratio")
    private double ratio;
}
