package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.SkillSet;
import com.ndb.auction.models.avatar.AvatarComponent;
import com.ndb.auction.models.avatar.AvatarProfile;
import com.ndb.auction.models.avatar.AvatarSet;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class AvatarResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver{
	
	// create new component
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public AvatarComponent createNewComponent(
		int groupId, 
		Integer tierLevel, 
		Long price, 
		Integer limited,
		String svg,
		int width,
		int top,
		int left
	) {
		return avatarService.createAvatarComponent(groupId, tierLevel, price, limited, svg, width, top, left);
	}
	
	// update component
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public AvatarComponent updateComponent(String groupId, String compId, Integer tierLevel, Double price, Integer limited, String svg, int width, int top, int left) {
		return avatarService.updateAvatar(groupId, compId, tierLevel, price, limited, svg, width, top, left);
	}

	// create new avatar
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public AvatarProfile createNewAvatar(String name, String surname, String shortName, List<SkillSet> skillSet, List<AvatarSet> avatarSet, String enemy, String invention, String bio, String hairColor) 
	{
		return avatarService.createAvatarProfile(name, surname, shortName, skillSet, avatarSet, enemy, invention, bio, hairColor);
	}
	
	// update existing avatar
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public AvatarProfile updateAvatarProfile(String id, String name, String surname, String shortName, List<SkillSet> skillSet, List<AvatarSet> avatarSet, String enemy, String invention, String bio, String hairColor) 
	{
		return avatarService.updateAvatarProfile(id, name, surname, shortName, skillSet, avatarSet, enemy, invention, bio, hairColor);
	}
	
	// get avatar list
	@PreAuthorize("isAuthenticated()")
	public List<AvatarProfile> getAvatars() {
		return avatarService.getAvatarProfiles();
	}
	
	@PreAuthorize("isAuthenticated()")
	public AvatarProfile getAvatar(String id) {
		return avatarService.getAvatarProfile(id);
	}

	@PreAuthorize("isAuthenticated()")
	public AvatarProfile getAvatarByName(String fname) {
		return avatarService.getAvatarProfileByName(fname);
	}
	
	@PreAuthorize("isAuthenticated()")
	public List<AvatarComponent> getAvatarComponents() {
		return avatarService.getAvatarComponents();
	}
	
	@PreAuthorize("isAuthenticated()")
	public List<AvatarComponent> getAvatarComponentsByGroup(String groupId) {
		return avatarService.getAvatarComponentsById(groupId);
	}
	
	@PreAuthorize("isAuthenticated()")
	public AvatarComponent getAvatarComponent(String groupId, String compId) {
		return avatarService.getAvatarComponent(groupId, compId);
	}
	
	@PreAuthorize("isAuthenticated()")
	public List<AvatarComponent> getAvatarComponentsBySet(List<AvatarSet> set) {
		return avatarService.getAvatarComponentsBySet(set);
	}

	// select avatar by user
	

	// purchase by user
	
	
	// 
}
