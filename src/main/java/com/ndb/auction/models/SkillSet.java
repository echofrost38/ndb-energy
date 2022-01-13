package com.ndb.auction.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class SkillSet {

	private String skill;
	private Integer skillRate;
	
	@DynamoDBAttribute(attributeName = "skill")
	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}
	
	@DynamoDBAttribute(attributeName = "skill_rate")
	public Integer getSkillRate() {
		return skillRate;
	}

	public void setSkillRate(Integer skillRate) {
		this.skillRate = skillRate;
	}
}
