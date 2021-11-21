package com.ndb.auction.payload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ndb.auction.models.coinbase.CoinbasePricing;

public class CryptoPayload {
    
    private List<AddressList> addresses;
    private List<PricingList> pricing;

    public CryptoPayload(Map<String, String> address, Map<String, CoinbasePricing> pricingMap) {
    	this.addresses = new ArrayList<AddressList>();
    	Set<String> addrKeySet = address.keySet();
    	for(String key: addrKeySet) {
    		AddressList addr = new AddressList();
    		addr.setKey(key);
    		addr.setValue(address.get(key));
    		this.addresses.add(addr);
    	}
    	
    	this.pricing = new ArrayList<PricingList>();
    	Set<String> priceKeySet = pricingMap.keySet();
    	for(String key: priceKeySet) {
    		PricingList price = new PricingList();
    		price.setKey(key);
    		price.setValue(pricingMap.get(key));
    		this.pricing.add(price);
    	}
    }
    
    public List<AddressList> getAddresses() {
        return addresses;
    }
    
    public List<PricingList> getPricing() {
        return pricing;
    }

}
