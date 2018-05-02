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
package org.webcurator.ui.target.controller;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.webcurator.core.harvester.HarvesterType;
import org.webcurator.core.profiles.ProfileManager;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Overrideable;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.ProfileDTO;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.ProfileCommand;
import org.webcurator.ui.target.command.TargetInstanceProfileCommand;
import org.webcurator.ui.util.OverrideGetter;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabHandler;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The abstract tab handler for handling all profile override tabs.
 * This handler is extended for Targets, Groups and TargetInstances.
 * @author nwaight
 */
public abstract class AbstractOverrideTabHandler extends TabHandler {
	/** The Profile Manager */
	protected ProfileManager profileManager;
	/** The Override Getter. */
	protected OverrideGetter overrideGetter;
	/** the prefix for the credential url forms. */
	private String credentialUrlPrefix = "";
	
	/* (non-Javadoc)
	 * @see TabHandler#initBinder(HttpServletRequest, ServletRequestDataBinder). 
	 */
	@Override
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		// Determine the necessary formats.
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        
        // Register the binders.
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, nf, true));
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, nf, true));
    }	
	
	/* (non-Javadoc)
	 * @see TabHandler#processTab(TabbedController, Tab, HttpServletRequest, HttpServletResponse, Object, BindException).  
	 */
	@Override
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		ProfileCommand command = (ProfileCommand) comm;		
		if(command.getProfileOid() != null && overrideGetter.isOverrideableEditable(req)) {			
			//TODO Security constraint
			command.updateOverrides(overrideGetter.getOverrideable(req).getProfileOverrides());
			
			Overrideable overrideable = overrideGetter.getOverrideable(req);
			if(overrideable instanceof AbstractTarget) {
				((AbstractTarget) overrideable).setProfileNote(command.getProfileNote());
			}
		}
	}
	
	
	public ProfileCommand buildCommand(Overrideable o) {
		ProfileCommand command = null;
		
		if(o instanceof TargetInstance) {
			command = new TargetInstanceProfileCommand();
			TargetInstanceProfileCommand tiCommand = (TargetInstanceProfileCommand) command; 
			TargetInstance ti = (TargetInstance) o;
			
			tiCommand.setOverrideTarget(ti.getOverrides() != null);
			command.setFromOverrides(ti.getProfileOverrides());
		}
		else {
			command = new ProfileCommand();
			if(o.getProfile()!=null) {
				command.setProfileOid(o.getProfile().getOid());
				command.setFromOverrides(o.getProfileOverrides());
			}
		}
		
		// Prepare the overrides.
		
		
		// If we are showing an AbstractTarget.
		if(o instanceof AbstractTarget) {
			command.setProfileNote(((AbstractTarget) o).getProfileNote());
		}	
		
		return command;
	}
	
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
		
		// Load the session model.
		Overrideable o = overrideGetter.getOverrideable(req);
		ProfileCommand command = buildCommand(o);
		
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		
		// Add the objects to the model.
		tmav.addObject(Constants.GBL_CMD_DATA, command);
		List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();
		if(o.getProfile()!=null) {
			profiles = profileManager.getAvailableProfiles(o.getProfile().getOid());
			tmav.addObject("profileName", o.getProfile().getName());
			tmav.addObject("harvesterTypeName", o.getProfile().getHarvesterType());
		}
		List<String> harvesterTypes = HarvesterType.getHarvesterTypeNames();
		tmav.addObject("profiles", profiles);
		tmav.addObject("harvesterTypes", harvesterTypes);
		tmav.addObject("credentials", o.getProfileOverrides().getCredentials());
		tmav.addObject("urlPrefix", credentialUrlPrefix);
						
		return tmav;

	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processOther(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		Overrideable o = overrideGetter.getOverrideable(req);
		ProfileCommand command = (ProfileCommand) comm;
		
		if(WebUtils.hasSubmitParameter(req, "delete")) {
			// Process the main tab.
			processTab(tc, currentTab, req, res, comm, errors);
		
			o.getProfileOverrides().getCredentials().remove((int)command.getCredentialToRemove());
			
			TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
			tmav.getTabStatus().setCurrentTab(currentTab);
			
			return tmav;
		}
		
		return null;
	}

	/**
	 * @param overrideGetter the overrideGetter to set
	 */
	public void setOverrideGetter(OverrideGetter overrideGetter) {
		this.overrideGetter = overrideGetter;
	}

	/**
	 * @param profileManager the profileManager to set
	 */
	public void setProfileManager(ProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	/**
	 * @param credentialUrlPrefix the credentialUrlPrefix to set
	 */
	public void setCredentialUrlPrefix(String credentialUrlPrefix) {
		this.credentialUrlPrefix = credentialUrlPrefix;
	}
}
