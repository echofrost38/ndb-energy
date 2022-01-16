package com.ndb.auction.dao.oracle.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.user.UserVerify;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_USER_VERIFY")
public class UserVerifyDao extends BaseOracleDao {

	private static UserVerify extract(ResultSet rs) throws SQLException {
		UserVerify m = new UserVerify();
		m.setId(rs.getInt("ID"));
		m.setEmailVerified(rs.getBoolean("EMAIL_VERIFIED"));
		m.setPhoneVerified(rs.getBoolean("PHONE_VERIFIED"));
		m.setKycVerified(rs.getBoolean("KYC_VERIFIED"));
		m.setAmlVerified(rs.getBoolean("AML_VERIFIED"));
		m.setKybVerified(rs.getBoolean("KYB_VERIFIED"));
		m.setRegDate(rs.getTimestamp("REG_DATE").getTime());
		m.setUpdateDate(rs.getTimestamp("UPDATE_DATE").getTime());
		return m;
	}

	public UserVerify selectById(int id) {
		String sql = "SELECT * FROM TBL_USER_VERIFY WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<UserVerify>() {
			@Override
			public UserVerify extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

	public int insert(UserVerify m) {
		String sql = "INSERT INTO TBL_USER_VERIFY(ID,EMAIL_VERIFIED,PHONE_VERIFIED,KYC_VERIFIED,AML_VERIFIED,KYB_VERIFIED,REG_DATE,UPDATE_DATE)"
				+ "VALUES(?,?,?,?,?,?,SYSDATE,SYSDATE)";
		return jdbcTemplate.update(sql, m.getId(), m.isEmailVerified(), m.isPhoneVerified(), m.isKycVerified(),
				m.isAmlVerified(), m.isKybVerified());
	}

	public int insertOrUpdate(UserVerify m) {
		String sql = "MERGE INTO TBL_USER_VERIFY USING DUAL ON (ID=?)"
				+ "WHEN MATCHED THEN UPDATE SET EMAIL_VERIFIED=?,PHONE_VERIFIED=?,KYC_VERIFIED=?,AML_VERIFIED=?,KYB_VERIFIED=?,UPDATE_DATE=SYSDATE "
				+ "WHEN NOT MATCHED THEN INSERT(ID,EMAIL_VERIFIED,PHONE_VERIFIED,KYC_VERIFIED,AML_VERIFIED,KYB_VERIFIED,REG_DATE,UPDATE_DATE)"
				+ "VALUES(?,?,?,?,?,?,SYSDATE,SYSDATE)";
		return jdbcTemplate.update(sql, m.getId(), m.isEmailVerified(), m.isPhoneVerified(), m.isKycVerified(),
				m.isAmlVerified(), m.isKybVerified(), m.getId(), m.isEmailVerified(), m.isPhoneVerified(),
				m.isKycVerified(), m.isAmlVerified(), m.isKybVerified());
	}

	public int updateEmailVerified(int id, boolean value) {
		String sql = "UPDATE TBL_USER_VERIFY SET EMAIL_VERIFIED=? WHERE ID=?";
		return jdbcTemplate.update(sql, value, id);
	}

}
