package com.ndb.auction.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ndb.auction.models.Notification;
import com.ndb.auction.payload.NotificationType;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationResolver extends BaseResolver
        implements GraphQLMutationResolver, GraphQLQueryResolver {

    @PreAuthorize("isAuthenticated()")
    public List<NotificationType> getNotificationTypes() {
        List<NotificationType> notifyTypes = new ArrayList<NotificationType>();
        Map<String, Integer> typeMap = notificationService.getTypeMap();
        Set<String> keySet = typeMap.keySet();
        for (String type : keySet) {
            notifyTypes.add(new NotificationType(type, typeMap.get(type)));
        }
        return notifyTypes;
    }

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
