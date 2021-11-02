package com.ndb.auction.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ndb.auction.models.Bid;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.User;
import com.ndb.auction.service.interfaces.IProfileService;

/**
 * 1. get market data
 * 2. get avatar ? / set avatar!!!!!
 * 3. Airdrop where to go???????????
 * @author klinux
 *
 */

@Service
public class ProfileService extends BaseService implements IProfileService {

	// actually return user it self!!!!
	@Override
	public User getUserProfile(String userId) {
		return userDao.getUserById(userId);
	}

	@Override
	public List<Notification> getNotifications(String userId) {
		return notificationDao.getNotificationsByUser(userId);
	}

	@Override
	public Integer getNotifySetting(String userId) {
		User user = userDao.getUserById(userId);
		if(user == null) {
			return null; // or exception 404
		}
	
		return user.getNotifySetting();
	}

	@Override
	public Integer updateNotifySetting(String userId, Integer setting) {
		User user = userDao.getUserById(userId);
		if(user == null) {
			return null; // or exception 404
		}		
		user.setNotifySetting(setting);
		userDao.updateUser(user);
		return setting;
	}

	@Override
	public Integer changePassword(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Bid> getBidActivity(String userId) {
		return bidDao.getBidListByUser(userId);
	}

}
