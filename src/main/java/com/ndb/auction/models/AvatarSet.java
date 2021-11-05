package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class AvatarSet {

	private Integer facial;
	private Integer hair;
	private Integer acces;
	private Integer faColor;
	private Integer haColor;
	private Integer acColor;
	
	@DynamoDBAttribute(attributeName = "facial")
	public Integer getFacial() {
		return facial;
	}

	public void setFacial(Integer facial) {
		this.facial = facial;
	}

	@DynamoDBAttribute(attributeName = "hair")
	public Integer getHair() {
		return hair;
	}

	public void setHair(Integer hair) {
		this.hair = hair;
	}

	@DynamoDBAttribute(attributeName = "acces")
	public Integer getAcces() {
		return acces;
	}

	public void setAcces(Integer acces) {
		this.acces = acces;
	}

	@DynamoDBAttribute(attributeName = "fa_color")
	public Integer getFaColor() {
		return faColor;
	}

	public void setFaColor(Integer faColor) {
		this.faColor = faColor;
	}

	@DynamoDBAttribute(attributeName = "ha_color")
	public Integer getHaColor() {
		return haColor;
	}

	public void setHaColor(Integer haColor) {
		this.haColor = haColor;
	}

	@DynamoDBAttribute(attributeName = "ac_color")
	public Integer getAcColor() {
		return acColor;
	}

	public void setAcColor(Integer acColor) {
		this.acColor = acColor;
	}

}
