package com.ndb.auction.dao.oracle.transactions.stripe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transactions.stripe.StripeTransaction;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_STRIPE_TRANSACTION")
public class StripeTransactionDao extends BaseOracleDao {
    private static StripeTransaction extract(ResultSet rs) throws SQLException {
        return StripeTransaction.builder()
            .id(rs.getInt("ID"))
            .userId(rs.getInt("USER_ID"))
            .txnType(rs.getString("TXN_TYPE"))
            .txnId(rs.getInt("TXN_ID")) 
            .intentId(rs.getString("INTENT_ID"))
            .methodId(rs.getString("METHOD_ID"))
            .fiatType(rs.getString("FIAT_TYPE"))
            .fiatAmount(rs.getDouble("FIAT_AMOUNT"))
            .usdAmount(rs.getDouble("USD_AMOUNT"))
            .cryptoType(rs.getString("CRYPTO_TYPE"))
            .cryptoAmount(rs.getDouble("CRYPTO_AMOUNT"))
            .paymentStatus(rs.getString("PAY_STATUS"))
            .status(rs.getBoolean("STATUS"))
            .shown(rs.getBoolean("IS_SHOW"))
            .createdAt(rs.getTimestamp("CREATED_AT").getTime())
            .updatedAt(rs.getTimestamp("UPDATED_AT").getTime())
            .build();
    }

    public int insert(StripeTransaction m) {
        var sql = "INSERT INTO TBL_STRIPE_TRANSACTION(ID,USER_ID,TXN_TYPE,TXN_ID,INTENT_ID,METHOD_ID,FIAT_TYPE,FIAT_AMOUNT,USD_AMOUNT,CRYPTO_TYPE,CRYPTO_AMOUNT,PAY_STATUS,STATUS,CREATED_AT,UPDATED_AT)"
            + " VALUES(SEQ_STRIPE_TXN.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE";
        return jdbcTemplate.update(sql, m.getUserId(), m.getTxnType(), m.getTxnId(), m.getIntentId(), m.getMethodId(), m.getFiatType(), m.getFiatAmount(), 
            m.getUsdAmount(), m.getCryptoType(), m.getCryptoAmount(), m.getPaymentStatus(), m.isStatus(), m.isShown());
    }

    /**
     * default values of params
     * status: 1 - confirmed transaction
     * offset: 0 - from start
     * limit: 20 
     * txnType: not null
     * orderBy: ID
     */
    public List<StripeTransaction> selectPage(int status, int showStatus, Integer offset, Integer limit, String txnType, String orderBy) {
        var sql = "SELECT * FROM TBL_STRIPE_TRANSACTION WHERE TXN_TYPE = ?";
        if (status == 1) {
            sql += " AND STATUS = 1";
        }

        if(showStatus == 1) {
            sql += " AND IS_SHOW = 1";
        }

        orderBy = orderBy == null ? "ID" : orderBy;
        sql += " ORDER BY " + orderBy;

        offset = offset == null ? 0 : offset;
        limit = (limit == null || limit > 200) ? 20 : limit;
        sql += " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), txnType, offset, limit);
    }

    public List<StripeTransaction> selectByUser(int userId, int showStatus, String orderBy) {
        var sql = "SELECT * FROM TBL_STRIPE_TRANSACTION WHERE USER_ID = ?";
        if(showStatus == 1) {
            sql += " AND IS_SHOW = 1";
        }
        orderBy = orderBy == null ? "ID" : orderBy;
        sql += " ORDER BY " + orderBy;
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }

    public StripeTransaction selectById(int id) {
        var sql = "SELECT * FROM TBL_STRIPE_TRANSACTION WHERE ID = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) return null;
            return extract(rs);
        }, id);
    }

    public StripeTransaction selectByIntentId(String intentId) {
        var sql = "SELECT * FROM TBL_STRIPE_TRANSACTION WHERE INTENT_ID = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) return null;
            return extract(rs);
        }, intentId);
    }

    public List<StripeTransaction> selectRange(int userId, long from, long to) {
        var sql = "SELECT * FROM TBL_STRIPE_TRANSACTION WHERE USER_ID = ? AND CREATED_AT > ? AND CREATED_AT < ? AND IS_SHOW = 1 ORDER BY ID DESC";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, new Timestamp(from), new Timestamp(to));
    }

    public List<StripeTransaction> selectByIds(int userId, int txnId, String txnType) {
        var sql = "SELECT * FROM TBL_STRIPE_TRANSACTION WHERE USER_ID = ? AND TXN_ID = ? AND TXN_TYPE = ?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, txnId, txnType);
    }

    public int changeShowStatus(int id, int showStatus) {
        var sql = "UPDATE TBL_STRIPE_TRANSACTION SET IS_SHOW = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, showStatus, id);
    }

    public int updateTransactionStatus(boolean status, double cryptoAmount, double fee, String payStatus, String intentId) {
        var sql = "UPDATE TBL_STRIPE_TRANSACTION SET FEE = ?, CRYPTO_AMOUNT = ?, PAY_STATUS = ?, STATUS = ? WHERE INTENT_ID = ?";
        return jdbcTemplate.update(sql, fee, cryptoAmount, payStatus, status, intentId);
    }

    public int updatePaymentIntent(int id, String intentId) {
        var sql = "UPDATE TBL_STRIPE_TRANSACTION SET INTENT_ID = ?, UPDATED_AT = SYSTDATE WHERE ID = ?";
        return jdbcTemplate.update(sql, intentId, id);
    }

    public int update(int id, int status) {
        var sql = "UPDATE TBL_STRIPE_TRANSACTION SET STATUS = ?, UPDATED_AT = SYSDATE WHERE ID = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    public int updatePaymentStatus(String paymentIntentId, int status) {
        String sql = "UPDATE TBL_STRIPE_TRANSACTION SET STATUS=?, UPDATED_AT=SYSDATE WHERE INTENT_ID=?";
		return jdbcTemplate.update(sql, status, paymentIntentId);
    }

    public int update(int userId, int txnId, String txnType, String intentId) {
        var sql = "UPDATE TBL_STRIPE_TRANSACTION SET INTENT_ID = ?, STATUS = ?, UPDATED_AT = SYSDATE WHERE USER_ID = ? AND TXN_ID = ? AND TXN_TYPE = ?";
        return jdbcTemplate.update(sql, intentId, true, userId, txnId, txnType);
    }

}
