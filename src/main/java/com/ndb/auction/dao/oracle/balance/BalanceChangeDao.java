package com.ndb.auction.dao.oracle.balance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.balance.BalanceChange;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_BALANCE_LOG")
public class BalanceChangeDao extends BaseOracleDao {
    private static BalanceChange extract(ResultSet rs) throws SQLException {
        return BalanceChange.builder()
            .id(rs.getInt("ID"))
            .userId(rs.getInt("USER_ID"))
            .tokenId(rs.getInt("TOKEN_ID"))
            .reason(rs.getString("REASON"))
            .amount(rs.getDouble("AMOUNT"))
            .updatedAt(rs.getTimestamp("UPDATED_AT").getTime())
            .build();
    }

    public int insert(BalanceChange m) {
        var sql = "INSERT INTO TBL_BALANCE_LOG(ID,USER_ID,TOKEN_ID,REASON,AMOUNT,UPDATED_AT)"
            + "VALUES(SEQ_BALANCE_LOG.NEXTVAL,?,?,?,?,SYSDATE)";
        return jdbcTemplate.update(sql, m.getUserId(), m.getTokenId(), m.getReason(), m.getAmount());
    }

    public List<BalanceChange> selectLogByUser(int userId) {
        var sql = "SELECT * FROM TBL_BALANCE_LOG WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }
}
