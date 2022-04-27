/**
 *     MyExtranet, il portale per collaborare con l’ente Regione Veneto.
 *     Copyright (C) 2022  Regione Veneto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.regioneveneto.myp3.myextranet.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import it.regioneveneto.myp3.myextranet.security.Acl;
import it.regioneveneto.myp3.myextranet.service.MyProfileService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myprofile.client.ProfileService;
import it.regioneveneto.myp3.myprofile.client.model.myprofile.MyProfileApplication;
import it.regioneveneto.myp3.myprofile.client.model.myprofile.MyProfileGroup;
import it.regioneveneto.myp3.myprofile.client.model.myprofile.MyProfileGroupRole;
import it.regioneveneto.myp3.myprofile.client.model.myprofile.MyProfileRole;
import it.regioneveneto.myp3.myprofile.client.model.myprofile.MyProfileUserOrgan;
import it.regioneveneto.myp3.myprofile.client.model.myprofile.MyProfileUserOrganRole;

@Service
public class MyProfileServiceImpl implements MyProfileService {
	
	private final static Logger LOG = LoggerFactory.getLogger(MyProfileServiceImpl.class);
		
    private ProfileService myprofileClient;
    
	public MyProfileServiceImpl(
			@Value("${myprofile.baseUrl}") String myProfileBaseUrl
			) {
		super();
		this.myprofileClient = new ProfileService(myProfileBaseUrl);
	}
	
	@Override
	@Cacheable(value = Constants.CACHE_NAME_USER_ROLES, key = "#code")
	public List<Acl> getUserAcls(String code, String ipa, String applicationCode) {
		LOG.debug(String.format("*** Calling getUserAcls(\"%s\", \"%s\", \"%s\")", code, ipa, applicationCode));
		
		it.regioneveneto.myp3.myprofile.client.model.myprofile.MyProfileTenantUser tenantUser = null;
		List<Acl> aclList = new ArrayList<Acl>();
				
		try {			
			tenantUser = myprofileClient.getTenantUserProfiles(code, ipa);
		} catch (Exception e) {
			LOG.debug(String.format("User profile not found for code \"%s\"", code));
		}
		
		if (tenantUser == null) {
			return new ArrayList<Acl>();
		}
		
		//  search in applications
		Set<it.regioneveneto.myp3.myprofile.client.model.myprofile.MyProfileTenantapplUser> applications = tenantUser.getTenantAppls();
		for (it.regioneveneto.myp3.myprofile.client.model.myprofile.MyProfileTenantapplUser tenantUserApplication : applications) {
			it.regioneveneto.myp3.myprofile.client.model.myprofile.MyProfileTenantAppl tenantApplication = tenantUserApplication.getTenantappl();
			if (tenantApplication != null) {
				MyProfileApplication application = tenantApplication.getAppl();
				if (application != null && application.getApplCode().equals(applicationCode)) {
					// found
					Set<it.regioneveneto.myp3.myprofile.client.model.myprofile.MyProfileTenantApplUserRole> roles = tenantUserApplication.getRoles();
					for (it.regioneveneto.myp3.myprofile.client.model.myprofile.MyProfileTenantApplUserRole tenantUserApplicationRole : roles) {
						String aclName = tenantUserApplicationRole.getRole().getAcl();
						String[] permissions = tenantUserApplicationRole.getPermissions().stream()
							.map(perm -> perm.getPermission().getPermission()).toArray(String[]::new);
						addToAclList(aclList, new Acl(aclName, permissions));
					}
				}
			}
		}
		
		// search in groups
		Set<MyProfileGroup> myProfileGroups = tenantUser.getGroups();
		for (MyProfileGroup myProfileGroup : myProfileGroups) {
			Set<MyProfileGroupRole> myProfileGroupRoles = myProfileGroup.getRoles();
			for (MyProfileGroupRole myProfileGroupRole : myProfileGroupRoles) {
				MyProfileRole myProfileRole = myProfileGroupRole.getRole();
				if (myProfileRole != null && myProfileRole.getApplication() != null && myProfileRole.getApplication().getApplCode().equals(applicationCode)) {
					// found
					String aclName = myProfileRole.getAcl();
					String[] permissions = myProfileGroupRole.getPermissions().stream()
							.map(perm -> perm.getPermission().getPermission()).toArray(String[]::new);
					addToAclList(aclList, new Acl(aclName, permissions));
					String[] permissions2 = myProfileRole.getPermissions().stream()
							.map(perm -> perm.getPermission()).toArray(String[]::new);
					addToAclList(aclList, new Acl(aclName, permissions2));
				}
			}
		}

		// search in organs
		Set<MyProfileUserOrgan> myProfileUserOrgans = tenantUser.getUserOrgans();
		for (MyProfileUserOrgan myProfileUserOrgan : myProfileUserOrgans) {
			Set<MyProfileUserOrganRole> myProfileUserOrganRoles = myProfileUserOrgan.getRoles();
			for (MyProfileUserOrganRole myProfileUserOrganRole : myProfileUserOrganRoles) {
				MyProfileRole myProfileRole = myProfileUserOrganRole.getRole();
				if (myProfileRole != null && myProfileRole.getApplication() != null && myProfileRole.getApplication().getApplCode().equals(applicationCode)) {
					// found
					String aclName = myProfileRole.getAcl();
					String[] permissions = myProfileUserOrganRole.getPermissions().stream()
							.map(perm -> perm.getPermission().getPermission()).toArray(String[]::new);
					addToAclList(aclList, new Acl(aclName, permissions));
				}
			}
		}

		return aclList;
	}
	
	private void addToAclList(List<Acl> aclList, Acl acl) {
		// check if acl already present
		Optional<Acl> existing = aclList.stream().filter(a -> a.getAcl().equals(acl.getAcl())).findFirst();
		if (existing.isPresent()) {
			Acl oldAcl = existing.get();
			LinkedHashSet<String> set = new LinkedHashSet<String>(Arrays.asList(oldAcl.getPermissions()));
			set.addAll(Arrays.asList(acl.getPermissions()));
			oldAcl.setPermissions(set.toArray(new String[] {}));
		} else {
			// new acl
			aclList.add(acl);
		}
	}
}
