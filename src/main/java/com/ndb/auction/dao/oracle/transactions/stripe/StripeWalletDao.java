package com.ndb.auction.dao.oracle.transactions.stripe;

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
import com.ndb.auction.models.transactions.stripe.StripeWalletTransaction;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_STRIPE_WALLET")
public class StripeWalletDao extends BaseOracleDao implements ITransactionDao {
    
    private static StripeWalletTransaction extract(ResultSet rs) throws SQLException {
		StripeWalletTransaction m = new StripeWalletTransaction();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setAmount(rs.getLong("AMOUNT"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
        m.setConfirmedAt(rs.getTimestamp("UPDATED_AT").getTime());
		m.setStatus(rs.getBoolean("STATUS"));
		m.setFiatType(rs.getString("FIAT_TYPE"));
        m.setFiatAmount(rs.getLong("FIAT_AMOUNT"));
        m.setPaymentMethodId(rs.getString("METHOD_ID"));
        m.setPaymentIntentId(rs.getString("INTENT_ID"));
		return m;
	}
    
    @Override
    public Transaction insert(Transaction _m) {
        StripeWalletTransaction m = (StripeWalletTransaction) _m;
        String sql = "INSERT INTO TBL_STRIPE_WALLET(ID,USER_ID,AMOUNT,CREATED_AT,UPDATED_AT,STATUS,FIAT_TYPE,FIAT_AMOUNT,METHOD_ID,INTENT_ID)"
        + " VALUES(SEQ_STRIPE_WALLET.NEXTVAL,?,?,SYSDATE,SYSDATE,0,?,?,?,?)";
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
                        ps.setString(i++, m.getPaymentMethodId());
                        ps.setString(i++, m.getPaymentIntentId());
                        return ps;
                    }
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;
    }

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        String sql = "SELECT * FROM TBL_STRIPE_WALLET";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, new RowMapper<StripeWalletTransaction>() {
			@Override
			public StripeWalletTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        String sql = "SELECT * FROM TBL_STRIPE_WALLET WHERE USER_ID = ?";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, new RowMapper<StripeWalletTransaction>() {
			@Override
			public StripeWalletTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

    @Override
    public Transaction selectById(int id) {
        String sql = "SELECT * FROM TBL_STRIPE_WALLET WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<StripeWalletTransaction>() {
			@Override
			public StripeWalletTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
    }

    @Override
    public int update(int id, int status) {
        String sql = "UPDATE TBL_STRIPE_WALLET SET STATUS=?, UPDATED_AT=SYSDATE WHERE ID=?";
		return jdbcTemplate.update(sql, status, id);
    }

    public int updatePaymentStatus(String paymentIntentId, int status) {
        String sql = "UPDATE TBL_STRIPE_WALLET SET STATUS=?, UPDATED_AT=SYSDATE WHERE INTENT_ID=?";
		return jdbcTemplate.update(sql, status, paymentIntentId);
    }

    public List<StripeWalletTransaction> selectRange(int userId, long from, long to) {
        String sql = "SELECT * FROM TBL_STRIPE_WALLET WHERE USER_ID = ? AND CREATED_AT > ? AND CREATED_AT < ? ORDER BY ID DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, new Timestamp(from), new Timestamp(to));
    }
    
}
