package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.service.TaskSettingService;
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

    @Autowired
    private TaskSettingService taskSettingService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Tier addNewUserTier(int level, String name, long point) {
        return tierService.addNewUserTier(level, name, point);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Tier updateUserTier(int level, String name, long point) {
        return tierService.updateUserTier(level, name, point);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Tier> getUserTiers() {
        return tierService.getUserTiers();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int deleteUserTier(int level) {
        return tierService.deleteUserTier(level);   
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TaskSetting addNewSetting(TaskSetting setting) {
        return taskSettingService.updateTaskSetting(setting);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TaskSetting updateTaskSetting(TaskSetting setting) {
        return taskSettingService.updateTaskSetting(setting);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TaskSetting getTaskSetting() {
        return taskSettingService.getTaskSetting();
    }
}
