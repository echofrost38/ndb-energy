package com.ndb.auction.dao.oracle.transactions.coinpayment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.dao.oracle.transactions.ICryptoDepositTransactionDao;
import com.ndb.auction.dao.oracle.transactions.ITransactionDao;
import com.ndb.auction.models.transactions.CoinpaymentPresaleTransaction;
import com.ndb.auction.models.transactions.CryptoDepositTransaction;
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
@Table(name = "TBL_COINPAYMENT_PRESALE")
public class CoinpaymentPresaleDao extends BaseOracleDao implements ITransactionDao, ICryptoDepositTransactionDao {
    
    private static CoinpaymentPresaleTransaction extract(ResultSet rs) throws SQLException {
		CoinpaymentPresaleTransaction m = new CoinpaymentPresaleTransaction();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setAmount(rs.getLong("AMOUNT"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
		m.setStatus(rs.getBoolean("STATUS"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
		m.setNetwork(rs.getString("NETWORK"));
        m.setCryptoAmount(rs.getDouble("CRYPTO_AMOUNT"));
		m.setConfirmedAt(rs.getTimestamp("UPDATED_AT").getTime());
        m.setDepositAddress(rs.getString("DEPOSIT_ADDR"));
        m.setCoin(rs.getString("COIN"));
		m.setPresaleId(rs.getInt("PRESALE_ID"));
        m.setOrderId(rs.getInt("ORDER_ID"));
		return m;
	}

    @Override
    public List<CryptoDepositTransaction> selectByDepositAddress(String depositAddress) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Transaction insert(Transaction _m) {
        CoinpaymentPresaleTransaction m = (CoinpaymentPresaleTransaction)_m;
        String sql = "INSERT INTO TBL_COINPAYMENT_PRESALE(ID,USER_ID,AMOUNT,CREATED_AT,STATUS,CRYPTO_TYPE,NETWORK,CRYPTO_AMOUNT,UPDATED_AT,DEPOSIT_ADDR,COIN,PRESALE_ID,ORDER_ID)"
				+ " VALUES(SEQ_COINPAY_PRESALE.NEXTVAL,?,?,SYSDATE,0,?,?,?,SYSDATE,?,?,?,?)";
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
						ps.setString(i++, m.getCryptoType());
                        ps.setString(i++, m.getNetwork());
						ps.setDouble(i++, m.getCryptoAmount());
                        ps.setString(i++, m.getDepositAddress());
                        ps.setString(i++, m.getCoin());
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
        String sql = "SELECT * FROM TBL_COINPAYMENT_PRESALE";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, new RowMapper<CoinpaymentPresaleTransaction>() {
			@Override
			public CoinpaymentPresaleTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        String sql = "SELECT * FROM TBL_COINPAYMENT_PRESALE WHERE USER_ID = ?";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, new RowMapper<CoinpaymentPresaleTransaction>() {
			@Override
			public CoinpaymentPresaleTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

    public List<? extends Transaction> selectByPresaleId(int presaleId) {
        String sql = "SELECT * FROM TBL_COINPAYMENT_PRESALE WHERE PRESALE_ID = ?";
		return jdbcTemplate.query(sql, new RowMapper<CoinpaymentPresaleTransaction>() {
			@Override
			public CoinpaymentPresaleTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, presaleId);
    }

    public List<? extends Transaction> select(int userId, int presaleId) {
        String sql = "SELECT * FROM TBL_COINPAYMENT_PRESALE WHERE USER_ID = ? AND PRESALE_ID = ?";
		return jdbcTemplate.query(sql, new RowMapper<CoinpaymentPresaleTransaction>() {
			@Override
			public CoinpaymentPresaleTransaction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId, presaleId);
    }

    @Override
    public Transaction selectById(int id) {
        String sql = "SELECT * FROM TBL_COINPAYMENT_PRESALE WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<CoinpaymentPresaleTransaction>() {
			@Override
			public CoinpaymentPresaleTransaction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
    }

    @Override
    public int update(int id, int status) {
        String sql = "UPDATE TBL_COINPAYMENT_PRESALE SET STATUS=?, UPDATE_AT=SYSDATE WHERE ID=?";
		return jdbcTemplate.update(sql, status, id);
    }

    public int updateStatus(int id, int status, Double _amount, String _type) {
		String sql = "UPDATE TBL_COINPAYMENT_PRESALE SET STATUS=?, UPDATE_AT=SYSDATE, CRYPTO_AMOUNT = ?, CRYPTO_TYPE = ? WHERE ID=?";
		return jdbcTemplate.update(sql, status, _amount, _type,  id);
	}

    @Override
    public int insertDepositAddress(int id, String address) {
        String sql = "UPDATE TBL_COINPAYMENT_PRESALE SET DEPOSIT_ADDR=? WHERE ID=?";
        return jdbcTemplate.update(sql, address, id);
    }

	public int deleteExpired(double days) {
		String sql = "DELETE FROM TBL_COINPAYMENT_PRESALE WHERE SYSDATE-CREATED_AT>?";
		return jdbcTemplate.update(sql, days);
	}
    
}
