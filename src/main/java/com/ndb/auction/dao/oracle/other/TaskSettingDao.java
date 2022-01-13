package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.TaskSetting;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_TASK_SETTING")
public class TaskSettingDao extends BaseOracleDao {

	private static TaskSetting extract(ResultSet rs) throws SQLException {
		TaskSetting m = new TaskSetting();
		m.setVerification(rs.getDouble("VERIFICATION"));
		m.setAuction(rs.getDouble("AUCTION"));
		m.setDirect(rs.getDouble("DIRECT"));
		return m;
	}

	public TaskSetting updateSetting(TaskSetting setting) {
		String sql = "UPDATE TBL_TASK_SETTING SET VERIFICATION=?, AUCTION=?, DIRECT=?, UPDATE_DATE=SYSDATE";
		jdbcTemplate.update(sql, setting.getVerification(), setting.getAuction(), setting.getDirect());
		return setting;
	}

	public TaskSetting getTaskSettings() {
		String sql = "SELECT * FROM TBL_TASK_SETTING";
		return jdbcTemplate.query(sql, new ResultSetExtractor<TaskSetting>() {
			@Override
			public TaskSetting extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		});
	}

}
