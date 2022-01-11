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
public class UserKyb extends BaseModel {

	private String country;
	private String companyName;
	private String regNum;
	private String attach1Key;
	private String attach1Filename;
	private String attach2Key;
	private String attach2Filename;
	private String status;

}
