package com.ndb.auction.dao.oracle.other;

import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.models.Coin;
import com.ndb.auction.models.CryptoTransaction;

import org.springframework.stereotype.Repository;

@Repository
public class CryptoPaymentDao extends BaseOracleDao {

	public CryptoTransaction createNewPayment(CryptoTransaction tx) {
		// dynamoDBMapper.save(tx);
		return tx;
	}

	public CryptoTransaction getTransactionById(String code) {
		// return dynamoDBMapper.load(CryptoTransaction.class, code);
		return null;
	}

	public List<CryptoTransaction> getTransactionByUser(int userId) {
		// Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		// eav.put(":v1", new AttributeValue().withS(String.valueOf(userId)));
		// DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		// .withFilterExpression("user_id = :v1")
		// .withExpressionAttributeValues(eav);
		// return dynamoDBMapper.scan(CryptoTransaction.class, scanExpression);
		return null;
	}

	public List<CryptoTransaction> getTransactionByRound(int roundId) {
		// Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		// eav.put(":v1", new AttributeValue().withS(roundId));
		// DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		// .withFilterExpression("round_id = :v1")
		// .withExpressionAttributeValues(eav);
		// return dynamoDBMapper.scan(CryptoTransaction.class, scanExpression);
		return null;
	}

	public List<CryptoTransaction> getTransaction(int roundId, int userId) {
		// Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		// eav.put(":v1", new AttributeValue().withS(roundId));
		// eav.put(":v2", new AttributeValue().withS(String.valueOf(userId)));

		// DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		// .withFilterExpression("round_id = :v1 and user_id = :v2")
		// .withExpressionAttributeValues(eav);
		// return dynamoDBMapper.scan(CryptoTransaction.class, scanExpression);
		return null;
	}

	public CryptoTransaction updateCryptoTransaction(CryptoTransaction tx) {
		// dynamoDBMapper.save(tx, updateConfig);
		return tx;
	}

	// for crypto coin
	public List<Coin> getCoins() {
		// return dynamoDBMapper.scan(Coin.class, new DynamoDBScanExpression());
		return null;
	}

	public Coin addNewCoin(Coin coin) {
		// dynamoDBMapper.save(coin);
		return coin;
	}

	public Coin deleteCoin(Coin coin) {
		// dynamoDBMapper.delete(coin);
		return coin;
	}
}
