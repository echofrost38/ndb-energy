package com.ndb.auction.dao.oracle.other;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.ndb.auction.models.NotificationType;
import com.ndb.auction.models.NotificationType;

@Repository
public class NotificationTypeDao extends BaseOracleDao {

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

	private static final int MAX_NOTIFICATION = 100;

	public UserDao() {
		super(TABLE_NAME);
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

	////////////////////// version 2 //////////////////////////
	public NotificationType addNewNotificationType(NotificationType type2) {
		dynamoDBMapper.save(type2);
		return type2;
	}

	public List<NotificationType> getNotificationTypes() {
		return dynamoDBMapper.scan(NotificationType.class, new DynamoDBScanExpression());
	}

}
