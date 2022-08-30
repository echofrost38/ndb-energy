package com.ndb.auction.resolver;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.Part;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ndb.auction.exceptions.BalanceException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.KYCSetting;
import com.ndb.auction.models.Shufti.ShuftiReference;
import com.ndb.auction.models.Shufti.Request.Names;
import com.ndb.auction.models.avatar.AvatarSet;
import com.ndb.auction.models.tier.TierTask;
import com.ndb.auction.payload.response.ShuftiRefPayload;
import com.ndb.auction.service.NamePriceService;
import com.ndb.auction.service.ProfileService;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.service.utils.MailService;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class ProfileResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {

    @Autowired
    private NamePriceService namePriceService;

	@Autowired
	protected ProfileService profileService;

    @Autowired
    private MailService mailService;

    // select avatar profile
    // prefix means avatar name!!!
    @PreAuthorize("isAuthenticated()")
    public String setAvatar(String prefix, String name) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
        return profileService.setAvatar(id, prefix, name);
    }

    // update avatar profile ( avatar set )
    @PreAuthorize("isAuthenticated()")
    public List<AvatarSet> updateAvatarSet(List<AvatarSet> components, String hairColor, String skinColor) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
        return profileService.updateAvatarSet(id, components, hairColor, skinColor);
    }

    @PreAuthorize("isAuthenticated()")
    public TierTask getUserTierTask() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
        return tierTaskService.getTierTask(id);
    }
    
    // Identity Verification
    @PreAuthorize("isAuthenticated()")
    public String createNewReference() throws JsonProcessingException, IOException, InvalidKeyException, NoSuchAlgorithmException {
        // create reference record
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        ShuftiReference referenceObj = shuftiService.getShuftiReference(userId);
        int status = 1;
        if(referenceObj != null) {
            status = shuftiService.kycStatusRequestAsync(referenceObj.getReference());
            if(status == 1) {
                String msg = messageSource.getMessage("already_verified", null, Locale.ENGLISH);
                throw new UnauthorizedException(msg, "userId");
            }
        }
        String ref = "";
        if(status == 1) {
            ref = shuftiService.createShuftiReference(userId, "KYC");
        } else {
            ref = shuftiService.updateShuftiReference(userId, UUID.randomUUID().toString());
        }
        return ref;
    }

    @PreAuthorize("isAuthenticated()")
    public ShuftiRefPayload getShuftiRefPayload() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return shuftiService.getShuftiRefPayload(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public Integer kycStatusRequest() throws JsonProcessingException, IOException, InvalidKeyException, NoSuchAlgorithmException {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        ShuftiReference referenceObj = shuftiService.getShuftiReference(userId);
        if(referenceObj == null) {
            String msg = messageSource.getMessage("no_ref", null, Locale.ENGLISH);
            throw new UserNotFoundException(msg, "user");
        }
        return shuftiService.kycStatusRequestAsync(referenceObj.getReference());
    }

    @PreAuthorize("isAuthenticated()")
    public Boolean uploadDocument(Part document) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return shuftiService.uploadDocument(userId, document);
    }

    @PreAuthorize("isAuthenticated()")
    public Boolean uploadAddress(Part address) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return shuftiService.uploadAddress(userId, address);
    }

    @PreAuthorize("isAuthenticated()")
    public Boolean uploadConsent(Part consent) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return shuftiService.uploadConsent(userId, consent);
    }

    @PreAuthorize("isAuthenticated()")
    public Boolean uploadSelfie(Part selfie) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return shuftiService.uploadSelfie(userId, selfie);
    }

    @PreAuthorize("isAuthenticated()")
    public String sendVerifyRequest(String country, String fullAddr, Names names) throws ClientProtocolException, IOException, InvalidKeyException, NoSuchAlgorithmException {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        ShuftiReference referenceObj = shuftiService.getShuftiReference(userId);
        int status = 1;
        if(referenceObj != null) {
            status = shuftiService.kycStatusRequestAsync(referenceObj.getReference());
            if(status == 1) {
                String msg = messageSource.getMessage("already_verified", null, Locale.ENGLISH);
                throw new UnauthorizedException(msg, "userId");
            }
        }
        if(status == 1) {
            shuftiService.createShuftiReference(userId, "KYC");
        } else {
            shuftiService.updateShuftiReference(userId, UUID.randomUUID().toString());
        }
        return shuftiService.sendVerifyRequest(userId, country, fullAddr, names);
    }
    
    // Admin
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int updateKYCSetting(String kind, Double bid, Double direct, Double deposit, Double withdraw) {
        return baseVerifyService.updateKYCSetting(kind, bid, direct, deposit, withdraw);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<KYCSetting> getKYCSettings() {
        return baseVerifyService.getKYCSettings();
    }

    // frontend version
    @PreAuthorize("isAuthenticated()")
    public int insertOrUpdateReference(String reference) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return shuftiService.insertOrUpdateReference(userId, reference);
    }

    @PreAuthorize("isAuthenticated()")
    public ShuftiReference getShuftiReference() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return shuftiService.getShuftiReference(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public String changeEmail() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var email = userDetails.getEmail();
        var code = totpService.getVerifyCode(email);
        var user = userService.getUserByEmail(email);
        try {
            mailService.sendVerifyEmail(user, code, "confirmEmailChange.ftlh"); 
        } catch (Exception e) {
        }
        
        return "Success";
    }

    @PreAuthorize("isAuthenticated()")
    public int confirmChangeEmail(String newEmail, String code) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var email = userDetails.getEmail();
        if(!totpService.checkVerifyCode(email, code)) {
            String msg = messageSource.getMessage("invalid_twostep", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "code");
        }
        var user = userService.getUserByEmail(newEmail);
        if(user != null) {
            String msg = messageSource.getMessage("email_exists", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "code");
        }

        return profileService.updateEmail(userDetails.getId(), newEmail);
    }

    @PreAuthorize("isAuthenticated()")
    public int changeBuyName(String newName) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        // check user balance
        int chars = newName.length();
        var namePrice = namePriceService.select(chars);

        if(namePrice == null) {
            var priceList = namePriceService.selectAll();
            var len = priceList.size();
            namePrice = priceList.get(len - 1); // get last one
        }

        // get ndb with default NDB price : 0.01
        double ndbOrder = namePrice.getPrice() / 0.01;

        double ndbBalance = internalBalanceService.getFreeBalance(userId, "NDB");
        if(ndbBalance < ndbOrder) {
            String msg = messageSource.getMessage("insufficient", null, Locale.ENGLISH);
            throw new BalanceException(msg, "userId");
        }
        
        // change name
        int result = profileService.updateBuyName(userId, newName);
        if(result == 1) {
            return internalBalanceService.deductFree(userId, "NDB", ndbOrder);
        }
        return 0;
    }

}
