package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.DirectSale;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_DIRECT_SALE")
public class DirectSaleDao extends BaseOracleDao {

    private static DirectSale extract(ResultSet rs) throws SQLException {
        DirectSale m = new DirectSale();
        m.setUserId(rs.getInt("USER_ID"));
        m.setTxnId(rs.getString("TXN_ID"));
        m.setPayType(rs.getInt("PAY_TYPE"));
        m.setNdbPrice(rs.getLong("NDB_PRICE"));
        m.setNdbAmount(rs.getLong("NDB_AMOUNT"));
        m.setWhereTo(rs.getInt("WHERE_TO"));
        m.setExtAddr(rs.getString("EXT_ADDR"));
        m.setConfirmed(rs.getBoolean("IS_CONFIRMED"));
        m.setCreatedAt(rs.getTimestamp("CREATED_DATE").getTime());
        m.setConfirmedAt(rs.getTimestamp("COMFIRMED_DATE").getTime());
        m.setPaymentIntentId(rs.getString("PAYMENT_INTENT_ID"));
        m.setCode(rs.getString("CODE"));
        m.setCryptoType(rs.getString("CRYPTO_TYPE"));
        m.setCryptoPrice(rs.getLong("CRYPTO_PRICE"));
        m.setCryptoAmount(rs.getLong("CRYPTO_AMOUNT"));
        return m;
    }

    // create new empty transaction
    public DirectSale createEmptyDirectSale(DirectSale m) {
        String sql = "INSERT INTO TBL_DIRECT_SALE(USER_ID, TXN_ID, PAY_TYPE, NDB_PRICE, NDB_AMOUNT, WHERE_TO, EXT_ADDR, "
                + "IS_CONFIRMED, CREATED_DATE, COMFIRMED_DATE, PAYMENT_INTENT_ID, CODE, CRYPTO_TYPE, CRYPTO_PRICE, CRYPTO_AMOUNT)"
                + "VALUES(SEQ_AUCTION.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql, m.getUserId(), m.getTxnId(), m.getPayType(), m.getNdbPrice(), m.getNdbAmount(),
                m.getWhereTo(), m.getExtAddr(), m.isConfirmed(), new Timestamp(m.getCreatedAt()),
                new Timestamp(m.getConfirmedAt()), m.getPaymentIntentId(), m.getCode(), m.getCryptoType(),
                m.getCryptoPrice(), m.getCryptoAmount());
        return m;
    }

    // get transaction by txn id
    public DirectSale getDirectSale(int userId, String txnId) {
        String sql = "SELECT * FROM TBL_DIRECT_SALE WHERE USER_ID=? AND TXN_ID=?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<DirectSale>() {
            @Override
            public DirectSale extractData(ResultSet rs) throws SQLException {
                if (!rs.next())
                    return null;
                return extract(rs);
            }
        }, userId, txnId);
    }

    // get transaction by intent id
    public DirectSale getDirectSaleByIntent(String intentId) {
        String sql = "SELECT * FROM TBL_DIRECT_SALE WHERE PAYMENT_INTENT_ID=?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<DirectSale>() {
            @Override
            public DirectSale extractData(ResultSet rs) throws SQLException {
                if (!rs.next())
                    return null;
                return extract(rs);
            }
        }, intentId);
    }

    public DirectSale getDirectSaleByCode(String code) {
        String sql = "SELECT * FROM TBL_DIRECT_SALE WHERE CODE=?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<DirectSale>() {
            @Override
            public DirectSale extractData(ResultSet rs) throws SQLException {
                if (!rs.next())
                    return null;
                return extract(rs);
            }
        }, code);
    }

    public DirectSale updateDirectSale(DirectSale m) {
        String sql = "UPDATE TBL_DIRECT_SALE SET PAY_TYPE=?, NDB_PRICE=?, NDB_AMOUNT=?, WHERE_TO=?, EXT_ADDR=?, "
                + "IS_CONFIRMED=?, CREATED_DATE=?, COMFIRMED_DATE=?, PAYMENT_INTENT_ID=?, CODE=?, CRYPTO_TYPE=?, CRYPTO_PRICE=?, CRYPTO_AMOUNT=? "
                + "WHERE USER_ID=? AND TXN_ID=?";
        jdbcTemplate.update(sql, m.getPayType(), m.getNdbPrice(), m.getNdbAmount(),
                m.getWhereTo(), m.getExtAddr(), m.isConfirmed(), new Timestamp(m.getCreatedAt()),
                new Timestamp(m.getConfirmedAt()), m.getPaymentIntentId(), m.getCode(), m.getCryptoType(),
                m.getCryptoPrice(), m.getCryptoAmount(), m.getUserId(), m.getTxnId());
        return m;
    }

}
