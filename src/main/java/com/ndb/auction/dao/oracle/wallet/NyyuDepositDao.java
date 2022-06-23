package com.ndb.auction.dao.oracle.wallet;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.wallet.NyyuDeposit;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@NoArgsConstructor
@Table(name = "TBL_NYYU_DEPOSIT")
public class NyyuDepositDao extends BaseOracleDao {
    private static NyyuDeposit extract(ResultSet rs) throws SQLException {
        NyyuDeposit m = new NyyuDeposit();
        m.setId(rs.getInt("ID"));
        m.setUserId(rs.getInt("USER_ID"));
        m.setAmount(rs.getDouble("AMOUNT"));
        m.setWalletAddress(rs.getString("WALLET_ADDRESS"));
        m.setTxnHash(rs.getString("TXN_HASH"));
        return m;
    }
    public int insert(NyyuDeposit m) {
        String sql = "INSERT INTO TBL_NYYU_DEPOSIT(ID,USER_ID,WALLET_ADDRESS,AMOUNT,TXN_HASH)" +
                "VALUES(SEQ_NYYU_DEPOSIT,?,?,?,?)";
        return jdbcTemplate.update(sql, m.getUserId(),m.getWalletAddress(),m.getAmount(),m.getTxnHash());
    }
}
