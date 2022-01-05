package com.ndb.auction.service;

import com.ndb.auction.models.user.User;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl extends BaseService implements UserDetailsService {
	
	@Override
	public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userDao.getUserByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + email));
		return UserDetailsImpl.build(user);
	}

}
