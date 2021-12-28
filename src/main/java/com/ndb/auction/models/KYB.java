package com.ndb.auction.models;

import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "KYBList")
public class KYB {

	@DynamoDBHashKey(attributeName = "user_id")
	private String userId;

	@DynamoDBAttribute(attributeName = "country")
	private String country;

	@DynamoDBAttribute(attributeName = "company_name")
	private String companyName;

	@DynamoDBAttribute(attributeName = "reg_num")
	private String regNum;

	@DynamoDBAttribute(attributeName = "files")
	private Set<String> files;

	@DynamoDBAttribute(attributeName = "status")
	private String status;

	@DynamoDBAttribute(attributeName = "reg_time")
	private Long regTime;

	@DynamoDBAttribute(attributeName = "update_time")
	private Long updateTime;
	
}
