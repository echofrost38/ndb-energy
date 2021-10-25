package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.Bid;

@Repository
public class BidDao extends BaseDao implements IBidDao {
	
	@Autowired
	public BidDao(DynamoDBMapper dynamoDBMapper) {
		super(dynamoDBMapper);
	}

	@Override
	public Bid placeBid(Bid bid) {
		dynamoDBMapper.save(bid);
		return bid;
	}

	@Override
	public Bid getBid(Integer round, String userId) {		
		return dynamoDBMapper.load(Bid.class, userId, round);
	}

	@Override
	public Bid updateBid(Bid bid) {
		dynamoDBMapper.save(bid);
		return bid;
	}

	@Override
	public List<Bid> getBidListByRound(Integer round) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withN(round.toString()));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("round_number = :val1")
            .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(Bid.class, scanExpression);
	}

	@Override
	public List<Bid> getBidListByUser(String userId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(userId.toString()));
		DynamoDBQueryExpression<Bid> queryExpression = new DynamoDBQueryExpression<Bid>()
		    .withKeyConditionExpression("Id = :v1")
		    .withExpressionAttributeValues(eav);

		return dynamoDBMapper.query(Bid.class, queryExpression);
	}

}
