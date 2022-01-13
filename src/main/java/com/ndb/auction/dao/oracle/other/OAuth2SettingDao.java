package com.ndb.auction.dao.oracle.other;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.OAuth2Setting;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_OAUTH2_SETTING")
public class OAuth2SettingDao extends BaseOracleDao {

	private static OAuth2Setting extract(ResultSet rs) throws SQLException {
		OAuth2Setting m = new OAuth2Setting();
		m.setId(rs.getInt("ID"));
		m.setClientId(rs.getString("CLIENT_ID"));
		m.setClientName(rs.getString("CLIENT_NAME"));
		m.setClientSecret(rs.getString("CLIENT_SECRET"));
		m.setClientAuthMethod(rs.getString("CLIENT_AUTH_METHOD"));
		m.setAuthGrantType(rs.getString("AUTH_GRANT_TYPE"));
		m.setRedirectUriTemplate(rs.getString("REDIRECT_URL_TEMPLATE"));
		m.setScope(rs.getString("SCOPE"));
		m.setAuthUri(rs.getString("AUTH_URI"));
		m.setTokenUri(rs.getString("TOKEN_URI"));
		m.setUserInfoUri(rs.getString("USER_INFO_URI"));
		m.setUserAttributeName(rs.getString("USER_ATTRIBUTE_NAME"));
		m.setJwkSetUri(rs.getString("JWK_SET_URI"));
		return m;
	}

	public OAuth2Setting createRegistration(OAuth2Setting m) {
		String sql = "INSERT INTO TBL_OAUTH2_SETTING(ID, CLIENT_ID, CLIENT_NAME, CLIENT_SECRET, CLIENT_AUTH_METHOD, AUTH_GRANT_TYPE, "
				+ "REDIRECT_URL_TEMPLATE, SCOPE, AUTH_URI, TOKEN_URI, USER_INFO_URI, USER_ATTRIBUTE_NAME, JWK_SET_URI)"
				+ "VALUES(SEQ_AVATAR_PROFILE.NEXTVAL,?,?,?,?,?,?,?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(sql.toString(),
								new String[] { "ID" });
						int i = 1;
						ps.setString(i++, m.getClientId());
						ps.setString(i++, m.getClientName());
						ps.setString(i++, m.getClientSecret());
						ps.setString(i++, m.getClientAuthMethod());
						ps.setString(i++, m.getAuthGrantType());
						ps.setString(i++, m.getRedirectUriTemplate());
						ps.setString(i++, m.getScopeString());
						ps.setString(i++, m.getAuthUri());
						ps.setString(i++, m.getTokenUri());
						ps.setString(i++, m.getUserInfoUri());
						ps.setString(i++, m.getUserAttributeName());
						ps.setString(i++, m.getJwkSetUri());
						return ps;
					}
				}, keyHolder);
		m.setId(keyHolder.getKey().intValue());
		return m;
	}

	public List<OAuth2Setting> getAllRegistrations() {
		String sql = "SELECT * FROM TBL_OAUTH2_SETTING ORDER BY ID";
		return jdbcTemplate.query(sql, new RowMapper<OAuth2Setting>() {
			@Override
			public OAuth2Setting mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public OAuth2Setting getRegistrationById(int id) {
		String sql = "SELECT * FROM TBL_OAUTH2_SETTING WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<OAuth2Setting>() {
			@Override
			public OAuth2Setting extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

	public OAuth2Setting updateUser(OAuth2Setting m) {
		String sql = "UPDATE TBL_OAUTH2_SETTING SET CLIENT_ID=?, CLIENT_NAME=?, CLIENT_SECRET=?, CLIENT_AUTH_METHOD=?, AUTH_GRANT_TYPE=?, "
				+ "REDIRECT_URL_TEMPLATE=?, SCOPE=?, AUTH_URI=?, TOKEN_URI=?, USER_INFO_URI=?, USER_ATTRIBUTE_NAME=?, JWK_SET_URI=? WHERE ID=?";
		jdbcTemplate.update(sql, m.getClientId(), m.getClientName(), m.getClientSecret(), m.getClientAuthMethod(),
				m.getAuthGrantType(), m.getRedirectUriTemplate(), m.getScopeString(), m.getAuthUri(), m.getTokenUri(),
				m.getUserInfoUri(), m.getUserAttributeName(), m.getJwkSetUri());
		return m;
	}

}
