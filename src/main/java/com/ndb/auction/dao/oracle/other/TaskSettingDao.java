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
@Table(name = "TBL_TASK_SETTING")
public class TaskSettingDao extends BaseOracleDao {

	private static TaskSetting extract(ResultSet rs) throws SQLException {
		TaskSetting m = new TaskSetting();
		m.setVerification(rs.getLong("VERIFICATION"));
		m.setAuction(rs.getLong("AUCTION"));
		m.setDirect(rs.getLong("DIRECT"));
		return m;
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

}
