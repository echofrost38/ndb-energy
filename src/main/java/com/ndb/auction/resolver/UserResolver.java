package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.GeoLocation;
import com.ndb.auction.models.User;
import com.ndb.auction.service.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class UserResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    @PreAuthorize("isAuthenticated()")
    public User getUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String id = userDetails.getId();
        return userService.getUserById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public GeoLocation addDisallowed(String countryCode) {
        return userService.addDisallowed(countryCode);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<GeoLocation> getDisallowed() {
        return userService.getDisallowed();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public GeoLocation makeAllow(String countryCode) {
        return userService.makeAllow(countryCode);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String resetPassword(String email) {
        String rPassword = userService.getRandomPassword(10);
        String encoded = userService.encodePassword(rPassword);
        User user = userService.getUserByEmail(email);
        user.setPassword(encoded);
        return userService.resetPassword(user, rPassword);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createNewUser(String email, String country, String role, String avatarName, String shortName) {
        
        User user = userService.getUserByEmail(email);
        if(user != null) {
            return "Already Exists.";
        }

        String rPassword = userService.getRandomPassword(10);
        String encoded = userService.encodePassword(rPassword);
        user = new User(email, encoded, country, true);

        user.setAvatarPrefix(avatarName);
        user.setAvatarName(shortName);

        user.getVerify().replace("email", true);

		// check role
		if(role.equals("ROLE_ADMIN")) {
			user.getRole().add("ROLE_ADMIN");
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
    public List<User> getPaginatedUsers(String key, int limit) {
        return userService.getPaginatedUser(key, limit);
    }

    

//    @PreAuthorize("isAuthenticated")
//    public String setAvatar(String prefix, String name) {
//        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String id = userDetails.getId();
//        return profileService.setAvatar(id, prefix, name);
//    }
}
