package com.ndb.auction.resolver.mutation;

import java.util.Locale;

import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.payload.RecoveryRequest;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.service.user.UserService;
import com.ndb.auction.service.utils.MailService;
import com.ndb.auction.service.utils.TotpService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SupportResolver implements GraphQLMutationResolver {
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;
    @Autowired
    private TotpService totpService;
    @Autowired
	private MessageSource messageSource;

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

    
	@PreAuthorize("isAuthenticated()")
	public String requestPhone2FA(String phone) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = userDetails.getId();
		var user = userService.getUserById(userId);
        
        // if(!totpService.check2FACode(user.getEmail(), code)) {

        // }
        
        if(phone == null || phone.equals("")) {
            String msg = messageSource.getMessage("no_phone", null, Locale.ENGLISH);
            throw new UserNotFoundException(msg, "phone");
        }
        user.setPhone(phone);
		return userService.request2FA(user.getEmail(), "phone", user.getPhone());
	}

	@PreAuthorize("isAuthenticated()")
	public String confirmPhone2FA(String code) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = userDetails.getId();
		var user = userService.getUserById(userId);
		return userService.confirmRequest2FA(user.getEmail(), "phone", code);
	}

    @PreAuthorize("isAuthenticated()")
    public String sendCode() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var email = userDetails.getEmail();
        var user = userService.getUserByEmail(email);
        var code = totpService.getWithdrawCode(email);
        try {
            mailService.sendVerifyEmail(user, code, "withdraw.ftlh");   
        } catch (Exception e) {
            return "Failed";
        }
        return email.replaceAll("(?<=.)[^@](?=[^@]*?@)|(?:(?<=@.)|(?!^)\\G(?=[^@]*$)).(?!$)", "*");
    }

}
