package com.ndb.auction.dao;

import java.util.ArrayList;
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
import com.ndb.auction.models.AvatarSet;

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
		    .withKeyConditionExpression("group_id = :v1")
		    .withExpressionAttributeValues(eav);
		return dynamoDBMapper.query(AvatarComponent.class, queryExpression);
	}

	@Override
	public AvatarComponent getAvatarComponent(String groupId, String sKey) {
		return dynamoDBMapper.load(AvatarComponent.class, groupId, sKey);
	}

	@Override
	public AvatarComponent updateAvatarComponent(AvatarComponent component) {
		dynamoDBMapper.save(component, updateConfig);
		return component;
	}

	@Override
	public AvatarProfile createAvatarProfile(AvatarProfile avatar) {
		dynamoDBMapper.save(avatar);
		return avatar;
	}

	@Override
	public AvatarProfile updateAvatarProfile(AvatarProfile avatar) {
		dynamoDBMapper.save(avatar, updateConfig);
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

	@Override
	public AvatarProfile getAvatarProfileByName(String prefix) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(prefix));
        
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("fname = :val1")
            .withExpressionAttributeValues(eav);

        List<AvatarProfile> list = dynamoDBMapper.scan(AvatarProfile.class, scanExpression);
		if(list.size() == 0)
			return null;
		return list.get(0);
	}

	@Override
	public List<AvatarComponent> updateAvatarComponents(List<AvatarComponent> components) {
		dynamoDBMapper.batchSave(components, updateConfig);
		return components;
	}

	@Override
	public List<AvatarComponent> getAvatarComponentsBySet(List<AvatarSet> set) {
		List<AvatarComponent> list = new ArrayList<AvatarComponent>();
		
		for (AvatarSet field : set) {
			String groupId = field.getGroupId();
			String compId = field.getCompId();
			list.add(getAvatarComponent(groupId, compId));
		}
		
		return list;
	}

}
