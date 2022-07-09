package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.avatar.AvatarComponent;
import com.ndb.auction.models.avatar.AvatarProfile;
import com.ndb.auction.models.avatar.AvatarSet;
import com.ndb.auction.models.balance.CryptoBalance;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserAvatar;

import org.springframework.stereotype.Service;

/**
 * 1. get market data
 * 2. get avatar ? / set avatar!!!!!
 * 3. Airdrop where to go???????????
 * 
 * @author klinux
 *
 */

@Service
public class ProfileService extends BaseService {

	// actually return user it self!!!!
	public User getUserProfile(int userId) {
		return userDao.selectById(userId);
	}

	public List<Notification> getNotifications(int userId) {
		return notificationDao.getNotificationsByUser(userId);
	}

	public Integer getNotifySetting(int userId) {
		User user = userDao.selectById(userId);
		if (user == null) {
			String msg = messageSource.getMessage("no_user", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "user");
		}

		return user.getNotifySetting();
	}

	public int updateNotifySetting(int userId, int setting) {
		if (userDao.updateNotifySetting(userId, setting) < 1)
		{
			String msg = messageSource.getMessage("no_user", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "user");
		}
		return setting;
	}

	public Integer changePassword(int userId, String password) {

		return null;
	}

	public List<Bid> getBidActivity(int userId) {
		return bidDao.getBidListByUser(userId);
	}

	/**
	 * prefix means avatar first name!
	 * once user select avatar with first name, user will have owned components
	 */
	@SuppressWarnings("unchecked")
	public String setAvatar(int id, String prefix, String name) {
		// check user exists
		UserAvatar userAvatar = userAvatarDao.selectByPrefixAndName(prefix, name);
		if (userAvatar != null) {
			String msg = messageSource.getMessage("no_avatar", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "avatar");
		}

		User user = userDao.selectById(id);
		if (user == null) {
			String msg = messageSource.getMessage("no_user", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "user");
		}

		userAvatar = userAvatarDao.selectById(id);
		if (userAvatar == null) {
			userAvatar = new UserAvatar();
			userAvatar.setId(id);
		}

		// update purchase list and user avatar set!!
		AvatarProfile profile = avatarProfileDao.getAvatarProfileByName(prefix);

		if (profile == null) {
			String msg = messageSource.getMessage("no_avatar", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "avatar");
		}

		List<AvatarSet> sets = avatarSetDao.selectById(profile.getId());
		List<AvatarComponent> components = avatarComponentDao.getAvatarComponentsBySet(sets);


		Map<String, List<Integer>> purchasedMap = gson.fromJson(userAvatar.getPurchased(), Map.class);

		if(purchasedMap == null) {
			purchasedMap = new HashMap<>();
		}

		for (AvatarComponent component : components) {
			String groupId = component.getGroupId();
			int compId = component.getCompId();
			List<Integer> purchasedList = purchasedMap.get(groupId);
			if(purchasedList == null) {
				purchasedList = new ArrayList<>();
				purchasedList.add(compId);
				purchasedMap.put(groupId, purchasedList);
			} else {
				if(!purchasedList.contains(compId)) {
					purchasedList.add(compId);
				}
			}
		}
		
		userAvatar.setSelected(gson.toJson(sets));
		userAvatar.setPurchased(gson.toJson(purchasedMap));
		userAvatar.setPrefix(prefix);
		userAvatar.setName(name);
		userAvatar.setHairColor(profile.getHairColor());
		userAvatar.setSkinColor(profile.getSkinColor());
		userAvatarDao.insertOrUpdate(userAvatar);
		return "Success";
	}

	@SuppressWarnings("unchecked")
	public List<AvatarSet> updateAvatarSet(int userId, List<AvatarSet> set, String hairColor) {
		User user = userDao.selectById(userId);
		if (user == null) {
			String msg = messageSource.getMessage("no_user", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "user");
		}

		UserAvatar userAvatar = userAvatarDao.selectById(userId);
		if (userAvatar == null) {
			userAvatar = new UserAvatar();
			userAvatar.setId(userId);
		}
		double totalPrice = 0;
		double price = 0;
		String groupId = "";
		int compId = 0;
		List<AvatarComponent> purchasedComponents = new ArrayList<>();

		Map<String, List<Integer>> purchasedMap = gson.fromJson(userAvatar.getPurchased(), Map.class);
		
		// processing for each components
		for (AvatarSet avatarSet : set) {
			groupId = avatarSet.getGroupId();
			compId = avatarSet.getCompId();
			AvatarComponent component = avatarComponentDao.getAvatarComponent(groupId, compId);
			if (component == null) {
				String msg = messageSource.getMessage("no_avatar_component", null, Locale.ENGLISH);
            	throw new UnauthorizedException(msg, "avatar");
			}

			// check purchased
			List<Integer> purchaseList = purchasedMap.get(groupId);
			if (purchaseList == null) {
				purchaseList = new ArrayList<>();
				purchasedMap.put(String.valueOf(groupId), purchaseList);
			}

			if (purchaseList.contains(compId)) {
				continue;
			}

			// check remained
			if (component.getLimited() != 0 && component.getLimited() <= component.getPurchased()) {
				String msg = messageSource.getMessage("no_component_left", null, Locale.ENGLISH);
            	throw new UnauthorizedException(msg, "avatar");
			}

			// check free
			price = component.getPrice();
			if (price == 0) {
				purchaseList.add(compId);
				continue;
			}

			totalPrice += component.getPrice();
			component.increasePurchase();
			purchaseList.add(component.getCompId());
			purchasedComponents.add(component);
		}

		// check user's NDB wallet
		int tokenId = tokenAssetService.getTokenIdBySymbol("NDB");
		CryptoBalance ndbBalance = balanceDao.selectById(userId, tokenId);
		if(ndbBalance == null) {
			ndbBalance = new CryptoBalance(userId, tokenId);
			balanceDao.insert(ndbBalance);
		}

		double balance = ndbBalance.getFree();
		if (balance < totalPrice) {
			String msg = messageSource.getMessage("insufficient", null, Locale.ENGLISH);
			throw new UnauthorizedException(msg, "balance");
		}

		// update internal NDB balance
		ndbBalance.setFree(balance - totalPrice);
		balanceDao.update(ndbBalance);

		userAvatar.setPurchased(gson.toJson(purchasedMap));
		userAvatar.setSelected(gson.toJson(set));
		userAvatar.setHairColor(hairColor);
		
		userAvatarDao.insertOrUpdate(userAvatar);

		for (AvatarComponent avatarComponent : purchasedComponents) {
			avatarComponentDao.updateAvatarComponent(avatarComponent);
		}

		return set;
	}

	public int updateEmail(int userId, String email) {
		return userDao.updateEmail(userId, email);
	}

	public int updateBuyName(int userId, String newName) {
		// get user avatar
		UserAvatar avatar = userAvatarDao.selectById(userId);

		// get prefix 
		String prefix = avatar.getPrefix();

		UserAvatar checkAvatar = userAvatarDao.selectByPrefixAndName(prefix, newName);
		if(checkAvatar != null) {
			String msg = messageSource.getMessage("avatar_name_conflict", null, Locale.ENGLISH);
			throw new UnauthorizedException(msg, "name");
		}

		return userAvatarDao.updateName(userId, newName);
	}

}
