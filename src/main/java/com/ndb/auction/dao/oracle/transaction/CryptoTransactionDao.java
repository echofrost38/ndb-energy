package com.ndb.auction.dao.oracle.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transaction.CryptoTransaction;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_CRYPTO_TRANSACTION")
public class CryptoTransactionDao extends BaseOracleDao {

	private static CryptoTransaction extract(ResultSet rs) throws SQLException {
		CryptoTransaction m = new CryptoTransaction();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setRoundId(rs.getInt("ROUND_ID"));
		m.setAmount(rs.getDouble("AMOUNT"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
		m.setCryptoAmount(rs.getDouble("CRYPTO_AMOUNT"));
		m.setTransactionType(rs.getInt("TXN_TYPE"));
		m.setPresaleOrderId(rs.getInt("PRESALE_ID"));
		m.setStatus(rs.getInt("STATUS"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
		m.setUpdatedAt(rs.getTimestamp("UPDATED_AT").getTime());
		return m;
	}

	public CryptoTransaction insert(CryptoTransaction m) {
		String sql = "INSERT INTO TBL_CRYPTO_TRANSACTION(ID,USER_ID,ROUND_ID,TXN_TYPE,PRESALE_ID,AMOUNT,CRYPTO_TYPE,CRYPTO_AMOUNT,STATUS,CREATED_AT,UPDATED_AT)"
				+ " VALUES(SEQ_CRYPTO_TXN.NEXTVAL,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(sql,
								new String[] { "ID" });
						int i = 1;
						ps.setInt(i++, m.getUserId());
						ps.setInt(i++, m.getRoundId());
						ps.setInt(i++, m.getTransactionType());
						ps.setInt(i++, m.getPresaleOrderId());
						ps.setDouble(i++, m.getAmount());
						ps.setString(i++, m.getCryptoType());
						ps.setDouble(i++, m.getCryptoAmount());
						ps.setInt(i++, m.getStatus());
						return ps;
					}
				}, keyHolder);
		m.setId(keyHolder.getKey().intValue());
		return m;
	}

	public List<CryptoTransaction> selectAll(String orderby) {
		String sql = "SELECT * FROM TBL_CRYPTO_TRANSACTION";
		if (orderby == null)
			orderby = "ID";
		sql += " ORDER BY " + orderby;
		return jdbcTemplate.query(sql, new RowMapper<CryptoTransaction>() {
			@Override
			public CryptoTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public CryptoTransaction selectById(int id) {
		String sql = "SELECT * FROM TBL_CRYPTO_TRANSACTION WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<CryptoTransaction>() {
			@Override
			public CryptoTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

	public List<CryptoTransaction> selectByUserId(int userId) {
		String sql = "SELECT * FROM TBL_CRYPTO_TRANSACTION WHERE USER_ID=? ORDER BY ROUND_ID";
		return jdbcTemplate.query(sql, new RowMapper<CryptoTransaction>() {
			@Override
			public CryptoTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
	}

	public List<CryptoTransaction> selectByRoundId(int roundId) {
		String sql = "SELECT * FROM TBL_CRYPTO_TRANSACTION WHERE ROUND_ID=? ORDER BY NDB_PRICE DESC";
		return jdbcTemplate.query(sql, new RowMapper<CryptoTransaction>() {
			@Override
			public CryptoTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, roundId);
	}

	public List<CryptoTransaction> getTransaction(int userId, int roundId) {
		String sql = "SELECT * FROM TBL_CRYPTO_TRANSACTION WHERE ROUND_ID=? AND USER_ID=? ORDER BY UPDATED_AT";
		return jdbcTemplate.query(sql, new RowMapper<CryptoTransaction>() {
			@Override
			public CryptoTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, roundId, userId);
	}

	public int updateTransactionStatus(int id, int status, Double cryptoAmount, String cryptoType) {
		String sql = "UPDATE TBL_CRYPTO_TRANSACTION SET STATUS=?, UPDATE_AT=SYSDATE,CRYPTO_AMOUNT=?,CRYPTO_TYPE=? WHERE ID=?";
		return jdbcTemplate.update(sql, status, cryptoAmount, cryptoType, id);
	}

	public int deleteByTxnId(String txnId) {
		String sql = "DELETE FROM TBL_CRYPTO_TRANSACTION WHERE TXN_ID=?";
		return jdbcTemplate.update(sql, txnId);
	}

	public int deleteExpired(double days) {
		String sql = "DELETE FROM TBL_CRYPTO_TRANSACTION WHERE SYSDATE-CREATED_AT>?";
		return jdbcTemplate.update(sql, days);
	}

}
