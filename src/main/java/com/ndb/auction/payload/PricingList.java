package com.ndb.auction.payload;

import com.ndb.auction.models.coinbase.CoinbasePricing;

public class PricingList {
	
	private String key;
	private CoinbasePricing value;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public CoinbasePricing getValue() {
		return value;
	}
	public void setValue(CoinbasePricing value) {
		this.value = value;
	}
	
}
