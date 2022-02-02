package com.ndb.auction.dao.oracle.transaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transaction.CoinsPayments;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_COINSPAYMENT")
public class CoinsPaymentDao extends BaseOracleDao {
    
    private static CoinsPayments extract(ResultSet rs) throws SQLException {
		CoinsPayments m = new CoinsPayments();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setRoundId(rs.getInt("ROUND_ID"));
		m.setAmount(rs.getDouble("AMOUNT"));
        m.setNetwork(rs.getString("NETWORK"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
		m.setCryptoAmount(rs.getDouble("CRYPTO_AMOUNT"));
		m.setTransactionType(rs.getInt("TXN_TYPE"));
		m.setPresaleOrderId(rs.getInt("PRESALE_ORDER_ID"));
		m.setStatus(rs.getInt("STATUS"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
		m.setUpdatedAt(rs.getTimestamp("UPDATED_AT").getTime());
		return m;
	}

    public int insert(CoinsPayments m) {
        String sql = "INSERT INTO TBL_COINSPAYMENT(ID,USER_ID,ROUND_ID,AMOUNT,NETWORK,CRYPTO_TYPE,CRYPTO_AMOUNT,TXN_TYPE,PRESALE_ORDER_ID,STATUS,CREATED_AT,UPDATED_AT)"
            + " VALUES(SEQ_TBL_COINSPAYMENT.NEXTVAL,?,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE)";
        return jdbcTemplate.update(sql, m.getUserId(), m.getRoundId(), m.getAmount(), m.getNetwork(), m.getCryptoType(), m.getCryptoAmount(), m.getTransactionType(), m.getPresaleOrderId(), 0);
    }

    public List<CoinsPayments> selectByUserId(int userId) {
        String sql = "SELECT * FROM TBL_COINSPAYMENT WHERE USER_ID=? ORDER BY ROUND_ID";
		return jdbcTemplate.query(sql, new RowMapper<CoinsPayments>() {
			@Override
			public CoinsPayments mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

    public CoinsPayments selectById(int id) {
        String sql = "SELECT * FROM TBL_COINSPAYMENT WHERE ID=? ORDER BY ROUND_ID";
		return jdbcTemplate.query(sql, new ResultSetExtractor<CoinsPayments>() {
			@Override
			public CoinsPayments extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
    }

    public int updateStatus(int id) {
        String sql = "UPDATE TBL_COINSPAYMENT SET STATUS = 1, UPDATED_AT = SYSDATE WHERE ID = ?";
        return jdbcTemplate.update(sql, id);
    }

}
