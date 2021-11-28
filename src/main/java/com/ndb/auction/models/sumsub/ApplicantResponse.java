package com.ndb.auction.models.sumsub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantResponse {
	
	private String id;
	private String externalUserId;
	private String createdAt;
	private String clientId;
	private DocSets requiredIdDocs;
	private Review review;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getExternalUserId() {
		return externalUserId;
	}
	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}

	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public DocSets getRequiredIdDocs() {
		return requiredIdDocs;
	}
	public void setRequiredIdDocs(DocSets requiredIdDocs) {
		this.requiredIdDocs = requiredIdDocs;
	}

	public Review getReview() {
		return review;
	}
	public void setReview(Review review) {
		this.review = review;
	}

}
