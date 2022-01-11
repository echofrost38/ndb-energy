package com.ndb.auction.models.user;

import com.ndb.auction.models.BaseModel;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class UserVerify extends BaseModel {

	public UserVerify () {
		this.emailVerified = false;
		this.phoneVerified = false;
		this.kybVerified = false;
		this.kycVerified = false;
		this.amlVerified = false;
	}

	private boolean emailVerified;
	private boolean phoneVerified;
	private boolean kycVerified;
	private boolean amlVerified;
	private boolean kybVerified;

}
