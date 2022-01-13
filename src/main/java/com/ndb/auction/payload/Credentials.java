package com.ndb.auction.payload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ndb.auction.models.user.TwoFAMethod;

public class Credentials {
	
	private String status;
	private String token;
	private Map<String, Boolean> twoStep;
	
	public Credentials(String status, String token, Map<String, Boolean> twoStep) {
		this.status = status;
		this.token = token;
		this.twoStep = twoStep;
	}

	public Credentials(String status, String token) {
		this.status = status;
		this.token = token;
		twoStep = new HashMap<String, Boolean>();
	}
	
	public Map<String, Boolean> getTwoStep() {
		return twoStep;
	}

	public void setTwoStep(Map<String, Boolean> twoStep) {
		this.twoStep = twoStep;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public List<TwoFAMethod> getUserTwoStep() {
		List<TwoFAMethod> list = new ArrayList<TwoFAMethod>();
		Set<String> keys = twoStep.keySet();
		for (String keyString : keys) {
			TwoFAMethod ver = new TwoFAMethod();
			ver.setKey(keyString);
			ver.setValue(twoStep.get(keyString));
			list.add(ver);
		}
		return list;
	}
}
