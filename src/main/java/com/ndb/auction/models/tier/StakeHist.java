package com.ndb.auction.models.tier;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBDocument
public class StakeHist {
    @DynamoDBAttribute(attributeName="expired_time")
    private int expiredTime;

    @DynamoDBAttribute(attributeName="amount")
    private long amount;
}
