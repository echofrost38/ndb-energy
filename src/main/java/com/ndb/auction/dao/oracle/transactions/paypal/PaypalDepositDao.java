package com.ndb.auction.dao.oracle.transactions.paypal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.dao.oracle.transactions.ITransactionDao;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.paypal.PaypalAuctionTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_PAYPAL_DEPOSIT")
public class PaypalDepositDao extends BaseOracleDao implements ITransactionDao, IPaypalDao {
    
    private static PaypalDepositTransaction extract(ResultSet rs) throws SQLException {
		PaypalDepositTransaction m = new PaypalAuctionTransaction();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setAmount(rs.getLong("AMOUNT"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
        m.setConfirmedAt(rs.getTimestamp("UPDATED_AT").getTime());
		m.setStatus(rs.getBoolean("STATUS"));
		m.setFiatType(rs.getString("FIAT_TYPE"));
        m.setFiatAmount(rs.getLong("FIAT_AMOUNT"));
        m.setPaypalOrderId(rs.getString("ORDER_ID"));
        m.setPaypalOrderStatus(rs.getString("ORDER_STATUS"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
        m.setCryptoPrice(rs.getDouble("CRYPTO_PRICE"));
        m.setFee(rs.getDouble("FEE"));
        m.setDeposited(rs.getDouble("DEPOSITED"));
		return m;
	}
    
    @Override
    public PaypalDepositTransaction selectByPaypalOrderId(String orderId) {
        String sql = "SELECT * FROM TBL_PAYPAL_DEPOSIT WHERE ORDER_ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<PaypalDepositTransaction>() {
			@Override
			public PaypalDepositTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, orderId);
    }

    @Override
    public Transaction insert(Transaction _m) {
        PaypalDepositTransaction m = (PaypalDepositTransaction) _m;
        String sql = "INSERT INTO TBL_PAYPAL_DEPOSIT(ID,USER_ID,AMOUNT,CREATED_AT,UPDATED_AT,STATUS,FIAT_TYPE,FIAT_AMOUNT,ORDER_ID,ORDER_STATUS,CRYPTO_TYPE,CRYPTO_PRICE,FEE,DEPOSITED)"
        + " VALUES(SEQ_PAYPAL_DEPOSIT.NEXTVAL,?,?,SYSDATE,SYSDATE,0,?,?,?,?,?,?,?,?)";
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

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        String sql = "SELECT * FROM TBL_PAYPAL_DEPOSIT";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, new RowMapper<PaypalDepositTransaction>() {
			@Override
			public PaypalDepositTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        String sql = "SELECT * FROM TBL_PAYPAL_DEPOSIT WHERE USER_ID = ?";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, new RowMapper<PaypalDepositTransaction>() {
			@Override
			public PaypalDepositTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

    @Override
    public Transaction selectById(int id) {
        String sql = "SELECT * FROM TBL_PAYPAL_DEPOSIT WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<PaypalDepositTransaction>() {
			@Override
			public PaypalDepositTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
    }

    @Override
    public int update(int id, int status) {
        String sql = "UPDATE TBL_PAYPAL_DEPOSIT SET STATUS = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    public int updateOrderStatus(int id, String status) {
        String sql = "UPDATE TBL_PAYPAL_DEPOSIT SET STATUS = 1, ORDER_STATUS = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, status, id);
    }
    
}
