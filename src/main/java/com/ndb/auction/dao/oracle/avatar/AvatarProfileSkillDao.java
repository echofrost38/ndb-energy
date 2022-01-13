package com.ndb.auction.dao.oracle.avatar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.SkillSet;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_AVATAR_PROFILE_SKILL")
public class AvatarProfileSkillDao extends BaseOracleDao {

	private static SkillSet extract(ResultSet rs) throws SQLException {
		SkillSet m = new SkillSet();
		m.setId(rs.getInt("ID"));
		m.setName(rs.getString("NAME"));
		m.setRate(rs.getInt("RATE"));
		return m;
	}

	public List<SkillSet> selectById(int id) {
		String sql = "SELECT * FROM TBL_AVATAR_PROFILE_SKILL WHERE ID=?";
		return jdbcTemplate.query(sql, new RowMapper<SkillSet>() {
			@Override
			public SkillSet mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, id);
	}

	public int insert(SkillSet m) {
		String sql = "INSERT INTO TBL_AVATAR_PROFILE_SKILL(ID, NAME, RATE)"
				+ "VALUES(?,?,?)";
		return jdbcTemplate.update(sql, m.getId(), m.getName(), m.getRate());
	}

	@Override
	public int deleteById(int id) {
		String sql = "DELETE FROM TBL_AVATAR_PROFILE_SKILL WHERE ID=?";
		return jdbcTemplate.update(sql, id);
	}

	public int update(int id, List<SkillSet> list) {
		deleteById(id);
		int result = 0;
		for (SkillSet m : list) {
			m.setId(id);
			result += insert(m);
		}
		return result;
	}

}
