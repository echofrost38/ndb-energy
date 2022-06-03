package com.ndb.auction.service.auth;

import java.util.Random;
import java.util.UUID;

import com.ndb.auction.dao.oracle.auth.PhoneResetRequestDao;
import com.ndb.auction.models.auth.PhoneResetRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneResetService {
    
    @Autowired
    private PhoneResetRequestDao phoneResetRequestDao;

    // create new phone reset request
    public String saveNewRequest(int userId, String phone) {
        var array = new byte[32];
		new Random().nextBytes(array);
		var token = UUID.randomUUID().toString();
        var m = PhoneResetRequest.builder()
            .userId(userId)
            .phone(phone)
            .token(token)
            .build();
        if(phoneResetRequestDao.save(m) > 0) {
            return token;
        } else {
            return "Failed";
        }
    }

    public PhoneResetRequest getByUserId(int userId, String token) {
        return phoneResetRequestDao.selectById(userId, token);
    }

    public int updateStatus(int userId, String token, int status) {
        return phoneResetRequestDao.updateRequest(userId, token, status);
    }
}
