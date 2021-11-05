package com.ndb.auction.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.SkillSet;
import com.ndb.auction.service.interfaces.IAvatarService;

@Service
public class AvatarService extends BaseService implements IAvatarService{

	@Override
	public AvatarComponent createAvatarComponent(String groupId, Integer compId, Integer tierLevel, Double price) {
		// check existing
		AvatarComponent _component = avatarDao.getAvatarComponent(groupId, compId);
		if(_component != null) {
			return null;
		}
		AvatarComponent component = new AvatarComponent(groupId, compId, tierLevel, price);
		return avatarDao.createAvatarComponent(component);
	}

	@Override
	public List<AvatarComponent> getAvatarComponents() {
		return avatarDao.getAvatarComponents();
	}

	@Override
	public List<AvatarComponent> getAvatarComponentsById(String groupId) {
		return avatarDao.getAvatarComponentsByGid(groupId);
	}

	@Override
	public AvatarComponent getAvatarComponent(String groupId, Integer sKey) {
		return avatarDao.getAvatarComponent(groupId, sKey);
	}

	@Override
	public AvatarComponent updateAvatar(String groupId, Integer compId, Integer tierLevel, Double price) {
		AvatarComponent component = avatarDao.getAvatarComponent(groupId, compId);
		if(component == null) {
			return null;
		}
		component.setPrice(price);
		component.setTierLevel(tierLevel);
		return avatarDao.updateAvatarComponent(component);
	}

	@Override
	public AvatarProfile createAvatarProfile(String prefix, String name, String surname, String shortName,
			Set<SkillSet> skillSet, AvatarSet avatarSet, String enemy, String invention, String bio) {
		
		// check condition
		
		AvatarProfile profile = new AvatarProfile(prefix, name, surname, shortName, skillSet, avatarSet, enemy, invention, bio);
		return avatarDao.createAvatarProfile(profile);
	}

	@Override
	public AvatarProfile updateAvatarProfile(String id, String name, String surname, String shortName,
			Set<SkillSet> skillSet, AvatarSet avatarSet, String enemy, String invention, String bio) {
		
		return null;
	}

	@Override
	public List<AvatarProfile> getAvatarProfiles() {
		return avatarDao.getAvatarProfiles();
	}

	@Override
	public AvatarProfile getAvatarProfile(String id) {
		return avatarDao.getAvatarProfile(id);
	}

}
