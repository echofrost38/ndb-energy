package com.ndb.auction.service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.exceptions.S3Exception;
import com.ndb.auction.models.SkillSet;
import com.ndb.auction.models.avatar.AvatarComponent;
import com.ndb.auction.models.avatar.AvatarFacts;
import com.ndb.auction.models.avatar.AvatarProfile;
import com.ndb.auction.models.avatar.AvatarSet;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

@Service
public class AvatarService extends BaseService {

	private final AmazonS3 s3;

	private static final String bucketName = "nyyu-avatars";

	public AvatarService (AmazonS3 s3) {
		this.s3 = s3;
	}

	private String buildAvatarUrl(String groupId, int compId) {
		return String.format("avatar_comp_%s_%d", groupId, compId);
	}

	public AvatarComponent createAvatarComponent(String groupId, Integer tierLevel, Double price, Integer limited, String svg, int width, int top, int left) {
		price = price == null ? 0 : price;
		AvatarComponent component = new AvatarComponent(groupId, tierLevel, price, limited, svg, width, top, left);
		AvatarComponent newComponent = avatarComponentDao.createAvatarComponent(component);

		// upload into aws s3 bucket
		try {
			// generate input stream from svg string
			String avatarUrl = buildAvatarUrl(groupId, newComponent.getCompId());
			InputStream input = IOUtils.toInputStream(svg, StandardCharsets.UTF_8);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(svg.length());
			s3.putObject(bucketName, avatarUrl, input, metadata);
		} catch (Exception e) {
			// couldn't upload svg into s3
			e.printStackTrace();
			throw new S3Exception("Couldn't upload avatar component", "svg");
		}
		return newComponent;
	}

	public List<AvatarComponent> getAvatarComponents() {
		var components = avatarComponentDao.getAvatarComponents();

		// download svg from s3
		for (var component : components) {
			try {
				String avatarUrl = buildAvatarUrl(component.getGroupId(), component.getCompId());
				var svgObject = s3.getObject(bucketName, avatarUrl);
				var svgStream = svgObject.getObjectContent();
				var svg = new String(svgStream.readAllBytes(), StandardCharsets.UTF_8);
				component.setSvg(svg);
			} catch (Exception e) {
				continue;
			}
		}
		return components;
	}

	public List<AvatarComponent> getAvatarComponentsByGroupId(String groupId) {
		var components = avatarComponentDao.getAvatarComponentsByGid(groupId);
		// download svg from s3
		for (var component : components) {
			try {
				String avatarUrl = buildAvatarUrl(component.getGroupId(), component.getCompId());
				var svgObject = s3.getObject(bucketName, avatarUrl);
				var svgStream = svgObject.getObjectContent();
				var svg = new String(svgStream.readAllBytes(), StandardCharsets.UTF_8);
				component.setSvg(svg);
			} catch (Exception e) {
				continue;
			}
		}
		return components;
	}

	public AvatarComponent getAvatarComponent(String groupId, int compId) {
		var component = avatarComponentDao.getAvatarComponent(groupId, compId);
		try {
			String avatarUrl = buildAvatarUrl(component.getGroupId(), component.getCompId());
			var svgObject = s3.getObject(bucketName, avatarUrl);
			var svgStream = svgObject.getObjectContent();
			var svg = new String(svgStream.readAllBytes(), StandardCharsets.UTF_8);
			component.setSvg(svg);
		} catch (Exception e) {
			e.printStackTrace();
			throw new S3Exception("cannot download svg from s3", "svg");
		}
		return component;
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

		// upload into aws s3 bucket
		try {
			// generate input stream from svg string
			String avatarUrl = buildAvatarUrl(groupId, compId);
			InputStream input = IOUtils.toInputStream(svg, StandardCharsets.UTF_8);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(svg.length());
			s3.putObject(bucketName, avatarUrl, input, metadata);
		} catch (Exception e) {
			// couldn't upload svg into s3
			e.printStackTrace();
			throw new S3Exception("Couldn't upload avatar component", "svg");
		}
		
		return avatarComponentDao.updateAvatarComponent(component);
	}

	public AvatarProfile createAvatarProfile(String name, String surname, List<SkillSet> skillSet, List<AvatarSet> avatarSet, List<AvatarFacts> factsSet, String hairColor, String skinColor, String details) {
		
		// check condition
		AvatarProfile profile = avatarProfileDao.getAvatarProfileByName(surname);
		if (profile != null) {
			String msg = messageSource.getMessage("no_avatar", null, Locale.ENGLISH);
            throw new AuctionException(msg, "avatar");
		}
		
		profile = new AvatarProfile(name, surname, skillSet, avatarSet, hairColor, skinColor, factsSet, details);
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
			String skinColor,
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
		if(skinColor != null) profile.setSkinColor(skinColor);
		
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
		var components = avatarComponentDao.getAvatarComponentsBySet(set);
		// download svg from s3
		for (var component : components) {
			try {
				String avatarUrl = buildAvatarUrl(component.getGroupId(), component.getCompId());
				var svgObject = s3.getObject(bucketName, avatarUrl);
				var svgStream = svgObject.getObjectContent();
				var svg = new String(svgStream.readAllBytes(), StandardCharsets.UTF_8);
				component.setSvg(svg);
			} catch (Exception e) {
				continue;
			}
		}
		return components;
	}

	public List<AvatarComponent> getAvatarComponentsById(String groupId) {
		var components = avatarComponentDao.getAvatarComponentsByGid(groupId);
		// download svg from s3
		for (var component : components) {
			try {
				String avatarUrl = buildAvatarUrl(component.getGroupId(), component.getCompId());
				var svgObject = s3.getObject(bucketName, avatarUrl);
				var svgStream = svgObject.getObjectContent();
				var svg = new String(svgStream.readAllBytes(), StandardCharsets.UTF_8);
				component.setSvg(svg);
			} catch (Exception e) {
				continue;
			}
		}
		return components;
	}

	public List<AvatarSet> getAvatarSetById(int profileId) {
		return avatarSetDao.selectById(profileId);
	}

	public int deleteAvatarComponent(String groupId, int compId) {
		return avatarComponentDao.deleteById(groupId, compId);
	}

}
