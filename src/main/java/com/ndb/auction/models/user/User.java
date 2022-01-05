package com.ndb.auction.models.user;

import java.sql.Timestamp;

import com.ndb.auction.models.BaseModel;

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
	private Timestamp birthday;
	private Timestamp lastLoginDate;
	private String role;
	private int tierLevel;
	private int tierPoint;

	private UserAvatar avatar;
	private UserSecurity security;
	private UserVerify verify;

}
