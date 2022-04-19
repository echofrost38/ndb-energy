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
		m.setHairColor(rs.getString("HAIR_COLOR"));
		m.setSkinColor(rs.getString("SKIN_COLOR"));
		m.setPrefix(rs.getString("PREFIX"));
		m.setName(rs.getString("NAME"));
		m.setRegDate(rs.getTimestamp("REG_DATE").getTime());
		m.setUpdateDate(rs.getTimestamp("UPDATE_DATE").getTime());
		return m;
	}

    public int changeName(int id, String newName) {
		UserAvatar currentAvatar = selectById(id);
		String currentPrefix = currentAvatar.getPrefix();
        String sql = "SELECT COUNT(*) FROM TBL_USER_AVATAR WHERE PREFIX=? AND NAME=?";
        boolean exists = jdbcTemplate.queryForObject(sql, Integer.class, currentPrefix, newName) > 0;
        if(exists) return -1;
        else return jdbcTemplate.update("UPDATE TBL_USER_AVATAR SET NAME=? WHERE ID=?",newName,id);
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
		String sql = "INSERT INTO TBL_USER_AVATAR(ID,PURCHASED,HAIR_COLOR,SKIN_COLOR,PREFIX,NAME,REG_DATE,UPDATE_DATE)"
				+ "VALUES(?,?,?,?,?,?,SYSDATE,SYSDATE)";
		return jdbcTemplate.update(sql, m.getId(), m.getPurchased(), m.getHairColor(), m.getSkinColor(), m.getPrefix(), m.getName());
	}

	public int insertOrUpdate(UserAvatar m) {
		String sql = "MERGE INTO TBL_USER_AVATAR USING DUAL ON (ID=?)"
				+ "WHEN MATCHED THEN UPDATE SET PURCHASED=?, HAIR_COLOR=?,SKIN_COLOR=?, SELECTED=?, PREFIX=?, NAME=?, UPDATE_DATE=SYSDATE "
				+ "WHEN NOT MATCHED THEN INSERT(ID, PURCHASED, HAIR_COLOR,SKIN_COLOR, SELECTED, PREFIX, NAME, REG_DATE, UPDATE_DATE)"
				+ "VALUES(?,?,?,?,?,?,?,SYSDATE,SYSDATE)";
		return jdbcTemplate.update(sql, m.getId(), m.getPurchased(),m.getHairColor(), m.getSkinColor(), m.getSelected(), m.getPrefix(), m.getName(), m.getId(),
				m.getPurchased(),m.getHairColor(), m.getSkinColor(), m.getSelected(), m.getPrefix(), m.getName());
	}

	public int updateName(int userId, String newName) {
		String sql = "UPDATE TBL_USER_AVATAR SET NAME=? WHERE ID=?";
		return jdbcTemplate.update(sql, newName, userId);
	}

}
