package com.ndb.auction.dao.oracle.withdraw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.withdraw.BaseWithdraw;
import com.ndb.auction.models.withdraw.PaypalWithdraw;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_PAYPAL_WITHDRAW")
public class PaypalWithdrawDao extends BaseOracleDao implements IWithdrawDao {

    private static PaypalWithdraw extract(ResultSet rs) throws SQLException {
        PaypalWithdraw m = new PaypalWithdraw();
        m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
        m.setEmail(rs.getString("EMAIL"));
        m.setTargetCurrency(rs.getString("TARGET"));
		m.setSourceToken(rs.getString("SOURCE"));
        m.setTokenPrice(rs.getDouble("TOKEN_PRICE"));
        m.setTokenAmount(rs.getDouble("AMOUNT"));
        m.setFee(rs.getDouble("FEE"));
        m.setWithdrawAmount(rs.getDouble("WITHDRAW"));
        m.setStatus(rs.getInt("STATUS"));
        m.setDeniedReason(rs.getString("REASON"));
        m.setRequestedAt(rs.getTimestamp("REQUESTED_AT").getTime());
        m.setConfirmedAt(rs.getTimestamp("CONFIRMED_AT").getTime());
        m.setSenderBatchId(rs.getString("BATCH_ID"));
        m.setSenderItemId(rs.getString("ITEM_ID"));
        m.setPayoutBatchId(rs.getString("PAYOUT_ID"));
        m.setReceiver(rs.getString("RECEIVER"));
        m.setShow(rs.getBoolean("IS_SHOW"));
		return m;
    }

