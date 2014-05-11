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
import org.webcurator.domain.model.core.ProfileBasicCredentials;
import org.webcurator.domain.model.core.ProfileOverrides;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.BasicCredentialsCommand;
import org.webcurator.ui.util.OverrideGetter;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The controller for handling the creation of basic form credential overrides.
 * @author nwaight
 */
public class ProfileBasicCredentialsController extends AbstractCommandController {

	private TabbedController tabbedController = null;
	
	private OverrideGetter overrideGetter = null;
	
	private String urlPrefix = "";
		
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));   
    }
	
	@Override
	protected ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) throws Exception {
		BasicCredentialsCommand command = (BasicCredentialsCommand) comm;
		Overrideable o = overrideGetter.getOverrideable(req);		
		ProfileOverrides overrides = o.getProfileOverrides(); 
		
		if(command.getActionCmd().equals(BasicCredentialsCommand.ACTION_NEW)) {
			BasicCredentialsCommand newCommand = new BasicCredentialsCommand();
			ModelAndView mav = new ModelAndView(urlPrefix + "-basic-credentials");
			mav.addObject("command", newCommand);
			mav.addObject("urlPrefix", urlPrefix);
			return mav;
		}
		if( command.getActionCmd().equals(BasicCredentialsCommand.ACTION_EDIT)) {
			BasicCredentialsCommand newCommand = BasicCredentialsCommand.fromModel(overrides.getCredentials(), command.getListIndex());
			ModelAndView mav = new ModelAndView(urlPrefix + "-basic-credentials");
			mav.addObject("command", newCommand);
			mav.addObject("urlPrefix", urlPrefix);
			return mav;
			
		}
		else if(command.getActionCmd().equals(BasicCredentialsCommand.ACTION_SAVE)) {
			ProfileBasicCredentials creds = command.toModelObject();
			
			if (errors.hasErrors()) {
				ModelAndView mav = new ModelAndView(urlPrefix + "-basic-credentials");
				mav.addObject(Constants.GBL_CMD_DATA, errors.getTarget());
                mav.addObject(Constants.GBL_ERRORS, errors);
				mav.addObject("urlPrefix", urlPrefix);
				return mav; 
			}
			
			if(command.getListIndex() != null) {
				ProfileBasicCredentials orig = (ProfileBasicCredentials) o.getProfileOverrides().getCredentials().get(command.getListIndex());
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
		else if(command.getActionCmd().equals(BasicCredentialsCommand.ACTION_CANCEL)) {
			Tab profileTab = tabbedController.getTabConfig().getTabByID("PROFILE");
			
			TabbedModelAndView tmav = profileTab.getTabHandler().preProcessNextTab(tabbedController, profileTab, req, res, command, errors);
			tmav.getTabStatus().setCurrentTab(profileTab);
					
			return tmav;			
		}		
		else {
			errors.reject("Unknown command");
			return new ModelAndView(urlPrefix + "-basic-credentials");
		}
	}	

	/**
	 * @param aTabbedController The TabbedController to set.
	 */
	public void setTabbedController(TabbedController aTabbedController) {
		this.tabbedController = aTabbedController;
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
