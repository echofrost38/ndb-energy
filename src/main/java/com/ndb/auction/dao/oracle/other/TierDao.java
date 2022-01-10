package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_TIER")
public class TierDao extends BaseOracleDao {

	private static Tier extract(ResultSet rs) throws SQLException {
		Tier m = new Tier();
		m.setLevel(rs.getInt("LEVEL"));
		m.setName(rs.getString("NAME"));
		m.setPoint(rs.getLong("POINT"));
		return m;
	}

	// User Tier
	public Tier addNewUserTier(Tier m) {
		String sql = "INSERT INTO TBL_TIER(LEVEL, NAME, POINT)"
				+ "VALUES(?,?,?)";
		jdbcTemplate.update(sql, m.getLevel(), m.getName(), m.getPoint());
		return m;
	}

	public List<Tier> getUserTiers() {
		String sql = "SELECT * FROM TBL_TIER";
		return jdbcTemplate.query(sql, new RowMapper<Tier>() {
			@Override
			public Tier mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public Tier updateUserTier(Tier m) {
		String sql = "UPDATE TBL_TIER SET NAME=?, POINT=? WHERE LEVEL=?";
		jdbcTemplate.update(sql, m.getName(), m.getPoint(), m.getLevel());
		return m;
	}

	public int deleteUserTier(int level) {
		String sql = "DELETE FROM TBL_TIER WHERE LEVEL=?";
		jdbcTemplate.update(sql, level);
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
