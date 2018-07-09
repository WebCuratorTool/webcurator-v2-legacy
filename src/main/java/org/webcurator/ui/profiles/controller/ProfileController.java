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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.harvester.HarvesterType;
import org.webcurator.core.profiles.Heritrix3Profile;
import org.webcurator.core.profiles.HeritrixProfile;
import org.webcurator.core.profiles.ProfileManager;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.exceptions.WCTInvalidStateRuntimeException;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.domain.model.dto.ProfileDTO;
import org.webcurator.ui.common.CommonViews;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.profiles.command.DefaultCommand;
import org.webcurator.ui.profiles.command.ProfileListCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;

/**
 * Base controller for the profile editing screens.
 * @author bbeaumont
 *
 */
public class ProfileController extends TabbedController {
	/** the profile manager to use. */
	private ProfileManager profileManager;
	/** the agency user manager to use. */
	private AgencyUserManager agencyUserManager;
	/** Authority Manager */
	private AuthorityManager authorityManager = null;
	
	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabbedController#initBinder(javax.servlet.http.HttpServletRequest, org.springframework.web.bind.ServletRequestDataBinder)
	 */
	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);

		// Ensure that the Profile Oid parameter can be null (for create).
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(Long.class, "profileOid", new CustomNumberEditor(Long.class, nf, true));
	}
	
	@Override
	protected void switchToEditMode(HttpServletRequest req) {
		req.getSession().setAttribute(Constants.GBL_SESS_EDIT_MODE, true);
	};

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabbedController#processSave(org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView processSave(Tab currentTab, HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) {
		// Get the Profile out of the session.
		Profile profile = (Profile) req.getSession().getAttribute("profile");
		
		if(authorityManager.hasPrivilege(profile, Privilege.MANAGE_PROFILES)) {
			
			// Get the Heritrix Profile out of the session.
			if (profile.isHeritrix1Profile()) {
				HeritrixProfile heritrixProfile = (HeritrixProfile) req.getSession().getAttribute("heritrixProfile");
				// Set the XML String on the Profile object.
				profile.setProfile( heritrixProfile.toString());
			} else if (profile.isHeritrix3Profile()) {
				Heritrix3Profile heritrix3Profile = (Heritrix3Profile) req.getSession().getAttribute("heritrixProfile");
				// Set the XML String on the Profile object.
				profile.setProfile( heritrix3Profile.getProfileXml());
			}

			// Save to the database
			try {
				profileManager.saveOrUpdate(profile);
			} 
			catch (HibernateOptimisticLockingFailureException e) {
				Object[] vals = new Object[] {profile.getName(), profile.getOwningAgency().getName()};
				errors.reject("profile.modified", vals, "profile has been modified by another user.");
				
			} catch (WCTInvalidStateRuntimeException e1) {
				Object[] vals = new Object[] {profile.getName(), profile.getOwningAgency().getName()};
				errors.reject("profile.inuse", vals, "inuse profile cannot be de-activated.");
				
			}
			
			if (errors.hasErrors()) {
				
				ModelAndView mav = new ModelAndView("profile-list");
				ProfileListCommand command = new ProfileListCommand();

				// TODO This looks fishy... Should this really be ProfileListCommand.ACTION_FILTER?
		        String defaultAgency = (String)req.getSession().getAttribute(ProfileListCommand.ACTION_FILTER);
		        if(defaultAgency == null)
		        {
		        	defaultAgency = AuthUtil.getRemoteUserObject().getAgency().getName();
		        }
		        command.setDefaultAgency(defaultAgency);
				String harvesterType = (String)req.getSession().getAttribute(ProfileListController.SESSION_HARVESTER_TYPE_FILTER);
				if(harvesterType != null)
				{
					command.setHarvesterType(harvesterType);
				}

				List<Agency> agencies = new ArrayList<Agency>();
				List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();
				if(authorityManager.hasAtLeastOnePrivilege(new String[]{Privilege.VIEW_PROFILES, Privilege.MANAGE_PROFILES}))
				{
			        if (authorityManager.hasPrivilege(Privilege.VIEW_PROFILES, Privilege.SCOPE_ALL) || 
			        		authorityManager.hasPrivilege(Privilege.MANAGE_PROFILES, Privilege.SCOPE_ALL)) {
			        	agencies = agencyUserManager.getAgencies();
				        profiles = profileManager.getDTOs(command.isShowInactive(), command.getHarvesterType());
			        } else {
			            User loggedInUser = AuthUtil.getRemoteUserObject();
			            Agency usersAgency = loggedInUser.getAgency();
			            agencies = new ArrayList<Agency>();
			            agencies.add(usersAgency);
				        profiles = profileManager.getAgencyDTOs(usersAgency, command.isShowInactive(), command.getHarvesterType());
			        }
			        
				}

				mav.addObject(Constants.GBL_CMD_DATA, command);
				mav.addObject("profiles", profiles);
				mav.addObject("agencies", agencies);
				mav.addObject(Constants.GBL_ERRORS, errors);
				return mav;	
				
			} else {
				
				req.getSession().removeAttribute("profile");
				req.getSession().removeAttribute("heritrixProfile");
				req.getSession().removeAttribute(Constants.GBL_SESS_EDIT_MODE);
				
				boolean showInactive = (Boolean) req.getSession().getAttribute(ProfileListController.SESSION_KEY_SHOW_INACTIVE);
				return new ModelAndView("redirect:/curator/profiles/list.html?showInactive=" + showInactive);			
			}
			
		}
		else {
			return CommonViews.AUTHORISATION_FAILURE;
		}
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabbedController#processCancel(org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView processCancel(Tab currentTab, HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) {
		
		// Remove the attributes from the session.
		req.getSession().removeAttribute("profile");
		req.getSession().removeAttribute("heritrixProfile");
		req.getSession().removeAttribute(Constants.GBL_SESS_EDIT_MODE);
		
		boolean showInactive = (Boolean) req.getSession().getAttribute(ProfileListController.SESSION_KEY_SHOW_INACTIVE);
		return new ModelAndView("redirect:/curator/profiles/list.html?showInactive=" + showInactive);
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabbedController#processInitial(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView processInitial(HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) {
		DefaultCommand command = (DefaultCommand) comm;
		
		// There are several actions handled by this controller. Each action
		// has different authorisation requirements that must be checked. Unless
		// this flag is set to true, the user will get an authorisation failure
		// exception.
		boolean allowed = false;
		
		if(command.getProfileOid() != null) {
			// Load the Profile.
			Profile profile = profileManager.load(command.getProfileOid());

			if(DefaultCommand.MODE_COPY.equals(command.getMode())) {
				
				// Only allow copy if the user can view the profile in question
				// and has the manage profiles privilege.
				if(authorityManager.hasPrivilege(profile, Privilege.VIEW_PROFILES) &&
				   authorityManager.hasPrivilege(Privilege.MANAGE_PROFILES, Privilege.SCOPE_AGENCY)) {
					
					allowed = true;
					
					Profile newProfile = profile.clone();
					newProfile.setName("Copy of " + profile.getName());
					newProfile.setOwningAgency(AuthUtil.getRemoteUserObject().getAgency());
					newProfile.setOrigOid(null);
					newProfile.setStatus(Profile.STATUS_ACTIVE);
					newProfile.setDefaultProfile(false);
					
					// Set the items in the session.
					if (newProfile.isHeritrix1Profile()) {
						// Extract the Heritrix Profile
						HeritrixProfile heritrixProfile = HeritrixProfile.fromString(profile.getProfile());
						newProfile.setProfile(heritrixProfile.toString());
						req.getSession().setAttribute("heritrixProfile", heritrixProfile);
					} else if (newProfile.isHeritrix3Profile()) {
						Heritrix3Profile heritrix3Profile = new Heritrix3Profile(profile.getProfile());
						newProfile.setProfile(heritrix3Profile.getProfileXml());
						req.getSession().setAttribute("heritrixProfile", heritrix3Profile);
					}
					req.getSession().setAttribute("profile", newProfile);
				}
			}
			else if(DefaultCommand.MODE_EDIT.equals(command.getMode())) {
				
				// Only allow edit if the user has the edit permission for this
				// profile, and the profile is not locked.
				if(authorityManager.hasPrivilege(profile, Privilege.MANAGE_PROFILES) &&
						!profile.isLocked()) {
					// Set the items in the session.
					allowed = true;
					req.getSession().setAttribute("profile", profile);
					// Set the items in the session.
					if (profile.isHeritrix1Profile()) {
						// Extract the Heritrix Profile
						HeritrixProfile heritrixProfile = HeritrixProfile.fromString(profile.getProfile());
						req.getSession().setAttribute("heritrixProfile", heritrixProfile);
					} else if (profile.isHeritrix3Profile()) {
						Heritrix3Profile heritrix3Profile = new Heritrix3Profile(profile.getProfile());
						req.getSession().setAttribute("heritrixProfile", heritrix3Profile);
					}
				}
			}
			else if(DefaultCommand.MODE_EXPORT.equals(command.getMode())) {
				
				// Only allow export if the user has edit permission for this
				// profile.
				if( authorityManager.hasPrivilege(profile, Privilege.MANAGE_PROFILES) ) {
					// send the XML file.
					try {
						res.setContentType("application/octet-stream");
						res.setHeader("Content-Disposition", "attachment;filename=ExportedProfile.xml");
						res.setHeader("cache-control", "no-cache");
						
						byte[] buf = profile.getProfile().getBytes("UTF-8");
						res.setContentLength(buf.length);
					    
						ServletOutputStream out = res.getOutputStream();
						out.write(buf);
						out.close();
					}
					catch (IOException e) {
						errors.reject("profile.export.error");
					}
					ModelAndView mav = new ModelAndView("profile-list");
					ProfileListCommand cmd = new ProfileListCommand();

					mav.addObject(Constants.GBL_CMD_DATA, cmd);
					//mav.addObject("profiles", profiles);
					//mav.addObject("agencies", agencies);
					mav.addObject(Constants.GBL_ERRORS, errors);
					return mav;		
				}
			}
			
		}
		else {
			
			if(authorityManager.hasPrivilege(Privilege.MANAGE_PROFILES, Privilege.SCOPE_AGENCY)) {
				allowed = true;
			
				Profile profile = new Profile();
				profile.setOwningAgency(AuthUtil.getRemoteUserObject().getAgency());
				// Determine harvester type from the tab config view name
				if (getTabConfig().getViewName().equals("profile")) {
					profile.setHarvesterType(HarvesterType.HERITRIX1.name());
					req.getSession().setAttribute("heritrixProfile", HeritrixProfile.create());
				} else if (getTabConfig().getViewName().equals("profileH3")) {
					profile.setHarvesterType(HarvesterType.HERITRIX3.name());
					req.getSession().setAttribute("heritrixProfile", new Heritrix3Profile());
				}
				req.getSession().setAttribute("profile", profile);
			}
		}
		req.getSession().setAttribute(Constants.GBL_SESS_EDIT_MODE, true);
		if(allowed) {
			Tab general = getTabConfig().getTabByID("GENERAL");
			TabbedModelAndView tmav = general.getTabHandler().preProcessNextTab(this, general, req, res, null, errors);
			tmav.getTabStatus().setCurrentTab(general);
			return tmav;
		}
		else {
			return CommonViews.AUTHORISATION_FAILURE;
		}
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabbedController#showForm(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView showForm(HttpServletRequest req, HttpServletResponse res, Object command, BindException bex) throws Exception {
		return processInitial(req, res, command, bex);
	}

	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}

	/**
	 * @param profileManager the profileManager to set
	 */
	public void setProfileManager(ProfileManager profileManager) {
		this.profileManager = profileManager;
	}
}
