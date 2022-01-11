package com.ndb.auction.dao.oracle.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.user.User;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

/* TODOs 
	1. User ROLE
	2. Where Map!
*/

@Repository
@NoArgsConstructor
@Table(name = "TBL_USER")
public class UserDao extends BaseOracleDao {

	private static User extract(ResultSet rs) throws SQLException {
		User m = new User();
		m.setId(rs.getInt("ID"));
		m.setEmail(rs.getString("EMAIL"));
		m.setPassword(rs.getString("PASSWORD"));
		m.setName(rs.getString("NAME"));
		m.setCountry(rs.getString("COUNTRY"));
		m.setPhone(rs.getString("PHONE"));
		m.setBirthday(rs.getLong("BIRTHDAY"));
		m.setRegDate(rs.getLong("REG_DATE"));
		m.setLastLoginDate(rs.getLong("LAST_LOGIN_DATE"));
		m.setTierLevel(rs.getInt("TIER_LEVEL"));
		m.setTierPoint(rs.getInt("TIER_POINT"));
		m.setProvider(rs.getString("PROVIDER"));
		m.setProviderId(rs.getString("PROVIDER_ID"));
		m.setNotifySetting(rs.getInt("NOTIFY_SETTING"));
		m.setDeleted(rs.getInt("DELETED"));
		m.setRoleString(rs.getString("ROLE"));
		return m;
	}

	public User selectById(int id) {
		String sql = "SELECT * FROM TBL_USER WHERE ID=?";
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
		String sql = "SELECT * FROM TBL_USER WHERE EMAIL=?";
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
		String sql = "SELECT * FROM TBL_USER";
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
		String sql = "SELECT COUNT(*) FROM TBL_USER";
		if (whereMap != null) {
			StringBuilder where = new StringBuilder();
			if (whereMap.get("email") != null) {
				where.append(" AND EMAIL LIKE ?");
			}
			if (where.length()>0)
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
		String sql = "SELECT * FROM TBL_USER";
		if (whereMap != null) {
			StringBuilder where = new StringBuilder();
			if (whereMap.get("email") != null) {
				where.append(" AND EMAIL LIKE ?");
			}
			if (where.length() > 0)
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
		String sql = "INSERT INTO TBL_USER(ID, EMAIL, PASSWORD, NAME, COUNTRY, PHONE, BIRTHDAY, REG_DATE, LAST_LOGIN_DATE, LAST_PASSWORD_CHANGE_DATE, "
				+ "ROLE, TIER_LEVEL, TIER_POINT, PROVIDER, PROVIDER_ID, NOTIFY_SETTING, DELETED)"
				+ "VALUES(SEQ_USER.NEXTVAL,?,?,?,?,?,SYSDATE,SYSDATE,SYSDATE,?,?,?,?,?,?,?)";
		return jdbcTemplate.update(sql, m.getEmail(), m.getPassword(), m.getName(), m.getCountry(), m.getPhone(),
				m.getBirthday(), m.getLastLoginDate(), m.getRoleString(), m.getTierLevel(), m.getTierPoint(), m.getProvider(),
				m.getProviderId(), m.getNotifySetting(), m.getDeleted());
	}

	public int updatePassword(int id, String password) {
		String sql = "UPDATE TBL_USER SET PASSWORD=?,LAST_PASSWORD_CHANGE_DATE=SYSDATE WHERE ID=?";
		return jdbcTemplate.update(sql, password, id);
	}

	public int updatePhone(int id, String phone) {
		String sql = "UPDATE TBL_USER SET PHONE=? WHERE ID=?";
		return jdbcTemplate.update(sql, phone, id);
	}

	public int updateRole(int id, String role) {
		String sql = "UPDATE TBL_USER SET ROLE=? WHERE ID=?";
		return jdbcTemplate.update(sql, role, id);
	}

	public int updateTier(int id, int tierLevel, long tierPoint) {
		String sql = "UPDATE TBL_USER SET TIER_LEVEL=?,TIER_POINT=? WHERE ID=?";
		return jdbcTemplate.update(sql, tierLevel, tierPoint, id);
	}

	public int updateProvider(int id, int provider, long providerid) {
		String sql = "UPDATE TBL_USER SET PROVIDER=?,PROVIDER_ID=? WHERE ID=?";
		return jdbcTemplate.update(sql, provider, providerid, id);
	}

	public int updateNotifySetting(int id, int notifySetting) {
		String sql = "UPDATE TBL_USER SET NOTIFY_SETTING=? WHERE ID=?";
		return jdbcTemplate.update(sql, notifySetting, id);
	}

	public int updateDeleted(int id) {
		String sql = "UPDATE TBL_USER SET DELETED=1 WHERE ID=?";
		return jdbcTemplate.update(sql, id);
	}

}