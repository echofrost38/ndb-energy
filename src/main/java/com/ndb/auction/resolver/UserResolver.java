package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.GeoLocation;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserAvatar;
import com.ndb.auction.models.user.UserVerify;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class UserResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

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
        return userService.changePassword(id, newPassword);
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
            return "Already Exists.";
        }

        String rPassword = userService.getRandomPassword(10);
        String encoded = userService.encodePassword(rPassword);
        user = new User();
        user.setEmail(email);
        user.setPassword(encoded);
        user.setCountry(country);

        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setPrefix(avatarName);
        userAvatar.setName(shortName);
        user.setAvatar(userAvatar);

        UserVerify userVerify = new UserVerify();
        userVerify.setEmailVerified(true);
        user.setVerify(userVerify);

        // check role
        if (role.equals("ROLE_ADMIN"))
            user.addRole(role);

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
            UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            int id = userDetails.getId();
            return userService.deleteUser(id);
        } else {
            return "failed";
        }
    }


}
