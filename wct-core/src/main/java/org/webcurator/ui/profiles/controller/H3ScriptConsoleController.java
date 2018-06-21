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
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.common.CommonViews;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.profiles.command.H3ScriptConsoleCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Controller to handle users viewing profiles.
 * @author bbeaumont
 *
 */
public class H3ScriptConsoleController extends AbstractCommandController {
	/** The profile manager to load the profile */
	private TargetInstanceManager targetInstanceManager = null;
	/** The authority manager for checking permissions */
	private AuthorityManager authorityManager = null;

	/**
	 * Construct a new ProfileViewController.
	 */
	public H3ScriptConsoleController() {
		setCommandClass(H3ScriptConsoleCommand.class);
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractCommandController#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) throws Exception {
		H3ScriptConsoleCommand command = (H3ScriptConsoleCommand) comm;
		TargetInstance ti = targetInstanceManager.getTargetInstance(command.getTargetInstanceOid(), true);
		String result = "";

		if (authorityManager.hasAtLeastOnePrivilege(ti.getProfile(), new String[] {Privilege.MANAGE_TARGET_INSTANCES, Privilege.MANAGE_WEB_HARVESTER})) {
			if (req.getMethod().equals("POST") && ti.getState().equals("Running")
					&& command.getActionCommand().equals(H3ScriptConsoleCommand.ACTION_EXECUTE_SCRIPT)) {
				// Run the heritrix 3 script - only if the status is still running
				// Validation??
				String script = command.getScript();
				result = "XXXX: " + script;
			}
			ModelAndView mav = new ModelAndView("h3-script-console");
			mav.addObject("targetInstance", ti);
			mav.addObject("result", result);
			mav.addObject(Constants.GBL_CMD_DATA, command);
			return mav;
		}
		else { 
			return CommonViews.AUTHORISATION_FAILURE;
		}
	}

	/**
	 * @return Returns the targetInstanceManager.
	 */
	public TargetInstanceManager getTargetInstanceManager() {
		return targetInstanceManager;
	}

	/**
	 * @param targetInstanceManager The targetInstanceManager to set.
	 */
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}

	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}
	
	

}
