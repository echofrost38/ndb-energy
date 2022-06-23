package com.ndb.auction.dao.oracle.wallet;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.wallet.NyyuWallet;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        String sql = "INSERT INTO TBL_NYYU_WALLET(ID, USER_ID,NETWORK,PRIVATE_KEY,PUBLIC_KEY)" +
                "VALUES(SEQ_NYYU_WALLET.NEXTVAL,?,?,?,?)";
        return jdbcTemplate.update(sql, m.getUserId(),m.getNetwork(),m.getPrivateKey(),m.getPublicKey());
    }
}
