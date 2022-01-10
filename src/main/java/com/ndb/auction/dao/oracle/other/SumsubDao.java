package com.ndb.auction.dao.oracle.other;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.ndb.auction.models.KYCSetting;
import com.ndb.auction.models.sumsub.Applicant;

@Repository
@NoArgsConstructor
@Table(name = "TBL_AVATAR_PROFILE_SKILL")
public class SumsubDao extends BaseOracleDao {

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
	
	// Creating new applicant
	public Applicant createNewApplicant(Applicant applicant) {
		dynamoDBMapper.save(applicant);
		return applicant;
	}
	
	public List<Applicant> getApplicantByUserId(int userId) {
		Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val1", new AttributeValue().withS(String.valueOf(userId)));
        
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("user_id = :val1")
            .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(Applicant.class, scanExpression);
	}
	
	public Applicant getApplicantById(String id) {
		return dynamoDBMapper.load(Applicant.class, id);
	}

	public KYCSetting updateKYCSetting(KYCSetting setting) {
		dynamoDBMapper.save(setting, updateConfig);
		return setting;
	} 

	public List<KYCSetting> getKYCSettings() {
		return dynamoDBMapper.scan(KYCSetting.class, new DynamoDBScanExpression());
	}

}
