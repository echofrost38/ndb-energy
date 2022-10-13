package com.ndb.auction.dao.oracle.transactions.paypal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transactions.paypal.PaypalTransaction;

@Repository
@Table(name = "TBL_PAYPAL_TRANSACTION")
public class PaypalTransactionDao extends BaseOracleDao {
    
    public static PaypalTransaction extract(ResultSet rs) throws SQLException {
        return PaypalTransaction.builder()
            .id(rs.getInt("ID"))
            .userId(rs.getInt("USER_ID"))
            .txnType(rs.getString("TXN_TYPE"))
            .txnId(rs.getInt("TXN_ID"))
            .fiatType(rs.getString("FIAT_TYPE"))
            .fiatAmount(rs.getDouble("FIAT_AMOUNT"))
            .usdAmount(rs.getDouble("USD_AMOUNT"))
            .fee(rs.getDouble("FEE"))
            .paypalOrderId(rs.getString("ORDER_ID"))
            .paypalOrderStatus(rs.getString("ORDER_STATUS"))
            .cryptoType(rs.getString("CRYPTO_TYPE"))
            .cryptoAmount(rs.getDouble("CRYPTO_AMOUNT"))
            .status(rs.getInt("STATUS"))
            .shown(rs.getBoolean("IS_SHOW"))
            .createdAt(rs.getTimestamp("CREATED_AT").getTime())
            .updatedAt(rs.getTimestamp("UPDATED_AT").getTime())
            .build();
    }

    public PaypalTransaction insert(PaypalTransaction m) {
        var sql = "INSERT INTO TBL_PAYPAL_TRANSACTION(ID,USER_ID,TXN_TYPE,TXN_ID,FIAT_TYPE,FIAT_AMOUNT,USD_AMOUNT,FEE,ORDER_ID,ORDER_STATUS,CRYPTO_TYPE,CRYPTO_AMOUNT,STATUS,IS_SHOW,CREATED_AT,UPDATED_AT)"
            + "VALUES(SEQ_PAYPAL_TRANSACTION.NEXTVALL,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE)";
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql,
                        new String[] { "ID" });
                int i = 1;
                ps.setInt(i++, m.getUserId());
                ps.setString(i++, m.getTxnType());
                ps.setInt(i++, m.getTxnId());
                ps.setString(i++, m.getFiatType());
                ps.setDouble(i++, m.getFiatAmount());
                ps.setDouble(i++, m.getUsdAmount());
                ps.setDouble(i++, m.getFee());
                ps.setString(i++, m.getPaypalOrderId());
                ps.setString(i++, m.getPaypalOrderStatus());
                ps.setString(i++, m.getCryptoType());
                ps.setDouble(i++, m.getCryptoAmount());
                ps.setInt(i++, m.getStatus());
                ps.setBoolean(i++, m.isShown());
                return ps;
            }
        }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;
    }

    public List<PaypalTransaction> selectPage(int status, int showStatus, Integer offset, Integer limit, String txnType, String orderBy) {
        var sql = "SELECT * FROM TBL_PAYPAL_TRANSACTION WHERE TXN_TYPE = ?";
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

    public List<PaypalTransaction> selectByUser(int userId, int showStatus, String orderBy) {
        var sql = "SELECT * FROM TBL_PAYPAL_TRANSACTION WHERE USER_ID = ?";
        if(showStatus == 1) {
            sql += " AND IS_SHOW = 1";
        }
        orderBy = orderBy == null ? "ID" : orderBy;
        sql += " ORDER BY " + orderBy;
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }

    public PaypalTransaction selectById(int id) {
        var sql = "SELECT * FROM TBL_PAYPAL_TRANSACTION WHERE ID = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) return null;
            return extract(rs);
        }, id);
    }

    public List<PaypalTransaction> selectRange(int userId, long from, long to) {
        var sql = "SELECT * FROM TBL_PAYPAL_TRANSACTION WHERE USER_ID = ? AND CREATED_AT > ? AND CREATED_AT < ? AND IS_SHOW = 1 ORDER BY ID DESC";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, new Timestamp(from), new Timestamp(to));
    }

    public List<PaypalTransaction> selectByIds(int userId, int txnId, String txnType) {
        var sql = "SELECT * FROM TBL_PAYPAL_TRANSACTION WHERE USER_ID = ? AND TXN_ID = ? AND TXN_TYPE = ?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, txnId, txnType);
    }

    public PaypalTransaction selectByOrderId(String orderId) {
        var sql = "SELECT * FROM TBL_PAYPAL_TRANSACTION WHERE ORDER_ID = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) return null;
            return extract(rs);
        }, orderId);
    }

    public int changeShowStatus(int id, int showStatus) {
        var sql = "UPDATE TBL_PAYPAL_TRANSACTION SET IS_SHOW = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, showStatus, id);
    }

    public int updateOrderStatus(int id, String orderStatus) {
        var sql = "UPDATE TBL_PAYPAL_TRANSACTION SET ORDER_STATUS = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, orderStatus, id);
    }

}
