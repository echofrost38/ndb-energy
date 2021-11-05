package com.ndb.auction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;

@Repository
public class AvatarDao extends BaseDao implements IAvatarDao {

	public AvatarDao(DynamoDBMapper dynamoDBMapper) {
		super(dynamoDBMapper);
	}

	@Override
	public AvatarComponent createAvatarComponent(AvatarComponent component) {
		dynamoDBMapper.save(component);
		return component;
	}

	@Override
	public List<AvatarComponent> getAvatarComponents() {
		return dynamoDBMapper.scan(AvatarComponent.class, new DynamoDBScanExpression());
	}

	@Override
	public List<AvatarComponent> getAvatarComponentsByGid(String groupId) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":v1", new AttributeValue().withS(groupId));
		DynamoDBQueryExpression<AvatarComponent> queryExpression = new DynamoDBQueryExpression<AvatarComponent>()
		    .withFilterExpression("group_id = :v1")
		    .withExpressionAttributeValues(eav);
		return dynamoDBMapper.query(AvatarComponent.class, queryExpression);
	}

	@Override
	public AvatarComponent getAvatarComponent(String groupId, Integer sKey) {
		return dynamoDBMapper.load(AvatarComponent.class, groupId, sKey);
	}

	@Override
	public AvatarComponent updateAvatarComponent(AvatarComponent component) {
		dynamoDBMapper.save(component);
		return component;
	}

	@Override
	public AvatarProfile createAvatarProfile(AvatarProfile avatar) {
		dynamoDBMapper.save(avatar);
		return avatar;
	}

	@Override
	public AvatarProfile updateAvatarProfile(AvatarProfile avatar) {
		dynamoDBMapper.save(avatar);
		return avatar;
	}

	@Override
	public List<AvatarProfile> getAvatarProfiles() {
		return dynamoDBMapper.scan(AvatarProfile.class, new DynamoDBScanExpression());
	}

	@Override
	public AvatarProfile getAvatarProfile(String id) {
		return dynamoDBMapper.load(AvatarProfile.class, id);
	}

}