    @Override
    public BaseWithdraw insert(BaseWithdraw baseWithdraw) {
        var m = (PaypalWithdraw)baseWithdraw;
        var sql = "INSERT INTO TBL_PAYPAL_WITHDRAW(ID,USER_ID,TARGET,SOURCE,TOKEN_PRICE,AMOUNT,WITHDRAW,FEE,STATUS,REASON,REQUESTED_AT,CONFIRMED_AT,BATCH_ID,ITEM_ID,RECEIVER,IS_SHOW)"
        + " VALUES(SEQ_PAYPAL_WITHDRAW.NEXTVAL,?,?,?,?,?,?,?,0,?,SYSDATE,SYSDATE,?,?,?,1)";
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(sql,
                                new String[] { "ID" });
                        int i = 1;
                        ps.setInt(i++, m.getUserId());
                        ps.setString(i++, m.getTargetCurrency());
                        ps.setString(i++, m.getSourceToken());
                        ps.setDouble(i++, m.getTokenPrice());
                        ps.setDouble(i++, m.getTokenAmount());
                        ps.setDouble(i++, m.getWithdrawAmount());
                        ps.setDouble(i++, m.getFee());
                        ps.setString(i++, m.getDeniedReason());
                        ps.setString(i++, m.getSenderBatchId());
                        ps.setString(i++, m.getSenderItemId());
                        ps.setString(i++, m.getReceiver());
                        return ps;
                    }
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;
    }

    @Override
    public int confirmWithdrawRequest(int requestId, int status, String reason) {
        var sql = "UPDATE TBL_PAYPAL_WITHDRAW SET CONFIRMED_AT = SYSDATE, STATUS = ?, REASON = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, status, reason, requestId);
    }

    public List<? extends BaseWithdraw> selectAll() {
        var sql = "SELECT TBL_PAYPAL_WITHDRAW.*, TBL_USER.EMAIL from TBL_PAYPAL_WITHDRAW left JOIN TBL_USER on TBL_PAYPAL_WITHDRAW.USER_ID = TBL_USER.ID";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    @Override
    public List<? extends BaseWithdraw> selectByUser(int userId, int showStatus) {
        var sql = "SELECT TBL_PAYPAL_WITHDRAW.*, TBL_USER.EMAIL from TBL_PAYPAL_WITHDRAW left JOIN TBL_USER on TBL_PAYPAL_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_PAYPAL_WITHDRAW.USER_ID = ? AND TBL_PAYPAL_WITHDRAW.IS_SHOW = ?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, showStatus);
    }

    @Override
    public List<? extends BaseWithdraw> selectByStatus(int userId, int status) {
        var sql = "SELECT TBL_PAYPAL_WITHDRAW.*, TBL_USER.EMAIL from TBL_PAYPAL_WITHDRAW left JOIN TBL_USER on TBL_PAYPAL_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_PAYPAL_WITHDRAW.USER_ID=? AND TBL_PAYPAL_WITHDRAW.STATUS=?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, status);
    }

    @Override
    public List<? extends BaseWithdraw> selectPendings() {
        var sql = "SELECT TBL_PAYPAL_WITHDRAW.*, TBL_USER.EMAIL from TBL_PAYPAL_WITHDRAW left JOIN TBL_USER on TBL_PAYPAL_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_PAYPAL_WITHDRAW.STATUS=1";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public List<? extends BaseWithdraw> selectPendings(int userId) {
        var sql = "SELECT TBL_PAYPAL_WITHDRAW.*, TBL_USER.EMAIL from TBL_PAYPAL_WITHDRAW left JOIN TBL_USER on TBL_PAYPAL_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_PAYPAL_WITHDRAW.STATUS=1 AND TBL_PAYPAL_WITHDRAW.USER_ID=?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }

    @Override
    public BaseWithdraw selectById(int id, int showStatus) {
        String sql = "SELECT TBL_PAYPAL_WITHDRAW.*, TBL_USER.EMAIL from TBL_PAYPAL_WITHDRAW left JOIN TBL_USER on TBL_PAYPAL_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_PAYPAL_WITHDRAW.ID=?";
		if(showStatus == 0) {
            sql += " AND TBL_PAYPAL_WITHDRAW.IS_SHOW = ?";
        }
        return jdbcTemplate.query(sql, rs -> {
			if (!rs.next())
				return null;
			return extract(rs);
		}, id);
    }

    public BaseWithdraw selectByUserId(int id, int userId, int showStatus) {
        String sql = "SELECT TBL_PAYPAL_WITHDRAW.*, TBL_USER.EMAIL from TBL_PAYPAL_WITHDRAW left JOIN TBL_USER on TBL_PAYPAL_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_PAYPAL_WITHDRAW.ID=? AND TBL_PAYPAL_WITHDRAW.USER_ID=?";
		if(showStatus == 0) {
            sql += " AND TBL_PAYPAL_WITHDRAW.IS_SHOW = ?";
        }
        return jdbcTemplate.query(sql, rs -> {
			if (!rs.next())
				return null;
			return extract(rs);
		}, id, userId);
    }

    public PaypalWithdraw selectByPayoutId(String payoutId) {
        String sql = "SELECT TBL_PAYPAL_WITHDRAW.*, TBL_USER.EMAIL from TBL_PAYPAL_WITHDRAW left JOIN TBL_USER on TBL_PAYPAL_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_PAYPAL_WITHDRAW.PAYOUT_ID=?";
        return jdbcTemplate.query(sql,  rs -> {
			if (!rs.next())
				return null;
			return extract(rs);
		}, payoutId);
    }
    
    public int updatePaypalID(int id, String payoutId, String batchId, String itemId) {
        var sql = "UPDATE TBL_PAYPAL_WITHDRAW SET PAYOUT_ID=?,BATCH_ID=?,ITEM_ID=? WHERE ID = ?";
        return jdbcTemplate.update(sql, payoutId, batchId, itemId, id);
    }

    public List<PaypalWithdraw> selectRange(int userId, long from, long to) {
        String sql = "SELECT TBL_PAYPAL_WITHDRAW.*, TBL_USER.EMAIL from TBL_PAYPAL_WITHDRAW left JOIN TBL_USER on TBL_PAYPAL_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_PAYPAL_WITHDRAW.USER_ID = ? AND TBL_PAYPAL_WITHDRAW.REQUESTED_AT > ? AND TBL_PAYPAL_WITHDRAW.REQUESTED_AT < ? ORDER BY TBL_PAYPAL_WITHDRAW.ID DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, new Timestamp(from), new Timestamp(to));
    }

    public int changeShowStatus(int id, int showStatus) {
        var sql = "UPDATE TBL_PAYPAL_WITHDRAW SET IS_SHOW = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, showStatus, id);
    }
}
