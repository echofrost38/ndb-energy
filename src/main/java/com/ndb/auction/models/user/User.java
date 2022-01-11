package com.ndb.auction.models.user;

import java.util.Set;

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

	private String email;
	private String password;
	private String name;
	private String country;
	private String phone;
	private long birthday;
	private long lastLoginDate;
	private Set<String> role;
	private int tierLevel;
	private long tierPoint;
	private String provider;
	private String providerId;
	private int notifySetting;

	private UserAvatar avatar;
	private UserSecurity security;
	private UserVerify verify;

	public User (String email, String encodedPass, String country) {
		this.email = email;
		this.password = encodedPass;
		this.country = country;
	}

	public User addRole(String value) {
		this.role.add(value);
		return this;
	}

	public User removeRole(String value) {
		this.role.remove(value);
		return this;
	}

	// 

	public boolean allowNotification(Notification notification) {
		return ((this.notifySetting >> (notification.getNType() - 1)) & 0x01) > 0;
	}

}
