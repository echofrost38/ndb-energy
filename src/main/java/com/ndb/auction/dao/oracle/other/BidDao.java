package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.Bid;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_BID")
public class BidDao extends BaseOracleDao {

	private static Bid extract(ResultSet rs) throws SQLException {
		Bid m = new Bid();
		m.setUserId(rs.getInt("USER_ID"));
		m.setRoundId(rs.getInt("ROUND_ID"));
		m.setPrefix(rs.getString("PREFIX"));
		m.setName(rs.getString("NAME"));
		m.setTokenAmount(rs.getLong("TOKEN_AMOUNT"));
		m.setTotalPrice(rs.getLong("TOTAL_PRICE"));
		m.setTokenPrice(rs.getLong("TOKEN_PRICE"));
		m.setTempTokenAmount(rs.getLong("TEMP_TOKEN_AMOUNT"));
		m.setTempTokenPrice(rs.getLong("TEMP_TOKEN_PRICE"));
		m.setDelta(rs.getLong("DELTA"));
		m.setPendingIncrease(rs.getBoolean("PENDING_INCREASE"));
		m.setHoldingList(gson.fromJson(rs.getString("HOLDING"), Map.class));
		m.setPayType(rs.getInt("PAY_TYPE"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
		m.setPlacedAt(rs.getTimestamp("REG_DATE").getTime());
		m.setUpdatedAt(rs.getTimestamp("UPDATE_DATE").getTime());
		m.setStatus(rs.getInt("STATUS"));
		return m;
	}

	public Bid placeBid(Bid m) {
		String sql = "INSERT INTO TBL_BID(USER_ID, ROUND_ID, TOKEN_AMOUNT, TOTAL_PRICE, TOKEN_PRICE, TEMP_TOKEN_AMOUNT, TEMP_TOKEN_PRICE, "
				+ "DELTA, PENDING_INCREASE, HOLDING, PAY_TYPE, CRYPTO_TYPE, REG_DATE, UPDATE_DATE, STATUS)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE,?)";
		jdbcTemplate.update(sql, m.getUserId(), m.getRoundId(), m.getTokenAmount(), m.getTotalPrice(),
				m.getTokenPrice(), m.getTempTokenAmount(), m.getTempTokenPrice(), m.getDelta(), m.isPendingIncrease(),
				gson.toJson(m.getHoldingList()), m.getPayType(), m.getCryptoType(),
				m.getStatus());
		return m;
	}
	
	public Bid getBid(int userId, int roundId) {
		String sql = "SELECT TBL_BID.*,TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME FROM TBL_BID LEFT JOIN TBL_USER_AVATAR on TBL_BID.USER_ID=TBL_USER_AVATAR.ID WHERE TBL_BID.USER_ID=? and TBL_BID.ROUND_ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<Bid>() {
			@Override
			public Bid extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, userId, roundId);
	}

	public Bid updateBid(Bid m) {
		String sql = "UPDATE TBL_BID SET TOKEN_AMOUNT=?, TOTAL_PRICE=?, TOKEN_PRICE=?, TEMP_TOKEN_AMOUNT=?, TEMP_TOKEN_PRICE=?, "
				+ "DELTA=?, PENDING_INCREASE=?, HOLDING=?, PAY_TYPE=?, CRYPTO_TYPE=?, UPDATE_DATE=SYSDATE, STATUS=? WHERE USER_ID=? AND ROUND_ID=?";
		jdbcTemplate.update(sql, m.getTokenAmount(), m.getTotalPrice(), m.getTokenPrice(), m.getTempTokenAmount(),
				m.getTempTokenPrice(), m.getDelta(), m.isPendingIncrease(), gson.toJson(m.getHoldingList()),
				m.getPayType(), m.getCryptoType(), m.getStatus(), m.getUserId(), m.getRoundId());
		return m;
	}

	public List<Bid> getBidListByRound(int roundId) {
		String sql = "SELECT TBL_BID.*,TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME FROM TBL_BID LEFT JOIN TBL_USER_AVATAR on TBL_BID.USER_ID=TBL_USER_AVATAR.ID WHERE ROUND_ID=? AND STATUS!=0 ORDER BY TOKEN_PRICE DESC";
		return jdbcTemplate.query(sql, new RowMapper<Bid>() {
			@Override
			public Bid mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, roundId);
	}

	public List<Bid> getBidListByUser(int userId) {
		String sql = "SELECT TBL_BID.*,TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME FROM TBL_BID LEFT JOIN TBL_USER_AVATAR on TBL_BID.USER_ID=TBL_USER_AVATAR.ID WHERE USER_ID=? AND STATUS!=0 ORDER BY ROUND_ID";
		return jdbcTemplate.query(sql, new RowMapper<Bid>() {
			@Override
			public Bid mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
	}

	public List<Bid> getBidList() {
		String sql = "SELECT TBL_BID.*,TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME FROM TBL_BID LEFT JOIN TBL_USER_AVATAR on TBL_BID.USER_ID=TBL_USER_AVATAR.ID ORDER BY ROUND_ID";
		return jdbcTemplate.query(sql, new RowMapper<Bid>() {
			@Override
			public Bid mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public List<Bid> getBidListFrom(Long from) {
		String sql = "SELECT TBL_BID.*,TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME FROM TBL_BID LEFT JOIN TBL_USER_AVATAR on TBL_BID.USER_ID=TBL_USER_AVATAR.ID ORDER BY ROUND_ID WHERE TBL_BID.REG_DATE > ?";
		return jdbcTemplate.query(sql, new RowMapper<Bid>() {
			@Override
			public Bid mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, new Timestamp(from));
	}

}
