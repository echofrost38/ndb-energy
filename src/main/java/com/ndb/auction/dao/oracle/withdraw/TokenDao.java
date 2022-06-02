package com.ndb.auction.dao.oracle.withdraw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.withdraw.Token;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_TOKEN")
public class TokenDao extends BaseOracleDao {
    private static Token extract(ResultSet rs) throws SQLException {
        var m = new Token();
        m.setId(rs.getLong("ID"));
        m.setTokenName(rs.getString("TOKEN_NAME"));
        m.setTokenSymbol(rs.getString("TOKEN_SYMBOL"));
        m.setAddress(rs.getString("ADDRESS"));
        m.setNetwork(rs.getString("NETWORK"));
        m.setWithdrawable(rs.getBoolean("WITHDRAWABLE"));
        return m;
    }

    public Token save(Token m) {
        var sql = "INSERT INTO NDB.TBL_TOKEN(ID,TOKEN_NAME,TOKEN_SYMBOL,ADDRESS,NETWORK,WITHDRAWABLE)"
            + "VALUES(SEQ_TOKEN.NEXTVAL,?,?,?,?,?)";
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sql,
                            new String[] { "ID" });
                    int i = 1;
                    ps.setString(i++, m.getTokenName());
                    ps.setString(i++, m.getTokenSymbol());
                    ps.setString(i++, m.getAddress());
                    ps.setString(i++, m.getNetwork());
                    ps.setBoolean(i++, m.isWithdrawable());
                    return ps;
                }
            }, keyHolder
        );
        m.setId(keyHolder.getKey().longValue());
        return m;
    }

    public List<Token> selectAll() {
        var sql = "SELECT * FROM NDB.TBL_TOKEN";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }    
}