package com.ndb.auction.dao.oracle.transactions.paypal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.dao.oracle.transactions.ITransactionDao;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalPresaleTransaction;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_PAYPAL_PRESALE")
public class PaypalPresaleDao extends BaseOracleDao implements ITransactionDao, IPaypalDao {
    
    private static PaypalPresaleTransaction extract(ResultSet rs) throws SQLException {
		PaypalPresaleTransaction m = new PaypalPresaleTransaction();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setAmount(rs.getDouble("AMOUNT"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
        m.setConfirmedAt(rs.getTimestamp("UPDATED_AT").getTime());
		m.setStatus(rs.getBoolean("STATUS"));
		m.setFiatType(rs.getString("FIAT_TYPE"));
        m.setFiatAmount(rs.getDouble("FIAT_AMOUNT"));
        m.setFee(rs.getDouble("FEE"));
        m.setPaypalOrderId(rs.getString("ORDER_ID"));
        m.setPaypalOrderStatus(rs.getString("ORDER_STATUS"));
		m.setPresaleId(rs.getInt("PRESALE_ID"));
        m.setOrderId(rs.getInt("P_ORDER_ID"));
		return m;
	}
    
    @Override
    public PaypalDepositTransaction selectByPaypalOrderId(String orderId) {
        String sql = "SELECT * FROM TBL_PAYPAL_PRESALE WHERE ORDER_ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<PaypalPresaleTransaction>() {
			@Override
			public PaypalPresaleTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, orderId);
    }

    @Override
    public Transaction insert(Transaction _m) {
        PaypalPresaleTransaction m = (PaypalPresaleTransaction) _m;
        String sql = "INSERT INTO TBL_PAYPAL_PRESALE(ID,USER_ID,AMOUNT,CREATED_AT,UPDATED_AT,STATUS,FIAT_TYPE,FIAT_AMOUNT,FEE,ORDER_ID,ORDER_STATUS,PRESALE_ID,P_ORDER_ID)"
        + " VALUES(SEQ_PAYPAL_PRESALE.NEXTVAL,?,?,SYSDATE,SYSDATE,0,?,?,?,?,?,?)";
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
                        ps.setDouble(i++, m.getFee());
                        ps.setString(i++, m.getPaypalOrderId());
                        ps.setString(i++, m.getPaypalOrderStatus());
                        ps.setInt(i++, m.getPresaleId());
                        ps.setInt(i++, m.getOrderId());
                        return ps;
                    }
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;
    }

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        String sql = "SELECT * FROM TBL_PAYPAL_PRESALE";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, new RowMapper<PaypalPresaleTransaction>() {
			@Override
			public PaypalPresaleTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        String sql = "SELECT * FROM TBL_PAYPAL_PRESALE WHERE USER_ID = ?";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, new RowMapper<PaypalPresaleTransaction>() {
			@Override
			public PaypalPresaleTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

    @Override
    public Transaction selectById(int id) {
        String sql = "SELECT * FROM TBL_PAYPAL_PRESALE WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<PaypalPresaleTransaction>() {
			@Override
			public PaypalPresaleTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
    }

    @Override
    public int update(int id, int status) {
        // TODO Auto-generated method stub
        return 0;
    }

    public List<PaypalPresaleTransaction> selectByIds(int userId, int presaleId) {
        String sql = "SELECT * FROM TBL_PAYPAL_PRESALE WHERE USER_ID = ? AND PRESALE_ID = ?";
		return jdbcTemplate.query(sql, new RowMapper<PaypalPresaleTransaction>() {
			@Override
			public PaypalPresaleTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId, presaleId);
    }

    public int updateOrderStatus(int id, String status) {
        String sql = "UPDATE TBL_PAYPAL_PRESALE SET STATUS = 1, ORDER_STATUS = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    public List<PaypalPresaleTransaction> selectRange(int userId, long from, long to) {
        String sql = "SELECT * FROM TBL_PAYPAL_PRESALE WHERE USER_ID = ? AND CREATED_AT > ? AND CREATED_AT < ? ORDER BY ID DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, new Timestamp(from), new Timestamp(to));
    }
        
}
