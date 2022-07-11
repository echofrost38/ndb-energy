package com.ndb.auction.dao.oracle.user;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.user.UserReferral;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@NoArgsConstructor
@Table(name = "TBL_USER_REFERRAL")
public class UserReferralDao extends BaseOracleDao {

    private static UserReferral extract(ResultSet rs) throws SQLException {
        UserReferral m = new UserReferral();
        m.setId(rs.getInt("ID"));
        m.setReferralCode(rs.getString("REFERRAL_CODE"));
        m.setReferredByCode(rs.getString("REFERRED_BY_CODE"));
        m.setWalletConnect(rs.getString("WALLET_CONNECT"));
        m.setPaidTxn(rs.getString("PAID_TXN"));
        m.setActive(rs.getBoolean("ACTIVE"));
        m.setDeleted(rs.getInt("DELETED"));
        m.setRegDate(rs.getTimestamp("REG_DATE").getTime());
        m.setUpdateDate(rs.getTimestamp("UPDATE_DATE").getTime());
        return m;
    }

    public UserReferral selectById(int id) {
        String sql = "SELECT * FROM TBL_USER_REFERRAL WHERE ID=? AND DELETED=0";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, id);
    }

    public UserReferral selectByReferralCode(String referredCode) {
        String sql = "SELECT * FROM TBL_USER_REFERRAL WHERE REFERRAL_CODE=? AND DELETED=0";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, referredCode);
    }

    public UserReferral selectByWalletConnect(String wallet) {
        String sql = "SELECT * FROM TBL_USER_REFERRAL WHERE LOWER(WALLET_CONNECT)=? AND DELETED=0";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, wallet);
    }

    public List<UserReferral> selectAll(String orderby) {
        String sql = "SELECT * FROM TBL_USER_REFERRAL";
        if (orderby == null || orderby.equals(""))
            orderby = "ID";
        sql += " ORDER BY " + orderby;
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public int insert(UserReferral m) {
        String sql = "INSERT INTO TBL_USER_REFERRAL(ID, REFERRAL_CODE, REFERRED_BY_CODE,WALLET_CONNECT, DELETED, REG_DATE, UPDATE_DATE)"
                + "VALUES(?,?,?,?,0,SYSDATE,SYSDATE)";
        return jdbcTemplate.update(sql,m.getId(), m.getReferralCode(), m.getReferredByCode(),m.getWalletConnect());
    }

    public int insertOrUpdate(UserReferral m) {
        String sql =  "MERGE INTO TBL_USER_REFERRAL USING DUAL ON (ID=?)"
                + "WHEN MATCHED THEN UPDATE SET WALLET_CONNECT= ? "
                + "WHEN NOT MATCHED THEN INSERT (ID, REFERRAL_CODE, REFERRED_BY_CODE,WALLET_CONNECT, DELETED, REG_DATE, UPDATE_DATE)"
                + "VALUES(?,?,?,?,0,SYSDATE,SYSDATE)";
        return jdbcTemplate.update(sql,m.getId(),m.getWalletConnect(),m.getId(), m.getReferralCode(), m.getReferredByCode(),m.getWalletConnect());
    }

    public int updateWalletConnect(int id, String walletConnect) {
        String sql = "UPDATE TBL_USER_REFERRAL SET WALLET_CONNECT=? WHERE ID=?";
        return jdbcTemplate.update(sql, walletConnect, id);
    }

    public int updatePaidCommissionTxn(String txn, String referralCode,String referredByCode) {
        String sql = "UPDATE TBL_USER_REFERRAL SET PAID_TXN=? WHERE REFERRAL_CODE=? AND REFERRED_BY_CODE=?";
        return jdbcTemplate.update(sql, txn, referralCode,referredByCode);
    }

    public int setReferralStatus(int id ,boolean status) {
        String sql = "UPDATE TBL_USER_REFERRAL SET ACTIVE=? WHERE ID=?";
        return jdbcTemplate.update(sql, status, id);
    }

    public int update(UserReferral m) {
        String sql = "UPDATE TBL_USER_REFERRAL SET REFERRAL_CODE = ?, REFERRED_BY_CODE = ?, WALLET_CONNECT= ? WHERE ID = ?";
        return jdbcTemplate.update(sql, m.getReferralCode(), m.getReferredByCode(), m.getWalletConnect(),m.getId());
    }
//    public int insertOrUpdate(UserAvatar m) {
//        String sql = "MERGE INTO TBL_USER_REFERRAL USING DUAL ON (ID=?)"
//                + "WHEN MATCHED THEN UPDATE SET PURCHASED=?, HAIR_COLOR=?,SKIN_COLOR=?, SELECTED=?, PREFIX=?, NAME=?, UPDATE_DATE=SYSDATE "
//                + "WHEN NOT MATCHED THEN INSERT(ID, PURCHASED, HAIR_COLOR,SKIN_COLOR, SELECTED, PREFIX, NAME, REG_DATE, UPDATE_DATE)"
//                + "VALUES(?,?,?,?,?,?,?,SYSDATE,SYSDATE)";
//        return jdbcTemplate.update(sql, m.getId(), m.getPurchased(),m.getHairColor(), m.getSkinColor(), m.getSelected(), m.getPrefix(), m.getName(), m.getId(),
//                m.getPurchased(),m.getHairColor(), m.getSkinColor(), m.getSelected(), m.getPrefix(), m.getName());
//    }

//    public int insert(UserReferral m) {
//        String sql = "INSERT INTO TBL_USER_REFERRAL(ID, USER_ID, REFERRAL_CODE, REFERRED_BY_CODE, DELETED, REG_DATE, UPDATE_DATE)"
//                + "VALUES(SEQ_USER_DETAIL.NEXTVAL,?,?,0,SYSDATE,SYSDATE)";
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        jdbcTemplate.update(
//                connection -> {
//                    PreparedStatement ps = connection.prepareStatement(sql, new String[] { "ID" });
//                    int i = 1;
//                    ps.setLong(i++, m.getUserId());
//                    ps.setString(i++, m.getReferralCode());
//                    ps.setString(i++, m.getReferredByCode());
//                    return ps;
//                }, keyHolder);
//        m.setId(keyHolder.getKey().intValue());
//        return m;
//    }

    public List<UserReferral> getAllByReferredByCode(String referredByCode) {
        String sql = "SELECT * FROM TBL_USER_REFERRAL WHERE REFERRED_BY_CODE=? AND DELETED=0";
        return jdbcTemplate.query(sql, new RowMapper<UserReferral>() {
            @Override
            public UserReferral mapRow(ResultSet rs, int rownumber) throws SQLException {
                return extract(rs);
            }
        },referredByCode);
    }

    public int existsUserByReferralCode(String code){
        String sql = "SELECT COUNT(*) FROM TBL_USER_REFERRAL WHERE REFERRAL_CODE=?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException {
                if (!rs.next())
                    return 0;
                return rs.getInt(1);
            }
        },code);
    }
}
