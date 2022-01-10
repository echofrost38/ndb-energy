package com.ndb.auction.dao.oracle.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.user.UserKyb;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_USER_KYB")
public class UserKybDao extends BaseOracleDao {

	private static UserKyb extract(ResultSet rs) throws SQLException {
		UserKyb model = new UserKyb();
		model.setId(rs.getInt("ID"));
		model.setCountry(rs.getString("COUNTRY"));
		model.setCompanyName(rs.getString("COMPANY_NAME"));
		model.setRegNum(rs.getString("REG_NUM"));
		model.setAttach1Key(rs.getString("ATTACH1_KEY"));
		model.setAttach1Filename(rs.getString("ATTACH1_FILENAME"));
		model.setAttach2Key(rs.getString("ATTACH2_KEY"));
		model.setAttach2Filename(rs.getString("ATTACH2_FILENAME"));
		model.setStatus(rs.getString("STATUS"));
		model.setRegDate(rs.getTimestamp("REG_TIME"));
		model.setUpdateDate(rs.getTimestamp("UPDATE_TIME"));
		return model;
	}

	public UserKyb selectById(int id) {
		String sql = "SELECT * FROM TBL_USER_KYB WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<UserKyb>() {
			@Override
			public UserKyb extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

	public List<UserKyb> selectAll(String orderby) {
		String sql = "SELECT * FROM TBL_USER_KYB";
		if (orderby == null)
			orderby = "ID";
		sql += " ORDER BY " + orderby;
		return jdbcTemplate.query(sql, new RowMapper<UserKyb>() {
			@Override
			public UserKyb mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public int insert(UserKyb m) {
		String sql = "INSERT INTO TBL_USER_KYB(ID,COUNTRY,COMPANY_NAME,REG_NUM,ATTACH1_KEY,ATTACH1_FILENAME,ATTACH2_KEY,ATTACH2_FILENAME,STATUS,REG_TIME,UPDATE_TIME)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		return jdbcTemplate.update(sql, m.getId(), m.getCountry(), m.getCompanyName(), m.getRegNum(), m.getAttach1Key(),
				m.getAttach1Filename(), m.getAttach2Key(), m.getAttach2Filename(), m.getStatus(), m.getRegDate(),
				m.getUpdateDate());
	}

	public int insertOrUpdate(UserKyb m) {
		String sql = "MERGE INTO TBL_USER_KYB USING DUAL ON (id=?)"
				+ "WHEN MATCHED THEN UPDATE SET COUNTRY=?,COMPANY_NAME=?,REG_NUM=?,ATTACH1_KEY=?,ATTACH1_FILENAME=?,ATTACH2_KEY=?,ATTACH2_FILENAME=?,STATUS=?,UPDATE_DATE=SYSDATE"
				+ "WHEN NOT MATCHED THEN INSERT(ID,COUNTRY,COMPANY_NAME,REG_NUM,ATTACH1_KEY,ATTACH1_FILENAME,ATTACH2_KEY,ATTACH2_FILENAME,STATUS,REG_TIME,UPDATE_TIME)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE)";
		return jdbcTemplate.update(sql, m.getId(), m.getCountry(), m.getCompanyName(), m.getRegNum(), m.getAttach1Key(),
				m.getAttach1Filename(), m.getAttach2Key(), m.getAttach2Filename(), m.getStatus(), m.getId(),
				m.getCountry(), m.getCompanyName(), m.getRegNum(), m.getAttach1Key(),
				m.getAttach1Filename(), m.getAttach2Key(), m.getAttach2Filename(), m.getStatus());
	}

}
