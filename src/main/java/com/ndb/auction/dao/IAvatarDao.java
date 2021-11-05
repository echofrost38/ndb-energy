package com.ndb.auction.dao;

import java.util.List;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;

public interface IAvatarDao {
	
	// upload avatar component
	AvatarComponent createAvatarComponent(AvatarComponent component);
	
	// get avatar components
	List<AvatarComponent> getAvatarComponents();
	
	List<AvatarComponent> getAvatarComponentsByGid(String groupId);
	
	AvatarComponent getAvatarComponent(String groupId, Integer sKey);
	
	// update avatar component
	AvatarComponent updateAvatarComponent(AvatarComponent component);
	
	// create new avatar
	AvatarProfile createAvatarProfile(AvatarProfile avatar);
	
	// update avatar
	AvatarProfile updateAvatarProfile(AvatarProfile avatar);
	
	// get avatars
	List<AvatarProfile> getAvatarProfiles();
	
	AvatarProfile getAvatarProfile(String id);
	
	
	/**
	 *  User tier
	 */
	
}
