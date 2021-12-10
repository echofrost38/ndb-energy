package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.GeoLocation;

@Repository
public class GeoLocationDao extends BaseDao {
	
	public GeoLocationDao(DynamoDBMapper dynamoDBMapper) {
		super(dynamoDBMapper);
	}

	// Add disallowed country
	public GeoLocation addDisallowedCountry(String countryCode) {
		GeoLocation geoLocation = new GeoLocation(countryCode, false);
		dynamoDBMapper.save(geoLocation);
		return geoLocation;
	}
	
	// Make Allow
	public GeoLocation makeAllow(String countryCode) {
		GeoLocation geoLocation = new GeoLocation(countryCode, true);
		dynamoDBMapper.save(geoLocation, updateConfig);
		return geoLocation;
	}
	
	// get location
	public GeoLocation getGeoLocation(String countryCode) {
		return dynamoDBMapper.load(GeoLocation.class, countryCode);
	}
	
	public List<GeoLocation> getGeoLocations() {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withBOOL(false));
        
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("is_allowed = :val1")
            .withExpressionAttributeValues(eav);
		return dynamoDBMapper.scan(GeoLocation.class, scanExpression);
	}
}
