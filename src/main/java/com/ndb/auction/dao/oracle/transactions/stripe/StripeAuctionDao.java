package com.ndb.auction.dao.oracle.transactions.stripe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.stripe.StripeAuctionTransaction;

import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_STRIPE_AUCTION")
public class StripeAuctionDao extends BaseOracleDao {

    private static StripeAuctionTransaction extract(ResultSet rs) throws SQLException {
		StripeAuctionTransaction m = new StripeAuctionTransaction();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setAmount(rs.getDouble("USD_AMOUNT"));
        m.setFee(rs.getDouble("FEE"));
        m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
        m.setConfirmedAt(rs.getTimestamp("UPDATED_AT").getTime());
		m.setStatus(rs.getBoolean("STATUS"));
		m.setFiatType(rs.getString("FIAT_TYPE"));
        m.setFiatAmount(rs.getLong("FIAT_AMOUNT"));
        m.setPaymentMethodId(rs.getString("METHOD_ID"));
        m.setPaymentIntentId(rs.getString("INTENT_ID"));
		m.setAuctionId(rs.getInt("AUCTION_ID"));
        m.setBidId(rs.getInt("BID_ID"));
		return m;
	}

    public int insert(Transaction _m) {
        StripeAuctionTransaction m = (StripeAuctionTransaction) _m;
        String sql = "INSERT INTO TBL_STRIPE_AUCTION(ID,USER_ID,USD_AMOUNT,CREATED_AT,UPDATED_AT,STATUS,FIAT_TYPE,FIAT_AMOUNT,METHOD_ID,INTENT_ID,AUCTION_ID,BID_ID,FEE)"
        + " VALUES(SEQ_STRIPE_AUCTION.NEXTVAL,?,?,SYSDATE,SYSDATE,0,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql,m.getUserId(), m.getAmount(), m.getFiatType(), m.getFiatAmount(), m.getPaymentMethodId(), m.getPaymentIntentId(), m.getAuctionId(), m.getBidId(), m.getFee());
    }

    public List<? extends Transaction> selectAll(String orderBy) {
        String sql = "SELECT * FROM TBL_STRIPE_AUCTION";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        String sql = "SELECT * FROM TBL_STRIPE_AUCTION WHERE USER_ID = ?";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }

    public Transaction selectById(int id) {
        String sql = "SELECT * FROM TBL_STRIPE_AUCTION WHERE ID=?";
		return jdbcTemplate.query(sql, rs -> {
			if (!rs.next())
				return null;
			return extract(rs);
		}, id);
    }

    public List<StripeAuctionTransaction> selectByIds(int auctionId, int userId) {
        String sql = "SELECT * FROM TBL_STRIPE_AUCTION WHERE USER_ID = ? AND AUCTION_ID=?";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, auctionId);
    }

    public List<StripeAuctionTransaction> selectByRound(int auctionId, String orderBy) {
        String sql = "SELECT * FROM TBL_STRIPE_AUCTION WHERE AUCTION_ID=?";
        if (orderBy == null)
            orderBy = "ID";
        sql += " ORDER BY " + orderBy;
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), auctionId);
    }

    public int update(int id, int status) {
        String sql = "UPDATE TBL_STRIPE_AUCTION SET STATUS=?, UPDATED_AT=SYSDATE WHERE ID=?";
		return jdbcTemplate.update(sql, status, id);
    }

    public int update(int userId, int auctionId, String intentId) {
        String sql = "UPDATE TBL_STRIPE_AUCTION SET INTENT_ID=?, STATUS = ?, UPDATED_AT=SYSDATE WHERE USER_ID=? AND AUCTION_ID = ?";
		return jdbcTemplate.update(sql, intentId, true, userId, auctionId);
    }

    public int updatePaymentStatus(String paymentIntentId, int status) {
        String sql = "UPDATE TBL_STRIPE_AUCTION SET STATUS=?, UPDATED_AT=SYSDATE WHERE INTENT_ID=?";
		return jdbcTemplate.update(sql, status, paymentIntentId);
    }

    public List<StripeAuctionTransaction> selectRange(int userId, long from, long to) {
        String sql = "SELECT * FROM TBL_STRIPE_AUCTION WHERE USER_ID = ? AND CREATED_AT > ? AND CREATED_AT < ? ORDER BY ID DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, new Timestamp(from), new Timestamp(to));
    }
    
}
