package com.ndb.auction.dao.oracle.withdraw;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        // m.setEmail(rs.getString("EMAIL"));
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

        m.setMode(rs.getInt("MODE"));
        m.setCountry(rs.getString("COUNTRY"));
        m.setNameOfHolder(rs.getString("HOLDER_NAME"));
        m.setBankName(rs.getString("BANK_NAME"));
        m.setAccountNumber(rs.getString("ACC_NUM"));

        // json string
        m.setMetadata(rs.getString("METADATA"));
		return m;
	}

    public int insert(BankWithdrawRequest m) {
        String sql = "INSERT INTO TBL_BANK_WITHDRAW(ID,USER_ID,TAR_CURRENCY,WITHDRAW,FEE,SRC_TOKEN,TKN_PRICE,TKN_AMT," + 
            "STATUS,DENIED_REASON,REQUESTED_AT,CONFIRMED_AT,MODE,COUNTRY,HOLDER_NAME,BANK_NAME,ACC_NUM,METADATA)" + 
            "VALUES(SEQ_BANK_WITHDRAW.NEXTVAL,?,?,?,?,?,?,?,0,?,SYSDATE,SYSDATE,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, m.getUserId(), m.getTargetCurrency(), m.getWithdrawAmount(), m.getFee(), 
            m.getSourceToken(), m.getTokenPrice(), m.getTokenAmount(), m.getDeniedReason(), m.getMode(), m.getCountry(),
            m.getNameOfHolder(), m.getBankName(), m.getAccountNumber(), m.getMetadata());
    }

    public List<BankWithdrawRequest> selectPending() {
        String sql = "SELECT * FROM TBL_BANK_WITHDRAW WHERE STATUS = 0";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public List<BankWithdrawRequest> selectApproved() {
        String sql = "SELECT * FROM TBL_BANK_WITHDRAW WHERE STATUS = 1";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public List<BankWithdrawRequest> selectDenied() {
        String sql = "SELECT * FROM TBL_BANK_WITHDRAW WHERE STATUS = 2";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public List<BankWithdrawRequest> selectByUser(int userId) {
        String sql = "SELECT * FROM TBL_BANK_WITHDRAW WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public BankWithdrawRequest selectById(int id) {
        String sql = "SELECT * FROM TBL_BANK_WITHDRAW WHERE ID = ?";
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

}
