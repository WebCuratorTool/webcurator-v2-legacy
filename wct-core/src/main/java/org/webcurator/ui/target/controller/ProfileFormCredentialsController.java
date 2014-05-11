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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.domain.model.core.Overrideable;
import org.webcurator.domain.model.core.ProfileFormCredentials;
import org.webcurator.domain.model.core.ProfileOverrides;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.FormCredentialsCommand;
import org.webcurator.ui.util.OverrideGetter;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The controller for form credentials profile overrides.
 * @author nwaight
 */
public class ProfileFormCredentialsController extends AbstractCommandController {

	private TabbedController tabbedController = null;
	
	private OverrideGetter overrideGetter = null;
	
	private String urlPrefix = "";
	
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));   
    }
	
	@Override
	protected ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) throws Exception {
		FormCredentialsCommand command = (FormCredentialsCommand) comm;
		Overrideable o = overrideGetter.getOverrideable(req);		
		ProfileOverrides overrides = o.getProfileOverrides(); 
		
		if(command.getActionCmd().equals(FormCredentialsCommand.ACTION_NEW)) {
			FormCredentialsCommand newCommand = new FormCredentialsCommand();
			ModelAndView mav = new ModelAndView(urlPrefix + "-form-credentials");
			mav.addObject("command", newCommand);
			mav.addObject("urlPrefix", urlPrefix);
			return mav;
		}
		if( command.getActionCmd().equals(FormCredentialsCommand.ACTION_EDIT)) {
			FormCredentialsCommand newCommand = FormCredentialsCommand.fromModel(overrides.getCredentials(), command.getListIndex());
			ModelAndView mav = new ModelAndView(urlPrefix + "-form-credentials");
			mav.addObject("command", newCommand);
			mav.addObject("urlPrefix", urlPrefix);
			return mav;
			
		}
		else if(command.getActionCmd().equals(FormCredentialsCommand.ACTION_SAVE)) {
			ProfileFormCredentials creds = command.toModelObject();
			
			if (errors.hasErrors()) {
				ModelAndView mav = new ModelAndView(urlPrefix + "-form-credentials");
				mav.addObject(Constants.GBL_CMD_DATA, errors.getTarget());
                mav.addObject(Constants.GBL_ERRORS, errors);
				mav.addObject("urlPrefix", urlPrefix);
				return mav; 
			}
			
			if(command.getListIndex() != null) {
				ProfileFormCredentials orig = (ProfileFormCredentials) o.getProfileOverrides().getCredentials().get(command.getListIndex());
				creds.setOid(orig.getOid());
				overrides.getCredentials().set(command.getListIndex(), creds);
			}
			else {
				overrides.getCredentials().add(creds);
			}
			
			Tab profileTab = tabbedController.getTabConfig().getTabByID("PROFILE");
			
			TabbedModelAndView tmav = profileTab.getTabHandler().preProcessNextTab(tabbedController, profileTab, req, res, command, errors);
			tmav.getTabStatus().setCurrentTab(profileTab);
					
			return tmav;
		}
		else if(command.getActionCmd().equals(FormCredentialsCommand.ACTION_CANCEL)) {
			Tab profileTab = tabbedController.getTabConfig().getTabByID("PROFILE");
			
			TabbedModelAndView tmav = profileTab.getTabHandler().preProcessNextTab(tabbedController, profileTab, req, res, command, errors);
			tmav.getTabStatus().setCurrentTab(profileTab);
					
			return tmav;			
		}
		else {
			errors.reject("Unknown command");
			return new ModelAndView(urlPrefix + "-form-credentials");
		}
	}	

	/**
	 * @param tabbedController The tabbedController to set.
	 */
	public void setTabbedController(TabbedController tabbedController) {
		this.tabbedController = tabbedController;
	}

	/**
	 * @param overrideGetter the overrideGetter to set
	 */
	public void setOverrideGetter(OverrideGetter overrideGetter) {
		this.overrideGetter = overrideGetter;
	}

	/**
	 * @param urlPrefix the urlPrefix to set
	 */
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}	
}
