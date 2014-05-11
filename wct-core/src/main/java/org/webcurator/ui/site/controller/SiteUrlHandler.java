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

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.UrlPattern;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.ui.site.command.UrlCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the harvest authorisations URL's tab.
 * @author bbeaumont
 */
public class SiteUrlHandler extends AbstractSiteHandler {
	/** Logger for the SiteUrlHandler. **/
	private static Log log = LogFactory.getLog(SiteUrlHandler.class);
	/** BusinessObjectFactory */
	private BusinessObjectFactory businessObjectFactory = null;

	public void processTab(TabbedController tc, Tab currentTab, HttpServletRequest req,
			HttpServletResponse res, Object comm, BindException errors) {
	}

	public TabbedModelAndView preProcessNextTab(TabbedController tc, Tab nextTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		SiteEditorContext ctx = getEditorContext(req);
		
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject("urls", ctx.getSortedUrlPatterns());
		return tmav;		
	}

	public ModelAndView processOther(TabbedController tc, Tab currentTab, HttpServletRequest req,
			HttpServletResponse res, Object comm, BindException errors) {
		SiteEditorContext ctx = getEditorContext(req);
		UrlCommand command = (UrlCommand) comm;
		
		if(errors.hasErrors()) {
			Tab tab = tc.getTabConfig().getTabByID("URLS");
			TabbedModelAndView tmav = tab.getTabHandler().preProcessNextTab(tc, tab, req, res, comm ,errors);
			tmav.getTabStatus().setCurrentTab(tab);
			tmav.addObject(Constants.GBL_CMD_DATA, command);
			tmav.addObject(Constants.GBL_ERRORS, errors);
			return tmav;
		}

		if( command.isAction(UrlCommand.ACTION_REMOVE_URL) ) {
			log.info("In the URLS tab - REMOVE URL: " + command.getUrlId());
			
			UrlPattern patternToRemove = (UrlPattern) ctx.getObject(UrlPattern.class, command.getUrlId());
			if(patternToRemove.getPermissions().size() > 0) {
				errors.reject("sitecontroller.error.url_in_use", new Object[] { patternToRemove.getPattern() }, "URL Pattern is linked to a permission.");
			}
			else {
				ctx.getSite().removeUrlPattern(patternToRemove);
			}
						
			TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
			tmav.getTabStatus().setCurrentTab(currentTab);
			return tmav;
		}		
	
	
		if( command.isAction(UrlCommand.ACTION_ADD_URL) ) {
			log.info("In the URLS tab - ADD URL: " + command.getUrl());
			
			if(!"".equals(command.getUrl())) {
				try {
					new URL(command.getUrl());
				}
				catch(MalformedURLException ex) {
					throw new WCTRuntimeException("The URL " + command.getUrl() + " is invaid.");
				}
				
				UrlPattern url = businessObjectFactory.newUrlPattern(ctx.getSite());
				url.setPattern(command.getUrl());
				ctx.putObject(url);
				ctx.getSite().getUrlPatterns().add(url);
				
			}
			
			TabbedModelAndView tmav = preProcessNextTab(tc, tc.getTabConfig().getTabByID("URLS"), req, res, comm, errors);
			tmav.getTabStatus().setCurrentTab(tc.getTabConfig().getTabByID("URLS"));
			
			return tmav;		
		}

		TabbedModelAndView tmav = preProcessNextTab(tc, tc.getTabConfig().getTabByID("URLS"), req, res, comm, errors);
		tmav.getTabStatus().setCurrentTab(tc.getTabConfig().getTabByID("URLS"));		
		return tmav;
	}

	/**
	 * @param businessObjectFactory The businessObjectFactory to set.
	 */
	public void setBusinessObjectFactory(BusinessObjectFactory businessObjectFactory) {
		this.businessObjectFactory = businessObjectFactory;
	}

}
