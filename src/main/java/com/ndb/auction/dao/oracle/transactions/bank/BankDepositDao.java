package com.ndb.auction.dao.oracle.transactions.bank;

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
import com.ndb.auction.models.transactions.bank.BankDepositTransaction;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Repository
@Table(name = "TBL_BANK_DEPOSIT")
public class BankDepositDao extends BaseOracleDao implements ITransactionDao {
    
    private static BankDepositTransaction extract(ResultSet rs) throws SQLException {
		BankDepositTransaction m = new BankDepositTransaction();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
        m.setEmail(rs.getString("EMAIL"));
        m.setUid(rs.getString("UNID"));
		m.setAmount(rs.getDouble("AMOUNT"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
        m.setConfirmedAt(rs.getTimestamp("UPDATED_AT").getTime());
		m.setStatus(rs.getBoolean("STATUS"));
		m.setFiatType(rs.getString("FIAT_TYPE"));
        m.setUsdAmount(rs.getDouble("USD_AMOUNT"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
        m.setCryptoPrice(rs.getDouble("CRYPTO_PRICE"));
        m.setFee(rs.getDouble("FEE"));
        m.setDeposited(rs.getDouble("DEPOSITED"));
		return m;
	}
    
    @Override
    public Transaction insert(Transaction _m) {
        BankDepositTransaction m = (BankDepositTransaction) _m;
        String sql = "INSERT INTO TBL_BANK_DEPOSIT(ID,USER_ID,UNID,AMOUNT,CREATED_AT,UPDATED_AT,STATUS,FIAT_TYPE,USD_AMOUNT,CRYPTO_TYPE,CRYPTO_PRICE,FEE,DEPOSITED)"
        + " VALUES(SEQ_BANK_DEPOSIT.NEXTVAL,?,?,?,SYSDATE,SYSDATE,0,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(sql,
                                new String[] { "ID" });
                        int i = 1;
                        ps.setInt(i++, m.getUserId());
                        ps.setString(i++, m.getUid());
                        ps.setDouble(i++, m.getAmount());
                        ps.setString(i++, m.getFiatType());
                        ps.setDouble(i++, m.getUsdAmount());
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
        String sql = "SELECT TBL_BANK_DEPOSIT.*, TBL_USER.EMAIL from TBL_BANK_DEPOSIT left JOIN TBL_USER on TBL_BANK_DEPOSIT.USER_ID = TBL_USER.ID";
		if (orderBy == null)
			orderBy = "TBL_BANK_DEPOSIT.ID";
		sql += " ORDER BY " + orderBy + " DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        String sql = "SELECT TBL_BANK_DEPOSIT.*, TBL_USER.EMAIL from TBL_BANK_DEPOSIT left JOIN TBL_USER on TBL_BANK_DEPOSIT.USER_ID = TBL_USER.ID WHERE TBL_BANK_DEPOSIT.USER_ID = ?";
		if (orderBy == null)
			orderBy = "TBL_BANK_DEPOSIT.ID";
		sql += " ORDER BY " + orderBy + " DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }

    @Override
    public Transaction selectById(int id) {
        String sql = "SELECT TBL_BANK_DEPOSIT.*, TBL_USER.EMAIL from TBL_BANK_DEPOSIT left JOIN TBL_USER on TBL_BANK_DEPOSIT.USER_ID = TBL_USER.ID WHERE TBL_BANK_DEPOSIT.ID=?";
		return jdbcTemplate.query(sql, rs -> {
            if(!rs.next())
                return null;
            return extract(rs);
        }, id);
    }

    @Override
    public int update(int id, int status) {
        String sql = "UPDATE TBL_BANK_DEPOSIT SET STATUS = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    public BankDepositTransaction selectByUid(String uid) {
        String sql = "SELECT TBL_BANK_DEPOSIT.*, TBL_USER.EMAIL from TBL_BANK_DEPOSIT left JOIN TBL_USER on TBL_BANK_DEPOSIT.USER_ID = TBL_USER.ID WHERE TBL_BANK_DEPOSIT.UNID=?";
		return jdbcTemplate.query(sql, rs -> {
            if(!rs.next())
                return null;
            return extract(rs);
        }, uid);
    }

    public List<BankDepositTransaction> selectUnconfirmedByAdmin() {
        String sql = "SELECT TBL_BANK_DEPOSIT.*, TBL_USER.EMAIL from TBL_BANK_DEPOSIT left JOIN TBL_USER on TBL_BANK_DEPOSIT.USER_ID = TBL_USER.ID WHERE TBL_BANK_DEPOSIT.STATUS = 0 ORDER BY TBL_BANK_DEPOSIT.ID DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public List<BankDepositTransaction> selectUnconfirmedByUser(int userId) {
        String sql = "SELECT TBL_BANK_DEPOSIT.*, TBL_USER.EMAIL from TBL_BANK_DEPOSIT left JOIN TBL_USER on TBL_BANK_DEPOSIT.USER_ID = TBL_USER.ID WHERE TBL_BANK_DEPOSIT.USER_ID = ? AND TBL_BANK_DEPOSIT.STATUS = 0 ORDER BY TBL_BANK_DEPOSIT.ID DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }

    public List<BankDepositTransaction> selectRange(int userId, long from, long to) {
        String sql = "SELECT TBL_BANK_DEPOSIT.*, TBL_USER.EMAIL from TBL_BANK_DEPOSIT left JOIN TBL_USER on TBL_BANK_DEPOSIT.USER_ID = TBL_USER.ID WHERE TBL_BANK_DEPOSIT.USER_ID = ? AND TBL_BANK_DEPOSIT.CREATED_AT > ? AND TBL_BANK_DEPOSIT.CREATED_AT < ? ORDER BY TBL_BANK_DEPOSIT.ID DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, new Timestamp(from), new Timestamp(to));
    }

    public int update(int id, String currency, double amount, double usdAmount, double deposited, double fee, String cryptoType, double cryptoPrice) {
        String sql = "UPDATE TBL_BANK_DEPOSIT SET STATUS=1,AMOUNT=?,USD_AMOUNT=?,FIAT_TYPE=?,DEPOSITED=?,FEE=?,CRYPTO_TYPE=?,CRYPTO_PRICE=? WHERE ID=?";
        return jdbcTemplate.update(sql, amount, usdAmount, currency, deposited, fee, cryptoType, cryptoPrice, id);
    }

}
