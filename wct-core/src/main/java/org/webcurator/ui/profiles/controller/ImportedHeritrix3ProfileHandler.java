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

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.core.profiles.Heritrix3Profile;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.profiles.command.ImportedHeritrix3ProfileCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabHandler;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handle the submission of the general tab.
 * @author bbeaumont
 *
 */
public class ImportedHeritrix3ProfileHandler extends TabHandler {

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		// Use the command object to update the profile.
		ImportedHeritrix3ProfileCommand command = (ImportedHeritrix3ProfileCommand) comm;
		Heritrix3Profile heritrix3Profile = (Heritrix3Profile) req.getSession().getAttribute("heritrixProfile");
		heritrix3Profile.setProfileXml(command.getRawProfile());
	}


	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#preProcessNextTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {

		// Create the command object
		Heritrix3Profile heritrix3Profile = (Heritrix3Profile)req.getSession().getAttribute("heritrixProfile");
		Profile profile = (Profile) req.getSession().getAttribute("profile");
		ImportedHeritrix3ProfileCommand command = new ImportedHeritrix3ProfileCommand();
		command.setRawProfile(heritrix3Profile.getProfileXml());
		command.setProfileName(profile.getName());

		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject(Constants.GBL_CMD_DATA, command);

		return tmav;
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processOther(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		// TODO Auto-generated method stub
		return null;
	}

}
