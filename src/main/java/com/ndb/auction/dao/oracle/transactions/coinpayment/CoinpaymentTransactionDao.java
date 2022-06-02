package com.ndb.auction.dao.oracle.transactions.coinpayment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentDepositTransaction;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_COINPAYMENT_TRANSACTION")
public class CoinpaymentTransactionDao extends BaseOracleDao {

    // 8 hours to long epoch
    private static final long _8_HOURS = 8 * 60 * 60 * 1000L;
    
    private static CoinpaymentDepositTransaction extract(ResultSet rs) throws SQLException {
		CoinpaymentDepositTransaction m = new CoinpaymentDepositTransaction();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setAmount(rs.getDouble("AMOUNT"));
		m.setFee(rs.getDouble("FEE"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
		m.setDepositStatus(rs.getInt("STATUS"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
		m.setNetwork(rs.getString("NETWORK"));
        m.setCryptoAmount(rs.getDouble("CRYPTO_AMOUNT"));
		m.setConfirmedAt(rs.getTimestamp("UPDATED_AT").getTime());
        m.setDepositAddress(rs.getString("DEPOSIT_ADDR"));
        m.setCoin(rs.getString("COIN"));
		m.setOrderId(rs.getInt("ORDER_ID"));
        m.setOrderType(rs.getString("ORDER_TYPE"));
        m.setTxHash(rs.getString("TX_HASH"));
        m.setIsShow(rs.getBoolean("IS_SHOW"));
		return m;
	}

    public CoinpaymentDepositTransaction insert(CoinpaymentDepositTransaction m) {
        String sql = "INSERT INTO TBL_COINPAYMENT_TRANSACTION(ID,USER_ID,AMOUNT,FEE,CREATED_AT,STATUS,CRYPTO_TYPE,NETWORK,CRYPTO_AMOUNT,UPDATED_AT,DEPOSIT_ADDR,COIN,ORDER_ID,ORDER_TYPE,TX_HASH,IS_SHOW)"
				+ " VALUES(SEQ_COINPAY_TRANSACTION.NEXTVAL,?,?,?,SYSDATE,0,?,?,?,SYSDATE,?,?,?,?,?,1)";
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
                    ps.setDouble(i++, m.getFee());
                    ps.setString(i++, m.getCryptoType());
                    ps.setString(i++, m.getNetwork());
                    ps.setDouble(i++, m.getCryptoAmount());
                    ps.setString(i++, m.getDepositAddress());
                    ps.setString(i++, m.getCoin());
                    ps.setInt(i++, m.getOrderId());
                    ps.setString(i++, m.getOrderType());
                    ps.setString(i++, null);
                    return ps;
                }
            }, keyHolder);
		m.setId(keyHolder.getKey().intValue());
		return m;
    }

    // getting transactions by order type
//---------------------- for user ----------------------
    public List<CoinpaymentDepositTransaction> selectByOrderTypeByUser(int userId, int showStatus, String orderType) {
        var sql = "SELECT * FROM TBL_COINPAYMENT_TRANSACTION WHERE USER_ID = ? AND ORDER_TYPE = ? AND STATUS != ?";
        if(showStatus == 0) {
            sql += "AND IS_SHOW = 1";
        }
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), 
            userId, orderType, CoinpaymentDepositTransaction.EXPIRED);
    }

    public List<CoinpaymentDepositTransaction> selectByOrderIdByUser(int userId, int orderId, String orderType) {
        var sql = "SELECT * FROM TBL_COINPAYMENT_TRANSACTION WHERE USER_ID = ? AND ORDER_ID = ? AND ORDER_TYPE = ? AND STATUS != ?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, orderId, orderType, CoinpaymentDepositTransaction.EXPIRED);
    }

    public CoinpaymentDepositTransaction selectByTxHash(String txHash) {
        var sql = "SELECT * FROM TBL_COINPAYMENT_TRANSACTION WHERE TX_HASH = ? AND STATUS != ?";
        return jdbcTemplate.query(sql, rs -> {
			if (!rs.next())
				return null;
			return extract(rs);
		}, txHash, CoinpaymentDepositTransaction.EXPIRED);
    }

    public CoinpaymentDepositTransaction selectById(int id) {
        var sql = "SELECT * FROM TBL_COINPAYMENT_TRANSACTION WHERE ID = ? AND STATUS != ?";
        return jdbcTemplate.query(sql, rs -> {
			if (!rs.next())
				return null;
			return extract(rs);
		}, id, CoinpaymentDepositTransaction.EXPIRED);
    }


    // update status
    public int updateStatus(int id, int newStatus, double cryptoAmount, String cryptoType) {
        var sql = "UPDATE TBL_COINPAYMENT_TRANSACTION SET STATUS=?, UPDATED_AT=SYSDATE, CRYPTO_AMOUNT = ?, CRYPTO_TYPE = ? WHERE ID=?";
		return jdbcTemplate.update(sql, newStatus, cryptoAmount, cryptoType,  id);
    }

    public int updateStatus(int id, int newStatus, double cryptoAmount, double deposited, double fee, String cryptoType) {
        var sql = "UPDATE TBL_COINPAYMENT_TRANSACTION SET STATUS=?, UPDATED_AT=SYSDATE, AMOUNT = ?, CRYPTO_AMOUNT=?,FEE=?, CRYPTO_TYPE = ? WHERE ID=?";
		return jdbcTemplate.update(sql, newStatus, cryptoAmount, deposited, fee, cryptoType,  id);
    }

    // update transaction hash
    public int updateTxHash(int id, String txHash) {
        var sql = "UPDATE TBL_COINPAYMENT_TRANSACTION SET TX_HASH = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, txHash, id);
    }

    // update deposit address
    public int updateDepositAddress(int id, String depositAddr) {
        var sql = "UPDATE TBL_COINPAYMENT_TRANSACTION SET DEPOSIT_ADDR = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, depositAddr, id);
    }

    public int updateDepositAddress(int id, double cryptoAmount, double fee, String depositAddr) {
        var sql = "UPDATE TBL_COINPAYMENT_TRANSACTION SET CRYPTO_AMOUNT = ?, FEE = ?, DEPOSIT_ADDR = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, cryptoAmount, depositAddr, id);
    }

    public int changeShowStatus(int id, int showStatus) {
        var sql = "UPDATE TBL_COINPAYMENT_TRANSACTION SET IS_SHOW = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, showStatus, id);
    }


