package com.ndb.auction.dao.oracle.user;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.user.UserReferral;
import com.ndb.auction.models.user.UserSocial;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@NoArgsConstructor
@Table(name = "TBL_USER_SOCIAL")
public class UserSocialDao extends BaseOracleDao {

    private static UserSocial extract(ResultSet rs) throws SQLException {
        UserSocial m = new UserSocial();
        m.setId(rs.getInt("ID"));
        m.setDiscord(rs.getString("DISCORD"));
        return m;
    }
    public UserSocial selectById(int id) {
        String sql = "SELECT * FROM TBL_USER_SOCIAL WHERE ID=?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, id);
    }

    public UserSocial selectByDiscordUsername(String discordUsername) {
        String sql = "SELECT * FROM TBL_USER_SOCIAL WHERE DISCORD=?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, discordUsername);
    }

    public int insert(UserSocial m) {
        String sql = "INSERT INTO TBL_USER_SOCIAL(ID, DISCORD)";
        return jdbcTemplate.update(sql,m.getId(), m.getDiscord());
    }

    public int insertOrUpdate(UserSocial m) {
        String sql =  "MERGE INTO TBL_USER_SOCIAL USING DUAL ON (ID=?)"
                + "WHEN MATCHED THEN UPDATE SET DISCORD= ? "
                + "WHEN NOT MATCHED THEN INSERT (ID, DISCORD)"
                + "VALUES(?,?)";
        return jdbcTemplate.update(sql,m.getId(),m.getDiscord(),m.getId(),m.getDiscord());
    }
}
