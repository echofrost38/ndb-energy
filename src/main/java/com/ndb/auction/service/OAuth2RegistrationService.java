package com.ndb.auction.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.ndb.auction.dao.OAuth2Dao;
import com.ndb.auction.models.OAuth2Registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OAuth2RegistrationService {

    @Autowired
	private OAuth2Dao oAuth2Dao;
    
    private Map<String, OAuth2Registration> oAuth2Registrations;

    @PostConstruct
    public void init() {
        try{
            oAuth2Registrations = new HashMap<String, OAuth2Registration>();
    
            List<OAuth2Registration> registrations = oAuth2Dao.getAllRegistrations();
            for(OAuth2Registration registration : registrations) {
                oAuth2Registrations.put(registration.getRegistrationId(), registration);
            }
        }catch(Exception e) {
            
        }
    }

    public OAuth2Registration getByRegistrationId(String id) {
        return oAuth2Registrations.get(id);
    }

    public OAuth2Registration createRegistration(OAuth2Registration registration) {
        return oAuth2Dao.createRegistration(registration);
    }
}
