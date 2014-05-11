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
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.springframework.web.util.WebUtils;
import org.webcurator.core.sites.SiteManager;
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.ui.site.command.SiteAuthorisingAgencyCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The manager for Harvest Authorisation actions.
 * @author nwaight
 */
public class SiteAgencyController extends AbstractCommandController {

	/** The site manager */
	private SiteManager siteManager = null;
	
	private SiteController siteController;
	/** BusinessObjectFactory */
	private BusinessObjectFactory busObjFactory = null;
	
	public SiteAgencyController() {
		setCommandClass(SiteAuthorisingAgencyCommand.class);
	}
	
	public SiteEditorContext getEditorContext(HttpServletRequest req) {
		SiteEditorContext ctx = (SiteEditorContext) req.getSession().getAttribute("siteEditorContext");
		if( ctx == null) {
			throw new IllegalStateException("siteEditorContext not yet bound to the session");
		}
		
		return ctx;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractCommandController#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView handle(HttpServletRequest aReq,
			HttpServletResponse aResp, Object aCommand, BindException aErrors)
			throws Exception {
		
		SiteAuthorisingAgencyCommand cmd = (SiteAuthorisingAgencyCommand) aCommand;
		SiteEditorContext ctx = getEditorContext(aReq);
		
		// Handle Cancel
		if(WebUtils.hasSubmitParameter(aReq, "_cancel_auth_agent")) {
			Tab membersTab = siteController.getTabConfig().getTabByID("AUTHORISING_AGENCIES");
			TabbedModelAndView tmav = membersTab.getTabHandler().preProcessNextTab(siteController, membersTab, aReq, aResp, cmd, aErrors);
			tmav.getTabStatus().setCurrentTab(membersTab);
			return tmav;
		}
		
		// Handle Save
		if(WebUtils.hasSubmitParameter(aReq, "_save_auth_agent")) {		
			
			if (aErrors.hasErrors()) {				
				ModelAndView mav = new ModelAndView();
				mav.addObject(Constants.GBL_CMD_DATA, aErrors.getTarget());
				mav.addObject(Constants.GBL_ERRORS, aErrors);
				mav.setViewName(Constants.VIEW_SITE_AGENCIES);
				mav.addObject("authAgencyEditMode", true);	
				
				return mav;			
			}
			
			// Are we creating a new item, or updating an existing
			// one?
			if(isEmpty(cmd.getIdentity())) {
				AuthorisingAgent agent = busObjFactory.newAuthorisingAgent();
				cmd.updateBusinessModel(agent);
				ctx.putObject(agent);
				ctx.getSite().getAuthorisingAgents().add(agent);
			}
			else {
				AuthorisingAgent agent = (AuthorisingAgent) ctx.getObject(AuthorisingAgent.class, cmd.getIdentity());
				cmd.updateBusinessModel(agent);
			}
						
			Tab membersTab = siteController.getTabConfig().getTabByID("AUTHORISING_AGENCIES");
			TabbedModelAndView tmav = membersTab.getTabHandler().preProcessNextTab(siteController, membersTab, aReq, aResp, cmd, aErrors);
			tmav.getTabStatus().setCurrentTab(membersTab);
			return tmav;
		}
		
		ModelAndView mav = new ModelAndView();
		mav.addObject(Constants.GBL_CMD_DATA, cmd);
		mav.addObject(Constants.GBL_ERRORS, aErrors);
		mav.setViewName(Constants.VIEW_SITE_AGENCIES);
		
		return mav;		
	}

	private boolean isEmpty(String aString) {
		return aString == null || aString.trim().equals("");
	}
	
	/**
	 * @param siteController the siteController to set
	 */
	public void setSiteController(SiteController siteController) {
		this.siteController = siteController;
	}

	/**
	 * @param busObjFactory The busObjFactory to set.
	 */
	public void setBusObjFactory(BusinessObjectFactory busObjFactory) {
		this.busObjFactory = busObjFactory;
	}

	/**
	 * @return Returns the siteManager.
	 */
	public SiteManager getSiteManager() {
		return siteManager;
	}

	/**
	 * @param siteManager The siteManager to set.
	 */
	public void setSiteManager(SiteManager siteManager) {
		this.siteManager = siteManager;
	}
}
