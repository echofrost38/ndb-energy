package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.SkillSet;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class AvatarResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver{
	
	// create new component
	public AvatarComponent createNewComponent(
		String groupId, 
		Integer tierLevel, 
		Double price, 
		Integer limited
	) {
		return avatarService.createAvatarComponent(groupId, tierLevel, price, limited);
	}
	
	// update component
	public AvatarComponent updateComponent(String groupId, String compId, Integer tierLevel, Double price, Integer limited) {
		return avatarService.updateAvatar(groupId, compId, tierLevel, price, limited);
	}

	// create new avatar
	public AvatarProfile createNewAvatar(String name, String surname, String shortName, List<SkillSet> skillSet, AvatarSet avatarSet, String enemy, String invention, String bio) 
	{
		return avatarService.createAvatarProfile(name, surname, shortName, skillSet, avatarSet, enemy, invention, bio);
	}
	
	// update existing avatar
	public AvatarProfile updateAvatarProfile(String id, String name, String surname, String shortName, List<SkillSet> skillSet, AvatarSet avatarSet, String enemy, String invention, String bio) 
	{
		return avatarService.updateAvatarProfile(id, name, surname, shortName, skillSet, avatarSet, enemy, invention, bio);
	}
	
	// get avatar list
	public List<AvatarProfile> getAvatars() {
		return avatarService.getAvatarProfiles();
	}
	
	public AvatarProfile getAvatar(String id) {
		return avatarService.getAvatarProfile(id);
	}

	public AvatarProfile getAvatarByName(String fname) {
		return avatarService.getAvatarProfileByName(fname);
	}
	
	public List<AvatarComponent> getAvatarComponents() {
		return avatarService.getAvatarComponents();
	}
	
	public List<AvatarComponent> getAvatarComponentsByGroup(String groupId) {
		return avatarService.getAvatarComponentsById(groupId);
	}
	
	public AvatarComponent getAvatarComponent(String groupId, String compId) {
		return avatarService.getAvatarComponent(groupId, compId);
	}
	
	public List<AvatarComponent> getAvatarComponentsBySet(AvatarSet set) {
		return avatarService.getAvatarComponentsBySet(set);
	}

	// select avatar by user
	
	// purchase by user
	
	// 
}
