package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.Auction;

@Repository
public class AuctionDao extends BaseDao implements IAuctionDao {
		
	@Autowired
	public AuctionDao(DynamoDBMapper dynamoDBMapper) {
		super(dynamoDBMapper);
	}

	@Override
	public Auction createNewAuction(Auction auction) {
		dynamoDBMapper.save(auction);
		return auction;
	}

	@Override
	public List<Auction> getAuctionList() {
		List<Auction> list = dynamoDBMapper.scan(Auction.class, new DynamoDBScanExpression());
		return list;
	}

	@Override
	public Auction getAuctionById(String id) {
		Auction auction = dynamoDBMapper.load(Auction.class, id);
		return auction;
	}
	
	@Override
	public Auction getAuctionByRound(Integer round) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withN(round.toString()));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("round_number = :val1")
            .withExpressionAttributeValues(eav);

		List<Auction> list = dynamoDBMapper.scan(Auction.class, scanExpression);
		if(list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public Auction updateAuctionByAdmin(Auction auction) {
		dynamoDBMapper.save(auction, updateConfig);
		return auction;
	}

	@Override
	public Auction startAuction(Auction targetAuction) {
		targetAuction.setStatus(Auction.STARTED);
		dynamoDBMapper.save(targetAuction, updateConfig);
		return targetAuction;
	}

	@Override
	public Auction endAuction(Auction targetAuction) {
		targetAuction.setStatus(Auction.ENDED);
		dynamoDBMapper.save(targetAuction, updateConfig);
		return targetAuction;
	}

	@Override
	public Auction updateAuctionStats(Auction stats) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Auction> getAuctionByStatus(Integer status) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":val1", new AttributeValue().withN(status.toString()));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("status = :val1")
            .withExpressionAttributeValues(eav);
        
		return dynamoDBMapper.scan(Auction.class, scanExpression);
	}
	
}
