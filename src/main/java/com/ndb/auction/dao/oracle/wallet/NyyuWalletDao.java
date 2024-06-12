package com.ndb.auction.dao.oracle.wallet;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.wallet.NyyuWallet;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
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
        m.setNyyuPayRegistered(rs.getBoolean("NYYUPAY_REGISTERED"));
        return m;
    }
    public int insert(NyyuWallet m) {
        String sql = "INSERT INTO TBL_NYYU_WALLET(ID, USER_ID,NETWORK,PRIVATE_KEY,PUBLIC_KEY,NYYUPAY_REGISTERED)" +
                "VALUES(SEQ_NYYU_WALLET.NEXTVAL,?,?,?,?,?)";
        return jdbcTemplate.update(sql, m.getUserId(),m.getNetwork(),m.getPrivateKey(),m.getPublicKey());
    }

    public int insertOrUpdate(NyyuWallet m) {
        String sql =  "MERGE INTO TBL_NYYU_WALLET USING DUAL ON (ID=?)"
                + "WHEN MATCHED THEN UPDATE SET NYYUPAY_REGISTERED= ? "
                + "WHEN NOT MATCHED THEN INSERT (ID, USER_ID,NETWORK,PRIVATE_KEY,PUBLIC_KEY,NYYUPAY_REGISTERED)"
                + "VALUES(SEQ_NYYU_WALLET.NEXTVAL,?,?,?,?,?)";
        return jdbcTemplate.update(sql,m.getId(),m.getNyyuPayRegistered(), m.getUserId(), m.getNetwork(),m.getPrivateKey(),m.getPublicKey(), m.getNyyuPayRegistered());
    }

    public int isInteralWallet(String address){
        String sql = "SELECT COUNT(*) FROM TBL_NYYU_WALLET WHERE PUBLIC_KEY=?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException {
                if (!rs.next())
                    return 0;
                return rs.getInt(1);
            }
        },address);
    }

    public NyyuWallet selectByAddress(String address) {
        String sql = "SELECT * FROM TBL_NYYU_WALLET WHERE LOWER(PUBLIC_KEY)=?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, address);
    }

    public NyyuWallet selectByUserId(int userId, String network) {
        String sql = "SELECT * FROM TBL_NYYU_WALLET WHERE USER_ID=? AND NETWORK = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, userId, network);
    }

}
