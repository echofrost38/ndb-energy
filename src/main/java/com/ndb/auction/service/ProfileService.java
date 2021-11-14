package com.ndb.auction.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.User;
import com.ndb.auction.models.user.Wallet;
import com.ndb.auction.service.interfaces.IProfileService;

/**
 * 1. get market data
 * 2. get avatar ? / set avatar!!!!!
 * 3. Airdrop where to go???????????
 * @author klinux
 *
 */

@Service
public class ProfileService extends BaseService implements IProfileService {

	// actually return user it self!!!!
	@Override
	public User getUserProfile(String userId) {
		return userDao.getUserById(userId);
	}

	@Override
	public List<Notification> getNotifications(String userId) {
		return notificationDao.getNotificationsByUser(userId);
	}

	@Override
	public Integer getNotifySetting(String userId) {
		User user = userDao.getUserById(userId);
		if(user == null) {
			return null; // or exception 404
		}
	
		return user.getNotifySetting();
	}

	@Override
	public Integer updateNotifySetting(String userId, Integer setting) {
		User user = userDao.getUserById(userId);
		if(user == null) {
			return null; // or exception 404
		}		
		user.setNotifySetting(setting);
		userDao.updateUser(user);
		return setting;
	}

	@Override
	public Integer changePassword(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Bid> getBidActivity(String userId) {
		return bidDao.getBidListByUser(userId);
	}

	/**
	 * prefix means avatar first name!
	 * once user select avatar with first name, user will have owned components 
	 */
	@Override
	public String setAvatar(String id, String prefix, String name) {
		// check user exists
		User user = userDao.getUserByAvatar(prefix, name);
		if(user != null) {
			return "Already exists";
		}

		user = userDao.getUserById(id);
		if(user == null) {
			return "Not found user";
		}
		
		// update purchase list and user avatar set!!
		AvatarProfile profile = avatarDao.getAvatarProfileByName(prefix);
		AvatarSet sets = profile.getAvatarSet();
		List<AvatarComponent> components = avatarDao.getAvatarComponentsBySet(sets);
		Map<String, List<String>> purchasedMap = user.getAvatarPurchase();
		for (AvatarComponent comp : components) {
			String group = comp.getGroupId();
			List<String> purchased = purchasedMap.get(group);
			purchased.add(comp.getCompId());
			purchasedMap.replace(group, purchased);
		}
		user.setAvatarPurchase(purchasedMap);
		user.setAvatar(sets);
		user.setAvatarPrefix(prefix);
		user.setAvatarName(name);
		userDao.updateUser(user);
		return "Success";
	}

	@Override
	public AvatarSet updateAvatarSet(String userId, AvatarSet set) {
		User user = userDao.getUserById(userId);
		if(user == null) {
			return null;
		}
		double totalPrice = 0.0;
		double price = 0.0;
		String groupId = "";
		String compId = "";
		List<AvatarComponent> purchasedComponents = new ArrayList<AvatarComponent>();

		Field[] fields = AvatarSet.class.getDeclaredFields();
		for (Field field : fields) {
			groupId = field.getName();
			try {
				compId = (String) field.get(set);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}

			AvatarComponent component = avatarDao.getAvatarComponent(groupId, compId);
			if(component == null) {
				return null;
			}

			// check free
			price = component.getPrice();
			if(price == 0) {
				continue;
			}

			// check purchased
			List<String> purchaseList = user.getAvatarPurchase().get(groupId);
			if(purchaseList.contains(compId)) {
				continue;
			}

			// check remained
			if(component.getLimited() <= component.getPurchased()) {
				return null;
			}

			totalPrice += component.getPrice();
			component.increasePurchase();
			purchasedComponents.add(component);
		}

		// check user's NDB wallet 
		Map<String, Wallet> tempWallet = user.getWallet();
		Wallet ndbWallet = tempWallet.get("NDB");
		double balance = ndbWallet.getFree();
		if(balance > totalPrice) {
			return null;
		}
		ndbWallet.setFree(balance - totalPrice);
		tempWallet.replace("NDB", ndbWallet);
		user.setWallet(tempWallet);

		user.setAvatar(set);
		userDao.updateUser(user);

		// update component purchased ?
		avatarDao.updateAvatarComponents(purchasedComponents);

		return set;
	}

	@Override
	public String purchaseComponent(List<AvatarComponent> components) {
		
		return null;
	}

}
