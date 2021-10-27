package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.User;

@Repository
public class UserDao extends BaseDao implements IUserDao {
	
	@Autowired
	public UserDao(DynamoDBMapper dynamoDBMapper) {
		super(dynamoDBMapper);
	}

	@Override
	public User createUser(User user) {
		dynamoDBMapper.save(user);
		return user;
	}

	@Override
	public User getUserByEmail(String email) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(email));
		DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
		    .withKeyConditionExpression("email = :v1")
		    .withExpressionAttributeValues(eav);
        List<User> users = dynamoDBMapper.query(User.class, queryExpression);
		return users.get(0);		
	}

	@Override
	public User getUserById(String id) {
		return dynamoDBMapper.load(User.class, id);
	}

	@Override
	public List<User> getUserList() {
		return dynamoDBMapper.scan(User.class, new DynamoDBScanExpression());
	}

	@Override
	public User deleteUser(String id) {
		User user = dynamoDBMapper.load(User.class, id);
		dynamoDBMapper.delete(user);
		return user;
	}

}
