package com.ndb.auction.dao.oracle.avatar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.avatar.AvatarProfile;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_AVATAR_PROFILE")
public class AvatarProfileDao extends BaseOracleDao {

	private static AvatarProfile extract(ResultSet rs) throws SQLException {
		AvatarProfile m = new AvatarProfile();
		m.setId(rs.getInt("ID"));
		m.setFname(rs.getString("FNAME"));
		m.setSurname(rs.getString("SURNAME"));
		m.setShortName(rs.getString("SHORT_NAME"));
		m.setHairColor(rs.getString("HAIR_COLOR"));
		m.setDetails(rs.getString("DETAILS"));
		return m;
	}

	public AvatarProfile createAvatarProfile(AvatarProfile m) {
		String sql = "INSERT INTO TBL_AVATAR_PROFILE(ID, FNAME, SURNAME, SHORT_NAME, HAIR_COLOR, DETAILS)"
				+ "VALUES(SEQ_AVATAR_PROFILE.NEXTVAL,?,?,?,?,?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(sql.toString(),
								new String[] { "ID" });
						int i = 1;
						ps.setString(i++, m.getFname());
						ps.setString(i++, m.getSurname());
						ps.setString(i++, m.getShortName());
						ps.setString(i++, m.getHairColor());
						ps.setString(i++, m.getDetails());
						return ps;
					}
				}, keyHolder);
		m.setId(keyHolder.getKey().intValue());
		return m;
	}

	public AvatarProfile updateAvatarProfile(AvatarProfile m) {
		String sql = "UPDATE TBL_AVATAR_PROFILE SET FNAME=?, SURNAME=?, SHORT_NAME=?, HAIR_COLOR=?, DETAILS=? WHERE ID=?";
		jdbcTemplate.update(sql, m.getFname(), m.getSurname(), m.getShortName(), m.getHairColor(), m.getDetails(), m.getId());
		return m;
	}

	public List<AvatarProfile> getAvatarProfiles() {
		String sql = "SELECT * FROM TBL_AVATAR_PROFILE";
		return jdbcTemplate.query(sql, new RowMapper<AvatarProfile>() {
			@Override
			public AvatarProfile mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public AvatarProfile getAvatarProfile(int id) {
		String sql = "SELECT * FROM TBL_AVATAR_PROFILE WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<AvatarProfile>() {
			@Override
			public AvatarProfile extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

	public AvatarProfile getAvatarProfileByName(String fname) {
		String sql = "SELECT * FROM TBL_AVATAR_PROFILE WHERE FNAME=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<AvatarProfile>() {
			@Override
			public AvatarProfile extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, fname);
	}

}
