package com.ndb.auction.service.interfaces;

import java.util.List;
import java.util.Set;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.SkillSet;

public interface IAvatarService {
	
	AvatarComponent createAvatarComponent(String groupId, Integer compId, Integer tierLevel, Double price);
	
	List<AvatarComponent> getAvatarComponents();
	
	List<AvatarComponent> getAvatarComponentsById(String groupId);
	
	AvatarComponent getAvatarComponent(String groupId, Integer sKey);
	
	AvatarComponent updateAvatar(String groupId, Integer compId, Integer tierLevel, Double price);
	
	AvatarProfile createAvatarProfile(
				String prefix,
				String name,
				String surname,
				String shortName,
				Set<SkillSet> skillSet,
				AvatarSet avatarSet,
				String enemy,
				String invention,
				String bio
			);
	
	AvatarProfile updateAvatarProfile(
				String id,
				String name,
				String surname,
				String shortName,
				Set<SkillSet> skillSet,
				AvatarSet avatarSet,
				String enemy,
				String invention,
				String bio
			);
	
	List<AvatarProfile> getAvatarProfiles();
	
	AvatarProfile getAvatarProfile(String id);
}
