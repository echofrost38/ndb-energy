package com.ndb.auction.dao.oracle.transaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transaction.DepositTransaction;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_DEPOSIT_TXN")
public class DepositTransactionDao extends BaseOracleDao {
    
    private static DepositTransaction extract(ResultSet rs) throws SQLException {
		DepositTransaction m = new DepositTransaction();
		m.setTxnId(rs.getString("TXN_ID"));
		m.setCode(rs.getString("CODE"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setAmount(rs.getDouble("AMOUNT"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
		m.setCryptoAmount(rs.getDouble("CRYPTO_AMOUNT"));
		m.setStatus(rs.getInt("STATUS"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
		m.setUpdatedAt(rs.getTimestamp("UPDATED_AT").getTime());
		return m;
	}

    public int insert(DepositTransaction m) {
        String sql = "INSERT INTO TBL_DEPOSIT_TXN(TXN_ID,CODE,USER_ID,AMOUNT,CRYPTO_TYPE,CRYPTO_AMOUNT,STATUS,CREATED_AT,UPDATED_AT)"
				+ " VALUES(?,?,?,?,?,?,?,?,SYSDATE,SYSDATE)";
		return jdbcTemplate.update(sql, m.getTxnId(), m.getCode(), m.getUserId(), m.getAmount(), m.getCryptoType(), m.getCryptoAmount(), m.getStatus(), m.getCreatedAt(), m.getUpdatedAt());
    }

    public int updateStatus(String code) {
        String sql = "UPDATE TBL_DEPOSIT_TXN SET STATUS = 1, UPDATED_AT = SYSDATE WHERE CODE = ?";
        return jdbcTemplate.update(sql, code);
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

	public DepositTransaction selectByCode(String code) {
		String sql = "SELECT * FROM TBL_DEPOSIT_TXN WHERE CODE=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<DepositTransaction>() {
			@Override
			public DepositTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, code);
	}

}
