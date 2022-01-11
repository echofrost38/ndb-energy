package com.ndb.auction.dao.oracle.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.user.UserSecurity;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_USER_SECURITY")
public class UserSecurityDao extends BaseOracleDao {

	private static UserSecurity extract(ResultSet rs) throws SQLException {
		UserSecurity m = new UserSecurity();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setAuthType(rs.getString("AUTH_TYPE"));
		m.setTfaEnabled(rs.getBoolean("TFA_ENABLED"));
		m.setTfaSecret(rs.getString("TFA_SECRET"));
		m.setRegDate(rs.getLong("REG_DATE"));
		m.setUpdateDate(rs.getLong("UPDATE_DATE"));
		return m;
	}

	public UserSecurity selectById(int userId) {
		String sql = "SELECT * FROM TBL_USER_SECURITY WHERE USER_ID=? ODER BY ID";
		return jdbcTemplate.query(sql, new ResultSetExtractor<UserSecurity>() {
			@Override
			public UserSecurity extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, userId);
	}

	public int insert(UserSecurity m) {
		String sql = "INSERT INTO TBL_USER_SECURITY(SEC_USER_SECURITY.NEXTVAL,AUTH_TYPE,TFA_ENABLED,TFA_SECRET,REG_DATE,UPDATE_DATE, USER_ID)"
				+ "VALUES(?,?,?,?,SYSDATE,SYSDATE)";
		return jdbcTemplate.update(sql, m.getAuthType(), m.isTfaEnabled(), m.getTfaSecret(), m.getUserId());
	}

	public int insertOrUpdate(UserSecurity m) {
		String sql = "MERGE INTO TBL_USER_SECURITY USING DUAL ON (id=?)"
				+ "WHEN MATCHED THEN UPDATE SET AUTH_TYPE=?,TFA_ENABLED=?,TFA_SECRET=?,UPDATE_DATE=SYSDATE, USER_ID=?"
				+ "WHEN NOT MATCHED THEN INSERT(SEC_USER_SECURITY.NEXTVAL,AUTH_TYPE,TFA_ENABLED,TFA_SECRET,REG_DATE,UPDATE_DATE, USER_ID)"
				+ "VALUES(?,?,?,?,SYSDATE,SYSDATE)";
		return jdbcTemplate.update(sql, m.getAuthType(), m.isTfaEnabled(), m.getTfaSecret(), m.getUserId(), m.getId(),
				m.getAuthType(), m.isTfaEnabled(), m.getTfaSecret(), m.getUserId());
	}

	public int updateTfaEnabled(int id, boolean tfaEnabled) {
		String sql = "UPDATE TBL_USER_SECURITY SET TFA_ENABLED=? WHERE ID=?";
		return jdbcTemplate.update(sql, tfaEnabled, id);
	}

	public int updateTfaSecret(int id, String tfaSecret) {
		String sql = "UPDATE TBL_USER_SECURITY SET TFA_SECRET=? WHERE ID=?";
		return jdbcTemplate.update(sql, tfaSecret, id);
	}

	public int updateSecretAndAuthType(int id, String tfaSecret, String authType) {
		String sql = "UPDATE TBL_USER_SECURITY SET TFA_SECRET=?, AUTH_TYPE=? WHERE ID=?";
		return jdbcTemplate.update(sql, tfaSecret, authType, id);
	}

}
