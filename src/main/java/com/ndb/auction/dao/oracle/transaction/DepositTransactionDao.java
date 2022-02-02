package com.ndb.auction.dao.oracle.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transaction.DepositTransaction;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_DEPOSIT_TXN")
public class DepositTransactionDao extends BaseOracleDao {
    
    private static DepositTransaction extract(ResultSet rs) throws SQLException {
		DepositTransaction m = new DepositTransaction();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setAmount(rs.getDouble("AMOUNT"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
		m.setCryptoAmount(rs.getDouble("CRYPTO_AMOUNT"));
		m.setStatus(rs.getInt("STATUS"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
		m.setUpdatedAt(rs.getTimestamp("UPDATED_AT").getTime());
		return m;
	}

    public DepositTransaction insert(DepositTransaction m) {
        String sql = "INSERT INTO TBL_DEPOSIT_TXN(ID,USER_ID,AMOUNT,CRYPTO_TYPE,CRYPTO_AMOUNT,STATUS,CREATED_AT,UPDATED_AT)"
				+ " VALUES(SEQ_DEPOSIT_TXN.NEXTVAL,?,?,?,?,?,SYSDATE,SYSDATE)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(sql,
								new String[] { "ID" });
						int i = 1;
						ps.setInt(i++, m.getUserId());
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

    public int updateStatus(int id, String currency, Double amount, Double fiatAmount) {
        String sql = "UPDATE TBL_DEPOSIT_TXN SET CRYPTO_TYPE = ?, CRYPTO_AMOUNT = ?, AMOUNT = ?, STATUS = 1, UPDATED_AT = SYSDATE WHERE ID = ?";
        return jdbcTemplate.update(sql, currency, amount, fiatAmount, id);
    }

    public List<DepositTransaction> selectByUser(int userId) {
        String sql = "SELECT * FROM TBL_DEPOSIT_TXN WHERE USER_ID = ?";
		return jdbcTemplate.query(sql, new RowMapper<DepositTransaction>() {
			@Override
			public DepositTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

	public DepositTransaction selectById(int id) {
		String sql = "SELECT * FROM TBL_DEPOSIT_TXN WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<DepositTransaction>() {
			@Override
			public DepositTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

}
