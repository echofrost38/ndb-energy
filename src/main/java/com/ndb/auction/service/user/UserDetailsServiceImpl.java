package com.ndb.auction.service.user;

import java.util.Locale;

import com.ndb.auction.exceptions.UserNotFoundException;
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
		if (user == null) {
			String msg = messageSource.getMessage("unregistered_email", null, Locale.ENGLISH);
			throw new UserNotFoundException(msg, "email");
		}
			
		return UserDetailsImpl.build(user);
	}

}
