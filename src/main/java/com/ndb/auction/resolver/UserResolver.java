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
    
    @PreAuthorize("isAuthenticated")
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
        return userService.resetPassword(email);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createNewUser(String email, String country, String role, String avatarName, String shortName) {
        return createNewUser(email, country, role, avatarName, shortName);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String changeRole(String email, String role) {
        return userService.changeRole(email, role);
    }

//    @PreAuthorize("isAuthenticated")
//    public String setAvatar(String prefix, String name) {
//        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String id = userDetails.getId();
//        return profileService.setAvatar(id, prefix, name);
//    }
}
