package com.ndb.auction.dao.oracle.other;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.Tier;
import com.ndb.auction.models.tier.TierTask;

import org.springframework.stereotype.Repository;

@Repository
public class TierDao extends BaseOracleDao {

	private static final String TABLE_NAME = "TBL_USER";

	private static User extract(ResultSet rs) throws SQLException {
		User m = new User();
		m.setId(rs.getInt("ID"));
		m.setEmail(rs.getString("EMAIL"));
		m.setPassword(rs.getString("PASSWORD"));
		m.setName(rs.getString("NAME"));
		m.setCountry(rs.getString("COUNTRY"));
		m.setPhone(rs.getString("PHONE"));
		m.setBirthday(rs.getTimestamp("BIRTHDAY"));
		m.setRegDate(rs.getTimestamp("REG_DATE"));
		m.setLastLoginDate(rs.getTimestamp("LAST_LOGIN_DATE"));
		m.setLastPasswordChangeDate(rs.getTimestamp("LAST_PASSWORD_CHANGE_DATE"));
		m.setRole(rs.getString("ROLE"));
		m.setTierLevel(rs.getInt("TIER_LEVEL"));
		m.setTierPoint(rs.getInt("TIER_POINT"));
		m.setProvider(rs.getString("PROVIDER"));
		m.setProviderId(rs.getString("PROVIDER_ID"));
		m.setNotifySetting(rs.getInt("NOTIFY_SETTING"));
		m.setDeleted(rs.getInt("DELETED"));
		return m;
	}

	public UserDao() {
		super(TABLE_NAME);
	}

    // User Tier
	public Tier addNewUserTier(Tier tier) {
		dynamoDBMapper.save(tier);
		return tier;
	}

	public List<Tier> getUserTiers() {
		return dynamoDBMapper.scan(Tier.class, new DynamoDBScanExpression());
	}

	public Tier updateUserTier(Tier tier) {
		dynamoDBMapper.save(tier, updateConfig);
		return tier;
	}

	public int deleteUserTier(int level) {
		Tier tier = new Tier();
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

	public TierTask getTierTask(int userId) {
		return dynamoDBMapper.load(TierTask.class, userId);
	}
}
