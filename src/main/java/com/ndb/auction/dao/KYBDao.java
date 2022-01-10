package com.ndb.auction.dao;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.ndb.auction.models.user.UserKyb;

import org.springframework.stereotype.Repository;

@Repository
public class KYBDao extends BaseDao {

    public KYBDao(DynamoDBMapper dynamoDBMapper) {
        super(dynamoDBMapper);
    }

    public UserKyb addList(UserKyb kyb) {
        DynamoDBMapperConfig dynamoDBMapperConfig = new DynamoDBMapperConfig.Builder()
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES)
                .build();
        dynamoDBMapper.save(kyb, dynamoDBMapperConfig);
        return kyb;
    }

    public UserKyb getByUserId(String userId) {
        return dynamoDBMapper.load(UserKyb.class, userId);
    }

    public List<UserKyb> getAll() {
        return dynamoDBMapper.scan(UserKyb.class, new DynamoDBScanExpression());
    }

}
