package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.user.User;

import org.springframework.stereotype.Repository;

@Repository
public class NotificationDao extends BaseOracleDao {

	private static final String TABLE_NAME = "TBL_USER";

	private static User extract(ResultSet rs) throws SQLException {
		User m = new User();
		m.setId(rs.getInt("ID"));
		m.setEmail(rs.getString("EMAIL"));
		m.setPassword(rs.getString("PASSWORD"));
		m.setName(rs.getString("NAME"));
		m.setCountry(rs.getString("COUNTRY"));
		m.setPhone(rs.getString("PHONE"));
		m.setBirthday(rs.getLong("BIRTHDAY"));
		m.setRegDate(rs.getLong("REG_DATE"));
		m.setLastLoginDate(rs.getLong("LAST_LOGIN_DATE"));
		// m.setRole(rs.getString("ROLE"));
		m.setTierLevel(rs.getInt("TIER_LEVEL"));
		m.setTierPoint(rs.getInt("TIER_POINT"));
		m.setProvider(rs.getString("PROVIDER"));
		m.setProviderId(rs.getString("PROVIDER_ID"));
		m.setNotifySetting(rs.getInt("NOTIFY_SETTING"));
		m.setDeleted(rs.getInt("DELETED"));
		return m;
	}

	public NotificationDao() {
		super(TABLE_NAME);
	}

	public List<Notification> getNotificationsByUser(int userId) {
		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":v1", new AttributeValue().withS(String.valueOf(userId)));
		
		DynamoDBQueryExpression<Notification> queryExpression = new DynamoDBQueryExpression<Notification>()
		    .withKeyConditionExpression("user_id = :v1")
		    .withExpressionAttributeValues(eav);

		return dynamoDBMapper.query(Notification.class, queryExpression);
	}

	public void pushNewNotifications(List<Notification> list) {
		// each Notification has userId & auto generated id
		dynamoDBMapper.batchSave(list);
	}

	public void pushNewNotification(Notification notification) {
		dynamoDBMapper.save(notification);	
	}

	public Notification setReadStatus(int userId, String nId) {
		Notification notification = dynamoDBMapper.load(Notification.class, userId, nId);
		if(notification == null) {
			return null; // or exception
		}
		
		notification.setRead(true);
		dynamoDBMapper.save(notification, updateConfig);
		return notification;
	}

    public List<Notification> getAllUnReadNotificationsByUser(int userId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(String.valueOf(userId)));
		eav.put(":v2", new AttributeValue().withN("0"));
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("user_id = :v1 and read_flag = :v2")
		    .withExpressionAttributeValues(eav);

		return dynamoDBMapper.scan(Notification.class, scanExpression);
    }

	////////////////// version 2 ////////////////////////////////////////
	
	public Notification addNewNotification(Notification notification2) {
		dynamoDBMapper.save(notification2);
		return notification2;
	}

	public Notification setReadFlag(Notification notification2) {
		notification2.setRead(true);
		dynamoDBMapper.save(notification2, updateConfig);
		return notification2;
	}
	
	public String setReadFlagAll(int userId) {
		List<Notification> unReadList = getUnreadNotifications(userId);
		for (Notification unRead: unReadList) {
			setReadFlag(unRead);
		}
		return "Success";
	}
	
	public Notification getNotification2(int id, long timeStamp){
		return dynamoDBMapper.load(Notification.class, id, timeStamp);
	}

	public List<Notification> getFirstNotificationsByUser(int userId, int limit) {
		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":val1", new AttributeValue().withS(String.valueOf(userId)));
		DynamoDBQueryExpression<Notification> queryExpression = new DynamoDBQueryExpression<Notification>()
			.withKeyConditionExpression("user_id = :val1")
			.withLimit(limit)
			.withScanIndexForward(false)
			.withExpressionAttributeValues(eav);
		return dynamoDBMapper.queryPage(Notification.class, queryExpression).getResults();
	}

	public List<Notification> getMoreNotificationsByUser(int userId, long stamp, int limit) {
		Map<String, AttributeValue> exclusiveKey = new HashMap<>();
		exclusiveKey.put("user_id", new AttributeValue().withS(String.valueOf(userId)));
		exclusiveKey.put("time_stamp", new AttributeValue().withN(Long.valueOf(stamp).toString()));

		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":val1", new AttributeValue().withS(String.valueOf(userId)));

		DynamoDBQueryExpression<Notification> queryExpression = new DynamoDBQueryExpression<Notification>()
			.withExclusiveStartKey(exclusiveKey)
			.withLimit(limit)
			.withScanIndexForward(false)
			.withKeyConditionExpression("user_id = :val1")
			.withExpressionAttributeValues(eav);

		return dynamoDBMapper.queryPage(Notification.class, queryExpression).getResults();
	}

	public List<Notification> getUnreadNotifications(int userId) {
		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":val1", new AttributeValue().withS(String.valueOf(userId)));
		eav.put(":val2", new AttributeValue().withN("0"));

		DynamoDBQueryExpression<Notification> queryExpression = new DynamoDBQueryExpression<Notification>()
			.withKeyConditionExpression("user_id = :val1")
			.withFilterExpression("n_read = :val2")
			.withExpressionAttributeValues(eav);
		return dynamoDBMapper.query(Notification.class, queryExpression);
	}
}
