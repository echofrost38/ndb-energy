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
		    .withFilterExpression("user_id = :v1 and read <> :v2")
		    .withExpressionAttributeValues(eav);

		return dynamoDBMapper.scan(Notification.class, scanExpression);
    }
}
