package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Select;
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
	public Optional<User> getUserByEmail(String email) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(email));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("email = :v1")
		    .withExpressionAttributeValues(eav);
        List<User> users = dynamoDBMapper.scan(User.class, scanExpression);
        if(users.size() == 0) {
        	return null;
        }
		return Optional.of(users.get(0));		
	}

	@Override
	public User getUserById(String id) {
		return dynamoDBMapper.load(User.class, id);
	}

	@Override
	public List<User> getUserList() {
		return dynamoDBMapper.scan(User.class, new DynamoDBScanExpression());
	}

	public int getUserCount() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		scanExpression.setSelect(Select.COUNT);
		return dynamoDBMapper.count(User.class, scanExpression);
	}

	public List<User> getFirstPageOfUser(int limit) {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
			.withLimit(limit);
		return dynamoDBMapper.scanPage(User.class, scanExpression).getResults();
	}

	public List<User> getPaginatedUser(String exclusiveKey, int limit) {		
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put("id", new AttributeValue().withS(exclusiveKey));

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
			.withExclusiveStartKey(eav)
			.withLimit(limit);

		return dynamoDBMapper.scanPage(User.class, scanExpression).getResults();		
	}

	@Override
	public User deleteUser(String id) {
		User user = dynamoDBMapper.load(User.class, id);
		dynamoDBMapper.delete(user);
		return user;
	}

	@Override
	public User updateUser(User user) {
		dynamoDBMapper.save(user, updateConfig);
		return user;
	}

	@Override
	public User getUserByAvatar(String prefix, String name) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(prefix));
		eav.put(":v2", new AttributeValue().withS(name));

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
		    .withFilterExpression("avatar_prefix = :v1 and avatar_name = :v2")
		    .withExpressionAttributeValues(eav);
        List<User> users = dynamoDBMapper.scan(User.class, scanExpression);
        if(users.size() == 0) {
        	return null;
        }
		return users.get(0);	
	}

}
