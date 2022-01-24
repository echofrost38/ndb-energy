package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ndb.auction.exceptions.AvatarNotFoundException;
import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.InternalBalance;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.avatar.AvatarComponent;
import com.ndb.auction.models.avatar.AvatarProfile;
import com.ndb.auction.models.avatar.AvatarSet;
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
			throw new UserNotFoundException("We were unable to find a user id", "userId");
		}

		return user.getNotifySetting();
	}

	public int updateNotifySetting(int userId, int setting) {
		if (userDao.updateNotifySetting(userId, setting) < 1)
			throw new UserNotFoundException("We were unable to find a user id", "userId");
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
	public String setAvatar(int id, String prefix, String name) {
		// check user exists
		UserAvatar userAvatar = userAvatarDao.selectByPrefixAndName(prefix, name);
		if (userAvatar != null) {
			return "Already exists";
		}

		User user = userDao.selectById(id);
		if (user == null) {
			throw new UserNotFoundException("We were unable to find a user with avatar", "name");
		}

		userAvatar = userAvatarDao.selectById(id);
		if (userAvatar == null) {
			userAvatar = new UserAvatar();
			userAvatar.setId(id);
		}

		// update purchase list and user avatar set!!
		AvatarProfile profile = avatarProfileDao.getAvatarProfileByName(prefix);

		if (profile == null) {
			throw new AvatarNotFoundException("There is not avatar: [" + prefix + "]", "prefix", 0);
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
		userAvatarDao.insertOrUpdate(userAvatar);
		return "Success";
	}

	public List<AvatarSet> updateAvatarSet(int userId, List<AvatarSet> set) {
		User user = userDao.selectById(userId);
		if (user == null) {
			throw new UserNotFoundException("We were unable to find a user id", "userId");
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
				throw new AvatarNotFoundException("Cannot find avatar component.", "compId", 0);
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
				throw new BidException("Avatar Components are sold out.","set");
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
		InternalBalance ndbBalance = balanceDao.selectById(userId, tokenId);
		if(ndbBalance == null) {
			ndbBalance = new InternalBalance(userId, tokenId);
			balanceDao.insert(ndbBalance);
		}

		double balance = ndbBalance.getFree();
		if (balance < totalPrice) {
			throw new BidException("You don't have enough balance in wallet.", "set");
		}

		// update internal NDB balance
		ndbBalance.setFree(balance - totalPrice);
		balanceDao.update(ndbBalance);

		userAvatar.setPurchased(gson.toJson(purchasedMap));
		userAvatar.setSelected(gson.toJson(set));
		userAvatarDao.insertOrUpdate(userAvatar);

		for (AvatarComponent avatarComponent : purchasedComponents) {
			avatarComponentDao.updateAvatarComponent(avatarComponent);
		}

		return set;
	}

}
