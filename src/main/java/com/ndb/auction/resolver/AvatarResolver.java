package com.ndb.auction.resolver;

import java.util.List;

import javax.servlet.http.Part;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.SkillSet;

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
		Long price, 
		Integer limited,
		Part file
	) {
		return avatarService.createAvatarComponent(groupId, tierLevel, price, limited, file);
	}
	
	// update component
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public AvatarComponent updateComponent(String groupId, String compId, Integer tierLevel, Long price, Integer limited, Part file) {
		return avatarService.updateAvatar(groupId, compId, tierLevel, price, limited, file);
	}

	// create new avatar
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public AvatarProfile createNewAvatar(String name, String surname, String shortName, List<SkillSet> skillSet, List<AvatarSet> avatarSet, String enemy, String invention, String bio) 
	{
		return avatarService.createAvatarProfile(name, surname, shortName, skillSet, avatarSet, enemy, invention, bio);
	}
	
	// update existing avatar
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public AvatarProfile updateAvatarProfile(String id, String name, String surname, String shortName, List<SkillSet> skillSet, List<AvatarSet> avatarSet, String enemy, String invention, String bio) 
	{
		return avatarService.updateAvatarProfile(id, name, surname, shortName, skillSet, avatarSet, enemy, invention, bio);
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
