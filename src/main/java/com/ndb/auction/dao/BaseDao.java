package com.ndb.auction.dao;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;

@Repository
public class BaseDao {
	
	protected DynamoDBMapperConfig updateConfig;
	protected DynamoDBMapper dynamoDBMapper;

	public BaseDao(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.updateConfig = DynamoDBMapperConfig.builder()
    			.withSaveBehavior(SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES)
    			.build();
    }
}
