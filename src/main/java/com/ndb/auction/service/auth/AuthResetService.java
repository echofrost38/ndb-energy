package com.ndb.auction.service.auth;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.ndb.auction.dao.oracle.auth.GAuthResetRequestDao;
import com.ndb.auction.models.auth.GAuthResetRequest;
import com.ndb.auction.payload.response.GAuthResetResponse;
import com.ndb.auction.service.utils.TotpService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthResetService {
    
    @Autowired
    private GAuthResetRequestDao requestDao;

    @Autowired
    private TotpService totpService;

    // save new request
    public GAuthResetResponse saveNewGAuthRequest(int userId, String email) {
        // generate token
        var array = new byte[32];
		new Random().nextBytes(array);
		var token = UUID.randomUUID().toString();
        var secret = totpService.generateSecret();
        var m = GAuthResetRequest.builder()
            .userId(userId)
            .token(token)
            .secret(secret)
            .build();

        if(requestDao.save(m) > 0) {
            return GAuthResetResponse.builder()
                .secret(totpService.getUriForImage(secret, email))
                .token(token)
                .build();
        } else {
            return null;
        }
    }

    // get request by user id and token
    public GAuthResetRequest getRequestByUser(int userId, String token) {
        return requestDao.selectById(userId, token);
    }

    public List<GAuthResetRequest> getRequestsByUser(int userId) {
        return requestDao.selectByUser(userId);
    }

    public int updateRequestStatus(int userId, String token, int status) {
        return requestDao.updateRequest(userId, token, status);
    }
}
