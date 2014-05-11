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
package org.webcurator.ui.site.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.ui.site.command.SiteAuthorisingAgencyCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the authorising agency tab.
 * @author bbeaumont
 */
public class SiteAuthorisingAgencyHandler extends AbstractSiteHandler {
	
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		// Doesn't need to do anything.
	}

	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
		SiteEditorContext ctx = getEditorContext(req);

		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.getTabStatus().setCurrentTab(tc.getTabConfig().getTabByID("AUTHORISING_AGENCIES"));
		tmav.addObject("agents", ctx.getSortedAuthAgents());
		
		return tmav;
	}

	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		SiteEditorContext ctx = getEditorContext(req);
		
		// Handle the move into New Authorising Agent. 
		if(WebUtils.hasSubmitParameter(req, "_new_auth")) {
			SiteAuthorisingAgencyCommand command = new SiteAuthorisingAgencyCommand();
			
			ModelAndView mav = new ModelAndView();
			mav.addObject(Constants.GBL_CMD_DATA, command);
			mav.setViewName(Constants.VIEW_SITE_AGENCIES);
			mav.addObject("authAgencyEditMode", true);	
			return mav;
		}
		
		if(WebUtils.hasSubmitParameter(req, "_edit_agency")) {
			// Find the authorising Agency
			SiteAuthorisingAgencyCommand command = (SiteAuthorisingAgencyCommand) comm;
			
			AuthorisingAgent agent = (AuthorisingAgent) ctx.getObject(AuthorisingAgent.class, command.getIdentity());
			
			SiteAuthorisingAgencyCommand target = SiteAuthorisingAgencyCommand.buildFromModel(agent);
			
			ModelAndView mav = new ModelAndView();
			
			mav.addObject(Constants.GBL_CMD_DATA, target);
			mav.addObject("authAgencyEditMode", true);			
			
			mav.setViewName(Constants.VIEW_SITE_AGENCIES);
			return mav;
		}
		
		if(WebUtils.hasSubmitParameter(req, "_view_agency")) {
			// Find the authorising Agency
			SiteAuthorisingAgencyCommand command = (SiteAuthorisingAgencyCommand) comm;
			
			AuthorisingAgent agent = (AuthorisingAgent) ctx.getObject(AuthorisingAgent.class, command.getIdentity());
			
			SiteAuthorisingAgencyCommand target = SiteAuthorisingAgencyCommand.buildFromModel(agent);
			
			ModelAndView mav = new ModelAndView();
			
			mav.addObject(Constants.GBL_CMD_DATA, target);			
			mav.addObject("authAgencyEditMode", false);
			
			mav.setViewName(Constants.VIEW_SITE_AGENCIES);
			return mav;
		}		
		
		if(WebUtils.hasSubmitParameter(req, "_remove_agency")) {
			SiteAuthorisingAgencyCommand command = (SiteAuthorisingAgencyCommand) comm;
			AuthorisingAgent agent = (AuthorisingAgent) ctx.getObject(AuthorisingAgent.class, command.getIdentity());
			
			ctx.getSite().getAuthorisingAgents().remove(agent);

			return preProcessNextTab(tc, currentTab, req, res, comm, errors);
		}
				
		return null;
	}
}
