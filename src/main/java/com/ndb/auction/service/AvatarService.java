package com.ndb.auction.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.SkillSet;
import com.ndb.auction.service.interfaces.IAvatarService;

@Service
public class AvatarService extends BaseService implements IAvatarService{

	@Override
	public AvatarComponent createAvatarComponent(String groupId, Integer tierLevel, Double price, Integer limited) {
		AvatarComponent component = new AvatarComponent(groupId, tierLevel, price, limited);
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
	public AvatarComponent getAvatarComponent(String groupId, String sKey) {
		return avatarDao.getAvatarComponent(groupId, sKey);
	}

	@Override
	public AvatarComponent updateAvatar(String groupId, String compId, Integer tierLevel, Double price, Integer limited) {
		AvatarComponent component = avatarDao.getAvatarComponent(groupId, compId);
		if(component == null) {
			return null;
		}
		price = price == null ? 0 : price;
		tierLevel = tierLevel == null ? 0 : tierLevel;
		limited = limited == null ? 0 : limited;
		component.setPrice(price);
		component.setTierLevel(tierLevel);
		component.setLimited(limited);
		
		return avatarDao.updateAvatarComponent(component);
	}

	@Override
	public AvatarProfile createAvatarProfile(String name, String surname, String shortName,
			List<SkillSet> skillSet, AvatarSet avatarSet, String enemy, String invention, String bio) {
		
		// check condition
		AvatarProfile profile = avatarDao.getAvatarProfileByName(name);
		if(profile != null) {
			return null;
		}
		
		profile = new AvatarProfile(name, surname, shortName, skillSet, avatarSet, enemy, invention, bio);
		return avatarDao.createAvatarProfile(profile);
	}

	@Override
	public AvatarProfile updateAvatarProfile(
			String id, 
			String name, 
			String surname, 
			String shortName,
			List<SkillSet> skillSet, 
			AvatarSet avatarSet, 
			String enemy, 
			String invention, 
			String bio) 
	{
		AvatarProfile profile = avatarDao.getAvatarProfile(id);
		if(profile == null) {
			return null;
		}
		profile.setName(name);
		profile.setSurname(surname);
		profile.setShortName(shortName);
		profile.setSkillSet(skillSet);
		profile.setAvatarSet(avatarSet);
		profile.setEnemy(enemy);
		profile.setInvention(invention);
		profile.setBio(bio);
		
		return avatarDao.updateAvatarProfile(profile);
	}

	@Override
	public List<AvatarProfile> getAvatarProfiles() {
		return avatarDao.getAvatarProfiles();
	}

	@Override
	public AvatarProfile getAvatarProfile(String id) {
		return avatarDao.getAvatarProfile(id);
	}

	@Override
	public AvatarProfile getAvatarProfileByName(String fname) {
		return avatarDao.getAvatarProfileByName(fname);
	}

	@Override
	public List<AvatarComponent> getAvatarComponentsBySet(AvatarSet set) {
		return avatarDao.getAvatarComponentsBySet(set);
	}

}
