package com.ndb.auction.resolver;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.Part;

import com.ndb.auction.models.KYCSetting;
import com.ndb.auction.models.avatar.AvatarComponent;
import com.ndb.auction.models.avatar.AvatarSet;
import com.ndb.auction.models.sumsub.Applicant;
import com.ndb.auction.service.user.UserDetailsImpl;

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
        int id = userDetails.getId();
        return profileService.setAvatar(id, prefix, name);
    }

    // update avatar profile ( avatar set )
    @PreAuthorize("isAuthenticated()")
    public List<AvatarSet> updateAvatarSet(List<AvatarSet> components) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
        return profileService.updateAvatarSet(id, components);
    }

    // purchase component
    public String purchaseNewComponents(List<AvatarComponent> components) {
    	
        return "";
    }
    
    // Identity Verification
    @PreAuthorize("isAuthenticated()")
    public String createApplicant(String country, String docType, String levelName) {
    	
    	return "Success";
    }

    @PreAuthorize("isAuthenticated()")
    public String upgradeApplicant(String levelName) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
   
        return "Success";
    }
    
    @PreAuthorize("isAuthenticated()")
    public String upload(String docType, String country, Part file) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
    	UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
    	return "";
    }
    
    @PreAuthorize("isAuthenticated()")
    public String uploadSelfie(String country, Part selfie) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
    	UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
    	return "imageId";
    }
    
    @PreAuthorize("isAuthenticated()")
    public String requestCheck()  throws InvalidKeyException, NoSuchAlgorithmException, IOException {
    	UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
    	return "result";
    }
    
    @PreAuthorize("isAuthenticated()")
    public String gettingApplicantData(String applicantId) {
    	String levelName = "";
    	return levelName;
    }
    
    // Admin
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public KYCSetting updateKYCSetting(String kind, double withdraw, double deposit, double bid, double direct) {
        // return sumsubService.updateKYCSetting(kind, withdraw, deposit, bid, direct);
        return null;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<KYCSetting> getKYCSetting() {
        // return sumsubService.getKYCSettings();
        return null;
    }
}
