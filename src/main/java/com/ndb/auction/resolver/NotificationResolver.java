package com.ndb.auction.resolver;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import lombok.RequiredArgsConstructor;

import java.util.List;

import com.ndb.auction.models.Notification;
import com.ndb.auction.service.user.UserDetailsImpl;

@Component
@RequiredArgsConstructor
public class NotificationResolver extends BaseResolver
        implements GraphQLSubscriptionResolver, GraphQLMutationResolver, GraphQLQueryResolver {

    @PreAuthorize("isAuthenticated()")
    public Notification setNotificationRead(int id) {
        return notificationService.setNotificationRead(id);
    }

    @PreAuthorize("isAuthenticated()")
    public List<Notification> getAllUnReadNotifications() {
        return notificationService.getAllUnReadNotifications();
    }

    @PreAuthorize("isAuthenticated()")
    public List<Notification> getNotifications(Integer offset, Integer limit) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int userId = userDetails.getId();
        return notificationService.getPaginatedNotifications(userId, offset, limit);
    }

    @PreAuthorize("isAuthenticated()")
    public Notification setNotificationReadFlag(int id) {
        return notificationService.setNotificationRead(id);
    }

    @PreAuthorize("isAuthenticated()")
    public String setNotificationReadFlagAll() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int userId = userDetails.getId();
        return notificationService.setNotificationReadFlagAll(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public List<Notification> getUnreadNotifications() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int userId = userDetails.getId();
        return notificationService.getUnreadNotifications(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public int changeNotifySetting(int nType, boolean status) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int userId = userDetails.getId();
        return userService.changeNotifySetting(userId, nType, status);
    }

}
