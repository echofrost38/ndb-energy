package com.ndb.auction.dao.oracle.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.user.UserAvatar;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_USER_AVATAR")
public class UserAvatarDao extends BaseOracleDao {

	private static UserAvatar extract(ResultSet rs) throws SQLException {
		UserAvatar m = new UserAvatar();
		m.setId(rs.getInt("ID"));
		m.setPurchased(rs.getString("PURCHASED"));
		m.setSelected(rs.getString("SELECTED"));
		m.setPrefix(rs.getString("PREFIX"));
		m.setName(rs.getString("NAME"));
		m.setRegDate(rs.getLong("REG_DATE"));
		m.setUpdateDate(rs.getLong("UPDATE_DATE"));
		return m;
	}

	public UserAvatar selectById(int id) {
		String sql = "SELECT * FROM TBL_USER_AVATAR WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<UserAvatar>() {
			@Override
			public UserAvatar extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

	public UserAvatar selectByPrefixAndName(String prefix, String name) {
		String sql = "SELECT * FROM TBL_USER_AVATAR WHERE PREFIX=? AND NAME=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<UserAvatar>() {
			@Override
			public UserAvatar extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, prefix, name);
	}

	public int insert(UserAvatar m) {
		String sql = "INSERT INTO TBL_USER_AVATAR(ID,PURCHASED,PREFIX,NAME,REG_DATE,UPDATE_DATE)"
				+ "VALUES(?,?,?,?,SYSDATE,SYSDATE)";
		return jdbcTemplate.update(sql, m.getId(), m.getPurchased(), m.getPrefix(), m.getName());
	}

	public int insertOrUpdate(UserAvatar m) {
		String sql = "MERGE INTO TBL_USER_AVATAR USING DUAL ON (ID=?)"
				+ "WHEN MATCHED THEN UPDATE SET PURCHASED=?, SELECTED=?, PREFIX=?, NAME=?, UPDATE_DATE=SYSDATE "
				+ "WHEN NOT MATCHED THEN INSERT(ID, PURCHASED, SELECTED, PREFIX, NAME, REG_DATE, UPDATE_DATE)"
				+ "VALUES(?,?,?,?,?,SYSDATE,SYSDATE)";
		return jdbcTemplate.update(sql, m.getId(), m.getPurchased(),m.getSelected(), m.getPrefix(), m.getName(), m.getId(),
				m.getPurchased(), m.getSelected(), m.getPrefix(), m.getName());
	}

}
