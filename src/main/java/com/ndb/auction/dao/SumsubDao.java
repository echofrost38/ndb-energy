package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.KYCSetting;
import com.ndb.auction.models.sumsub.Applicant;

@Repository
public class SumsubDao extends BaseDao{

	public SumsubDao(DynamoDBMapper dynamoDBMapper) {
		super(dynamoDBMapper);
	}
	
	// Creating new applicant
	public Applicant createNewApplicant(Applicant applicant) {
		dynamoDBMapper.save(applicant);
		return applicant;
	}
	
	public List<Applicant> getApplicantByUserId(String userId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(userId));
        
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("user_id = :val1")
            .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(Applicant.class, scanExpression);
	}
	
	public Applicant getApplicantById(String id) {
		return dynamoDBMapper.load(Applicant.class, id);
	}

	public KYCSetting updateKYCSetting(KYCSetting setting) {
		dynamoDBMapper.save(setting, updateConfig);
		return setting;
	} 

	public List<KYCSetting> getKYCSettings() {
		return dynamoDBMapper.scan(KYCSetting.class, new DynamoDBScanExpression());
	}

}
