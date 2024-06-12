package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ndb.auction.exceptions.AvatarNotFoundException;
import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.Wallet;
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
		}

		// update purchase list and user avatar set!!
		AvatarProfile profile = avatarDao.getAvatarProfileByName(prefix);

		if (profile == null) {
			throw new AvatarNotFoundException("There is not avatar: [" + prefix + "]", "prefix");
		}

		List<AvatarSet> sets = profile.getAvatarSet();
		List<AvatarComponent> components = avatarDao.getAvatarComponentsBySet(sets);

		String purchasedJsonString = userAvatar.getPurchased();
		JsonObject purchasedJson = purchasedJsonString == null ? new JsonObject()
				: JsonParser.parseString(purchasedJsonString).getAsJsonObject();
		for (AvatarComponent component : components) {
			String group =String.valueOf(component.getGroupId());
			JsonElement el = purchasedJson.get(group);
			JsonArray array;
			if (el == null || el.isJsonNull())
				array = new JsonArray();
			else
				array = el.getAsJsonArray();
			array.add(component.getCompId());
			purchasedJson.add(group, array);
		}
		userAvatar.setPurchased(purchasedJson.toString());
		userAvatar.setSelected(gson.toJson(sets));
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
		}
		long totalPrice = 0;
		long price = 0;
		int groupId = 0;
		int compId = 0;
		List<AvatarComponent> purchasedComponents = new ArrayList<>();

		Map<String, List<String>> purchasedMap = gson.fromJson(userAvatar.getPurchased(), Map.class);
		for (AvatarSet avatarSet : set) {
			groupId = avatarSet.getGroupId();
			compId = avatarSet.getCompId();
			AvatarComponent component = avatarDao.getAvatarComponent(groupId, compId);
			if (component == null) {
				throw new AvatarNotFoundException("Cannot find avatar component.", "compId");
			}

			// check free
			price = component.getPrice();
			if (price == 0) {
				continue;
			}

			// check purchased
			List<String> purchaseList = purchasedMap.get(groupId);
			if (purchaseList == null) {
				purchaseList = new ArrayList<>();
				purchasedMap.put(String.valueOf(groupId), purchaseList);
			}

			if (purchaseList.contains(compId)) {
				continue;
			}

			// check remained
			if (component.getLimited() <= component.getPurchased()) {
				return null;
			}

			totalPrice += component.getPrice();
			component.increasePurchase();
			purchaseList.add(component.getCompId());
			purchasedComponents.add(component);
		}

		// check user's NDB wallet
		Wallet ndbWallet = userWalletService.getWalletById(userId, "NDB");
		long balance = ndbWallet.getFree();
		if (balance < totalPrice) {
			throw new BidException("You don't have enough balance in wallet.", "set");
		}
		ndbWallet.setFree(balance - totalPrice);
		userAvatar.setPurchased(gson.toJson(purchasedMap));
		userAvatar.setSelected(gson.toJson(set));

		// update component purchased ?
		avatarDao.updateAvatarComponents(purchasedComponents);

		return set;
	}

	public String purchaseComponent(List<AvatarComponent> components) {

		return null;
	}

}
