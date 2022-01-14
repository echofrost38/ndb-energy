package com.ndb.auction.service.user;

import com.ndb.auction.models.user.UserKyb;
import com.ndb.auction.service.BaseService;

import org.springframework.stereotype.Service;

@Service
public class UserKybService extends BaseService {

	public UserKyb selectById(int id) {
		return userKybDao.selectById(id);
	}

	public int insert(UserKyb m) {
		return userKybDao.insert(m);
	}

	public int insertOrUpdate(UserKyb m) {
		return userKybDao.insertOrUpdate(m);
	}

}
