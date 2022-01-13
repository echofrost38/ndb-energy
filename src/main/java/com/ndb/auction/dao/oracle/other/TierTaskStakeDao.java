package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.tier.StakeHist;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_TIER_TASK_STAKE")
public class TierTaskStakeDao extends BaseOracleDao {

	private static StakeHist extract(ResultSet rs) throws SQLException {
		StakeHist m = new StakeHist();
		m.setExpiredTime(rs.getLong("EXPIRED_TIME"));
		m.setAmount(rs.getLong("AMOUNT"));
		return m;
	}

	public List<StakeHist> selectAll(int userId) {
		String sql = "SELECT * FROM TBL_TIER_TASK_STAKE WHERE USER_ID=?";
		return jdbcTemplate.query(sql, new RowMapper<StakeHist>() {
			@Override
			public StakeHist mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
	}

	public int insert(int userId, StakeHist m) {
		String sql = "INSERT INTO TBL_TIER_TASK_STAKE(USER_ID, EXPIRED_TIME, AMOUNT)"
				+ "VALUES(?, ?, ?)";
		return jdbcTemplate.update(sql, userId, m.getExpiredTime(), m.getAmount());
	}

	public int updateAll(int userId, List<StakeHist> list) {
		deleteAll();
		int result = 0;
		for (StakeHist m : list) {
			result += insert(userId, m);
		}
		return result;
	}

}
