package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.UserTier;
import com.ndb.auction.service.TierService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class TierResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    @Autowired
    private TierService tierService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserTier addNewUserTier(int level, String name, double points) {
        return tierService.addNewUserTier(level, name, points);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserTier updateUserTier(int level, String name, double points) {
        return tierService.updateUserTier(level, name, points);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserTier> getUserTiers() {
        return tierService.getUserTiers();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int deleteUserTier(int level) {
        return tierService.deleteUserTier(level);   
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TaskSetting addNewSetting(TaskSetting setting) {
        return tierService.addNewSetting(setting);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TaskSetting updateTaskSetting(TaskSetting setting) {
        return tierService.updateTaskSetting(setting);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TaskSetting getTaskSetting() {
        return tierService.getTaskSetting();
    }
}
