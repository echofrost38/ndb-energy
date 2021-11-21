package com.ndb.auction.resolver;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.Part;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarSet;
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
    public String initVerification(String country, String docType, String levelName) {
    	UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getId();
    	try {
			String result = sumsubService.createApplicant(userId, docType, levelName);
			if(result == null) return "Failed";
		} catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			return "Failed";
		}
    	return "Success";
    }
    
    public String uploadDocuments(Part part[]) {
    	UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getId();
        
    	return "";
    }
    
    public String uploadSelfie(Part part) {
    	
    	return "";
    }
    
    public String requestCheck() {
    	
    	return "";
    }
    
//    public String upload(Part part) throws IOException {
//        System.out.println("Part: " + part.getSubmittedFileName());
//        part.write(part.getSubmittedFileName());
//        
//        return "Success";
//    }

}
