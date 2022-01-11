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
public class TaskSetting extends BaseModel {

    private long verification;
    private List<WalletTask> wallet;
    private long auction;
    private long direct;
    private List<StakeTask> staking;
}
