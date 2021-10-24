package com.ndb.auction.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
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
	public Auction updateAuctionByAdmin(Auction auction) {
		DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
				.withSaveBehavior(SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES)
				.build();
		dynamoDBMapper.save(auction, config);
		return null;
	}
	
}
