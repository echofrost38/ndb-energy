package com.ndb.auction.dao;

import java.util.List;

import com.ndb.auction.models.Notification;

public interface INotificationDao {
	List<Notification> getNotificationsByUser(String userId);
	
	Notification setReadStatus(String userId, String nId);
	
	void pushNewNotifications(List<Notification> list);

	void pushNewNotification(Notification notification);
}
