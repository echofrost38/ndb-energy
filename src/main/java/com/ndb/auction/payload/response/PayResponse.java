package com.ndb.auction.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayResponse {
	
	private int paymentId;
	private String clientSecret;
	private String paymentIntentId;
	private Boolean requiresAction;
	private String error;
}
