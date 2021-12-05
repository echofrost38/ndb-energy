package com.ndb.auction.dao;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
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
        List<FinancialTransaction> list = dynamoDBMapper.scan(FinancialTransaction.class, new DynamoDBScanExpression());
        return list; 
    }
    
    // get transactions by type
    public List<FinancialTransaction> getTransactionsByType(int type) {
        List<FinancialTransaction> list = dynamoDBMapper.scan(FinancialTransaction.class, new DynamoDBScanExpression());
        return list;
    }
}
