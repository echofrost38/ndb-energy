package com.ndb.auction.dao.oracle.other;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.StripeTransaction;

@Repository
public class StripePaymentDao extends BaseOracleDao {

	private static final String TABLE_NAME = "TBL_USER";

	private static User extract(ResultSet rs) throws SQLException {
		User m = new User();
		m.setId(rs.getInt("ID"));
		m.setEmail(rs.getString("EMAIL"));
		m.setPassword(rs.getString("PASSWORD"));
		m.setName(rs.getString("NAME"));
		m.setCountry(rs.getString("COUNTRY"));
		m.setPhone(rs.getString("PHONE"));
		m.setBirthday(rs.getTimestamp("BIRTHDAY"));
		m.setRegDate(rs.getTimestamp("REG_DATE"));
		m.setLastLoginDate(rs.getTimestamp("LAST_LOGIN_DATE"));
		m.setLastPasswordChangeDate(rs.getTimestamp("LAST_PASSWORD_CHANGE_DATE"));
		m.setRole(rs.getString("ROLE"));
		m.setTierLevel(rs.getInt("TIER_LEVEL"));
		m.setTierPoint(rs.getInt("TIER_POINT"));
		m.setProvider(rs.getString("PROVIDER"));
		m.setProviderId(rs.getString("PROVIDER_ID"));
		m.setNotifySetting(rs.getInt("NOTIFY_SETTING"));
		m.setDeleted(rs.getInt("DELETED"));
		return m;
	}

	public UserDao() {
		super(TABLE_NAME);
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
	
	public List<StripeTransaction> getTransactionsByUser(int userId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(String.valueOf(userId)));
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
	
	public List<StripeTransaction> getTransactions(String roundId, int userId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(roundId));
		eav.put(":v2", new AttributeValue().withS(String.valueOf(userId)));

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("round_id = :v1 and user_id = :v2")
		    .withExpressionAttributeValues(eav);
		return dynamoDBMapper.scan(StripeTransaction.class, scanExpression);
	}
	
	public StripeTransaction getTransactionById(String id) {
		return dynamoDBMapper.load(StripeTransaction.class, id);
	}
}
