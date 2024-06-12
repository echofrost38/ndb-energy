package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.FiatTransaction;

@Repository
public class StripePaymentDao extends BaseDao {

	public StripePaymentDao(DynamoDBMapper dynamoDBMapper) {
		super(dynamoDBMapper);
	}
	
	public FiatTransaction createNewPayment(FiatTransaction tx) {
		dynamoDBMapper.save(tx);
		return tx;
	}
	
	public FiatTransaction updatePaymentStatus(String paymentId, Integer newStatus) {
		FiatTransaction tx = getTransactionById(paymentId);
		if(tx == null) {
			return null;
		}
		tx.setStatus(newStatus);
		dynamoDBMapper.save(tx, updateConfig);
		return tx;
	}
	
	public List<FiatTransaction> getTransactionsByUser(String userId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(userId));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("user_id = :v1")
		    .withExpressionAttributeValues(eav);
		return dynamoDBMapper.scan(FiatTransaction.class, scanExpression);
	}
	
	public List<FiatTransaction> getTransactionsByRound(String roundId){
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(roundId));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("round_id = :v1")
		    .withExpressionAttributeValues(eav);
		return dynamoDBMapper.scan(FiatTransaction.class, scanExpression);
	}
	
	public List<FiatTransaction> getTransactions(String roundId, String userId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(roundId));
		eav.put(":v2", new AttributeValue().withS(userId));

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("round_id = :v1 and user_id = :v2")
		    .withExpressionAttributeValues(eav);
		return dynamoDBMapper.scan(FiatTransaction.class, scanExpression);
	}
	
	public FiatTransaction getTransactionById(String paymentId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(paymentId));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("payment_intent = :v1")
		    .withExpressionAttributeValues(eav);
		List<FiatTransaction> list = dynamoDBMapper.scan(FiatTransaction.class, scanExpression);
		if(list.size() == 0) {
			return null;
		}
		return list.get(0);
	}
}
