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
	public Auction startAuction(String id) {
		
		// get all opened auction
		List<Auction> openedList = dynamoDBMapper.scan(Auction.class, new DynamoDBScanExpression());
		
		if(openedList.size() != 0) {
			// Opened auction already exists
			
		}
		
		Auction targetAuction = dynamoDBMapper.load(Auction.class, id);
		if(targetAuction == null) {
			// There is no such auction with id
			
		}
		
		// check other required fields. ? All required fields are check when it is created.
		
		targetAuction.setStatus(Auction.STARTED);
		dynamoDBMapper.save(targetAuction, updateConfig);
		
		return targetAuction;
	}

	@Override
	public Auction endAuction(String id) {
		Auction targetAuction = dynamoDBMapper.load(Auction.class, id);
		if(targetAuction == null) {
			// There is no such auction with id
			
		}
		targetAuction.setStatus(Auction.ENDED);
		dynamoDBMapper.save(targetAuction, updateConfig);
		return targetAuction;
	}

	@Override
	public Auction updateAuctionStats(Auction stats) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
