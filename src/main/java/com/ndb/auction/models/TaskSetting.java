package com.ndb.auction.models;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.ndb.auction.models.tier.StakeTask;
import com.ndb.auction.models.tier.WalletTask;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "TaskSetting")
public class TaskSetting {

    @DynamoDBHashKey(attributeName="setting_id")
    private String settingId;

    @DynamoDBAttribute(attributeName="verification")
    private double verification;

    @DynamoDBAttribute(attributeName="wallet")
    private List<WalletTask> walletTasks;

    @DynamoDBAttribute(attributeName="auction")
    private double auction;

    @DynamoDBAttribute(attributeName="direct")
    private double direct;

    @DynamoDBAttribute(attributeName="staking")
    private List<StakeTask> stakingTasks;
}
