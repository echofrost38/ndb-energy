package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.SkillSet;
import com.ndb.auction.models.avatar.AvatarComponent;
import com.ndb.auction.models.avatar.AvatarFacts;
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
		String groupId, 
		Integer tierLevel, 
		Double price, 
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
	public AvatarComponent updateComponent(String groupId, int compId, Integer tierLevel, Double price, Integer limited, String svg, int width, int top, int left) {
		return avatarService.updateAvatarComponent(groupId, compId, tierLevel, price, limited, svg, width, top, left);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public int deleteAvatarComponent(String groupId, int compId) {
		return avatarService.deleteAvatarComponent(groupId, compId);
	}

	// create new avatar
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public AvatarProfile createNewAvatar(
		String fname, 
		String surname, 
		List<SkillSet> skillSet, 
		List<AvatarSet> avatarSet, 
		List<AvatarFacts> factSet, 
		String hairColor, 
		String skinColor,
		String details
	) 
	{
		return avatarService.createAvatarProfile(fname, surname, skillSet, avatarSet, factSet, hairColor, skinColor, details);
	}
	
	// update existing avatar
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Boolean updateAvatarProfile(int id, String fname, String surname, List<SkillSet> skillSet, List<AvatarSet> avatarSet, List<AvatarFacts> factSet, String hairColor, String skinColor, String details) 
	{
		return avatarService.updateAvatarProfile(id, fname, surname, skillSet, avatarSet, factSet, hairColor, skinColor, details);
	}
	
	// get avatar list
	@PreAuthorize("isAuthenticated()")
	public List<AvatarProfile> getAvatars() {
		return avatarService.getAvatarProfiles();
	}
	
	@PreAuthorize("isAuthenticated()")
	public AvatarProfile getAvatar(int id) {
		return avatarService.getAvatarProfile(id);
	}

	@PreAuthorize("isAuthenticated()")
	public AvatarProfile getAvatarByName(String surname) {
		return avatarService.getAvatarProfileByName(surname);
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
	public AvatarComponent getAvatarComponent(String groupId, int compId) {
		return avatarService.getAvatarComponent(groupId, compId);
	}
	
	@PreAuthorize("isAuthenticated()")
	public List<AvatarComponent> getAvatarComponentsBySet(List<AvatarSet> set) {
		return avatarService.getAvatarComponentsBySet(set);
	}

}
