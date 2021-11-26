package com.ndb.auction.payload;

public class SumsubPayload {
	
	private String applicantId;
	private String inspectionId;
	private String correlationId;
	private String externalUserId;
	private String type;
	private String reviewStatus;
	private String createdAt;
	private String applicantType;
	private ReviewResult reviewResult;
	private String videoIdentReviewStatus;
	private String applicantActionId;
	private String externalApplicantActionId;
	private String clientId;

	public String getApplicantId() {
		return applicantId;
	}

	public void setApplicantId(String applicantId) {
		this.applicantId = applicantId;
	}

	public String getInspectionId() {
		return inspectionId;
	}

	public void setInspectionId(String inspectionId) {
		this.inspectionId = inspectionId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getExternalUserId() {
		return externalUserId;
	}

	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getApplicantType() {
		return applicantType;
	}

	public void setApplicantType(String applicantType) {
		this.applicantType = applicantType;
	}

	public ReviewResult getReviewResult() {
		return reviewResult;
	}

	public void setReviewResult(ReviewResult reviewResult) {
		this.reviewResult = reviewResult;
	}

	public String getVideoIdentReviewStatus() {
		return videoIdentReviewStatus;
	}

	public void setVideoIdentReviewStatus(String videoIdentReviewStatus) {
		this.videoIdentReviewStatus = videoIdentReviewStatus;
	}

	public String getApplicantActionId() {
		return applicantActionId;
	}

	public void setApplicantActionId(String applicantActionId) {
		this.applicantActionId = applicantActionId;
	}

	public String getExternalApplicantActionId() {
		return externalApplicantActionId;
	}

	public void setExternalApplicantActionId(String externalApplicantActionId) {
		this.externalApplicantActionId = externalApplicantActionId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
