package com.ndb.auction.resolver.mutation;

import com.ndb.auction.payload.RecoveryRequest;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.service.user.UserService;
import com.ndb.auction.service.utils.MailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;

@Component
public class SupportResolver implements GraphQLMutationResolver {
    
    private MailService mailService;
    private UserService userService;

    @Autowired
    public SupportResolver(MailService mailService, UserService userService) {
        this.mailService = mailService;
        this.userService = userService;
    }

    // Unknown Memo/Tag Recovery
	@PreAuthorize("isAuthenticated()")
    public String unknownMemoRecovery(String coin, String receiverAddr, Double depositAmount, String txId) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = userDetails.getId();
        var user = userService.getUserById(userId);
        var request = RecoveryRequest.builder()
            .user(user)
            .coin(coin)
            .receiverAddr(receiverAddr)
            .txId(txId)
            .depositAmount(depositAmount)
            .build();
        try { 
            mailService.sendRecoveryEmail(request); 
        } catch (Exception e) { return "Failed"; }
        return "Success";
    }

}
