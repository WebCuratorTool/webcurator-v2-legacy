/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.ui.profiles.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.profiles.ProfileManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.profiles.command.ProfileListCommand;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.domain.model.dto.ProfileDTO;

/**
 * Controller to list all the profiles.
 * @author bbeaumont
 *
 */
public class ProfileListController extends AbstractCommandController {
	
	public static final String SESSION_KEY_SHOW_INACTIVE = "profile-list-show-inactive";
	public static final String SESSION_AGENCY_FILTER = "agency-filter";

	/** The profile Manager */
	protected ProfileManager profileManager;
	private AgencyUserManager agencyUserManager;
    protected AuthorityManager authorityManager;

	
	/**
	 * Construct a new ProfileListController.
	 */
	public ProfileListController() {
		setCommandClass(ProfileListCommand.class);
	}
	
	@Override
	protected ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) throws Exception {
		ProfileListCommand command = (ProfileListCommand) comm;
		
		req.getSession().setAttribute(ProfileListController.SESSION_KEY_SHOW_INACTIVE, command.isShowInactive());
		if(command.getActionCommand().equals(ProfileListCommand.ACTION_LIST))
		{
	        String defaultAgency = (String)req.getSession().getAttribute(ProfileListController.SESSION_AGENCY_FILTER);
	        if(defaultAgency == null)
	        {
	        	defaultAgency = AuthUtil.getRemoteUserObject().getAgency().getName();
	        }
	        command.setDefaultAgency(defaultAgency);
		}
		else if(command.getActionCommand().equals(ProfileListCommand.ACTION_FILTER))
		{
        	req.getSession().setAttribute(ProfileListController.SESSION_AGENCY_FILTER, command.getDefaultAgency());
		}
		else if(command.getActionCommand().equals(ProfileListCommand.ACTION_IMPORT))
		{
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) req;
			CommonsMultipartFile uploadedFile = (CommonsMultipartFile) multipartRequest.getFile("sourceFile");
			Profile profile = new Profile();
			profile.setProfile( new String(uploadedFile.getBytes()) );
			Date now = new Date();
			profile.setName("Profile Imported on "+now.toString());
			profile.setDescription("Imported");
			String importAgency = req.getParameter("importAgency");
			if(importAgency == null || importAgency.trim().equals("")) {
				profile.setOwningAgency(AuthUtil.getRemoteUserObject().getAgency());
			} else {
				long agencyOid = Long.parseLong(importAgency);
				Agency agency = agencyUserManager.getAgencyByOid(agencyOid);
				profile.setOwningAgency(agency);
			}
			// Save to the database
			try {
				profileManager.saveOrUpdate(profile);
			} 
			catch (HibernateOptimisticLockingFailureException e) {
				Object[] vals = new Object[] {profile.getName(), profile.getOwningAgency().getName()};
				errors.reject("profile.modified", vals, "profile has been modified by another user.");
			}
			return new ModelAndView("redirect:/curator/profiles/list.html");
		}
		
		
		ModelAndView mav = getView(command);
		mav.addObject(Constants.GBL_CMD_DATA, command);
		return mav;
	}
	
	/**
	 * Get the view of the list.
	 * @return The view.
	 */
	protected ModelAndView getView(ProfileListCommand command) {
		ModelAndView mav = new ModelAndView("profile-list");

		List<Agency> agencies = new ArrayList<Agency>();
		List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();
		if(authorityManager.hasAtLeastOnePrivilege(new String[]{Privilege.VIEW_PROFILES, Privilege.MANAGE_PROFILES}))
		{
	        if (authorityManager.hasPrivilege(Privilege.VIEW_PROFILES, Privilege.SCOPE_ALL) || 
	        		authorityManager.hasPrivilege(Privilege.MANAGE_PROFILES, Privilege.SCOPE_ALL)) {
	        	agencies = agencyUserManager.getAgencies();
		        profiles = profileManager.getDTOs(command.isShowInactive());
	        } else {
	            User loggedInUser = AuthUtil.getRemoteUserObject();
	            Agency usersAgency = loggedInUser.getAgency();
	            agencies = new ArrayList<Agency>();
	            agencies.add(usersAgency);
		        profiles = profileManager.getAgencyDTOs(usersAgency, command.isShowInactive());
	        }
	        
		}

		mav.addObject(Constants.GBL_CMD_DATA, command);
		mav.addObject("profiles", profiles);
		mav.addObject("agencies", agencies);
		return mav;		
	}

	/**
	 * @param profileManager The profileManager to set.
	 */
	public void setProfileManager(ProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	/**
	 * @param agencyUserManager The agencyUserManager to set.
	 */
	public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
		this.agencyUserManager = agencyUserManager;
	}
	
	/**
	 * @param authorityManager The authorityManager to set.
	 */
    public void setAuthorityManager(AuthorityManager authorityManager) {
        this.authorityManager = authorityManager;
    }
}
