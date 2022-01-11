package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.models.GeoLocation;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class GeoLocationDao extends BaseOracleDao {

	private static final String TABLE_NAME = "TBL_GEO_LOCATION";

	private static GeoLocation extract(ResultSet rs) throws SQLException {
		GeoLocation m = new GeoLocation();
		m.setCountryCode(rs.getString("COUNTRY_CODE"));
		m.setAllowed(rs.getBoolean("IS_ALLOWED"));
		return m;
	}

	public GeoLocationDao() {
		super(TABLE_NAME);
	}

	// Add disallowed country
	public GeoLocation addDisallowedCountry(String countryCode) {
		String sql = "MERGE INTO TBL_GEO_LOCATION USING DUAL ON (COUNTRY_CODE=?)"
				+ "WHEN MATCHED THEN UPDATE SET IS_ALLOWED=0"
				+ "WHEN NOT MATCHED THEN INSERT(COUNTRY_CODE, IS_ALLOWED)"
				+ "VALUES(?, 0)";
		jdbcTemplate.update(sql, countryCode, countryCode);
		return new GeoLocation(countryCode, false);
	}

	// Make Allow
	public GeoLocation makeAllow(String countryCode) {
		String sql = "UPDATE TBL_GEO_LOCATION SET IS_ALLOWED=1 WHERE COUNTRY_CODE=?";
		jdbcTemplate.update(sql, countryCode);
		return new GeoLocation(countryCode, true);
	}

	// get location
	public GeoLocation getGeoLocation(String countryCode) {
		String sql = "SELECT * FROM TBL_GEO_LOCATION WHERE COUNTRY_CODE=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<GeoLocation>() {
			@Override
			public GeoLocation extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, countryCode);
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
