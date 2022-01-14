package com.ndb.auction.service.user;

import com.ndb.auction.models.user.UserAvatar;
import com.ndb.auction.service.BaseService;

import org.springframework.stereotype.Service;

@Service
public class UserAvatarService extends BaseService {

	public UserAvatar selectById(int id) {
		return userAvatarDao.selectById(id);
	}

	public int insert(UserAvatar m) {
		return userAvatarDao.insert(m);
	}

	public int insertOrUpdate(UserAvatar m) {
		return userAvatarDao.insertOrUpdate(m);
	}

}
