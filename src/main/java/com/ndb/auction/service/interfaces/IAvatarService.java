package com.ndb.auction.service.interfaces;

import java.util.List;

import javax.servlet.http.Part;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.SkillSet;

public interface IAvatarService {
	
	AvatarComponent createAvatarComponent(String groupId, Integer tierLevel, Double price, Integer limited, Part file);
	
	List<AvatarComponent> getAvatarComponents();
	
	List<AvatarComponent> getAvatarComponentsById(String groupId);
	
	AvatarComponent getAvatarComponent(String groupId, String sKey);
	
	AvatarComponent updateAvatar(String groupId, String compId, Integer tierLevel, Double price, Integer limited, Part file);
	
	AvatarProfile createAvatarProfile(
				String name,
				String surname,
				String shortName,
				List<SkillSet> skillSet,
				List<AvatarSet> avatarSet,
				String enemy,
				String invention,
				String bio
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
				String bio
			);
	
	List<AvatarProfile> getAvatarProfiles();
	
	AvatarProfile getAvatarProfile(String id);

	AvatarProfile getAvatarProfileByName(String name);

	List<AvatarComponent> getAvatarComponentsBySet(List<AvatarSet> set);
}
