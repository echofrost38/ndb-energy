package com.ndb.auction.dao.oracle.transaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transaction.WithdrawTransaction;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_WITHDRAW_TXN")
public class WithdrawTransactionDao extends BaseOracleDao {
    
    private static WithdrawTransaction extract(ResultSet rs) throws SQLException {
		WithdrawTransaction m = new WithdrawTransaction();
		m.setId(rs.getInt("ID"));
        m.setUserId(rs.getInt("USER_ID"));
        m.setTransactionHash(rs.getString("TXN_HASH"));
        m.setFrom(rs.getString("S_FROM"));
        m.setTo(rs.getString("S_TO"));
        m.setValue(rs.getDouble("N_VALUE"));
        m.setBlockNumber(rs.getString("BLOCK_NUMBER"));
        m.setStatus(rs.getBoolean("STATUS"));
        m.setRegDate(rs.getTimestamp("REG_DATE").getTime());
        m.setUpdateDate(rs.getTimestamp("UPDATE_DATE").getTime());
        return m;
	}

    public int insert(WithdrawTransaction m) {
        String sql = "INSERT INTO TBL_WITHDRAW_TXN(ID,USER_ID,TXN_HASH,S_FROM,S_TO,N_VALUE,BLOCK_NUMBER,STATUS,REG_DATE,UPDATE_DATE)"
            + "VALUES(SEQ_WITHDRAW_TXN.NEXTVAL,?,?,?,?,?,?,0,SYSDATE,SYSDATE)";
        return jdbcTemplate.update(sql, m.getUserId(), m.getTransactionHash(), m.getFrom(), m.getTo(), m.getValue(), m.getBlockNumber());
    }

    public int update(String from, String to, Double value, String blockNumber, String txnHash) {
        String sql = "UPDATE TBL_WITHDRAW_TXN SET S_FROM = ?, S_TO = ?, N_VALUE = ?, BLOCK_NUMBER = ?, UPDATE_DATE = SYSDATE WHERE TXN_HASH = ?";
        return jdbcTemplate.update(sql, from, to, value, blockNumber, txnHash);
    }

    public int udpateStatus(String txnHash) {
        String sql = "UPDATE TBL_WITHDRAW_TXN SET STATUS = 1, UPDATE_DATE = SYSDATE WHERE TXN_HASH = ?";
        return jdbcTemplate.update(sql, txnHash);
    }

    public WithdrawTransaction selectByHash(String txnHash) {
        String sql = "SELECT * FROM TBL_WITHDRAW_TXN WHERE TXN_HASH = ?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<WithdrawTransaction>() {
			@Override
			public WithdrawTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, txnHash);
    }

    public List<WithdrawTransaction> selectyByUser(int userId) {
        String sql = "SELECT * FROM TBL_WITHDRAW_TXN WHERE USER_ID = ?";
		return jdbcTemplate.query(sql, new RowMapper<WithdrawTransaction>() {
			@Override
			public WithdrawTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

}
