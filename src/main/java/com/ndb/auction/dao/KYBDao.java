package com.ndb.auction.dao;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.ndb.auction.models.KYB;

import org.springframework.stereotype.Repository;

@Repository
public class KYBDao extends BaseDao {

    public KYBDao(DynamoDBMapper dynamoDBMapper) {
        super(dynamoDBMapper);
    }

    public KYB addList(KYB kyb) {
        DynamoDBMapperConfig dynamoDBMapperConfig = new DynamoDBMapperConfig.Builder()
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES)
                .build();
        dynamoDBMapper.save(kyb, dynamoDBMapperConfig);
        return kyb;
    }

    public KYB getByUserId(String userId) {
        return dynamoDBMapper.load(KYB.class, userId);
    }

    public List<KYB> getAll() {
        return dynamoDBMapper.scan(KYB.class, new DynamoDBScanExpression());
    }

}
