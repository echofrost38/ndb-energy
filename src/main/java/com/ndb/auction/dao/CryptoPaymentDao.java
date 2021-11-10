package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.CryptoTransaction;

import org.springframework.stereotype.Repository;

@Repository
public class CryptoPaymentDao extends BaseDao {

    public CryptoPaymentDao(DynamoDBMapper dynamoDBMapper) {
        super(dynamoDBMapper);
    }

    public CryptoTransaction createNewPayment (CryptoTransaction tx) {
        dynamoDBMapper.save(tx);
        return tx;
    }
    
    public CryptoTransaction getTransactionById(String code) {
        return dynamoDBMapper.load(CryptoTransaction.class, code);
    }

    public List<CryptoTransaction> getTransactionByUser(String userId) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(userId));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("user_id = :v1")
		    .withExpressionAttributeValues(eav);
		return dynamoDBMapper.scan(CryptoTransaction.class, scanExpression);
    }

    public List<CryptoTransaction> getTransactionByRound(String roundId) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(roundId));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("round_id = :v1")
		    .withExpressionAttributeValues(eav);
		return dynamoDBMapper.scan(CryptoTransaction.class, scanExpression);
    }

    public CryptoTransaction getTransaction(String roundId, String userId) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(roundId));
		eav.put(":v2", new AttributeValue().withS(userId));

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("round_id = :v1 and user_id = :v2")
		    .withExpressionAttributeValues(eav);
		List<CryptoTransaction> list = dynamoDBMapper.scan(CryptoTransaction.class, scanExpression);
        if(list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public CryptoTransaction updateCryptoTransaction(CryptoTransaction tx) {
        dynamoDBMapper.save(tx, updateConfig);
        return tx;
    }
}
