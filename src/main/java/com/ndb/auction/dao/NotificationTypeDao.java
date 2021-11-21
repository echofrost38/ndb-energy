package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.ndb.auction.models.NotificationType;

@Repository
public class NotificationTypeDao extends BaseDao {

	private final int MAX_NOTIFICATION = 100;

	public NotificationTypeDao(DynamoDBMapper dynamoDBMapper) {
		super(dynamoDBMapper);
	}

	public String create(String name) {
		NotificationType notificationType = new NotificationType();
		notificationType.setName(name);
		notificationType.setId(getAvailableId());
		
        dynamoDBMapper.save(notificationType);

		return "Notification Type created successfully!";
    }
	private Integer getAvailableId() {
		List<NotificationType> notificationList = getAllNotificationTypes();
		if(notificationList.isEmpty())
			return 1;

		List<Integer> idList = notificationList.stream().map(NotificationType::getId).collect(Collectors.toList());
		
		for(Integer i = 1; i < MAX_NOTIFICATION; i++) {
			if(!idList.contains(i))
				return i;	
		}
		return 1;
	}
    
    public NotificationType getNotificationTypeById(String id) {
        return dynamoDBMapper.load(NotificationType.class, id);
    }

    public List<NotificationType> getAllNotificationTypes() {
        return dynamoDBMapper.scan(NotificationType.class, new DynamoDBScanExpression());
    }

	public NotificationType updateNotificationType(NotificationType notificationType) {
		dynamoDBMapper.save(notificationType, updateConfig);
		return notificationType;
	}

	public List<NotificationType> deleteById(Integer id) {
		NotificationType n = dynamoDBMapper.load(NotificationType.class, id);
		dynamoDBMapper.delete(n);

		return getAllNotificationTypes();
	}
}
