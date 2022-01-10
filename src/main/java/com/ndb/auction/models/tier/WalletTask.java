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
public class WalletTask {
    @DynamoDBAttribute(attributeName = "amount")
    private long amount;
    @DynamoDBAttribute(attributeName = "point")
    private long point;
}
