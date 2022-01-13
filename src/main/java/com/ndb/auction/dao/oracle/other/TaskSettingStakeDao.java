package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.tier.StakeTask;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_TASK_SETTING_STAKE")
public class TaskSettingStakeDao extends BaseOracleDao {

	private static StakeTask extract(ResultSet rs) throws SQLException {
		StakeTask model = new StakeTask();
		model.setExpiredTime(rs.getLong("EXPIRED_TIME"));
		model.setRatio(rs.getDouble("RATIO"));
		return model;
	}

	public List<StakeTask> selectAll() {
		String sql = "SELECT * FROM TBL_TASK_SETTING_STAKE";
		return jdbcTemplate.query(sql, new RowMapper<StakeTask>() {
			@Override
			public StakeTask mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public int insert(StakeTask m) {
		String sql = "INSERT INTO TBL_TASK_SETTING_STAKE(EXPIRED_TIME, RATIO)"
				+ "VALUES(?, ?)";
		return jdbcTemplate.update(sql, m.getExpiredTime(), m.getRatio());
	}

	public int updateAll(List<StakeTask> list) {
		deleteAll();
		int result = 0;
		for (StakeTask m : list) {
			result += insert(m);
		}
		return result;
	}

}
