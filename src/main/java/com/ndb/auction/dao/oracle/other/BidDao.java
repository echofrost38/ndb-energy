package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.BidHolding;

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
		m.setTokenAmount(rs.getDouble("TOKEN_AMOUNT"));
		m.setTokenPrice(rs.getDouble("TOKEN_PRICE"));
		m.setTotalAmount((double) (m.getTokenAmount() * m.getTokenPrice()));
		m.setPaidAmount(rs.getDouble("PAID_AMOUNT"));
		m.setTempTokenAmount(rs.getDouble("TEMP_TOKEN_AMOUNT"));
		m.setTempTokenPrice(rs.getDouble("TEMP_TOKEN_PRICE"));
		m.setDelta(rs.getLong("DELTA"));
		m.setPendingIncrease(rs.getBoolean("PENDING_INCREASE"));
		Type type = new TypeToken<Map<String, BidHolding>>() {}.getType();
		m.setHoldingList(gson.fromJson(rs.getString("HOLDING"), type));
		m.setPayType(rs.getInt("PAY_TYPE"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
		m.setPlacedAt(rs.getTimestamp("REG_DATE").getTime());
		m.setUpdatedAt(rs.getTimestamp("UPDATE_DATE").getTime());
		m.setStatus(rs.getInt("STATUS"));
		m.setRanking(rs.getInt("RANKING"));
		m.setRound(rs.getInt("ROUND"));
		return m;
	}

	public Bid placeBid(Bid m) {
		String sql = "MERGE INTO TBL_BID USING DUAL ON (USER_ID=? AND ROUND_ID=?)"
				+ "WHEN MATCHED THEN UPDATE SET TOKEN_AMOUNT=?, TOTAL_AMOUNT=?, TOKEN_PRICE=?, PAID_AMOUNT = ?, TEMP_TOKEN_AMOUNT=?, TEMP_TOKEN_PRICE=?, DELTA=?, PENDING_INCREASE=?, "
				+ "HOLDING=?,PAY_TYPE=?,CRYPTO_TYPE=?,REG_DATE=SYSDATE,UPDATE_DATE=SYSDATE,STATUS=?,RANKING=? "
				+ "WHEN NOT MATCHED THEN INSERT(USER_ID, ROUND_ID, TOKEN_AMOUNT, TOTAL_AMOUNT, TOKEN_PRICE, PAID_AMOUNT, TEMP_TOKEN_AMOUNT, TEMP_TOKEN_PRICE, "
				+ "DELTA, PENDING_INCREASE, HOLDING, PAY_TYPE, CRYPTO_TYPE, REG_DATE, UPDATE_DATE, STATUS, RANKING)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE,?,?)";
		jdbcTemplate.update(sql, m.getUserId(), m.getRoundId(), m.getTokenAmount(), m.getTotalAmount(), m.getTokenPrice(), m.getPaidAmount(), m.getTempTokenAmount(), m.getTempTokenPrice(), 
			m.getDelta(), m.isPendingIncrease(), gson.toJson(m.getHoldingList()), m.getPayType(), m.getCryptoType(), m.getStatus(), m.getRanking(), m.getUserId(), m.getRoundId(), m.getTokenAmount(), 
			m.getTotalAmount(), m.getTokenPrice(), m.getPaidAmount(), m.getTempTokenAmount(), m.getTempTokenPrice(), 
			m.getDelta(), m.isPendingIncrease(), gson.toJson(m.getHoldingList()), m.getPayType(), m.getCryptoType(), m.getStatus(), m.getRanking()
		);
		return m;
	}
	
	public Bid getBid(int userId, int roundId) {
		String sql = "SELECT TBL_BID.*,TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME, TBL_AUCTION.ROUND FROM TBL_BID LEFT JOIN TBL_USER_AVATAR ON TBL_BID.USER_ID=TBL_USER_AVATAR.ID LEFT JOIN TBL_AUCTION on TBL_BID.ROUND_ID = TBL_AUCTION.ID WHERE TBL_BID.USER_ID=? and TBL_BID.ROUND_ID=?";
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
		String sql = "UPDATE TBL_BID SET TOKEN_AMOUNT=?, TOTAL_AMOUNT=?, TOKEN_PRICE=?, PAID_AMOUNT = ?, TEMP_TOKEN_AMOUNT=?, TEMP_TOKEN_PRICE=?, "
				+ "DELTA=?, PENDING_INCREASE=?, HOLDING=?, PAY_TYPE=?, CRYPTO_TYPE=?, UPDATE_DATE=SYSDATE, STATUS=? WHERE USER_ID=? AND ROUND_ID=?";
		jdbcTemplate.update(sql, m.getTokenAmount(), m.getTotalAmount(), m.getTokenPrice(), m.getPaidAmount(), m.getTempTokenAmount(),
				m.getTempTokenPrice(), m.getDelta(), m.isPendingIncrease(), gson.toJson(m.getHoldingList()),
				m.getPayType(), m.getCryptoType(), m.getStatus(), m.getUserId(), m.getRoundId());
		return m;
	}

	public int updateBidHolding(Bid bid) {
		String sql = "UPDATE TBL_BID SET HOLDING = ? WHERE USER_ID = ? AND ROUND_ID = ?";
		return jdbcTemplate.update(sql, gson.toJson(bid.getHoldingList()), bid.getUserId(), bid.getRoundId());
	}

	public int updateStatus(int userId, int roundId, int payType, int status) {
		String sql = "UPDATE TBL_BID SET STATUS = ?, UPDATE_DATE=SYSDATE, PAY_TYPE=? WHERE USER_ID=? AND ROUND_ID=?";
		return jdbcTemplate.update(sql, status, payType, userId, roundId);
	}

	// For update paid amount 
	public int updatePaid(int userId, int auctionId, Double morePaid) {
		String sql = "UPDATE TBL_BID SET PAID_AMOUNT = PAID_AMOUNT + ? WHERE USER_ID = ? AND ROUND_ID = ?";
		return jdbcTemplate.update(sql, morePaid, userId, auctionId);
	}

	public int updateRanking(int userId, int auctionId, int ranking) {
		String sql = "UPDATE TBL_BID SET RANKING=? WHERE USER_ID=? AND ROUND_ID=?";
		return jdbcTemplate.update(sql, ranking, userId, auctionId);
	}

	public int updateTemp(int userId, int auctionId, Long newTokenAmount, Long newTokenPrice, double delta) {
		String sql = "UPDATE TBL_BID SET TEMP_TOKEN_AMOUNT = ?, TEMP_TOKEN_PRICE = ?, PENDING_INCREASE = ?, DELTA=? WHERE USER_ID = ? AND ROUND_ID = ?";
		return jdbcTemplate.update(sql, newTokenAmount, newTokenPrice, true, delta, userId, auctionId);
	}

	public int increaseAmount(int userId, int auctionId, double amount, double price) {
		String sql = "UPDATE TBL_BID SET TOKEN_AMOUNT = ?, TOKEN_PRICE = ?, PENDING_INCREASE = ? WHERE USER_ID = ? AND ROUND_ID = ?";
		return jdbcTemplate.update(sql, amount, price, false, userId, auctionId);
	}

	public List<Bid> getBidListByRound(int roundId) {
		String sql = "SELECT TBL_BID.*,TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME, TBL_AUCTION.ROUND FROM TBL_BID LEFT JOIN TBL_USER_AVATAR on TBL_BID.USER_ID=TBL_USER_AVATAR.ID LEFT JOIN TBL_AUCTION on TBL_BID.ROUND_ID = TBL_AUCTION.ID WHERE TBL_BID.ROUND_ID=? AND TBL_BID.STATUS != 0 ORDER BY TBL_BID.TOKEN_PRICE DESC";
		return jdbcTemplate.query(sql, new RowMapper<Bid>() {
			@Override
			public Bid mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, roundId);
	}

	public List<Bid> getBidListByUser(int userId) {
		String sql = "SELECT TBL_BID.*,TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME, TBL_AUCTION.ROUND FROM TBL_BID LEFT JOIN TBL_USER_AVATAR on TBL_BID.USER_ID=TBL_USER_AVATAR.ID LEFT JOIN TBL_AUCTION on TBL_BID.ROUND_ID = TBL_AUCTION.ID WHERE USER_ID=? ORDER BY ROUND_ID";
		return jdbcTemplate.query(sql, new RowMapper<Bid>() {
			@Override
			public Bid mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
	}

	public List<Bid> getBidList() {
		String sql = "SELECT TBL_BID.*,TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME, TBL_AUCTION.ROUND FROM TBL_BID LEFT JOIN TBL_USER_AVATAR on TBL_BID.USER_ID=TBL_USER_AVATAR.ID LEFT JOIN TBL_AUCTION on TBL_BID.ROUND_ID = TBL_AUCTION.ID ORDER BY ROUND_ID";
		return jdbcTemplate.query(sql, new RowMapper<Bid>() {
			@Override
			public Bid mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public List<Bid> getBidListFrom(Long from) {
		String sql = "SELECT TBL_BID.*,TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME, TBL_AUCTION.ROUND FROM TBL_BID LEFT JOIN TBL_USER_AVATAR on TBL_BID.USER_ID=TBL_USER_AVATAR.ID LEFT JOIN TBL_AUCTION on TBL_BID.ROUND_ID = TBL_AUCTION.ID ORDER BY ROUND_ID WHERE TBL_BID.REG_DATE > ?";
		return jdbcTemplate.query(sql, new RowMapper<Bid>() {
			@Override
			public Bid mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, new Timestamp(from));
	}

}
