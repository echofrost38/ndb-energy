package com.ndb.auction.payload;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VpnAPI {

	private String ip;
	private String message;
	private Map<String, Boolean> security;
	private Map<String, String> location;
	private Map<String, String> network;

}
