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
import com.ndb.auction.models.AuctionStats;
import com.ndb.auction.models.Bid;

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

        return dynamoDBMapper.scan(Auction.class, scanExpression).get(0);
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
	
	public List<Auction> getOpendedList() {
		// get all opened auction
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		// filter with started status ( STARTED = 1 ) 
		eav.put(":val1", new AttributeValue().withN("1"));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("status = :val1")
            .withExpressionAttributeValues(eav);
        
		return dynamoDBMapper.scan(Auction.class, scanExpression);
	}

	@Override
	public Auction endAuction(Auction targetAuction) {
		dynamoDBMapper.save(targetAuction, updateConfig);
		return targetAuction;
	}

	@Override
	public Auction updateAuctionStats(Auction stats) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
