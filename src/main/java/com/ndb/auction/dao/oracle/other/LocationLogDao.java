package com.ndb.auction.dao.oracle.other;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.models.LocationLog;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class LocationLogDao extends BaseOracleDao {

    private static final String TABLE_NAME = "TBL_LOCATION_LOG";

    private static LocationLog extract(ResultSet rs) throws SQLException {
        LocationLog m = new LocationLog();
        m.setId(rs.getInt("ID"));
        m.setUserId(rs.getInt("USER_ID"));
        m.setIpAddress(rs.getString("IP_ADDRESS"));
        m.setVpn(rs.getBoolean("IS_VPN"));
        m.setProxy(rs.getBoolean("IS_PROXY"));
        m.setTor(rs.getBoolean("IS_TOR"));
        m.setRelay(rs.getBoolean("IS_RELAY"));
        m.setCity(rs.getString("CITY"));
        m.setRegion(rs.getString("REGION"));
        m.setCountry(rs.getString("COUNTRY"));
        m.setContinent(rs.getString("CONTINENT"));
        m.setRegionCode(rs.getString("REGION_CODE"));
        m.setCountryCode(rs.getString("COUNTRY_CODE"));
        m.setContinentCode(rs.getString("CONTINENT_CODE"));
        m.setLatitude(rs.getFloat("LATITUDE"));
        m.setLongitude(rs.getFloat("LONGITUDE"));
        m.setVpnapiResponse(rs.getString("VPNAPI_RESPONSE"));
        m.setFinalResult(rs.getString("FINAL_RESULT"));
        m.setRegDate(rs.getLong("REG_DATE"));
        return m;
    }

    public LocationLogDao() {
        super(TABLE_NAME);
    }

    public LocationLog addLog(LocationLog m) {
        String sql = "INSERT INTO TBL_LOCATION_LOG(ID, USER_ID, IP_ADDRESS, IS_VPN, IS_PROXY, IS_TOR, IS_RELAY "
                + "CITY, REGION, COUNTRY, CONTINENT, REGION_CODE, COUNTRY_CODE, CONTINENT_CODE, "
                + "LATITUDE, LONGITUDE, VPNAPI_RESPONSE, FINAL_RESULT, REG_DATE)"
                + "VALUES(SEQ_LOCATION_LOG.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(sql.toString(),
                                new String[] { "ID" });
                        int i = 1;
                        ps.setInt(i++, m.getUserId());
                        ps.setString(i++, m.getIpAddress());
                        ps.setBoolean(i++, m.isVpn());
                        ps.setBoolean(i++, m.isProxy());
                        ps.setBoolean(i++, m.isTor());
                        ps.setBoolean(i++, m.isRelay());
                        ps.setString(i++, m.getCity());
                        ps.setString(i++, m.getRegion());
                        ps.setString(i++, m.getCountry());
                        ps.setString(i++, m.getContinent());
                        ps.setString(i++, m.getRegionCode());
                        ps.setString(i++, m.getCountryCode());
                        ps.setString(i++, m.getContinentCode());
                        ps.setFloat(i++, m.getLatitude());
                        ps.setFloat(i++, m.getLongitude());
                        ps.setString(i++, m.getVpnapiResponse());
                        ps.setString(i++, m.getFinalResult());
                        return ps;
                    }
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;
    }

    public int getCountByIp(int userId, String ipAddress) {
        String sql = "SELECT COUNT(*) TBL_LOCATION_LOG WHERE USER_ID=? AND IP_ADDRESS=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, ipAddress);
    }

    public int getCountByCountryAndCity(int userId, String country, String city) {
        String sql = "SELECT COUNT(*) TBL_LOCATION_LOG WHERE USER_ID=? AND COUNTRY=? AND CITY=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, country, city);
    }

    public LocationLog getLogById(int id) {
        String sql = "SELECT * FROM TBL_LOCATION_LOG WHERE ID=?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<LocationLog>() {
            @Override
            public LocationLog extractData(ResultSet rs) throws SQLException {
                if (!rs.next())
                    return null;
                return extract(rs);
            }
        }, id);
    }

    public List<LocationLog> getLogByUser(int userId) {
        String sql = "SELECT * FROM TBL_LOCATION_LOG WHERE USER_ID=?";
        return jdbcTemplate.query(sql, new RowMapper<LocationLog>() {
            @Override
            public LocationLog mapRow(ResultSet rs, int rownumber) throws SQLException {
                return extract(rs);
            }
        }, userId);
    }

}