// ------------------------- clean expired ---------------------------------------------
    public int checkExpired() {
        var _8hoursBefore = System.currentTimeMillis() - _8_HOURS;
        var sql = "UPDATE TBL_COINPAYMENT_TRANSACTION SET STATUS = ? WHERE CREATED_AT < ? AND STATUS = ?";
        return jdbcTemplate.update(sql, 
            CoinpaymentDepositTransaction.EXPIRED, 
            _8hoursBefore,
            CoinpaymentDepositTransaction.PENDING
        );
    }

    public List<CoinpaymentDepositTransaction> selectRange(int userId, long from, long to, String orderType) {
        String sql = "SELECT * FROM TBL_COINPAYMENT_TRANSACTION WHERE USER_ID = ? AND ORDER_TYPE = ? AND CREATED_AT > ? AND CREATED_AT < ? AND STATUS != ? ORDER BY ID DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, orderType, new Timestamp(from), new Timestamp(to), 
            CoinpaymentDepositTransaction.EXPIRED
        );
    }

// -------------------------- Admin -----------------------------------------------
    public List<CoinpaymentDepositTransaction> selectByOrderType(String orderType) {
        var sql = "SELECT * FROM TBL_COINPAYMENT_TRANSACTION WHERE ORDER_TYPE = ? AND STATUS != ?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), orderType, CoinpaymentDepositTransaction.EXPIRED);
    }

    public List<CoinpaymentDepositTransaction> selectByOrderId(int orderId, String orderType) {
        var sql = "SELECT * FROM TBL_COINPAYMENT_TRANSACTION WHERE ORDER_TYPE = ? AND ORDER_ID = ? AND STATUS != ?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), orderType, orderId, CoinpaymentDepositTransaction.EXPIRED);
    }

}
