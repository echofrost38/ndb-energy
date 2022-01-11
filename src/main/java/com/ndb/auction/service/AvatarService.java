package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.exceptions.AvatarNotFoundException;
import com.ndb.auction.models.SkillSet;
import com.ndb.auction.models.avatar.AvatarComponent;
import com.ndb.auction.models.avatar.AvatarFacts;
import com.ndb.auction.models.avatar.AvatarProfile;
import com.ndb.auction.models.avatar.AvatarSet;

import org.springframework.stereotype.Service;

@Service
public class AvatarService extends BaseService {

	public AvatarComponent createAvatarComponent(String groupId, Integer tierLevel, Long price, Integer limited, String svg, int width, int top, int left) {
		price = price == null ? 0 : price;
		AvatarComponent component = new AvatarComponent(groupId, tierLevel, price, limited, svg, width, top, left);
		AvatarComponent newComponent = avatarComponentDao.createAvatarComponent(component);
		return newComponent;
	}

	public List<AvatarComponent> getAvatarComponents() {
		return avatarComponentDao.getAvatarComponents();
	}

	public List<AvatarComponent> getAvatarComponentsByGroupId(String groupId) {
		return avatarComponentDao.getAvatarComponentsByGid(groupId);
	}

	public AvatarComponent getAvatarComponent(String groupId, int compId) {
		return avatarComponentDao.getAvatarComponent(groupId, compId);
	}

	public AvatarComponent updateAvatarComponent(String groupId, int compId, Integer tierLevel, Long price, Integer limited, String svg, int width, int top, int left) {
		AvatarComponent component = avatarComponentDao.getAvatarComponent(groupId, compId);
		if (component == null) {
			throw new AvatarNotFoundException("Cannot find avatar component.", "compId");
		}
		tierLevel = tierLevel == null ? 0 : tierLevel;
		limited = limited == null ? 0 : limited;
		price = price == null ? 0 : price;
		component.setPrice(price);
		component.setTierLevel(tierLevel);
		component.setLimited(limited);
		component.setSvg(svg);
		component.setWidth(width);
		component.setTop(top);
		component.setLeft(left);
		
		return avatarComponentDao.updateAvatarComponent(component);
	}

	public AvatarProfile createAvatarProfile(String name, String surname, String shortName,
			List<SkillSet> skillSet, List<AvatarSet> avatarSet, List<AvatarFacts> factsSet, String hairColor, String details) {
		
		// check condition
		AvatarProfile profile = avatarProfileDao.getAvatarProfileByName(name);
		if (profile != null) {
			throw new AvatarNotFoundException("Already exists with '" + name + "'", "name");
		}
		
		profile = new AvatarProfile(name, surname, shortName, skillSet, avatarSet, hairColor, factsSet, details);
		profile = avatarProfileDao.createAvatarProfile(profile);
		int profileId = profile.getId();

		for (SkillSet skill : skillSet) {
			skill.setId(profileId);
			avatarSkillDao.insert(skill);
		}
		profile.setSkillSet(skillSet);

		for (AvatarFacts avatarFacts : factsSet) {
			avatarFacts.setProfileId(profileId);
			avatarFactDao.insert(avatarFacts);
		}
		profile.setFactsSet(factsSet);

		for (AvatarSet _avatarSet : avatarSet) {
			_avatarSet.setId(profileId);
			avatarSetDao.insert(_avatarSet);
		}
		profile.setAvatarSet(avatarSet);
		return profile;
	}

	public Boolean updateAvatarProfile(
			int id,
			String name,
			String surname,
			String shortName,
			List<SkillSet> skillSet, 
			List<AvatarSet> avatarSet, 
			List<AvatarFacts> factSet,
			String hairColor,
			String details) 
	{
		AvatarProfile profile = avatarProfileDao.getAvatarProfile(id);
		if (profile == null) {
			throw new AvatarNotFoundException("Cannot find avatar profile.", "id");
		}
		profile.setFname(name);
		profile.setSurname(surname);
		profile.setShortName(shortName);
		profile.setSkillSet(skillSet);
		profile.setAvatarSet(avatarSet);
		profile.setDetails(details);
		profile.setHairColor(hairColor);
		
		avatarProfileDao.updateAvatarProfile(profile);
		avatarFactDao.update(id, factSet);
		avatarSkillDao.update(id, skillSet);
		avatarSetDao.update(id, avatarSet);

		return true;
	}

	public List<AvatarProfile> getAvatarProfiles() {
		List<AvatarProfile> profileList = avatarProfileDao.getAvatarProfiles();

		for (AvatarProfile profile : profileList) {
			profile.setFactsSet(avatarFactDao.selectByProfileId(profile.getId()));
			profile.setSkillSet(avatarSkillDao.selectById(profile.getId()));
			profile.setAvatarSet(avatarSetDao.selectById(profile.getId()));
		}
		
		return profileList;
	}

	public AvatarProfile getAvatarProfile(int id) {
		AvatarProfile profile = avatarProfileDao.getAvatarProfile(id);
		profile.setFactsSet(avatarFactDao.selectByProfileId(profile.getId()));
		profile.setSkillSet(avatarSkillDao.selectById(profile.getId()));
		profile.setAvatarSet(avatarSetDao.selectById(profile.getId()));
		return profile;
	}

	public AvatarProfile getAvatarProfileByName(String fname) {
		AvatarProfile profile = avatarProfileDao.getAvatarProfileByName(fname);
		profile.setFactsSet(avatarFactDao.selectByProfileId(profile.getId()));
		profile.setSkillSet(avatarSkillDao.selectById(profile.getId()));
		profile.setAvatarSet(avatarSetDao.selectById(profile.getId()));
		return profile;
	}

	public List<AvatarComponent> getAvatarComponentsBySet(List<AvatarSet> set) {
		return avatarComponentDao.getAvatarComponentsBySet(set);
	}

	public List<AvatarComponent> getAvatarComponentsById(String groupId) {
		return avatarComponentDao.getAvatarComponentsByGid(groupId);
	}

}
