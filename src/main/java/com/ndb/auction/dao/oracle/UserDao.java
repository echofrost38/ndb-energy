package com.ndb.auction.dao.oracle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ndb.auction.models.user.User;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends BaseOracleDao {

	private static final String TABLE_NAME = "NDB.TBL_USER";

	private static User extract(ResultSet rs) throws SQLException {
		User m = new User();
		m.setId(rs.getInt("ID"));
		m.setEmail(rs.getString("EMAIL"));
		m.setPassword(rs.getString("PASSWORD"));
		m.setName(rs.getString("NAME"));
		m.setCountry(rs.getString("COUNTRY"));
		m.setPhone(rs.getString("PHONE"));
		m.setBirthday(rs.getTimestamp("BIRTHDAY"));
		m.setRegDate(rs.getTimestamp("REG_DATE"));
		m.setLastLoginDate(rs.getTimestamp("LASTLOGIN_DATE"));
		m.setRole(rs.getString("ROLE"));
		m.setTierLevel(rs.getInt("TIER_LEVEL"));
		m.setTierPoint(rs.getInt("TIER_POINT"));
		m.setDeleted(rs.getInt("DELETED"));
		return m;
	}

	public UserDao() {
		super(TABLE_NAME);
	}

	public User selectById(int id) {
		String sql = "SELECT * FROM NDB.TBL_USER WHERE ID=? AND DELETED=0";
		return jdbcTemplate.query(sql, new ResultSetExtractor<User>() {
			@Override
			public User extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

	public User selectByEmail(String email) {
		String sql = "SELECT * FROM NDB.TBL_USER WHERE EMAIL=? AND DELETED=0";
		return jdbcTemplate.query(sql, new ResultSetExtractor<User>() {
			@Override
			public User extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, email);
	}

	public List<User> selectAll(String orderby) {
		String sql = "SELECT * FROM NDB.TBL_USER";
		if (orderby == null)
			orderby = "ID";
		sql += " ORDER BY " + orderby;
		return jdbcTemplate.query(sql, new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public int countList(Map<String, Object> whereMap) {
		String sql = "SELECT COUNT(*) FROM NDB.TBL_USER";
		if (whereMap != null) {
			StringBuilder where = new StringBuilder();
			if (whereMap.get("email") != null) {
				where.append(" AND EMAIL LIKE ?");
			}
			if (!where.isEmpty())
				sql += " WHERE" + where.substring(4);
		}
		return jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				if (whereMap != null) {
					String value;
					if ((value = (String) whereMap.get("email")) != null) {
						ps.setString(i++, '%' + value + '%');
					}
				}
			}
		}, new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return 0;
				return rs.getInt(1);
			}
		});
	}

	public List<User> selectList(Map<String, Object> whereMap, Integer offset, Integer limit, String orderby) {
		String sql = "SELECT * FROM NDB.TBL_USER";
		if (whereMap != null) {
			StringBuilder where = new StringBuilder();
			if (whereMap.get("email") != null) {
				where.append(" AND EMAIL LIKE ?");
			}
			if (!where.isEmpty())
				sql += " WHERE" + where.substring(4);
		}
		if (orderby == null)
			orderby = "ID";
		sql += " ORDER BY " + orderby;
		if (offset != null)
			sql += " OFFSET ? ROWS";
		if (limit != null)
			sql += " FETCH NEXT ? ROWS ONLY";
		return jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				if (whereMap != null) {
					String value;
					if ((value = (String) whereMap.get("email")) != null) {
						ps.setString(i++, '%' + value + '%');
					}
				}
				if (offset != null)
					ps.setInt(i++, offset);
				if (limit != null)
					ps.setInt(i++, limit);
			}
		}, new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public int insert(User m) {
		String sql = "INSERT INTO NDB.TBL_USER(ID,EMAIL,PASSWORD,NAME,COUNTRY,PHONE,BIRTHDAY,REG_DATE,LASTLOGIN_DATE,ROLE,TIER_LEVEL,TIER_POINT,DELETED)"
				+ "VALUES(SEQ_USER.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?)";
		return jdbcTemplate.update(sql, m.getEmail(), m.getPassword(), m.getName(), m.getCountry(), m.getPhone(),
				m.getBirthday(), m.getLastLoginDate(), m.getRole(), m.getTierLevel(), m.getTierPoint(), m.getDeleted());
	}

	public int updateDeleted(int id) {
		String sql = "UPDATE NDB.TBL_USER SET DELETED=1 WHERE ID=?";
		return jdbcTemplate.update(sql, id);
	}

}
