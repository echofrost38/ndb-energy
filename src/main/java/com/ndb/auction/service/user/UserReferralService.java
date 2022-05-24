package com.ndb.auction.service.user;

import com.ndb.auction.models.user.UserReferral;
import com.ndb.auction.service.BaseService;

import org.springframework.stereotype.Service;

@Service
public class UserReferralService extends BaseService {

    public UserReferral selectById(int id) {
        return userReferralDao.selectById(id);
    }

    public int insert(UserReferral m) {
        return userReferralDao.insert(m);
    }

//    public int insertOrUpdate(UserReferral m) {
//        return userReferralDao.insertOrUpdate(m);
//    }

}
