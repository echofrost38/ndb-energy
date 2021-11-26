package com.ndb.auction.resolver;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.Part;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.User;
import com.ndb.auction.models.sumsub.Applicant;
import com.ndb.auction.service.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    
    // Identity Verification
    @PreAuthorize("isAuthenticated()")
    public String createApplicant(String country, String docType, String levelName) {
    	UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getId();
    	try {
			String result = sumsubService.createApplicant(userId, levelName);
			if(result == null) return "Failed";
		} catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			return "Failed";
		}
    	User user = userService.getUserById(userId);
    	user.setDocType(docType);
    	userService.updateUser(user);
    	
    	return "Success";
    }
    
    @PreAuthorize("isAuthenticated()")
    public String upload(Part file) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
    	UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getId();
        User user = userService.getUserById(userId);
        List<Applicant> appList = sumsubService.getApplicantsByUserId(userId);
        if(appList.size() == 0) {
        	return null;
        }
        String applicantId = appList.get(0).getId();
    	String imageId = sumsubService.addDocument(applicantId, user.getCountry(), user.getDocType(), file);
    	return imageId;
    }
    
    @PreAuthorize("isAuthenticated()")
    public String uploadSelfie(Part part) {
    	
    	return "";
    }
    
    @PreAuthorize("isAuthenticated()")
    public String requestCheck() {
    	
    	return "";
    }
    
    @PreAuthorize("isAuthenticated()")
    public String gettingApplicantData(String applicantId) {
    	String levelName = "";
    	try {
			levelName = sumsubService.gettingApplicantData(applicantId).getReview().getLevelName();
		} catch (InvalidKeyException | NoSuchAlgorithmException | IOException | NullPointerException e) {
			e.printStackTrace();
			return "";
		}
    	return levelName;
    }
    
}
