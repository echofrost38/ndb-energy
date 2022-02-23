package com.ndb.auction.dao.oracle.transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transactions.StripeAuctionTransaction;
import com.ndb.auction.models.transactions.Transaction;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_STRIPE_AUCTION")
public class StripeAuctionDao extends BaseOracleDao implements ITransactionDao {

    private static StripeAuctionTransaction extract(ResultSet rs) throws SQLException {
		StripeAuctionTransaction m = new StripeAuctionTransaction();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setAmount(rs.getLong("AMOUNT"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
        m.setConfirmedAt(rs.getTimestamp("CONFIRMED_AT").getTime());
		m.setStatus(rs.getBoolean("STATUS"));
		m.setFiatType(rs.getString("FIAT_TYPE"));
        m.setFiatAmount(rs.getLong("FIAT_AMOUNT"));
        m.setPaymentMethodId(rs.getString("METHOD_ID"));
        m.setPaymentIntentId(rs.getString("INTENT_ID"));
		m.setAuctionId(rs.getInt("AUCTION_ID"));
        m.setBidId(rs.getInt("BID_ID"));
		return m;
	}

    @Override
    public Transaction insert(Transaction _m) {
        StripeAuctionTransaction m = (StripeAuctionTransaction) _m;
        String sql = "INSERT INTO TBL_STRIPE_AUCTION(ID,USER_ID,AMOUNT,CREATED_AT,CONFIRMED_AT,STATUS,FIAT_TYPE,FIAT_AMOUNT,METHOD_ID,INTENT_ID,AUCTION_ID,BID_ID)"
        + " VALUES(SEQ_STRIPE_AUCTION.NEXTVAL,?,?,SYSDATE,SYSDATE,0,?,?,?,?,?,?)";
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
                        ps.setInt(i++, m.getAuctionId());
                        ps.setInt(i++, m.getBidId());
                        return ps;
                    }
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;
    }

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        String sql = "SELECT * FROM TBL_STRIPE_AUCTION";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, new RowMapper<StripeAuctionTransaction>() {
			@Override
			public StripeAuctionTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        String sql = "SELECT * FROM TBL_STRIPE_AUCTION WHERE USER_ID = ?";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, new RowMapper<StripeAuctionTransaction>() {
			@Override
			public StripeAuctionTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

    @Override
    public Transaction selectById(int id) {
        String sql = "SELECT * FROM TBL_STRIPE_AUCTION WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<StripeAuctionTransaction>() {
			@Override
			public StripeAuctionTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
    }

    public List<StripeAuctionTransaction> selectByIds(int auctionId, int userId) {
        String sql = "SELECT * FROM TBL_STRIPE_AUCTION WHERE USER_ID = ? AND AUCTION_ID=?";
		return jdbcTemplate.query(sql, new RowMapper<StripeAuctionTransaction>() {
			@Override
			public StripeAuctionTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId, auctionId);
    }

    public List<StripeAuctionTransaction> selectByRound(int auctionId, String orderBy) {
        String sql = "SELECT * FROM TBL_STRIPE_AUCTION WHERE AUCTION_ID=?";
        if (orderBy == null)
            orderBy = "ID";
        sql += " ORDER BY " + orderBy;
        return jdbcTemplate.query(sql, new RowMapper<StripeAuctionTransaction>() {
			@Override
			public StripeAuctionTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, auctionId);
    }

    @Override
    public int update(int id, int status) {
        String sql = "UPDATE TBL_STRIPE_AUCTION SET STATUS=?, CONFIRMED_AT=SYSDATE WHERE ID=?";
		return jdbcTemplate.update(sql, status, id);
    }

    public int update(int userId, int auctionId, String intentId) {
        String sql = "UPDATE TBL_STRIPE_AUCTION SET INTENT_ID=?, STATUS = ?, CONFIRMED_AT=SYSDATE WHERE USER_ID=? AND AUCTION_ID = ?";
		return jdbcTemplate.update(sql, intentId, true, userId, auctionId);
    }

    public int updatePaymentStatus(String paymentIntentId, int status) {
        String sql = "UPDATE TBL_STRIPE_AUCTION SET STATUS=?, CONFIRMED_AT=SYSDATE WHERE INTENT_ID=?";
		return jdbcTemplate.update(sql, status, paymentIntentId);
    }
    
}
