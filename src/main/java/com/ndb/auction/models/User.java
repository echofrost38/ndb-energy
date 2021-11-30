package com.ndb.auction.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import com.ndb.auction.models.user.AuthProvider;
import com.ndb.auction.models.user.AvatarPurchased;
import com.ndb.auction.models.user.ExtWallet;
import com.ndb.auction.models.user.Security;
import com.ndb.auction.models.user.Verify;
import com.ndb.auction.models.user.Wallet;

@DynamoDBTable(tableName = "User")
public class User {
	
	private String id;
	private String name;
	private String surname;
	private Set<String> role;
	private Long birthDate;
	private String email;
	private String mobile;
	private String country;
	private Map<String, Wallet> wallet;
	private Map<String, String> extWallet;
	
	private Boolean tos;
	
	private String password;
	private String twoStep;
	private Map<String, Boolean> security;
	private Map<String, Boolean> verify;
	private String googleSecret;
	
	private Integer notifySetting;
	
	private String avatarPrefix;
	private String avatarName;
	private List<AvatarSet> avatar;
	private Map<String, List<String>> avatarPurchase;
	private String docType;

	private Long lastLogin;

	private AuthProvider provider;
	private String providerId;
	
	// for update purpose
	public User() {
		initUser();
	}

	public User(String email, String password, String country, boolean tos, List<Coin> coinList) {
		// from user input
		this.email = email;
		this.password = password;
		this.country = country;
		this.tos = tos;
		
		
		// initialize the wallet with possible coins
		this.wallet = new HashMap<String, Wallet>();
		for (Coin coin : coinList) {
			this.wallet.put(coin.getSymbol(), new Wallet());
		}
		
		initUser();
		
	}

	@DynamoDBIgnore
	public void initUser() {
		this.role = new HashSet<String>();
		this.role.add("ROLE_USER");

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

		this.avatarPurchase = new HashMap<String, List<String>>();

		this.provider = AuthProvider.local;

		this.notifySetting = 0xFFFF;
	}

	
	@DynamoDBHashKey(attributeName="id")
    @DynamoDBAutoGeneratedKey
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@DynamoDBAttribute(attributeName="fname")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@DynamoDBAttribute(attributeName="surname")
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	@DynamoDBAttribute(attributeName="role")
	public Set<String> getRole() {
		return role;
	}
	public void setRole(Set<String> role) {
		this.role = role;
	}
	
