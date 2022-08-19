package com.ndb.auction.dao.oracle.balance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.balance.CryptoBalance;

import com.ndb.auction.payload.BalancePerUser;
import com.ndb.auction.payload.BalancePayload;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_INTERNAL_BALANCE")
public class CryptoBalanceDao extends BaseOracleDao {

    private static CryptoBalance extract(ResultSet rs) throws SQLException {
        CryptoBalance model = new CryptoBalance();
        model.setUserId(rs.getInt("USER_ID"));
        model.setTokenId(rs.getInt("TOKEN_ID"));
        model.setFree(rs.getDouble("FREE"));
        model.setHold(rs.getDouble("HOLD"));
        return model;
    }

    public List<CryptoBalance> selectByUserId(int userId, String orderby) {
        String sql = "SELECT * FROM TBL_INTERNAL_BALANCE WHERE USER_ID = ?";
        if (orderby == null)
            orderby = "FREE";
        sql += " ORDER BY " + orderby;
        return jdbcTemplate.query(sql, new RowMapper<CryptoBalance>() {
            @Override
            public CryptoBalance mapRow(ResultSet rs, int rownumber) throws SQLException {
                return extract(rs);
            }
        }, userId);
    }

    public CryptoBalance selectById(int userId, int tokenId) {
        String sql = "SELECT * FROM TBL_INTERNAL_BALANCE WHERE USER_ID = ? AND TOKEN_ID = ?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<CryptoBalance>() {
            @Override
            public CryptoBalance extractData(ResultSet rs) throws SQLException {
                if (!rs.next())
                    return null;
                return extract(rs);
            }
        }, userId, tokenId);
    }

    public List<BalancePerUser> selectAll() {
        String sql = "SELECT TBL_INTERNAL_BALANCE.*, TBL_USER.EMAIL, TBL_USER_AVATAR.PREFIX||'.'||TBL_USER_AVATAR.NAME AVATAR_NAME, TBL_TOKEN_ASSET.TOKEN_SYMBOL FROM TBL_INTERNAL_BALANCE JOIN TBL_USER ON TBL_INTERNAL_BALANCE.USER_ID=TBL_USER.ID LEFT JOIN TBL_TOKEN_ASSET ON TBL_INTERNAL_BALANCE.TOKEN_ID=TBL_TOKEN_ASSET.ID LEFT JOIN TBL_USER_AVATAR ON TBL_USER_AVATAR.ID=TBL_USER.ID";
        List<BalancePerUser> resultList = new ArrayList<>();
        jdbcTemplate.query(sql, rs -> {
            int userId = rs.getInt("USER_ID");

            BalancePayload balance = new BalancePayload();
            balance.setTokenSymbol(rs.getString("TOKEN_SYMBOL"));
            balance.setFree(rs.getDouble("FREE"));
            balance.setHold(rs.getDouble("HOLD"));

            BalancePerUser old = null;
            for (BalancePerUser b : resultList) {
                if (b.getUserId() == userId) {
                    old = b;
                    break;
                }
            }
            if (old == null) {
                BalancePerUser model = new BalancePerUser();
                model.setUserId(userId);
                model.setEmail(rs.getString("EMAIL"));
                model.setAvatarName(rs.getString("AVATAR_NAME"));
                List<BalancePayload> balanceList = new ArrayList<>();
                balanceList.add(balance);
                model.setBalances(balanceList);
                resultList.add(model);
            } else {
                List<BalancePayload> balanceList;
                if (old.getBalances() == null) {
                    balanceList = new ArrayList<>();
                    old.setBalances(balanceList);
                } else {
                    balanceList = old.getBalances();
                }
                balanceList.add(balance);
            }
        });
        return resultList;
    }

    public int insert(CryptoBalance m) {
        String sql = "INSERT INTO TBL_INTERNAL_BALANCE(USER_ID,TOKEN_ID,FREE,HOLD)"
                + "VALUES(?,?,?,?)";
        return jdbcTemplate.update(sql, m.getUserId(), m.getTokenId(), m.getFree(), m.getHold());
    }

    public int update(CryptoBalance m) {
        String sql = "UPDATE TBL_INTERNAL_BALANCE SET FREE = ?, HOLD = ? WHERE USER_ID = ? AND TOKEN_ID = ?";
        return jdbcTemplate.update(sql, m.getFree(), m.getHold(), m.getUserId(), m.getTokenId());
    }

    public int addFreeBalance(int userId, int tokenId, double amount) {
        String sql = "MERGE INTO TBL_INTERNAL_BALANCE USING DUAL ON (USER_ID=? AND TOKEN_ID=?)"
                + "WHEN MATCHED THEN UPDATE SET FREE=FREE+? "
                + "WHEN NOT MATCHED THEN INSERT(USER_ID,TOKEN_ID,FREE,HOLD)"
                + "VALUES(?,?,?,?)";
        return jdbcTemplate.update(sql, userId, tokenId, amount, userId, tokenId, amount, 0);
    }

    public int addHoldBalance(int userId, int tokenId, double amount) {
        String sql = "MERGE INTO TBL_INTERNAL_BALANCE USING DUAL ON (USER_ID=? AND TOKEN_ID=?)"
                + "WHEN MATCHED THEN UPDATE SET HOLD=HOLD+? "
                + "WHEN NOT MATCHED THEN INSERT(USER_ID,TOKEN_ID,FREE,HOLD)"
                + "VALUES(?,?,?,?)";
        return jdbcTemplate.update(sql, userId, tokenId, amount, userId, tokenId, amount, 0);
    }

    public int makeHoldBalance(int userId, int tokenId, double amount) {
        String sql = "UPDATE TBL_INTERNAL_BALANCE SET FREE = FREE - ?, HOLD = HOLD + ? WHERE USER_ID = ? AND TOKEN_ID = ?";
        return jdbcTemplate.update(sql, amount, amount, userId, tokenId);
    }

    public int releaseHoldBalance(int userId, int tokenId, double amount) {
        String sql = "UPDATE TBL_INTERNAL_BALANCE SET FREE = FREE + ?, HOLD = HOLD - ? WHERE USER_ID = ? AND TOKEN_ID = ?";
        return jdbcTemplate.update(sql, amount, amount, userId, tokenId);
    }

    public int deductFreeBalance(int userId, int tokenId, double amount) {
        String sql = "UPDATE TBL_INTERNAL_BALANCE SET FREE = FREE - ? WHERE USER_ID = ? AND TOKEN_ID = ?";
        return jdbcTemplate.update(sql, amount, userId, tokenId);
    }

    public int deductHoldBalance(int userId, int tokenId, double amount) {
        String sql = "UPDATE TBL_INTERNAL_BALANCE SET HOLD = HOLD - ? WHERE USER_ID = ? AND TOKEN_ID = ?";
        return jdbcTemplate.update(sql, amount, userId, tokenId);
    }

}
