package com.ndb.auction.dao.oracle.other;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.ndb.auction.models.OAuth2Registration;

import org.springframework.stereotype.Repository;

@Repository
public class OAuth2Dao extends BaseOracleDao {

	private static final String TABLE_NAME = "TBL_USER";

	private static User extract(ResultSet rs) throws SQLException {
		User m = new User();
		m.setId(rs.getInt("ID"));
		m.setEmail(rs.getString("EMAIL"));
		m.setPassword(rs.getString("PASSWORD"));
		m.setName(rs.getString("NAME"));
		m.setCountry(rs.getString("COUNTRY"));
		m.setPhone(rs.getString("PHONE"));
		m.setBirthday(rs.getTimestamp("BIRTHDAY"));
		m.setRegDate(rs.getTimestamp("REG_DATE"));
		m.setLastLoginDate(rs.getTimestamp("LAST_LOGIN_DATE"));
		m.setLastPasswordChangeDate(rs.getTimestamp("LAST_PASSWORD_CHANGE_DATE"));
		m.setRole(rs.getString("ROLE"));
		m.setTierLevel(rs.getInt("TIER_LEVEL"));
		m.setTierPoint(rs.getInt("TIER_POINT"));
		m.setProvider(rs.getString("PROVIDER"));
		m.setProviderId(rs.getString("PROVIDER_ID"));
		m.setNotifySetting(rs.getInt("NOTIFY_SETTING"));
		m.setDeleted(rs.getInt("DELETED"));
		return m;
	}

	public UserDao() {
		super(TABLE_NAME);
	}
    
	public OAuth2Registration createRegistration(OAuth2Registration registration) {
		dynamoDBMapper.save(registration);
		return registration;
	}

	public List<OAuth2Registration> getAllRegistrations() {
		return dynamoDBMapper.scan(OAuth2Registration.class, new DynamoDBScanExpression());
	}

	public OAuth2Registration getRegistrationById(String registrationId) {
		return dynamoDBMapper.load(OAuth2Registration.class, registrationId);
	}

    public OAuth2Registration updateUser(OAuth2Registration registration) {
		dynamoDBMapper.save(registration, updateConfig);
		return registration;
	}

    public OAuth2Registration deleteUser(String registrationId) {
		OAuth2Registration registration = dynamoDBMapper.load(OAuth2Registration.class, registrationId);
		dynamoDBMapper.delete(registration);
		return registration;
	}
}
