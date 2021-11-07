package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class AvatarSet {

	public String facial;
	public String hair;
	public String haColor;
	public String exp;
	public String hats;
	public String others;
	
	@DynamoDBAttribute(attributeName = "facial")
	public String getFacial() {
		return facial;
	}
	public void setFacial(String facial) {
		this.facial = facial;
	}
	
	@DynamoDBAttribute(attributeName = "hair")
	public String getHair() {
		return hair;
	}
	public void setHair(String hair) {
		this.hair = hair;
	}
	
	@DynamoDBAttribute(attributeName = "haColor")
	public String getHaColor() {
		return haColor;
	}
	public void setHaColor(String haColor) {
		this.haColor = haColor;
	}
	
	@DynamoDBAttribute(attributeName = "exp")
	public String getExp() {
		return exp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}
	
	@DynamoDBAttribute(attributeName = "hats")
	public String getHats() {
		return hats;
	}
	public void setHats(String hats) {
		this.hats = hats;
	}
	
	@DynamoDBAttribute(attributeName = "others")
	public String getOthers() {
		return others;
	}
	public void setOthers(String others) {
		this.others = others;
	}

}
