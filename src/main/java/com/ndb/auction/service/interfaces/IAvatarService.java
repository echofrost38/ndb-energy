package com.ndb.auction.service.interfaces;

import java.util.List;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.SkillSet;

public interface IAvatarService {
	
	List<AvatarComponent> getAvatarComponents();
	
	List<AvatarComponent> getAvatarComponentsById(String groupId);
	
	AvatarComponent getAvatarComponent(String groupId, String sKey);
	
	AvatarProfile createAvatarProfile(
				String name,
				String surname,
				String shortName,
				List<SkillSet> skillSet,
				List<AvatarSet> avatarSet,
				String enemy,
				String invention,
				String bio,
				String hairColor
			);
	
	AvatarProfile updateAvatarProfile(
				String id,
				String name,
				String surname,
				String shortName,
				List<SkillSet> skillSet,
				List<AvatarSet> avatarSet,
				String enemy,
				String invention,
				String bio,
				String hairColor
			);
	
	List<AvatarProfile> getAvatarProfiles();
	
	AvatarProfile getAvatarProfile(String id);

	AvatarProfile getAvatarProfileByName(String name);

	List<AvatarComponent> getAvatarComponentsBySet(List<AvatarSet> set);
}
