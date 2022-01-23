package com.ndb.auction.dao.oracle.transaction;

import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.models.FinancialTransaction;

import org.springframework.stereotype.Repository;

@Repository
public class FinancialDao extends BaseOracleDao {

    // add new transaction
    public FinancialTransaction createNewTransaction(FinancialTransaction tx) {
        // dynamoDBMapper.save(tx);
        return tx;
    }

    // get all Transactions
    public List<FinancialTransaction> getTransactions() {
        // return dynamoDBMapper.scan(FinancialTransaction.class, new
        // DynamoDBScanExpression());
        return null;
    }

    // get transactions by ID
    public List<FinancialTransaction> getTransactionByUser(int userId) {
        // Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        // eav.put(":v1", new AttributeValue().withS(String.valueOf(userId)));
        // eav.put(":v2", new AttributeValue().withBOOL(true));

        // DynamoDBQueryExpression<FinancialTransaction> queryExpression = new
        // DynamoDBQueryExpression<FinancialTransaction>()
        // .withKeyConditionExpression("user_id = :v1")
        // .withFilterExpression("is_confirmed = :v2")
        // .withExpressionAttributeValues(eav);

        // return dynamoDBMapper.query(FinancialTransaction.class, queryExpression);
        return null;
    }

    // get transactions by type
    public List<FinancialTransaction> getTransactionsByType(int type) {
        // Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        // eav.put(":val1", new AttributeValue().withN(String.valueOf(type)));

        // DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        // .withFilterExpression("transaction_type = :val1")
        // .withExpressionAttributeValues(eav);
        // List<FinancialTransaction> list =
        // dynamoDBMapper.scan(FinancialTransaction.class, scanExpression);
        // return list;
        return null;
    }

    // get transaction by code
    public List<FinancialTransaction> getTransactionByCode(String code) {
        // Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        // eav.put(":val1", new AttributeValue().withS(code));

        // DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        // .withFilterExpression("code = :val1")
        // .withExpressionAttributeValues(eav);
        // return dynamoDBMapper.scan(FinancialTransaction.class, scanExpression);
        return null;
    }
}