	@DynamoDBAttribute(attributeName="birth_date")
	public Long getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Long birthDate) {
		this.birthDate = birthDate;
	}
	
	@DynamoDBAttribute(attributeName="email")
	@DynamoDBIndexHashKey(globalSecondaryIndexName="s_email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@DynamoDBAttribute(attributeName="mobile")
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@DynamoDBAttribute(attributeName="password")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@DynamoDBAttribute(attributeName="country")
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	@DynamoDBAttribute(attributeName="wallet")
	public Map<String, Wallet> getWallet() {
		return wallet;
	}
	public void setWallet(Map<String, Wallet> wallet) {
		this.wallet = wallet;
	}
	
	@DynamoDBAttribute(attributeName="two_step")
	public String getTwoStep() {
		return twoStep;
	}
	public void setTwoStep(String twoStep) {
		this.twoStep = twoStep;
	}
	
	@DynamoDBAttribute(attributeName="security")
	public Map<String, Boolean> getSecurity() {
		return security;
	}
	public void setSecurity(Map<String, Boolean> security) {
		this.security = security;
	}
	
	@DynamoDBAttribute(attributeName="avatar_prefix")
	public String getAvatarPrefix() {
		return avatarPrefix;
	}
	public void setAvatarPrefix(String avatarPrefix) {
		this.avatarPrefix = avatarPrefix;
	}
	
	@DynamoDBAttribute(attributeName="avatar_name")
	public String getAvatarName() {
		return avatarName;
	}
	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}
	
	@DynamoDBAttribute(attributeName="ext_wallet")
	public Map<String, String> getExtWallet() {
		return extWallet;
	}
	public void setExtWallet(Map<String, String> extWallet) {
		this.extWallet = extWallet;
	}
	
	@DynamoDBAttribute(attributeName="tos")
	public Boolean getTos() {
		return tos;
	}
	public void setTos(Boolean tos) {
		this.tos = tos;
	}
	
	@DynamoDBAttribute(attributeName="verify")
	public Map<String, Boolean> getVerify() {
		return verify;
	}
	public void setVerify(Map<String, Boolean> verify) {
		this.verify = verify;
	}
	
	@DynamoDBAttribute(attributeName="notify_set")
	public Integer getNotifySetting() {
		return notifySetting;
	}
	public void setNotifySetting(Integer notifySetting) {
		this.notifySetting = notifySetting;
	}
	public boolean allowNotification(Notification notification) {
		return ((this.notifySetting >> (notification.getType() - 1)) & 0x01) > 0;
	}
	
	@DynamoDBAttribute(attributeName="last_login")
	public Long getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Long lastLogin) {
		this.lastLogin = lastLogin;
	}
	
	@DynamoDBAttribute(attributeName="google_secret")
	public String getGoogleSecret() {
		return googleSecret;
	}

	public void setGoogleSecret(String googleSecret) {
		this.googleSecret = googleSecret;
	}
	
	@DynamoDBAttribute(attributeName="avatar")
	public List<AvatarSet> getAvatar() {
		return avatar;
	}
	
	public void setAvatar(List<AvatarSet> avatar) {
		this.avatar = avatar;
	}
	
	@DynamoDBAttribute(attributeName="avatar_purchase")
	public Map<String, List<String>> getAvatarPurchase() {
		return avatarPurchase;
	}
	
	public void setAvatarPurchase(Map<String, List<String>> avatar_purchase) {
		this.avatarPurchase = avatar_purchase;
	}
	
	@DynamoDBAttribute(attributeName="doc_type")
	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	@DynamoDBAttribute(attributeName="provider_id")
	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	@DynamoDBTypeConvertedEnum
	@DynamoDBAttribute(attributeName="provider")
	public AuthProvider getProvider() {
		return provider;
	}

	public void setProvider(AuthProvider provider) {
		this.provider = provider;
	}

	@DynamoDBIgnore
	public List<AvatarPurchased> getAvatarPurchased() {
		List<AvatarPurchased> list = new ArrayList<AvatarPurchased>();
		if(avatarPurchase == null) {
			return null;
		}
		Set<String> keys = avatarPurchase.keySet();
		for (String key : keys) {
			AvatarPurchased e = new AvatarPurchased();
			e.setKey(key);
			e.setValue(avatarPurchase.get(key));
			list.add(e);
		}
		return list;
	}

	@DynamoDBIgnore
	public List<Wallet> getUserWallet() {
		List<Wallet> list = new ArrayList<Wallet>();
		Set<String> keys = wallet.keySet();
		for (String key : keys) {
			Wallet w = wallet.get(key);
			w.setKey(key);
			list.add(w);
		}
		return list;
	}

	@DynamoDBIgnore
	public List<ExtWallet> getUserExtWallet() {
		List<ExtWallet> list = new ArrayList<ExtWallet>();
		Set<String> keys = extWallet.keySet();
		for (String key : keys) {
			ExtWallet e = new ExtWallet();
			e.setKey(key);
			e.setValue(extWallet.get(key));
			list.add(e);
		}
		return list;
	}

	@DynamoDBIgnore
	public List<Security> getUserSecurity() {
		List<Security> list = new ArrayList<Security>();
		Set<String> keys = security.keySet();
		for (String key : keys) {
			Security sec = new Security();
			sec.setKey(key);
			sec.setValue(security.get(key));
			list.add(sec);
		}
		return list;
	}

	@DynamoDBIgnore
	public List<Verify> getUserVerify() {
		List<Verify> list = new ArrayList<Verify>();
		Set<String> keys = verify.keySet();
		for (String keyString : keys) {
			Verify ver = new Verify();
			ver.setKey(keyString);
			ver.setValue(verify.get(keyString));
			list.add(ver);
		}
		return list;
	}


}
