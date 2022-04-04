package com.ndb.auction.service;

import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.models.SkillSet;
import com.ndb.auction.models.avatar.AvatarComponent;
import com.ndb.auction.models.avatar.AvatarFacts;
import com.ndb.auction.models.avatar.AvatarProfile;
import com.ndb.auction.models.avatar.AvatarSet;

import org.springframework.stereotype.Service;

@Service
public class AvatarService extends BaseService {

	public AvatarComponent createAvatarComponent(String groupId, Integer tierLevel, Double price, Integer limited, String svg, int width, int top, int left) {
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

	public AvatarComponent updateAvatarComponent(String groupId, int compId, Integer tierLevel, Double price, Integer limited, String svg, int width, int top, int left) {
		AvatarComponent component = avatarComponentDao.getAvatarComponent(groupId, compId);
		if (component == null) {
			String msg = messageSource.getMessage("no_avatar_component", null, Locale.ENGLISH);
            throw new AuctionException(msg, "avatar");
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

	public AvatarProfile createAvatarProfile(String name, String surname, List<SkillSet> skillSet, List<AvatarSet> avatarSet, List<AvatarFacts> factsSet, String hairColor, String details) {
		
		// check condition
		AvatarProfile profile = avatarProfileDao.getAvatarProfileByName(surname);
		if (profile != null) {
			String msg = messageSource.getMessage("no_avatar", null, Locale.ENGLISH);
            throw new AuctionException(msg, "avatar");
		}
		
		profile = new AvatarProfile(name, surname, skillSet, avatarSet, hairColor, factsSet, details);
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
			String fname,
			String surname,
			List<SkillSet> skillSet, 
			List<AvatarSet> avatarSet, 
			List<AvatarFacts> factSet,
			String hairColor,
			String details) 
	{
		AvatarProfile profile = avatarProfileDao.getAvatarProfile(id);
		if (profile == null) {
			String msg = messageSource.getMessage("no_avatar", null, Locale.ENGLISH);
            throw new AuctionException(msg, "avatar");
		}
		if(fname != null) profile.setFname(fname);
		if(surname != null) profile.setSurname(surname);
		if(details != null) profile.setDetails(details);
		if(hairColor != null) profile.setHairColor(hairColor);
		
		avatarProfileDao.updateAvatarProfile(profile);
		if((skillSet != null) && (skillSet.size() != 0)) avatarSkillDao.update(id, skillSet);
		if((factSet != null) && (factSet.size() != 0)) avatarFactDao.update(id, factSet);
		if((avatarSet != null) && (avatarSet.size() != 0)) avatarSetDao.update(id, avatarSet);
		
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

	public AvatarProfile getAvatarProfileByName(String sname) {
		AvatarProfile profile = avatarProfileDao.getAvatarProfileByName(sname);
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

	public List<AvatarSet> getAvatarSetById(int profileId) {
		return avatarSetDao.selectById(profileId);
	}

}
