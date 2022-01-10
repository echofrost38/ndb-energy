package com.ndb.auction.dao;

import java.util.List;
import java.util.Optional;

import com.ndb.auction.models.user.User;

public interface IUserDao {
	
	// create user
	User createUser(User user);
	
	// get user by email
	Optional<User> getUserByEmail(String email);

	// get user by avatar
	User getUserByAvatar(String prefix, String name);
	
	// get user by id
	User getUserById(String id);
	
	// update user
	User updateUser(User user);
	
	// update user verify status
	
	
	// get user list ( admin )
	List<User> getUserList();
	
	// delete user
	User deleteUser(String id);
	
}
