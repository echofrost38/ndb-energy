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
	
	private int id;
	private String country;
	private String countryCode;
	private boolean isAllowed;
	
	public GeoLocation(String country, String code, boolean allowed) {
		this.country = country;
		this.countryCode = code;
		this.isAllowed = allowed;
	}

}
