package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.FinancialTransaction;

public class FinancialDao extends BaseDao{

    public FinancialDao(DynamoDBMapper dynamoDBMapper) {
        super(dynamoDBMapper);
    }

    // add new transaction
    public FinancialTransaction createNewTransaction(FinancialTransaction tx) {
        dynamoDBMapper.save(tx);
        return tx;
    }

    // get all Transactions
    public List<FinancialTransaction> getTransactions() {
        return dynamoDBMapper.scan(FinancialTransaction.class, new DynamoDBScanExpression());
    }

    // get transactions by ID
    public List<FinancialTransaction> getTransactionByUser(String userId) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(userId));
		eav.put(":v2", new AttributeValue().withBOOL(true));

		DynamoDBQueryExpression<FinancialTransaction> queryExpression = new DynamoDBQueryExpression<FinancialTransaction>()
		    .withKeyConditionExpression("user_id = :v1")
			.withFilterExpression("is_confirmed = :v2")
		    .withExpressionAttributeValues(eav);

		return dynamoDBMapper.query(FinancialTransaction.class, queryExpression);
    }
    
    // get transactions by type
    public List<FinancialTransaction> getTransactionsByType(int type) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withN(String.valueOf(type)));
        
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("transaction_type = :val1")
            .withExpressionAttributeValues(eav);
        List<FinancialTransaction> list = dynamoDBMapper.scan(FinancialTransaction.class, scanExpression);
        return list;
    }

    // get transaction by code
    public List<FinancialTransaction> getTransactionByCode(String code) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(code));
        
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("code = :val1")
            .withExpressionAttributeValues(eav);
        return dynamoDBMapper.scan(FinancialTransaction.class, scanExpression);
    }
}
