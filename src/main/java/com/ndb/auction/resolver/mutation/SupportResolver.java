package com.ndb.auction.resolver.mutation;

import java.util.Locale;

import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.payload.RecoveryRequest;
import com.ndb.auction.payload.response.GAuthResetResponse;
import com.ndb.auction.service.auth.AuthResetService;
import com.ndb.auction.service.auth.PhoneResetService;
import com.ndb.auction.service.user.UserAuthService;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.service.user.UserService;
import com.ndb.auction.service.utils.MailService;
import com.ndb.auction.service.utils.SMSService;
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
    private UserAuthService userAuthService;

    @Autowired
	private MessageSource messageSource;

    @Autowired
    private AuthResetService gAuthResetService;

    @Autowired
    private PhoneResetService phoneResetService;

    @Autowired
    private TotpService totpService;

    @Autowired
    private SMSService smsService;

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
        
        var token = phoneResetService.saveNewRequest(userId, phone);
        if(token.equals("Failed")) {
            return token;
        }

        var code = totpService.get2FACode(user.getEmail());
        try {
            smsService.sendSMS(phone, code);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = messageSource.getMessage("error_phone", null, Locale.ENGLISH);
            throw new UserNotFoundException(msg, "phone");
        }
        return token;
	}
    
	@PreAuthorize("isAuthenticated()")
	public String confirmPhone2FA(String smsCode, String mailCode, String token) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        var user = userService.getUserById(userId);
        
        if(!totpService.checkVerifyCode(user.getEmail(), mailCode)) {
            String msg = messageSource.getMessage("invalid_twostep", null, Locale.ENGLISH);
            throw new UserNotFoundException(msg, "phone");
        }

        var phoneResetRequest = phoneResetService.getByUserId(userId, token);
        if(phoneResetRequest == null) {
            String msg = messageSource.getMessage("no_request", null, Locale.ENGLISH);
            throw new UserNotFoundException(msg, "token");
        }

        if(!totpService.check2FACode(user.getEmail(), smsCode)) {
            String msg = messageSource.getMessage("invalid_twostep", null, Locale.ENGLISH);
            throw new UserNotFoundException(msg, "phone");
        }

		var result = userAuthService.confirmPhone2FA(userId, phoneResetRequest.getPhone());
        if(result.equals("Success")) {
            phoneResetService.updateStatus(userId, token, 1);
        }
        return result;
	}

    @PreAuthorize("isAuthenticated()")
    public String sendVerifyCode() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var email = userDetails.getEmail();
        var user = userService.getUserByEmail(email);
        var code = totpService.getVerifyCode(email);
        try {
            mailService.sendVerifyEmail(user, code, "withdraw.ftlh");   
        } catch (Exception e) {
            return "Failed";
        }
        return email.replaceAll("(?<=.)[^@](?=[^@]*?@)|(?:(?<=@.)|(?!^)\\G(?=[^@]*$)).(?!$)", "*");
    }

    @PreAuthorize("isAuthenticated()")
	public GAuthResetResponse resetGoogleAuthRequest() {
		var userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = userDetails.getId();
		return gAuthResetService.saveNewGAuthRequest(userId, userDetails.getEmail());
	}

	@PreAuthorize("isAuthenticated()")
	public String confirmGoogleAuthReset(String googleCode, String mailCode, String token) {
		UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = userDetails.getId();
        String result = "";
		if(!totpService.checkVerifyCode(userDetails.getEmail(), mailCode)) {
            String msg = messageSource.getMessage("invalid_twostep", null, Locale.ENGLISH);
            throw new UserNotFoundException(msg, "phone");
        }

        // get request
        var resetRequest = gAuthResetService.getRequestByUser(userId, token);
        if(resetRequest == null) {
            String msg = messageSource.getMessage("no_request", null, Locale.ENGLISH);
            throw new UserNotFoundException(msg, "token");
        }

        // check google code
        if(totpService.verifyCode(googleCode, resetRequest.getSecret())) {
            // passed code
            // 1) update user security setting
            result = userAuthService.confirmGoogleAuthUpdate(userId, resetRequest.getSecret());
            // 2) update request status
            if(result.equals("Success")) {
                gAuthResetService.updateRequestStatus(userId, token, 1);
                return result;
            }
        }
        gAuthResetService.updateRequestStatus(userId, token, 0);
		return "Failed";
	}

}
