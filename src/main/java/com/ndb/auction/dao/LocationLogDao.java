package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.LocationLog;

import org.springframework.stereotype.Repository;

@Repository
public class LocationLogDao extends BaseDao {

    public LocationLogDao(DynamoDBMapper dynamoDBMapper) {
        super(dynamoDBMapper);
    }

    public LocationLog addLog(LocationLog log) {
        dynamoDBMapper.save(log);
        return log;
    }

    public int getCountByIp(String userId, String ipAddress) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(userId));
        eav.put(":v2", new AttributeValue().withS(ipAddress));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("user_id = :v1 and ip_address = :v2")
                .withExpressionAttributeValues(eav);
        return dynamoDBMapper.count(LocationLog.class, scanExpression);
    }

    public int getCountByCountryAndCity(String userId, String country, String city) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(userId));
        eav.put(":v2", new AttributeValue().withS(country));
        eav.put(":v3", new AttributeValue().withS(city));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("user_id = :v1 and country = :v2 and city = :v3")
                .withExpressionAttributeValues(eav);
        return dynamoDBMapper.count(LocationLog.class, scanExpression);
    }

    public LocationLog getLogById(String userId, String logId) {
        return dynamoDBMapper.load(LocationLog.class, userId, logId);
    }

    public List<LocationLog> getLogByUser(String userId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(userId));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("user_id = :v1")
                .withExpressionAttributeValues(eav);
        return dynamoDBMapper.scan(LocationLog.class, scanExpression);
    }

}
