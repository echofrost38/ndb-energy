package com.ndb.auction.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Credentials {
	
	private String status;
	private String token;
	private List<String> twoStep;
	
	public Credentials(String status, String token) {
		this.status = status;
		this.token = token;
	}
}
