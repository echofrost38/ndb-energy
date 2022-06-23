package com.ndb.auction.dao.oracle.wallet;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;

import com.ndb.auction.models.wallet.NyyuWallet;
import com.ndb.auction.models.withdraw.BankWithdrawRequest;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@NoArgsConstructor
@Table(name = "TBL_NYYU_WALLET")
public class NyyuWalletDao extends BaseOracleDao {
    private static NyyuWallet extract(ResultSet rs) throws SQLException {
        NyyuWallet m = new NyyuWallet();
        m.setId(rs.getInt("ID"));
        m.setUserId(rs.getInt("USER_ID"));
        m.setNetwork(rs.getString("NETWORK"));
        m.setPrivateKey(rs.getString("PRIVATE_KEY"));
        m.setPublicKey(rs.getString("PUBLIC_KEY"));
        return m;
    }
    public int insert(NyyuWallet m) {
        String sql = "INSERT INTO TBL_NYYU_WALLET(ID,USER_ID,NETWORK,PRIVATE_KEY,PUBLIC_KE)" +
                "VALUES(SEQ_NYYU_WALLET,?,?,?,?)";
               sql = "INSERT INTO TBL_NYYU_DEPOSIT(ID,USER_ID,WALLET_ADDRESS,AMOUNT,TXN_HASH)" +
                "VALUES(SEQ_NYYU_DEPOSIT,?,?,?,?)";
        return jdbcTemplate.update(sql, m.getUserId(),m.getNetwork(),m.getPrivateKey(),m.getPublicKey());
    }
}
