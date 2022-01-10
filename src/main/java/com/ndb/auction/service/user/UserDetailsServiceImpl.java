package com.ndb.auction.service.user;

import com.ndb.auction.models.user.User;
import com.ndb.auction.service.BaseService;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl extends BaseService implements UserDetailsService {

	@Override
	public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userDao.selectByEmail(email);
		if (user == null)
			throw new UsernameNotFoundException("User Not Found with username: " + email);
		return UserDetailsImpl.build(user);
	}

}
