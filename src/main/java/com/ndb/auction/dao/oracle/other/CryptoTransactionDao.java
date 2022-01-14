package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.Coin;
import com.ndb.auction.models.CryptoTransaction;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_CRYPTO_TRANSACTION")
public class CryptoTransactionDao extends BaseOracleDao {

	private static CryptoTransaction extract(ResultSet rs) throws SQLException {
		CryptoTransaction m = new CryptoTransaction();
		m.setTxnId(rs.getString("TXN_ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setRoundId(rs.getInt("ROUND_ID"));
		m.setAmount(rs.getString("AMOUNT"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
		m.setCryptoAmount(rs.getString("CRYPTO_AMOUNT"));
		m.setStatus(rs.getInt("STATUS"));
		m.setCode(rs.getString("CODE"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
		m.setUpdatedAt(rs.getTimestamp("UPDATED_AT").getTime());
		return m;
	}

	public int insert(CryptoTransaction m) {
		String sql = "INSERT INTO TBL_CRYPTO_TRANSACTION(TXN_ID,CODE,USER_ID,ROUND_ID,AMOUNT,CRYPTO_TYPE,CRYPTO_AMOUNT,STATUS,CREATED_AT,UPDATED_AT)"
				+ " VALUES(?,?,?,?,?,?,?,?,SYSDATE,SYSDATE)";
		return jdbcTemplate.update(sql, m.getTxnId(), m.getCode(), m.getUserId(), m.getRoundId(), m.getAmount(),
				m.getCryptoType(), m.getCryptoAmount(), m.getStatus());
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

	public CryptoTransaction selectByCode(String code) {
		String sql = "SELECT * FROM TBL_CRYPTO_TRANSACTION WHERE CODE=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<CryptoTransaction>() {
			@Override
			public CryptoTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, code);
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
		String sql = "SELECT * FROM TBL_CRYPTO_TRANSACTION WHERE ROUND_ID=? ORDER BY AMOUNT DESC";
		return jdbcTemplate.query(sql, new RowMapper<CryptoTransaction>() {
			@Override
			public CryptoTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, roundId);
	}

	public List<CryptoTransaction> getTransaction(int userId, int roundId) {
		String sql = "SELECT * FROM TBL_CRYPTO_TRANSACTION WHERE ROUND_ID=? AND USER_ID=? ORDER BY ID DESC";
		return jdbcTemplate.query(sql, new RowMapper<CryptoTransaction>() {
			@Override
			public CryptoTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, roundId, userId);
	}

	public int updateTransactionStatus(String code, int status, String cryptoAmount, String cryptoType) {
		String sql = "UPDATE TBL_CRYPTO_TRANSACTION SET STATUS=?, UPDATE_AT=SYSDATE,CRYPTO_AMOUNT=?,CRYPTO_TYPE=? WHERE CODE=?";
		return jdbcTemplate.update(sql, status, cryptoAmount, cryptoType, code);
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
