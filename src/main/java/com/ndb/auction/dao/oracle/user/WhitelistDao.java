package com.ndb.auction.dao.oracle.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.user.Whitelist;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_WHITELIST")
public class WhitelistDao extends BaseOracleDao {
    
    private static Whitelist extract(ResultSet rs) throws SQLException {
		Whitelist m = new Whitelist();
		m.setUserId(rs.getInt("USER_ID"));
        m.setReason(rs.getString("REASON"));
		return m;
	}

    public Whitelist selectByUserId(int userId) {
		String sql = "SELECT * FROM TBL_WHITELIST WHERE USER_ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<Whitelist>() {
			@Override
			public Whitelist extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, userId);
	}

	public List<Whitelist> selectAll() {
		String sql = "SELECT * FROM TBL_WHITELIST";
		return jdbcTemplate.query(sql, new RowMapper<Whitelist>() {
			@Override
			public Whitelist mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

    public int insert(Whitelist m) {
        String sql = "INSERT INTO TBL_WHITELIST (USER_ID, REASON)VALUES(?,?)";
        return jdbcTemplate.update(sql, m.getUserId(), m.getReason());
    }

    public int remove(int userId) {
        String sql = "REMOVE FROM TBL_WHITELIST WHERE USER_ID=?";
        return jdbcTemplate.update(sql, userId);
    }
}
