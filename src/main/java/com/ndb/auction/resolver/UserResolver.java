package com.ndb.auction.resolver;

import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.GeoLocation;
import com.ndb.auction.models.avatar.AvatarComponent;
import com.ndb.auction.models.avatar.AvatarProfile;
import com.ndb.auction.models.avatar.AvatarSet;
import com.ndb.auction.models.transactions.Statement;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserAvatar;
import com.ndb.auction.models.user.UserVerify;
import com.ndb.auction.service.FinancialService;
import com.ndb.auction.service.user.UserAuthService;
import com.ndb.auction.service.user.UserDetailsImpl;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
	private FinancialService financialService;

    @PreAuthorize("isAuthenticated()")
    public User getUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int id = userDetails.getId();
        return userService.getUserById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User getUserById(int id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("isAuthenticated()")
    public String changePassword(String newPassword) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int id = userDetails.getId();
        return userAuthService.changePassword(id, newPassword);
    }

    @PreAuthorize("isAuthenticated()")
    public String requestEmailChange() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int id = userDetails.getId();
        return userService.requestEmailChange(id);
    }

    @PreAuthorize("isAuthenticated()")
    public String confirmEmailChange(String code, String newEmail) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int id = userDetails.getId();
        return userService.confirmEmailChange(id,code, newEmail);
    }

    @PreAuthorize("isAuthenticated()")
    public String changeName(String newName) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int id = userDetails.getId();
        return userService.changeName(id,newName);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public GeoLocation addDisallowed(String country, String countryCode) {
        return userService.addDisallowed(country, countryCode);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<GeoLocation> getDisallowed() {
        return userService.getDisallowed();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int makeAllow(int locationId) {
        return userService.makeAllow(locationId);
    }

    // user management by admin
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String resetPasswordByAdmin(String email) {
        return userService.resetPasswordByAdmin(email);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createNewUser(String email, String country, String role, String avatarName, String shortName) {

        User user = userService.getUserByEmail(email);
        if (user != null) {
            String msg = messageSource.getMessage("email_exists", null, Locale.ENGLISH);
            throw new UserNotFoundException(msg, "email");
        }

        String rPassword = userService.getRandomPassword(10);
        String encoded = userService.encodePassword(rPassword);
        user = new User(email, encoded, country.toUpperCase());

        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setPrefix(avatarName);
        userAvatar.setName(shortName);

        // processing purchased map
        AvatarProfile profile = avatarService.getAvatarProfileByName(avatarName);
        List<AvatarSet> sets = avatarService.getAvatarSetById(profile.getId());
        List<AvatarComponent> components = avatarService.getAvatarComponentsBySet(sets);

        Map<String, List<Integer>> purchasedMap = new HashMap<>();
        for (AvatarComponent component : components) {
            String groupId = component.getGroupId();
            int compId = component.getCompId();
            List<Integer> purchasedList = purchasedMap.get(groupId);
            if (purchasedList == null) {
                purchasedList = new ArrayList<>();
                purchasedList.add(compId);
                purchasedMap.put(groupId, purchasedList);
            } else {
                if (!purchasedList.contains(compId)) {
                    purchasedList.add(compId);
                }
            }
        }
        userAvatar.setPurchased(gson.toJson(purchasedMap));
        userAvatar.setSelected(gson.toJson(sets));

        user.setAvatar(userAvatar);

        UserVerify userVerify = new UserVerify();
        userVerify.setEmailVerified(true);
        user.setVerify(userVerify);

        // check role
        if (role.equals("ROLE_USER")) {
            user.addRole(role);
        } else if (role.equals("ROLE_ADMIN")) {
            user.addRole(role);
            user.addRole("ROLE_USER");
        }
        return userService.createNewUser(user, rPassword);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String changeRole(String email, String role) {
        return userService.changeRole(email, role);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int getUserCount() {
        return userService.getUserCount();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> getPaginatedUsers(int offset, int limit) {
        return userService.getPaginatedUser(offset, limit);
    }

    @PreAuthorize("isAuthenticated()")
    public String deleteAccount() {
        return "To delete your account, please withdraw all your assets from NDB Wallet. Please note deleting process is irreversible.";
    }

    @PreAuthorize("isAuthenticated()")
    public String confirmDeleteAccount(String text) {
        if (text.equals("delete")) {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            int id = userDetails.getId();
            return userService.deleteUser(id);
        } else {
            return "failed";
        }
    }

    @PreAuthorize("isAuthenticated()")
    public Statement getStatement(long from, long to) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return financialService.selectStatements(userId, from, to);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String suspendUser(String email) {
        int response = userService.updateSuspended(email, true);
        return response > 0 ? "User suspended." : "Failed.";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String releaseUser(String email) {
        int response = userService.updateSuspended(email, false);
        return response > 0 ? "User suspension removed." : "Failed.";
    }

}
