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
@DynamoDBTable(tableName = "NotificationType2")
public class NotificationType2 {

    @DynamoDBHashKey(attributeName = "n_type")
    private int nType;

    @DynamoDBAttribute(attributeName = "t_name")
    private String tName;

    @DynamoDBAttribute(attributeName = "broadcast")
    private boolean broadcast;
    
}
