package com.ndb.auction.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "User")
public class User {
	
	private static final String[] COINS = {
			"Bitcoin",
            "Bitcoin Cash",
            "Dai",
            "Dogecoin",
            "Ethereum",
            "Litecoin",
            "USD Coin" 
	};
	
	private String id;
	private String name;
	private String surname;
	private String role;
	private Long birthDate;
	private String email;
	private String mobile;
	private String password;
	private String country;
	private Map<String, Wallet> wallet;
	private Set<String> twoStep;
	private Map<String, Boolean> security;
	private String avatarPrefix;
	private String avatarName;
	private Map<String, String> extWallet;
	private Boolean tos;
	private Map<String, Boolean> verify;
	private Map<String, Integer> notifySet;
	private Long lastLogin;
	
	public User(String email, String password, String country, boolean tos) {
		// from user input
		this.email = email;
		this.password = password;
		this.country = country;
		this.tos = tos;
		
		this.role = "User";
		
		// initialize the wallet with possible coins
		this.wallet = new HashMap<String, Wallet>();
		for(int i = 0; i < COINS.length; i++) {
			this.wallet.put(COINS[i], new Wallet());
		}
		
		this.security = new HashMap<String, Boolean>();
		this.security.put("2FA", false);
		this.security.put("KYC", false);
		this.security.put("AML", false);
		this.security.put("mobile", false);
		
		this.extWallet = new HashMap<String, String>();
		this.extWallet.put("provider", "");
		this.extWallet.put("addr", "");
		
		this.verify = new HashMap<String, Boolean>();
		verify.put("email", false);
		verify.put("mobile", false);
		verify.put("identity", false);
		
		this.notifySet = new HashMap<String, Integer>();
		this.notifySet.put("gateway_id", 0);
		// default notify setting is disabled
		this.notifySet.put("notify", 0); 
	}
	
	@DynamoDBHashKey(attributeName="id")
    @DynamoDBAutoGeneratedKey
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@DynamoDBAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@DynamoDBAttribute
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	@DynamoDBAttribute
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	@DynamoDBAttribute
	public Long getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Long birthDate) {
		this.birthDate = birthDate;
	}
	
	@DynamoDBAttribute
	@DynamoDBIndexHashKey(globalSecondaryIndexName="s_email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@DynamoDBAttribute
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@DynamoDBAttribute
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@DynamoDBAttribute
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	@DynamoDBAttribute
	public Map<String, Wallet> getWallet() {
		return wallet;
	}
	public void setWallet(Map<String, Wallet> wallet) {
		this.wallet = wallet;
	}
	
	@DynamoDBAttribute
	public Set<String> getTwoStep() {
		return twoStep;
	}
	public void setTwoStep(Set<String> twoStep) {
		this.twoStep = twoStep;
	}
	
	@DynamoDBAttribute
	public Map<String, Boolean> getSecurity() {
		return security;
	}
	public void setSecurity(Map<String, Boolean> security) {
		this.security = security;
	}
	
	@DynamoDBAttribute
	public String getAvatarPrefix() {
		return avatarPrefix;
	}
	public void setAvatarPrefix(String avatarPrefix) {
		this.avatarPrefix = avatarPrefix;
	}
	
	@DynamoDBAttribute
	public String getAvatarName() {
		return avatarName;
	}
	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}
	
	@DynamoDBAttribute
	public Map<String, String> getExtWallet() {
		return extWallet;
	}
	public void setExtWallet(Map<String, String> extWallet) {
		this.extWallet = extWallet;
	}
	
	@DynamoDBAttribute
	public Boolean getTos() {
		return tos;
	}
	public void setTos(Boolean tos) {
		this.tos = tos;
	}
	
	@DynamoDBAttribute
	public Map<String, Boolean> getVerify() {
		return verify;
	}
	public void setVerify(Map<String, Boolean> verify) {
		this.verify = verify;
	}
	
	@DynamoDBAttribute
	public Map<String, Integer> getNotifySet() {
		return notifySet;
	}
	public void setNotifySet(Map<String, Integer> notifySet) {
		this.notifySet = notifySet;
	}
	
	@DynamoDBAttribute
	public Long getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Long lastLogin) {
		this.lastLogin = lastLogin;
	}
}
