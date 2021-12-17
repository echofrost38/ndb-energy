package com.ndb.auction.dao;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.UserTier;
import com.ndb.auction.models.tier.TierTask;

import org.springframework.stereotype.Repository;

@Repository
public class TierDao extends BaseDao {
    
    public TierDao(DynamoDBMapper dynamoDBMapper) {
        super(dynamoDBMapper);
    }

    // User Tier
	public UserTier addNewUserTier(UserTier tier) {
		dynamoDBMapper.save(tier);
		return tier;
	}

	public List<UserTier> getUserTiers() {
		return dynamoDBMapper.scan(UserTier.class, new DynamoDBScanExpression());
	}

	public UserTier updateUserTier(UserTier tier) {
		dynamoDBMapper.save(tier, updateConfig);
		return tier;
	}

	public int deleteUserTier(int level) {
		UserTier tier = new UserTier();
		tier.setLevel(level);
		dynamoDBMapper.delete(tier);
		return level;
	}

	// Task Setting
	public TaskSetting addNewSetting(TaskSetting setting) {
		dynamoDBMapper.save(setting);
		return setting;
	}

	public TaskSetting updateSetting(TaskSetting setting) {
		dynamoDBMapper.save(setting, updateConfig);
		return setting;
	}

	public TaskSetting getTaskSettings() {
		return dynamoDBMapper.load(TaskSetting.class, "Setting");
	}

	// Tier Task
	public TierTask createNewTask(TierTask tierTask) {
		dynamoDBMapper.save(tierTask);
		return tierTask;
	}

	public TierTask updateTierTask(TierTask tierTask) {
		dynamoDBMapper.save(tierTask, updateConfig);
		return tierTask;
	}
}
