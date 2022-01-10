package com.ndb.auction.models;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class LocationLog extends BaseModel {

	private int userId;
	private String ipAddress;
	private boolean isVpn;
	private boolean isProxy;
	private boolean isTor;
	private boolean isRelay;
	private String city;
	private String region;
	private String country;
	private String continent;
	private String regionCode;
	private String countryCode;
	private String continentCode;
	private float latitude;
	private float longitude;
	private String vpnapiResponse;
	private String finalResult;

}
