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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.profiles.ProfileManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.core.util.CookieUtils;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.TargetDAO;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.User;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.profiles.command.ProfileTargetsCommand;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.dto.ProfileDTO;

/**
 * Controller to list and transfer the targets assigned to a given profile.
 * @author oakleigh_sk
 *
 */
public class ProfileTargetsController extends AbstractFormController {
	
	private TargetManager targetManager = null;
	private TargetDAO targetDao = null;
    
	protected ProfileManager profileManager;
	protected AuthorityManager authorityManager;

	
	/**
	 * Construct a new ProfileTargetsController.
	 */
	public ProfileTargetsController() {
		setCommandClass(ProfileTargetsCommand.class);
	}
	
	@Override
	protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors) throws Exception {
		
		// fetch command object (if any) from session..
		ProfileTargetsCommand command = (ProfileTargetsCommand) request.getSession().getAttribute("profileTargetsCommand");
		if(command == null) {
			command = getDefaultCommand();
			command.setSelectedPageSize(CookieUtils.getPageSize(request));
			command.setProfileOid(Long.parseLong(request.getParameter("profileOid")));
		}
		
		if(command.getActionCommand().equals(ProfileTargetsCommand.ACTION_LIST))
		{
			ModelAndView mav = getView(request, response, command);
			return mav;
		} else {
			return null;
		}
	}
	
	public ProfileTargetsCommand getDefaultCommand() {
		ProfileTargetsCommand command = new ProfileTargetsCommand();
		command.setPageNumber(0);
		command.setActionCommand(ProfileTargetsCommand.ACTION_LIST);
		command.setCancelTargets(false);
		
		return command;
	}
	
	/**
	 * Get the view of the list.
	 * @return The view.
	 */
	protected ModelAndView getView(HttpServletRequest request, HttpServletResponse response, ProfileTargetsCommand command) {
		
		ModelAndView mav = new ModelAndView("profile-targets");

		List<ProfileDTO> newprofiles = new ArrayList<ProfileDTO>();

		Profile currentProfile = profileManager.load(command.getProfileOid());
        User loggedInUser = AuthUtil.getRemoteUserObject();
        Agency usersAgency = loggedInUser.getAgency();
        newprofiles = profileManager.getAgencyDTOs(usersAgency, false, null);

		// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(request);

		Pagination results = null;
		if (command.getSelectedPageSize().equals(currentPageSize)) {
			// user has left the page size unchanged..
			results = targetDao.getAbstractTargetDTOsForProfile(command.getPageNumber(), Integer.parseInt(command.getSelectedPageSize()), command.getProfileOid());
		}
		else {
			// user has selected a new page size, so reset to first page..
			results = targetDao.getAbstractTargetDTOsForProfile(0, Integer.parseInt(command.getSelectedPageSize()), command.getProfileOid());
			// ..then update the page size cookie
			CookieUtils.setPageSize(response, command.getSelectedPageSize());
		}
        
		mav.addObject(Constants.GBL_CMD_DATA, command);
		mav.addObject("newprofiles", newprofiles);
		mav.addObject("currentprofile", currentProfile);
		mav.addObject("page", results);
		return mav;		
	}

	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request,
			HttpServletResponse response, Object comm, BindException errors)
			throws Exception {

		ProfileTargetsCommand command = (ProfileTargetsCommand) comm;
		
		if (ProfileTargetsCommand.ACTION_LIST.equals(command.getActionCommand())) {
			
			return getView(request, response, command);
			
		} else {
			
			if (ProfileTargetsCommand.ACTION_TRANSFER.equals(command.getActionCommand())) {

				// set the profile of the non-excluded targets to be the selected profile
				if (command.getTargetOids() != null) {
					long[] targetOids = command.getTargetOids();
					for(int i=0;i<targetOids.length;i++) {
						long targetOid = targetOids[i];			
						Target target = targetManager.load(targetOid);
						Profile profile = profileManager.load(command.getNewProfileOid());
						target.setProfile(profile);
						if (command.isCancelTargets()) {
							target.changeState(Target.STATE_CANCELLED);
						}
						targetManager.save(target);
					};
				};
			
				// refresh list view
				command.setPageNumber(0);
				return getView(request, response, command);
			
			} else {
				
				if (command.getActionCommand().equals(ProfileTargetsCommand.ACTION_CANCEL)) {
			
					//  go back to profile list.
					return new ModelAndView("redirect:/curator/profiles/list.html");

				} else {
					return null;
				}
			}
		}
	}
	
	/**
	 * @param profileManager The profileManager to set.
	 */
	public void setProfileManager(ProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	/**
	 * @param authorityManager The authorityManager to set.
	 */
    public void setAuthorityManager(AuthorityManager authorityManager) {
        this.authorityManager = authorityManager;
    }
    
	/**
	 * @param targetManager The targetManager to set.
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}
    
	/**
	 * @param targetDao The targetDao to set.
	 */
	public void setTargetDao(TargetDAO targetDao) {
		this.targetDao = targetDao;
	}
	
}
