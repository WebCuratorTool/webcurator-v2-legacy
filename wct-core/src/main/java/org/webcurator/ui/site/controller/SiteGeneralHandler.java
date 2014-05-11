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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.webcurator.core.sites.SiteManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.Site;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.ui.site.command.SiteCommand;
import org.webcurator.ui.target.command.TargetAnnotationCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The manager for handling the harvest autorisations general tab.
 * @author bbeaumont
 */
public class SiteGeneralHandler extends AbstractSiteHandler {

	SiteManager siteManager;
	
	public void processTab(TabbedController tc, Tab currentTab, HttpServletRequest req,
			HttpServletResponse res, Object comm, BindException errors) {
		SiteCommand sc = (SiteCommand) comm;		
		if (getEditorContext(req).isEditMode()) {	
			sc.updateBusinessModel(getEditorContext(req).getSite());
		}
		if(sc.isAction(SiteCommand.ACTION_ADD_NOTE) &&
				!"".equals(sc.getAnnotation())) {
			// Add the annotations
			Annotation a = new Annotation(new Date(), sc.getAnnotation());
			a.setUser(AuthUtil.getRemoteUserObject());
			
			SiteEditorContext ctx = getEditorContext(req);
			Site site = ctx.getSite();
			site.addAnnotation(a);
		}
	}

	public TabbedModelAndView preProcessNextTab(TabbedController tc, Tab nextTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		Site aSite = getEditorContext(req).getSite();
		//ensure annotations are sorted
		aSite.sortAnnotations();
		tmav.addObject(Constants.GBL_CMD_DATA, SiteCommand.buildFromModel(aSite));
		return tmav;		
	}

	public ModelAndView processOther(TabbedController tc, Tab currentTab, HttpServletRequest req,
			HttpServletResponse res, Object comm, BindException errors) {

		SiteCommand sc = (SiteCommand) comm;
		SiteEditorContext ctx = getEditorContext(req);
		
		if(errors.hasErrors()) {
			Tab tab = tc.getTabConfig().getTabByID("GENERAL");
			TabbedModelAndView tmav = tab.getTabHandler().preProcessNextTab(tc, tab, req, res, comm ,errors);
			tmav.getTabStatus().setCurrentTab(tab);
			tmav.addObject(Constants.GBL_ERRORS, errors);
			return tmav;
		}
		
		// Process the current tab as well as the annotation.
		processTab(tc, currentTab, req, res, comm, errors);
		
		Site site = ctx.getSite();

		//Add note section moved to processTab

		if(sc.isAction(SiteCommand.ACTION_MODIFY_NOTE)) {
			// Modify the annotations
        	Annotation annotation = site.getAnnotation(sc.getAnnotationIndex());
        	if(annotation != null &&
        			annotation.getUser().equals(AuthUtil.getRemoteUserObject()))
        	{
	        	annotation.setDate(new Date());
	        	annotation.setNote(sc.getAnnotation());
	     	}
		}
		else if(sc.isAction(SiteCommand.ACTION_DELETE_NOTE)) {
			// Delete the annotations
        	Annotation annotation = site.getAnnotation(sc.getAnnotationIndex());
        	if(annotation != null &&
        			annotation.getUser().equals(AuthUtil.getRemoteUserObject()))
        	{
	        	site.deleteAnnotation(sc.getAnnotationIndex());
	     	}
		}
		
		bindEditorContext(req, ctx);
		
		TabbedModelAndView tmav = preProcessNextTab(tc, tc.getTabConfig().getTabByID("GENERAL"), req, res, comm, errors);
		tmav.getTabStatus().setCurrentTab(tc.getTabConfig().getTabByID("GENERAL"));
		return tmav;
	}
	
	public void bindEditorContext(HttpServletRequest req, SiteEditorContext context) {
		req.getSession().setAttribute("siteEditorContext", context);
	}

	/**
	 * @param siteManager the siteManager to set
	 */
	public void setSiteManager(SiteManager siteManager) {
		this.siteManager = siteManager;
	}
}
