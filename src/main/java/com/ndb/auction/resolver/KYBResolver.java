package com.ndb.auction.resolver;

import java.util.List;

import javax.servlet.http.Part;

import com.ndb.auction.models.user.UserKyb;
import com.ndb.auction.service.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class KYBResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

	@PreAuthorize("isAuthenticated()")
	public UserKyb getMyKYBSetting() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return kybService.getByUserId(userDetails.getId());
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public UserKyb getKYBSetting(String userId) {
		return kybService.getByUserId(userId);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<UserKyb> getKYBSettingList() {
		return kybService.getAll();
	}

	@PreAuthorize("isAuthenticated()")
	public UserKyb updateInfo(String country, String companyName, String regNum) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		String userId = userDetails.getId();
		return kybService.updateInfo(userId, country, companyName, regNum);
	}

	@PreAuthorize("isAuthenticated()")
	public UserKyb updateFile(List<Part> files) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		String userId = userDetails.getId();
		return kybService.updateFile(userId, files);
	}
}
