package com.ndb.auction.models.user;

import java.sql.Timestamp;

import com.ndb.auction.models.BaseModel;
import com.ndb.auction.models.Notification;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseModel {

	public static final String ROLE_SEPARATOR = ";";

	private String email;
	private String password;
	private String name;
	private String country;
	private String phone;
	private Timestamp birthday;
	private Timestamp lastLoginDate;
	private Timestamp lastPasswordChangeDate;
	private String role;
	private int tierLevel;
	private long tierPoint;
	private String provider;
	private String providerId;
	private int notifySetting;

	private UserAvatar avatar;
	private UserSecurity security;
	private UserVerify verify;

	public User addRole(String value) {
		if (role == null || role.isEmpty())
			role = value;
		else if (role.endsWith(ROLE_SEPARATOR))
			role += value;
		else
			role += ROLE_SEPARATOR + value;
		return this;
	}

	public User removeRole(String value) {
		if (role != null && !role.isEmpty())
			role = role.replaceAll(ROLE_SEPARATOR + value, "").replaceAll(value + ROLE_SEPARATOR, "");
		return this;
	}

	public boolean allowNotification(Notification notification) {
		return ((this.notifySetting >> (notification.getType() - 1)) & 0x01) > 0;
	}

}
