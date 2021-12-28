package com.ndb.auction.resolver;

import java.util.List;

import javax.servlet.http.Part;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.KYB;
import com.ndb.auction.models.SkillSet;
import com.ndb.auction.service.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class KYBResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

	@PreAuthorize("isAuthenticated()")
	public KYB getMyKYBSetting() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return kybService.getByUserId(userDetails.getId());
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public KYB getKYBSetting(String userId) {
		return kybService.getByUserId(userId);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<KYB> getKYBSettingList() {
		return kybService.getAll();
	}

	@PreAuthorize("isAuthenticated()")
	public KYB updateInfo(String country, String companyName, String regNum) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		String userId = userDetails.getId();
		return kybService.updateInfo(userId, country, companyName, regNum);
	}

	@PreAuthorize("isAuthenticated()")
	public KYB updateFile(List<Part> fileList) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		String userId = userDetails.getId();
		return kybService.updateFile(userId, fileList);
	}
}
