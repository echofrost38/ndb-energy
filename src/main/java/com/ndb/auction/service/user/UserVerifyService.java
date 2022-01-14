package com.ndb.auction.service.user;

import com.ndb.auction.models.user.UserVerify;
import com.ndb.auction.service.BaseService;

import org.springframework.stereotype.Service;

@Service
public class UserVerifyService extends BaseService {

	public UserVerify selectById(int id) {
		return userVerifyDao.selectById(id);
	}

	public int insert(UserVerify m) {
		return userVerifyDao.insert(m);
	}

	public int insertOrUpdate(UserVerify m) {
		return userVerifyDao.insertOrUpdate(m);
	}

}
