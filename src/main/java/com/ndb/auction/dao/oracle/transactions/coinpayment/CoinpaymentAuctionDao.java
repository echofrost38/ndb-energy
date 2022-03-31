package com.ndb.auction.dao.oracle.transactions.coinpayment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.dao.oracle.transactions.ICryptoDepositTransactionDao;
import com.ndb.auction.dao.oracle.transactions.ITransactionDao;
import com.ndb.auction.models.transactions.CryptoDepositTransaction;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentAuctionTransaction;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_COINPAYMENT_AUCTION")
public class CoinpaymentAuctionDao extends BaseOracleDao implements ITransactionDao, ICryptoDepositTransactionDao {
    
	private static CoinpaymentAuctionTransaction extract(ResultSet rs) throws SQLException {
		CoinpaymentAuctionTransaction m = new CoinpaymentAuctionTransaction();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setAmount(rs.getDouble("AMOUNT"));
		m.setFee(rs.getDouble("FEE"));
		m.setCreatedAt(rs.getTimestamp("CREATED_AT").getTime());
		m.setStatus(rs.getBoolean("STATUS"));
		m.setCryptoType(rs.getString("CRYPTO_TYPE"));
		m.setNetwork(rs.getString("NETWORK"));
        m.setCryptoAmount(rs.getDouble("CRYPTO_AMOUNT"));
		m.setConfirmedAt(rs.getTimestamp("UPDATED_AT").getTime());
        m.setDepositAddress(rs.getString("DEPOSIT_ADDR"));
        m.setCoin(rs.getString("COIN"));
		m.setAuctionId(rs.getInt("AUCTION_ID"));
        m.setBidId(rs.getInt("BID_ID"));
		return m;
	}

    @Override
    public Transaction insert(Transaction _m) {
        CoinpaymentAuctionTransaction m = (CoinpaymentAuctionTransaction)_m;
        String sql = "INSERT INTO TBL_COINPAYMENT_AUCTION(ID,USER_ID,AMOUNT,FEE,CREATED_AT,STATUS,CRYPTO_TYPE,NETWORK,CRYPTO_AMOUNT,UPDATED_AT,DEPOSIT_ADDR,COIN,AUCTION_ID,BID_ID)"
				+ " VALUES(SEQ_COINPAY_AUCTION.NEXTVAL,?,?,?,SYSDATE,0,?,?,?,SYSDATE,?,?,?,?)";
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
						ps.setDouble(i++, m.getFee());
						ps.setString(i++, m.getCryptoType());
                        ps.setString(i++, m.getNetwork());
						ps.setDouble(i++, m.getCryptoAmount());
                        ps.setString(i++, m.getDepositAddress());
                        ps.setString(i++, m.getCoin());
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
        String sql = "SELECT * FROM TBL_COINPAYMENT_AUCTION";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    @Override
    public List<? extends Transaction> selectByUser(int userId, String orderBy) {
        String sql = "SELECT * FROM TBL_COINPAYMENT_AUCTION WHERE USER_ID = ?";
		if (orderBy == null)
			orderBy = "ID";
		sql += " ORDER BY " + orderBy;
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }

	public List<? extends Transaction> selectByAuctionId(int auctionId) {
        String sql = "SELECT * FROM TBL_COINPAYMENT_AUCTION WHERE AUCTION_ID = ?";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), auctionId);
    }

	public List<? extends Transaction> select(int userId, int auctionId) {
        String sql = "SELECT * FROM TBL_COINPAYMENT_AUCTION WHERE USER_ID = ? AND AUCTION_ID = ?";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, auctionId);
    }
    
    @Override
    public Transaction selectById(int id) {
        String sql = "SELECT * FROM TBL_COINPAYMENT_AUCTION WHERE ID=?";
		return jdbcTemplate.query(sql, rs -> {
            if(!rs.next())
                return null;
            return extract(rs);
        }, id);
    }

    @Override
    public int update(int id, int status) {
        String sql = "UPDATE TBL_COINPAYMENT_AUCTION SET STATUS=?, UPDATE_AT=SYSDATE WHERE ID=?";
		return jdbcTemplate.update(sql, status, id);
    }

	public int updateStatus(int id, int status, Double _amount, String _type) {
		String sql = "UPDATE TBL_COINPAYMENT_AUCTION SET STATUS=?, UPDATE_AT=SYSDATE, CRYPTO_AMOUNT = ?, CRYPTO_TYPE = ? WHERE ID=?";
		return jdbcTemplate.update(sql, status, _amount, _type,  id);
	}

    @Override
    public List<CryptoDepositTransaction> selectByDepositAddress(String depositAddress) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int insertDepositAddress(int id, String address) {
        String sql = "UPDATE TBL_COINPAYMENT_AUCTION SET DEPOSIT_ADDR=? WHERE ID=?";
        return jdbcTemplate.update(sql, address, id);
    }

	public int deleteExpired(double days) {
		String sql = "DELETE FROM TBL_COINPAYMENT_AUCTION WHERE SYSDATE-CREATED_AT>?";
		return jdbcTemplate.update(sql, days);
	}
    
	public List<CoinpaymentAuctionTransaction> selectRange(int userId, long from, long to) {
        String sql = "SELECT * FROM TBL_COINPAYMENT_AUCTION WHERE USER_ID = ? AND CREATED_AT > ? AND CREATED_AT < ? ORDER BY ID DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, new Timestamp(from), new Timestamp(to));
    }
	
}
