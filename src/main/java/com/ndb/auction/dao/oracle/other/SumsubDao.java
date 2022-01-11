package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.KYCSetting;
import com.ndb.auction.models.sumsub.Applicant;

import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "???")
public class SumsubDao extends BaseOracleDao {
	
	// Creating new applicant
	public Applicant createNewApplicant(Applicant applicant) {
		dynamoDBMapper.save(applicant);
		return applicant;
	}
	
	public List<Applicant> getApplicantByUserId(int userId) {
		Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val1", new AttributeValue().withS(String.valueOf(userId)));
        
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
