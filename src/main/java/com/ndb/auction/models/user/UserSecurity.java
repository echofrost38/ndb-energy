package com.ndb.auction.models.user;

import com.ndb.auction.models.BaseModel;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class UserSecurity extends BaseModel {

	public UserSecurity () {
		authType = "";
		tfaEnabled = false;
		tfaSecret = "";
	}

	private int userId;
	private String authType;
	private boolean tfaEnabled;
	private String tfaSecret;

}
