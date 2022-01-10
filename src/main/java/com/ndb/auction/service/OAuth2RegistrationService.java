package com.ndb.auction.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.ndb.auction.dao.oracle.other.OAuth2SettingDao;
import com.ndb.auction.models.OAuth2Setting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OAuth2RegistrationService {

    @Autowired
	private OAuth2SettingDao oAuth2Dao;
    
    private Map<String, OAuth2Setting> oAuth2Registrations;

    @PostConstruct
    public void init() {
        try{
            oAuth2Registrations = new HashMap<String, OAuth2Setting>();
    
            List<OAuth2Setting> registrations = oAuth2Dao.getAllRegistrations();
            for(OAuth2Setting registration : registrations) {
                oAuth2Registrations.put(registration.getRegistrationId(), registration);
            }
        }catch(Exception e) {
            
        }
    }

    public OAuth2Setting getByRegistrationId(String id) {
        return oAuth2Registrations.get(id);
    }

    public OAuth2Setting createRegistration(OAuth2Setting registration) {
        OAuth2Setting regist = oAuth2Dao.createRegistration(registration);
        init();
        return regist;
    }

}
