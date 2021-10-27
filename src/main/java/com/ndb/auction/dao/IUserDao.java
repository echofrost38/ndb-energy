package com.ndb.auction.dao;

import java.util.List;

import com.ndb.auction.models.User;

public interface IUserDao {
	
	// create user
	User createUser(User user);
	
	// get user by email
	User getUserByEmail(String email);
	
	// get user by id
	User getUserById(String id);
	
	// update user verify status
	
	
	// get user list ( admin )
	List<User> getUserList();
	
	// delete user
	User deleteUser(String id);
	
}
