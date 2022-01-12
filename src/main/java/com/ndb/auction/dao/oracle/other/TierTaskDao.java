package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_TIER_TASK")
public class TierTaskDao extends BaseOracleDao {

	private static TierTask extract(ResultSet rs) throws SQLException {
		TierTask m = new TierTask();
		m.setUserId(rs.getInt("USER_ID"));
		m.setVerification(rs.getBoolean("VERIFICATION"));
		m.setWallet(rs.getLong("WALLET"));
		m.setAuctions(rs.getString("AUCTIONS"));
		m.setDirect(rs.getLong("DIRECT"));
		m.setStaking(rs.getString("STAKING"));
		return m;
	}

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
