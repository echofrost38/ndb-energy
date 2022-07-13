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
        m.setTarget(rs.getInt("TARGET"));
        m.setWalletConnect(rs.getString("WALLET_CONNECT"));
        m.setRecord(rs.getBoolean("RECORD"));
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
        String sql = "SELECT * FROM TBL_USER_REFERRAL WHERE LOWER(WALLET_CONNECT)=LOWER(?) AND DELETED=0";
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
        String sql = "INSERT INTO TBL_USER_REFERRAL(ID, REFERRAL_CODE, REFERRED_BY_CODE,TARGET, WALLET_CONNECT, ACTIVE, RECORD, DELETED, REG_DATE, UPDATE_DATE)"
                + "VALUES(?,?,?,?,?,?,?,0,SYSDATE,SYSDATE)";
        return jdbcTemplate.update(sql,m.getId(), m.getReferralCode(), m.getReferredByCode(),m.getTarget(), m.getWalletConnect(),m.isActive(), m.isRecord());
    }

    public int insertOrUpdate(UserReferral m) {
        String sql =  "MERGE INTO TBL_USER_REFERRAL USING DUAL ON (ID=?)"
                + "WHEN MATCHED THEN UPDATE SET WALLET_CONNECT= ?, TARGET=?, REFERRAL_CODE=?, ACTIVE=? "
                + "WHEN NOT MATCHED THEN INSERT (ID, REFERRAL_CODE, REFERRED_BY_CODE,TARGET,WALLET_CONNECT, ACTIVE, DELETED, REG_DATE, UPDATE_DATE)"
                + "VALUES(?,?,?,?,?,?,0,SYSDATE,SYSDATE)";
        return jdbcTemplate.update(sql,m.getId(),m.getWalletConnect(),m.getTarget(), m.getReferralCode(), m.isActive(), 
            m.getId(), m.getReferralCode(), m.getReferredByCode(),m.getTarget(), m.getWalletConnect(), m.isActive());
    }

    public int updateWalletConnect(int id, int target, String walletConnect) {
        String sql = "UPDATE TBL_USER_REFERRAL SET WALLET_CONNECT=?, TARGET = ? WHERE ID=?";
        return jdbcTemplate.update(sql, walletConnect, target, id);
    }

    public int setReferralRecordOnchain(int id ,boolean status) {
        String sql = "UPDATE TBL_USER_REFERRAL SET RECORD=? WHERE ID=?";
        return jdbcTemplate.update(sql, status, id);
    }

    public int update(UserReferral m) {
        String sql = "UPDATE TBL_USER_REFERRAL SET REFERRAL_CODE = ?, REFERRED_BY_CODE = ?, WALLET_CONNECT= ? WHERE ID = ?";
        return jdbcTemplate.update(sql, m.getReferralCode(), m.getReferredByCode(), m.getWalletConnect(),m.getId());
    }

    public List<UserReferral> getAllByReferredByCode(String referredByCode) {
        String sql = "SELECT * FROM TBL_USER_REFERRAL WHERE REFERRED_BY_CODE=? AND RECORD=1 AND DELETED=0";
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
