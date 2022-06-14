package com.ndb.auction.dao.oracle.transactions.stripe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;

import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_STRIPE_DEPOSIT")
public class StripeDepositDao extends BaseOracleDao implements IStripeDao {

    private static StripeDepositTransaction extract(ResultSet rs) throws SQLException {
        StripeDepositTransaction m = new StripeDepositTransaction();
        m.setId(rs.getInt("ID"));
        m.setUserId(rs.getInt("USER_ID"));
        m.setAmount(rs.getDouble("USD_AMOUNT"));
        m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
        m.setConfirmedAt(rs.getTimestamp("UPDATED_AT").getTime());
        m.setStatus(rs.getBoolean("STATUS"));
        m.setFiatType(rs.getString("FIAT_TYPE"));
        m.setFiatAmount(rs.getLong("FIAT_AMOUNT"));
        m.setPaymentIntentId(rs.getString("INTENT_ID"));
        m.setPaymentMethodId(rs.getString("METHOD_ID"));
        m.setCryptoType(rs.getString("CRYPTO_TYPE"));
        m.setCryptoPrice(rs.getDouble("CRYPTO_PRICE"));
        m.setFee(rs.getDouble("FEE"));
        m.setDeposited(rs.getDouble("DEPOSITED"));
        m.setIsShow(rs.getBoolean("IS_SHOW"));
        return m;
    }

    @Override
    public StripeDepositTransaction selectByStripeIntentId(String intentId) {
        String sql = "SELECT * FROM TBL_STRIPE_DEPOSIT WHERE INTENT_ID=?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, intentId);
    }

    public int insert(Transaction _m) {
        StripeDepositTransaction m = (StripeDepositTransaction) _m;
        String sql = "INSERT INTO TBL_STRIPE_DEPOSIT(ID,USER_ID,USD_AMOUNT,CREATED_AT,UPDATED_AT,STATUS,FIAT_TYPE,FIAT_AMOUNT,INTENT_ID,METHOD_ID,CRYPTO_TYPE,CRYPTO_PRICE,FEE,DEPOSITED,IS_SHOW)"
                + " VALUES(SEQ_STRIPE_DEPOSIT.NEXTVAL,?,?,SYSDATE,SYSDATE,?,?,?,?,?,?,?,?,?,1)";

        return jdbcTemplate.update(sql, m.getUserId(), m.getAmount(), m.getStatus(), m.getFiatType(),
                m.getFiatAmount(), m.getPaymentIntentId(), m.getPaymentMethodId(),
                m.getCryptoType(), m.getCryptoPrice(), m.getFee(), m.getDeposited());
        }

    public List<? extends Transaction> selectAll(String orderBy) {
        String sql = "SELECT * FROM TBL_STRIPE_DEPOSIT";
        if (orderBy == null)
            orderBy = "ID";
        sql += " ORDER BY " + orderBy;
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));    }

    public List<? extends Transaction> selectByUser(int userId, String orderBy, int status) {
        String sql = "SELECT * FROM TBL_STRIPE_DEPOSIT WHERE USER_ID = ?";
        if(status == 0) {
            sql += " AND IS_SHOW = 1";
        }
        if (orderBy == null)
            orderBy = "ID";
        sql += " ORDER BY " + orderBy;
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }

    public Transaction selectById(int id, int status) {
        String sql = "SELECT * FROM TBL_STRIPE_DEPOSIT WHERE ID=?";
        if(status == 0) {
            sql += " AND IS_SHOW = 1";
        }
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, id);    }

    public int update(int id, int status) {
        return 0;
    }

    public List<StripeDepositTransaction> selectRange(int userId, long from, long to) {
        String sql = "SELECT * FROM TBL_STRIPE_DEPOSIT WHERE USER_ID = ? AND CREATED_AT > ? AND CREATED_AT < ? AND IS_SHOW = 1 ORDER BY ID DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, new Timestamp(from), new Timestamp(to));
    }

    public int changeShowStatus(int id, int status) {
        var sql = "UPDATE TBL_STRIPE_DEPOSIT IS_SHOW = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, status, id);
    }

}
