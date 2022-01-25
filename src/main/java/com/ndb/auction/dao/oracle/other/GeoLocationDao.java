package com.ndb.auction.dao.oracle.other;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.GeoLocation;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_GEO_LOCATION")
public class GeoLocationDao extends BaseOracleDao {

	private static GeoLocation extract(ResultSet rs) throws SQLException {
		GeoLocation m = new GeoLocation();
		m.setId(rs.getInt("ID"));
		m.setCountry(rs.getString("COUNTRY"));	
		m.setCountryCode(rs.getString("COUNTRY_CODE"));
		m.setAllowed(rs.getBoolean("IS_ALLOWED"));
		return m;
	}

	// Add disallowed country
	public GeoLocation addDisallowedCountry(String country, String code) {
		GeoLocation m = new GeoLocation(country, code, false);
		String sql = "INSERT INTO TBL_GEO_LOCATION(ID, COUNTRY, COUNTRY_CODE, IS_ALLOWED)"
					+ "VALUES(SEQ_GEO_LOCATION.NEXTVAL, ?, ?, 0)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(sql, new String[] { "ID" });
						int i = 1;
						ps.setString(i++, m.getCountry());
						ps.setString(i++, m.getCountryCode());
						ps.setBoolean(i++, m.isAllowed());
						return ps;
					}
				}, keyHolder);
		m.setId(keyHolder.getKey().intValue());
		return m;
	}

	// Make Allow
	public int makeAllow(int id) {
		String sql = "UPDATE TBL_GEO_LOCATION SET IS_ALLOWED=1 WHERE ID=?";
		return jdbcTemplate.update(sql, id);
	}

	public int makeDisallow(int id) {
		String sql = "UPDATE TBL_GEO_LOCATION SET IS_ALLOWED=0 WHERE ID=?";
		return jdbcTemplate.update(sql, id);
	}

	// get location
	public GeoLocation getGeoLocation(int id) {
		String sql = "SELECT * FROM TBL_GEO_LOCATION WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<GeoLocation>() {
			@Override
			public GeoLocation extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

	public GeoLocation getGeoLocation(String code) {
		String sql = "SELECT * FROM TBL_GEO_LOCATION WHERE COUNTRY_CODE=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<GeoLocation>() {
			@Override
			public GeoLocation extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, code);
	}

	public List<GeoLocation> getGeoLocations() {
		String sql = "SELECT * FROM TBL_GEO_LOCATION WHERE IS_ALLOWED=0";
		return jdbcTemplate.query(sql, new RowMapper<GeoLocation>() {
			@Override
			public GeoLocation mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

}
