package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.StripeTransaction;

@Repository
public class StripePaymentDao extends BaseDao {

	public StripePaymentDao(DynamoDBMapper dynamoDBMapper) {
		super(dynamoDBMapper);
	}
	
	public StripeTransaction createNewPayment(StripeTransaction tx) {
		dynamoDBMapper.save(tx);
		return tx;
	}
	
	public StripeTransaction updatePaymentStatus(String paymentId, Integer newStatus) {
		StripeTransaction tx = getTransactionById(paymentId);
		if(tx == null) {
			return null;
		}
		tx.setStatus(newStatus);
		dynamoDBMapper.save(tx, updateConfig);
		return tx;
	}
	
	public List<StripeTransaction> getTransactionsByUser(String userId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(userId));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("user_id = :v1")
		    .withExpressionAttributeValues(eav);
		return dynamoDBMapper.scan(StripeTransaction.class, scanExpression);
	}
	
	public List<StripeTransaction> getTransactionsByRound(String roundId){
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(roundId));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("round_id = :v1")
		    .withExpressionAttributeValues(eav);
		return dynamoDBMapper.scan(StripeTransaction.class, scanExpression);
	}
	
	public List<StripeTransaction> getTransactions(String roundId, String userId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(roundId));
		eav.put(":v2", new AttributeValue().withS(userId));

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("round_id = :v1 and user_id = :v2")
		    .withExpressionAttributeValues(eav);
		return dynamoDBMapper.scan(StripeTransaction.class, scanExpression);
	}
	
	public StripeTransaction getTransactionById(String id) {
		return dynamoDBMapper.load(StripeTransaction.class, id);
	}
}