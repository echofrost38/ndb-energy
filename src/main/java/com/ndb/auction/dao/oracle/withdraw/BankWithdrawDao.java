package com.ndb.auction.dao.oracle.withdraw;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.withdraw.BankWithdrawRequest;

import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_BANK_WITHDRAW")
public class BankWithdrawDao extends BaseOracleDao {

    private static BankWithdrawRequest extract(ResultSet rs) throws SQLException {
		BankWithdrawRequest m = new BankWithdrawRequest();
		m.setId(rs.getInt("ID"));
        m.setUserId(rs.getInt("USER_ID"));
        m.setEmail(rs.getString("EMAIL"));
        m.setTargetCurrency(rs.getString("TAR_CURRENCY"));
        m.setWithdrawAmount(rs.getDouble("WITHDRAW"));
        m.setFee(rs.getDouble("FEE"));
        m.setSourceToken(rs.getString("SRC_TOKEN"));
        m.setTokenPrice(rs.getDouble("TKN_PRICE"));
        m.setTokenAmount(rs.getDouble("TKN_AMT"));
        m.setStatus(rs.getInt("STATUS"));
        m.setDeniedReason(rs.getString("DENIED_REASON"));
        m.setRequestedAt(rs.getTimestamp("REQUESTED_AT").getTime());
        m.setConfirmedAt(   rs.getTimestamp("CONFIRMED_AT").getTime());

        m.setMode(rs.getInt("W_MODE"));
        m.setCountry(rs.getString("COUNTRY"));
        m.setHolderName(rs.getString("HOLDER_NAME"));
        m.setBankName(rs.getString("BANK_NAME"));
        m.setAccountNumber(rs.getString("ACC_NUM"));
        m.setShow(rs.getBoolean("IS_SHOW"));

        // json string
        m.setMetadata(rs.getString("METADATA"));

        m.setAddress(rs.getString("ADDRESS"));
        m.setPostCode(rs.getString("POSTCODE"));
		return m;
	}

    public int insert(BankWithdrawRequest m) {
        String sql = "INSERT INTO TBL_BANK_WITHDRAW(ID,USER_ID,TAR_CURRENCY,WITHDRAW,FEE,SRC_TOKEN,TKN_PRICE,TKN_AMT," + 
            "STATUS,DENIED_REASON,REQUESTED_AT,CONFIRMED_AT,W_MODE,COUNTRY,HOLDER_NAME,BANK_NAME,ACC_NUM,METADATA,ADDRESS,POSTCODE,IS_SHOW)" + 
            "VALUES(SEQ_BANK_WITHDRAW.NEXTVAL,?,?,?,?,?,?,?,0,?,SYSDATE,SYSDATE,?,?,?,?,?,?,?,?,1)";
        return jdbcTemplate.update(sql, m.getUserId(), m.getTargetCurrency(), m.getWithdrawAmount(), m.getFee(), 
            m.getSourceToken(), m.getTokenPrice(), m.getTokenAmount(), m.getDeniedReason(), m.getMode(), m.getCountry(),
            m.getHolderName(), m.getBankName(), m.getAccountNumber(), m.getMetadata(), m.getAddress(), m.getPostCode());
    }

    public List<BankWithdrawRequest> selectPending() {
        String sql = "SELECT TBL_BANK_WITHDRAW.*, TBL_USER.EMAIL FROM TBL_BANK_WITHDRAW LEFT JOIN TBL_USER ON TBL_BANK_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_BANK_WITHDRAW.STATUS = 0";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public List<BankWithdrawRequest> selectApproved() {
        String sql = "SELECT TBL_BANK_WITHDRAW.*, TBL_USER.EMAIL FROM TBL_BANK_WITHDRAW LEFT JOIN TBL_USER ON TBL_BANK_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_BANK_WITHDRAW.STATUS = 1";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public List<BankWithdrawRequest> selectDenied() {
        String sql = "SELECT TBL_BANK_WITHDRAW.*, TBL_USER.EMAIL FROM TBL_BANK_WITHDRAW LEFT JOIN TBL_USER ON TBL_BANK_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_BANK_WITHDRAW.STATUS = 2";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public List<BankWithdrawRequest> selectByUser(int userId, int status) {
        String sql = "SELECT TBL_BANK_WITHDRAW.*, TBL_USER.EMAIL FROM TBL_BANK_WITHDRAW LEFT JOIN TBL_USER ON TBL_BANK_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_BANK_WITHDRAW.USER_ID = ?";
        if(status == 0) {
            sql += " AND TBL_BANK_WITHDRAW.IS_SHOW = 1";
        }
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }

    public List<BankWithdrawRequest> selectAll() {
        String sql = "SELECT TBL_BANK_WITHDRAW.*, TBL_USER.EMAIL FROM TBL_BANK_WITHDRAW LEFT JOIN TBL_USER ON TBL_BANK_WITHDRAW.USER_ID = TBL_USER.ID";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public BankWithdrawRequest selectById(int id, int status) {
        String sql = "SELECT TBL_BANK_WITHDRAW.*, TBL_USER.EMAIL FROM TBL_BANK_WITHDRAW LEFT JOIN TBL_USER ON TBL_BANK_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_BANK_WITHDRAW.ID = ?";
        if(status == 0) {
            sql += " AND TBL_BANK_WITHDRAW.IS_SHOW = 1";
        }
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, id);
    }

    public int approveRequest(int id) {
        String sql = "UPDATE TBL_BANK_WITHDRAW SET STATUS = 1 WHERE ID = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int denyRequest(int id, String reason) {
        String sql = "UPDATE TBL_BANK_WITHDRAW SET STATUS = 2, DENIED_REASON = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, reason, id);
    }

    public List<BankWithdrawRequest> selectRange(int userId, long from, long to) {
        String sql = "SELECT TBL_BANK_WITHDRAW.*, TBL_USER.EMAIL from TBL_BANK_WITHDRAW left JOIN TBL_USER on TBL_BANK_WITHDRAW.USER_ID = TBL_USER.ID WHERE TBL_BANK_WITHDRAW.USER_ID = ? AND TBL_BANK_WITHDRAW.REQUESTED_AT > ? AND TBL_BANK_WITHDRAW.REQUESTED_AT < ? ORDER BY TBL_BANK_WITHDRAW.ID DESC";
		return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId, new Timestamp(from), new Timestamp(to));
    }
    
    public int changeShowStatus(int id, int showStatus) {
        var sql = "UPDATE TBL_BANK_WITHDRAW SET IS_SHOW WHERE ID = ?";
        return jdbcTemplate.update(sql, showStatus, id);
    }

}
