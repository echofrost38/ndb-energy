package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.Notification2;

@Repository
public class NotificationDao extends BaseDao implements INotificationDao {

	public NotificationDao(DynamoDBMapper dynamoDBMapper) {
		super(dynamoDBMapper);
	}

	@Override
	public List<Notification> getNotificationsByUser(String userId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(userId.toString()));
		
		DynamoDBQueryExpression<Notification> queryExpression = new DynamoDBQueryExpression<Notification>()
		    .withKeyConditionExpression("user_id = :v1")
		    .withExpressionAttributeValues(eav);

		return dynamoDBMapper.query(Notification.class, queryExpression);
	}

	@Override
	public void pushNewNotifications(List<Notification> list) {
		// each Notification has userId & auto generated id
		dynamoDBMapper.batchSave(list);
	}

	@Override
	public void pushNewNotification(Notification notification) {
		dynamoDBMapper.save(notification);	
	}

	@Override
	public Notification setReadStatus(String userId, String nId) {
		Notification notification = dynamoDBMapper.load(Notification.class, userId, nId);
		if(notification == null) {
			return null; // or exception
		}
		
		notification.setRead(true);
		dynamoDBMapper.save(notification, updateConfig);
		return notification;
	}

    public List<Notification> getAllUnReadNotificationsByUser(String userId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(userId.toString()));
		eav.put(":v2", new AttributeValue().withN("0"));
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("user_id = :v1 and read_flag = :v2")
		    .withExpressionAttributeValues(eav);

		return dynamoDBMapper.scan(Notification.class, scanExpression);
    }

	////////////////// version 2 ////////////////////////////////////////
	
	public Notification2 addNewNotification(Notification2 notification2) {
		dynamoDBMapper.save(notification2);
		return notification2;
	}

	public Notification2 setReadFlag(Notification2 notification2) {
		notification2.setRead(true);
		dynamoDBMapper.save(notification2, updateConfig);
		return notification2;
	}

	public Notification2 getNotification2(String id, long timeStamp){
		return dynamoDBMapper.load(Notification2.class, id, timeStamp);
	}

	public List<Notification2> getFirstNotificationsByUser(String userId, int limit) {
		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":val1", new AttributeValue().withS(userId));
		DynamoDBQueryExpression<Notification2> queryExpression = new DynamoDBQueryExpression<Notification2>()
			.withKeyConditionExpression("user_id = :val1")
			.withLimit(limit)
			.withScanIndexForward(false)
			.withExpressionAttributeValues(eav);
		return dynamoDBMapper.queryPage(Notification2.class, queryExpression).getResults();
	}

	public List<Notification2> getMoreNotificationsByUser(String userId, long stamp, int limit) {
		Map<String, AttributeValue> exclusiveKey = new HashMap<>();
		exclusiveKey.put("user_id", new AttributeValue().withS(userId));
		exclusiveKey.put("time_stamp", new AttributeValue().withN(Long.valueOf(stamp).toString()));

		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":val1", new AttributeValue().withS(userId));

		DynamoDBQueryExpression<Notification2> queryExpression = new DynamoDBQueryExpression<Notification2>()
			.withExclusiveStartKey(exclusiveKey)
			.withLimit(limit)
			.withScanIndexForward(false)
			.withKeyConditionExpression("user_id = :val1")
			.withExpressionAttributeValues(eav);

		return dynamoDBMapper.queryPage(Notification2.class, queryExpression).getResults();
	}

	public List<Notification2> getUnreadNotifications(String userId) {
		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":val1", new AttributeValue().withS(userId));
		eav.put(":val2", new AttributeValue().withN("0"));

		DynamoDBQueryExpression<Notification2> queryExpression = new DynamoDBQueryExpression<Notification2>()
			.withKeyConditionExpression("user_id = :val1")
			.withFilterExpression("n_read = :val2")
			.withExpressionAttributeValues(eav);
		return dynamoDBMapper.query(Notification2.class, queryExpression);
	}
}
