package com.ndb.auction.dao.oracle.withdraw;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        m.setTargetCurrency(rs.getString("TAR_CURRENCY"));
        m.setWithdrawAmount(rs.getDouble("WITHDRAW"));
        m.setFee(rs.getDouble("FEE"));
        m.setSourceToken(rs.getString("SRC_TOKEN"));
        m.setTokenPrice(rs.getDouble("TKN_PRICE"));
        m.setTokenAmount(rs.getDouble("TKN_AMT"));
        m.setStatus(rs.getInt("STATUS"));
        m.setDeniedReason(rs.getString("DENIED_REASON"));
        m.setRequestedAt(rs.getTimestamp("REQUESTED_AT").getTime());
        m.setConfirmedAt(rs.getTimestamp("CONFIRMED_AT").getTime());

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
        String sql = "INSERT INTO TBL_BANK_WITHDRAW()VALUES()";
        return 0;
    }

}
