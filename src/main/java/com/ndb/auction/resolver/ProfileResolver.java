package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.service.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class ProfileResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    
    // select avatar profile
    // prefix means avatar name!!!
    @PreAuthorize("isAuthenticated()")
    public String setAvatar(String prefix, String name) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String id = userDetails.getId();
        return profileService.setAvatar(id, prefix, name);
    }

    // update avatar profile ( avatar set )
    @PreAuthorize("isAuthenticated()")
    public List<AvatarSet> updateAvatarSet(List<AvatarSet> components) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String id = userDetails.getId();

        return profileService.updateAvatarSet(id, components);
    }

    // purchase component
    public String purchaseNewComponents(List<AvatarComponent> components) {
        return "";
    }

}
