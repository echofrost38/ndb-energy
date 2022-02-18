package com.ndb.auction.dao.oracle.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.StripeTransaction;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_STRIPE_TX")
public class StripeTransactionDao extends BaseOracleDao {

	private static StripeTransaction extract(ResultSet rs) throws SQLException {
		StripeTransaction m = new StripeTransaction();
		m.setId(rs.getInt("ID"));
		m.setRoundId(rs.getInt("ROUND_ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setPaymentIntentId(rs.getString("PAYMENT_INTENT_ID"));
		m.setAmount(rs.getLong("AMOUNT"));
		m.setStatus(rs.getInt("STATUS"));
		m.setCreatedAt(rs.getTimestamp("CREATED_DATE").getTime());
		m.setUpdatedAt(rs.getTimestamp("UPDATED_DATE").getTime());
		m.setTransactionType(rs.getInt("TXN_TYPE"));
		return m;
	}

	public StripeTransaction createNewPayment(StripeTransaction m) {
		String sql = "INSERT INTO TBL_STRIPE_TX(ID, ROUND_ID, USER_ID, PAYMENT_INTENT_ID, AMOUNT, STATUS, CREATED_DATE, UPDATED_DATE, TXN_TYPE)"
				+ "VALUES(SEQ_STRIPE_TX.NEXTVAL,?,?,?,?,?,SYSDATE,SYSDATE, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(sql,
								new String[] { "ID" });
						int i = 1;
						ps.setInt(i++, m.getRoundId());
						ps.setInt(i++, m.getUserId());
						ps.setString(i++, m.getPaymentIntentId());
						ps.setLong(i++, m.getAmount());
						ps.setInt(i++, m.getStatus());
						ps.setInt(i++, m.getTransactionType());
						return ps;
					}
				}, keyHolder);
		m.setId(keyHolder.getKey().intValue());
		return m;
	}

	public int updatePaymentStatus(String paymentIntentId, int status) {
		String sql = "UPDATE TBL_STRIPE_TX SET STATUS=? WHERE PAYMENT_INTENT_ID=?";
		return jdbcTemplate.update(sql, status, paymentIntentId);
	}

	public List<StripeTransaction> getTransactionsByUser(int userId) {
		String sql = "SELECT * FROM TBL_STRIPE_TX WHERE USER_ID=? ORDER BY ID DESC";
		return jdbcTemplate.query(sql, new RowMapper<StripeTransaction>() {
			@Override
			public StripeTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
	}

	public List<StripeTransaction> getTransactionsByRound(int roundId) {
		String sql = "SELECT * FROM TBL_STRIPE_TX WHERE ROUND_ID=? ORDER BY ID DESC";
		return jdbcTemplate.query(sql, new RowMapper<StripeTransaction>() {
			@Override
			public StripeTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, roundId);
	}

	public List<StripeTransaction> getTransactions(int roundId, int userId) {
		String sql = "SELECT * FROM TBL_STRIPE_TX WHERE ROUND_ID=? AND USER_ID=? ORDER BY ID DESC";
		return jdbcTemplate.query(sql, new RowMapper<StripeTransaction>() {
			@Override
			public StripeTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, roundId, userId);
	}

	public StripeTransaction getTransactionById(int id) {
		String sql = "SELECT * FROM TBL_STRIPE_TX WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<StripeTransaction>() {
			@Override
			public StripeTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

}
