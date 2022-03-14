package com.ndb.auction.dao.oracle.transactions.stripe;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.dao.oracle.transactions.ITransactionDao;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@NoArgsConstructor
@Table(name = "TBL_STRIPE_DEPOSIT")
public class StripeDepositDao extends BaseOracleDao implements ITransactionDao, IStripeDao {

    private static StripeDepositTransaction extract(ResultSet rs) throws SQLException {
        StripeDepositTransaction m = new StripeDepositTransaction();
        m.setId(rs.getInt("ID"));
        m.setUserId(rs.getInt("USER_ID"));
        m.setAmount(rs.getLong("AMOUNT"));
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

    @Override
    public Transaction insert(Transaction _m) {
        StripeDepositTransaction m = (StripeDepositTransaction) _m;
        String sql = "INSERT INTO TBL_STRIPE_DEPOSIT(ID,USER_ID,AMOUNT,CREATED_AT,UPDATED_AT,STATUS,FIAT_TYPE,FIAT_AMOUNT,INTENT_ID,METHOD_ID,CRYPTO_TYPE,CRYPTO_PRICE,FEE,DEPOSITED)"
                + " VALUES(SEQ_STRIPE_DEPOSIT.NEXTVAL,?,?,SYSDATE,SYSDATE,0,?,?,?,?,?,?,?,?)";
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
                        ps.setString(i++, m.getPaymentIntentId());
                        ps.setString(i++, m.getPaymentMethodId());
                        ps.setString(i++, m.getCryptoType());
                        ps.setDouble(i++, m.getCryptoPrice());
                        ps.setDouble(i++, m.getFee());
                        ps.setDouble(i++, m.getDeposited());
                        return ps;
                    }
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;    }

    @Override
    public List<? extends Transaction> selectAll(String orderBy) {
        String sql = "SELECT * FROM TBL_STRIPE_DEPOSIT";
        if (orderBy == null)
            orderBy = "ID";
        sql += " ORDER BY " + orderBy;
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        String sql = "SELECT * FROM TBL_STRIPE_DEPOSIT WHERE USER_ID = ?";
        if (orderBy == null)
            orderBy = "ID";
        sql += " ORDER BY " + orderBy;
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);    }

    @Override
    public Transaction selectById(int id) {
        String sql = "SELECT * FROM TBL_STRIPE_DEPOSIT WHERE ID=?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, id);    }

    @Override
    public int update(int id, int status) {
        return 0;
    }
}
