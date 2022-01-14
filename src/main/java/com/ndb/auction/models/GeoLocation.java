package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class GeoLocation extends BaseModel {
	
	private String countryCode;
	private boolean isAllowed;
	
	public GeoLocation(String code, boolean allowed) {
		this.countryCode = code;
		this.isAllowed = allowed;
	}

}
