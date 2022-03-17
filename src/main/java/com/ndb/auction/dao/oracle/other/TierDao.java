package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.tier.Tier;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_TIER")
public class TierDao extends BaseOracleDao {

	private static Tier extract(ResultSet rs) throws SQLException {
		Tier m = new Tier();
		m.setLevel(rs.getInt("T_LEVEL"));
		m.setName(rs.getString("NAME"));
		m.setPoint(rs.getLong("POINT"));
		m.setSvg(rs.getString("SVG"));
		return m;
	}

	// User Tier
	public Tier addNewUserTier(Tier m) {
		String sql = "INSERT INTO TBL_TIER(T_LEVEL, NAME, POINT, SVG)"
				+ "VALUES(?,?,?,?)";
		jdbcTemplate.update(sql, m.getLevel(), m.getName(), m.getPoint(), m.getSvg());
		return m;
	}

	public List<Tier> getUserTiers() {
		String sql = "SELECT * FROM TBL_TIER ORDER BY T_LEVEL";
		return jdbcTemplate.query(sql, new RowMapper<Tier>() {
			@Override
			public Tier mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public Tier selectByLevel(int level) {
		String sql = "SELECT * FROM TBL_TIER WHERE T_LEVEL = ?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<Tier>() {
			@Override
			public Tier extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, level);
	}

	public Tier updateUserTier(Tier m) {
		String sql = "UPDATE TBL_TIER SET NAME=?, POINT=?, SVG=? WHERE T_LEVEL=?";
		jdbcTemplate.update(sql, m.getName(), m.getPoint(), m.getSvg(), m.getLevel());
		return m;
	}

	public int deleteUserTier(int level) {
		String sql = "DELETE FROM TBL_TIER WHERE T_LEVEL=?";
		jdbcTemplate.update(sql, level);
		return level;
	}

}
