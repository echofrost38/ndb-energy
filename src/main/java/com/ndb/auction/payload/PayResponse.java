package com.ndb.auction.payload;

public class PayResponse {
	
	private String clientSecret;
	private String paymentIntentId;
	private Boolean requiresAction;
	private String error;

	public PayResponse() {

	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getPaymentIntentId() {
		return paymentIntentId;
	}

	public void setPaymentIntentId(String paymentIntentId) {
		this.paymentIntentId = paymentIntentId;
	}

	public Boolean getRequiresAction() {
		return requiresAction;
	}

	public void setRequiresAction(Boolean requiresAction) {
		this.requiresAction = requiresAction;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
