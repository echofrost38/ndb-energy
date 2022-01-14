package com.ndb.auction.service.user;

import java.util.List;

import com.ndb.auction.models.user.UserSecurity;
import com.ndb.auction.service.BaseService;

import org.springframework.stereotype.Service;

@Service
public class UserSecurityService extends BaseService {

	public List<UserSecurity> selectByUserId(int userId) {
		return userSecurityDao.selectByUserId(userId);
	}

	public UserSecurity insert(UserSecurity m) {
		return userSecurityDao.insert(m);
	}

	public int insertOrUpdate(UserSecurity m) {
		return userSecurityDao.insertOrUpdate(m);
	}

}
