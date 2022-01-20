package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.tier.TierTask;

import org.springframework.jdbc.core.ResultSetExtractor;
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
		m.setWallet(rs.getDouble("WALLET"));
		m.setAuctionsByString(rs.getString("AUCTIONS"));
		m.setDirect(rs.getDouble("DIRECT"));
		return m;
	}

	public TierTask selectByUserId(int userId) {
		String sql = "SELECT * FROM TBL_TIER_TASK WHERE USER_ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<TierTask>() {
			@Override
			public TierTask extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, userId);
	}

	public int insertOrUpdate(TierTask m) {
		String sql = "MERGE INTO TBL_TIER_TASK USING DUAL ON (USER_ID=?)"
				+ "WHEN MATCHED THEN UPDATE SET VERIFICATION=?, WALLET=?, AUCTIONS=?, DIRECT=?"
				+ "WHEN NOT MATCHED THEN INSERT(USER_ID, VERIFICATION, WALLET, AUCTIONS, DIRECT)"
				+ "VALUES(?,?,?,?,?)";
		return jdbcTemplate.update(sql, m.getUserId(), m.getVerification(), m.getWallet(), m.getAuctionsString(),
				m.getDirect(), m.getUserId(), m.getVerification(), m.getWallet(), m.getAuctionsString(), m.getDirect());
	}

}
