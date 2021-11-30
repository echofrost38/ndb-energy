package com.ndb.auction.dao;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.ndb.auction.models.OAuth2Registration;

import org.springframework.stereotype.Repository;

@Repository
public class OAuth2Dao extends BaseDao{

    public OAuth2Dao(DynamoDBMapper dynamoDBMapper) {
        super(dynamoDBMapper);
    }
    
	public OAuth2Registration createRegistration(OAuth2Registration registration) {
		dynamoDBMapper.save(registration);
		return registration;
	}

	public List<OAuth2Registration> getAllRegistrations() {
		return dynamoDBMapper.scan(OAuth2Registration.class, new DynamoDBScanExpression());
	}

	public OAuth2Registration getRegistrationById(String registrationId) {
		return dynamoDBMapper.load(OAuth2Registration.class, registrationId);
	}

    public OAuth2Registration updateUser(OAuth2Registration registration) {
		dynamoDBMapper.save(registration, updateConfig);
		return registration;
	}

    public OAuth2Registration deleteUser(String registrationId) {
		OAuth2Registration registration = dynamoDBMapper.load(OAuth2Registration.class, registrationId);
		dynamoDBMapper.delete(registration);
		return registration;
	}
}
