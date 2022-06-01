package com.ndb.auction.dao.oracle.transactions.paypal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.paypal.PaypalAuctionTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_PAYPAL_DEPOSIT")
public class PaypalDepositDao extends BaseOracleDao implements IPaypalDao {
    
    private static PaypalDepositTransaction extract(ResultSet rs) throws SQLException {
		PaypalDepositTransaction m = new PaypalAuctionTransaction();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setAmount(rs.getDouble("AMOUNT"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
        m.setConfirmedAt(rs.getTimestamp("UPDATED_AT").getTime());
		m.setStatus(rs.getBoolean("STATUS"));
		m.setFiatType(rs.getString("FIAT_TYPE"));
        m.setFiatAmount(rs.getDouble("FIAT_AMOUNT"));
        m.setPaypalOrderId(rs.getString("ORDER_ID"));
        m.setPaypalOrderStatus(rs.getString("ORDER_STATUS"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
        m.setCryptoPrice(rs.getDouble("CRYPTO_PRICE"));
        m.setFee(rs.getDouble("FEE"));
        m.setDeposited(rs.getDouble("DEPOSITED"));
        m.setIsShow(rs.getBoolean("IS_SHOW"));
		return m;
	}
    
    @Override
    public PaypalDepositTransaction selectByPaypalOrderId(String orderId) {
        String sql = "SELECT * FROM TBL_PAYPAL_DEPOSIT WHERE ORDER_ID=?";
		return jdbcTemplate.query(sql, rs -> {
			if (!rs.next())
				return null;
			return extract(rs);
		}, orderId);
    }

    public Transaction insert(Transaction _m) {
        PaypalDepositTransaction m = (PaypalDepositTransaction) _m;
        String sql = "INSERT INTO TBL_PAYPAL_DEPOSIT(ID,USER_ID,AMOUNT,CREATED_AT,UPDATED_AT,STATUS,FIAT_TYPE,FIAT_AMOUNT,ORDER_ID,ORDER_STATUS,CRYPTO_TYPE,CRYPTO_PRICE,FEE,DEPOSITED,IS_SHOW)"
        + " VALUES(SEQ_PAYPAL_DEPOSIT.NEXTVAL,?,?,SYSDATE,SYSDATE,0,?,?,?,?,?,?,?,?,1)";
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
                        ps.setString(i++, m.getFiatType());
                        ps.setDouble(i++, m.getFiatAmount());
                        ps.setString(i++, m.getPaypalOrderId());
                        ps.setString(i++, m.getPaypalOrderStatus());
                        ps.setString(i++, m.getCryptoType());
                        ps.setDouble(i++, m.getCryptoPrice());
                        ps.setDouble(i++, m.getFee());
                        ps.setDouble(i++, m.getDeposited());
                        return ps;
                    }
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;
    }

    public List<? extends Transaction> selectAll(String orderBy) {
        String sql = "SELECT * FROM TBL_PAYPAL_DEPOSIT";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public List<? extends Transaction> selectByUser(int userId, String orderBy, int status) {
        String sql = "SELECT * FROM TBL_PAYPAL_DEPOSIT WHERE USER_ID = ?";
		if(status == 0) {
            sql += " AND IS_SHOW = 1";
        }
        if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql,(rs, rownumber) -> extract(rs), userId);
    }

    public Transaction selectById(int id, int status) {
        String sql = "SELECT * FROM TBL_PAYPAL_DEPOSIT WHERE ID=?";
		if(status == 0) {
            sql += " AND IS_SHOW = 1";
        }
        return jdbcTemplate.query(sql, rs -> {
			if (!rs.next())
				return null;
			return extract(rs);
		}, id);
    }

    public int update(int id, int status) {
        String sql = "UPDATE TBL_PAYPAL_DEPOSIT SET STATUS = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    public int updateOrderStatus(int id, String status) {
        String sql = "UPDATE TBL_PAYPAL_DEPOSIT SET STATUS = 1, ORDER_STATUS = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    public List<PaypalDepositTransaction> selectRange(int userId, long from, long to) {
        String sql = "SELECT * FROM TBL_PAYPAL_DEPOSIT WHERE USER_ID = ? AND CREATED_AT > ? AND CREATED_AT < ? ORDER BY ID DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, new Timestamp(from), new Timestamp(to));
    }

    public int changeShowStatus(int id, int status) {
        var sql = "UPDATE TBL_PAYPAL_DEPOSIT SET IS_SHOW = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, status, id);
    }
    
}
