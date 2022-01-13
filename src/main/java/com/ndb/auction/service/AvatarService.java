package com.ndb.auction.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.ndb.auction.exceptions.AvatarNotFoundException;
import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.Facts;
import com.ndb.auction.models.SkillSet;

@Service
public class AvatarService extends BaseService {

	// private AmazonS3 s3;
	// private final String bucketName = "auctionupload";


	public AvatarService(AmazonS3 s3) {
		// this.s3 = s3;
	}

	public AvatarComponent createAvatarComponent(String groupId, Integer tierLevel, Double price, Integer limited, String svg, int width, int top, int left) {
		AvatarComponent component = new AvatarComponent(groupId, tierLevel, price, limited, svg, width, top, left);
		
		AvatarComponent newComponent = avatarDao.createAvatarComponent(component);
		// String key = newComponent.getGroupId() + "-" + newComponent.getCompId();
		// if(!uploadFileS3(key, file)) {
		// 	throw new S3Exception("Cannot Upload Avatar File.", "file");
		// }
		
		return newComponent;
	}

	public List<AvatarComponent> getAvatarComponents() {
		List<AvatarComponent> components = avatarDao.getAvatarComponents();
		
		// for (AvatarComponent avatarComponent : components) {
		// 	String key = avatarComponent.getGroupId() + "-" + avatarComponent.getCompId();
		// 	String imageStr = downloadAvatarAccessories(key);
		// 	avatarComponent.setBase64Image(imageStr);
		// }
		return components;
	}

	public List<AvatarComponent> getAvatarComponentsById(String groupId) {
		List<AvatarComponent> components = avatarDao.getAvatarComponentsByGid(groupId);
		// for (AvatarComponent avatarComponent : components) {
		// 	String key = avatarComponent.getGroupId() + "-" + avatarComponent.getCompId();
		// 	String imageStr = downloadAvatarAccessories(key);
		// 	avatarComponent.setBase64Image(imageStr);
		// }
		return components;
	}

	public AvatarComponent getAvatarComponent(String groupId, String sKey) {
		AvatarComponent avatarComponent = avatarDao.getAvatarComponent(groupId, sKey);
		// String key = avatarComponent.getGroupId() + "-" + avatarComponent.getCompId();
		// String imageStr = downloadAvatarAccessories(key);
		// avatarComponent.setBase64Image(imageStr);
		return avatarComponent;
	}

	public AvatarComponent updateAvatar(String groupId, String compId, Integer tierLevel, Double price, Integer limited, String svg, int width, int top, int left) {
		AvatarComponent component = avatarDao.getAvatarComponent(groupId, compId);
		if(component == null) {
			throw new AvatarNotFoundException("Cannot find avatar component.", "compId");
		}
		price = price == null ? 0 : price;
		tierLevel = tierLevel == null ? 0 : tierLevel;
		limited = limited == null ? 0 : limited;
		component.setPrice(price);
		component.setTierLevel(tierLevel);
		component.setLimited(limited);
		component.setBase64Image(svg);
		component.setWidth(width);
		component.setTop(top);
		component.setLeft(left);
		
		return avatarDao.updateAvatarComponent(component);
	}

	public AvatarProfile createAvatarProfile(String name, String surname, String shortName,
			List<SkillSet> skillSet, List<AvatarSet> avatarSet, List<Facts> factSet, String hairColor, String details) {
		
		// check condition
		AvatarProfile profile = avatarDao.getAvatarProfileByName(name);
		if(profile != null) {
			throw new AvatarNotFoundException("Already exists with '" + name + "'", "name");
		}
		
		profile = new AvatarProfile(name, surname, shortName, skillSet, avatarSet, factSet, hairColor, details);
		return avatarDao.createAvatarProfile(profile);
	}

	public AvatarProfile updateAvatarProfile(
			String id, 
			String name, 
			String surname, 
			String shortName,
			List<SkillSet> skillSet, 
			List<AvatarSet> avatarSet, 
			List<Facts> factSet,
			String hairColor,
			String details) 
	{
		AvatarProfile profile = avatarDao.getAvatarProfile(id);
		if(profile == null) {
			throw new AvatarNotFoundException("Cannot find avatar profile.", "id");
		}
		profile.setName(name);
		profile.setSurname(surname);
		profile.setShortName(shortName);
		profile.setSkillSet(skillSet);
		profile.setAvatarSet(avatarSet);
		profile.setFactsSet(factSet);
		profile.setHairColor(hairColor);
		profile.setDetails(details);
		return avatarDao.updateAvatarProfile(profile);
	}

	public List<AvatarProfile> getAvatarProfiles() {
		return avatarDao.getAvatarProfiles();
	}

	@PreAuthorize("isAuthenticated()")
	public AvatarProfile getAvatarProfile(String id) {
		return avatarDao.getAvatarProfile(id);
	}

	@PreAuthorize("isAuthenticated()")
	public AvatarProfile getAvatarProfileByName(String fname) {
		return avatarDao.getAvatarProfileByName(fname);
	}

	@PreAuthorize("isAuthenticated()")
	public List<AvatarComponent> getAvatarComponentsBySet(List<AvatarSet> set) {
		return avatarDao.getAvatarComponentsBySet(set);
	}

	// private boolean deleteS3Object(String key) {
	// 	try {
	// 		s3.deleteObject(bucketName, key);
	// 	} catch (Exception e) {
	// 		return false;
	// 	}
	// 	return true;
	// }

	// private String downloadAvatarAccessories(String key) {
	// 	S3Object s3object = null;
	// 	try {
	// 		s3object = s3.getObject(bucketName, key);
	// 	} catch (Exception e) {
	// 		return "";
	// 	}
	// 	InputStream finput = s3object.getObjectContent();
	// 	long length = s3object.getObjectMetadata().getContentLength();
	// 	byte[] imageBytes = new byte[(int)length];
	// 	try {
	// 		finput.read(imageBytes, 0, imageBytes.length);
	// 		finput.close();
	// 	} catch (IOException e) {
	// 		e.printStackTrace();
	// 		return "";
	// 	}
	// 	return Base64.encodeBase64String(imageBytes);
	// }

	// private boolean uploadFileS3(String key, Part file) {
	// 	// upload avatar component into Amazon S3 bucket!
	// 	InputStream content;
	// 	try {
	// 		content = file.getInputStream();
	// 		ObjectMetadata metadata = new ObjectMetadata();
	// 		metadata.setContentLength(file.getSize());
	// 		s3.putObject(bucketName, key, content, metadata);
	// 	} catch (IOException e) {
	// 		e.printStackTrace();
	// 		return false;
	// 	}
	// 	return true;
	// }

}
