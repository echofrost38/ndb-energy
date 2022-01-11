package com.ndb.auction.service.user;

import com.ndb.auction.models.user.UserSecurity;
import com.ndb.auction.service.BaseService;

import org.springframework.stereotype.Service;

@Service
public class UserSecurityService extends BaseService {

	public UserSecurity selectById(int id) {
		return userSecurityDao.selectById(id);
	}

	public int insert(UserSecurity m) {
		return userSecurityDao.insert(m);
	}

	public int insertOrUpdate(UserSecurity m) {
		return userSecurityDao.insertOrUpdate(m);
	}

}
