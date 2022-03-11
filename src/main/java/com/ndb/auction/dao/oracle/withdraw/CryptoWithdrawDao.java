package com.ndb.auction.dao.oracle.withdraw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.withdraw.BaseWithdraw;
import com.ndb.auction.models.withdraw.CryptoWithdraw;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_CRYPTO_WITHDRAW")
public class CryptoWithdrawDao extends BaseOracleDao implements IWithdrawDao {
    
    private static CryptoWithdraw extract(ResultSet rs) throws SQLException {
        CryptoWithdraw m = new CryptoWithdraw();
        m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setSourceToken(rs.getString("SOURCE"));
        m.setTokenPrice(rs.getDouble("TOKEN_PRICE"));
        // withdraw amount
        m.setWithdrawAmount(rs.getDouble("AMOUNT"));
        m.setFee(rs.getDouble("FEE"));
        // requested amount
        m.setTokenAmount(rs.getDouble("TOKEN_AMOUNT"));
        m.setStatus(rs.getInt("STATUS"));
        m.setDeniedReason(rs.getString("REASON"));
        m.setRequestedAt(rs.getTimestamp("REQUESTED_AT").getTime());
        m.setConfirmedAt(rs.getTimestamp("CONFIRMED_AT").getTime());
        m.setNetwork(rs.getString("NETWORK"));
        m.setDestination(rs.getString("DEST"));
		return m;
    }
    
    @Override
    public BaseWithdraw insert(BaseWithdraw baseWithdraw) {
        var m = (CryptoWithdraw)baseWithdraw;
        var sql = "INSERT INTO TBL_CRYPTO_WITHDRAW(ID,USER_ID,SOURCE,TOKEN_AMOUNT,TOKEN_PRICE,AMOUNT,FEE,STATUS,REASON,REQUESTED_AT,CONFIRMED_AT,NETWORK,DEST)"
        + " VALUES(SEQ_CRYPTO_WITHDRAW.NEXTVAL,?,?,?,?,?,?,0,?,SYSDATE,SYSDATE,?,?)";
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(sql,
                                new String[] { "ID" });
                        int i = 1;
                        ps.setInt(i++, m.getUserId());
                        ps.setString(i++, m.getSourceToken());
                        ps.setDouble(i++, m.getTokenAmount());
                        ps.setDouble(i++, m.getTokenPrice());
                        ps.setDouble(i++, m.getWithdrawAmount());
                        ps.setDouble(i++, m.getFee());
                        ps.setString(i++, m.getDeniedReason());
                        ps.setString(i++, m.getNetwork());
                        ps.setString(i++, m.getDestination());
                        return ps;
                    }
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;
    }

    @Override
    public int confirmWithdrawRequest(int requestId, int status, String reason) {
        var sql = "UPDATE TBL_CRYPTO_WITHDRAW SET CONFIRMED_AT = SYSDATE, STATUS = ?, REASON = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, status, reason, requestId);
    }

    @Override
    public List<? extends BaseWithdraw> selectByUser(int userId) {
        var sql = "SELECT * FROM TBL_CRYPTO_WITHDRAW WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, new RowMapper<CryptoWithdraw>() {
			@Override
			public CryptoWithdraw mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

    @Override
    public List<? extends BaseWithdraw> selectByStatus(int userId, int status) {
        var sql = "SELECT * FROM TBL_CRYPTO_WITHDRAW WHERE USER_ID=? AND STATUS=?";
        return jdbcTemplate.query(sql, new RowMapper<CryptoWithdraw>() {
			@Override
			public CryptoWithdraw mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId, status);
    }

    @Override
    public List<? extends BaseWithdraw> selectPendings() {
        var sql = "SELECT * FROM TBL_CRYPTO_WITHDRAW WHERE STATUS=1";
        return jdbcTemplate.query(sql, new RowMapper<CryptoWithdraw>() {
			@Override
			public CryptoWithdraw mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
    }

    @Override
    public BaseWithdraw selectById(int id) {
        String sql = "SELECT * FROM TBL_CRYPTO_WITHDRAW WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<CryptoWithdraw>() {
			@Override
			public CryptoWithdraw extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
    }
    
}