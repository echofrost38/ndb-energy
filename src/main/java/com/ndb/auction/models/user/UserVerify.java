package com.ndb.auction.models.user;

import com.ndb.auction.models.BaseModel;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class UserVerify extends BaseModel {

	private boolean emailVerified;
	private boolean phoneVerified;
	private boolean kycVerified;
	private boolean amlVerified;
	private boolean kybVerified;

}
