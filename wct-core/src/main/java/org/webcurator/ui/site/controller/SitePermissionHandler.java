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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.webcurator.core.sites.SiteManager;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.core.UrlPattern;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.ui.site.command.SitePermissionCommand;
import org.webcurator.ui.site.editor.EditorContextObjectEditor;
import org.webcurator.ui.site.editor.UrlPatternCollectionEditor;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.Utils;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the harvest authorisation permission tab.
 * @author bbeaumont
 */
public class SitePermissionHandler extends AbstractSiteHandler {
	/** The SiteManager */
	private SiteManager siteManager = null;
	/** The list of available Access Status strings */
	private List<String> accessStatusList = null;
	/** The Business Object Factory */
	private BusinessObjectFactory businessObjectFactory = null;
	
	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#initBinder(javax.servlet.http.HttpServletRequest, org.springframework.web.bind.ServletRequestDataBinder)
	 */
	@Override
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
		
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());	
        
        // Register the binders.
        binder.registerCustomEditor(Long.class, "selectedPermission", new CustomNumberEditor(Long.class, nf, true));
		binder.registerCustomEditor(java.util.Date.class, "startDate", DateUtils.get().getFullDateEditor(true));
		binder.registerCustomEditor(java.util.Date.class, "endDate", DateUtils.get().getFullDateEditor(true));
		
		// If the session model is available, we want to register the Permission's
		// authorising agency editor.
		if(getEditorContext(request) != null) {
			//binder.registerCustomEditor(AuthorisingAgent.class, new PermissionAuthAgencyEditor(sessionModel.getAuthorisingAgents()));
			binder.registerCustomEditor(AuthorisingAgent.class, "authorisingAgent", new EditorContextObjectEditor(getEditorContext(request), AuthorisingAgent.class));
			binder.registerCustomEditor(Set.class, "urls", new UrlPatternCollectionEditor(Set.class, true, getEditorContext(request)));
		}
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {

	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#preProcessNextTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
		
		SiteEditorContext ctx = getEditorContext(req);
		List<Permission> permissions = new LinkedList<Permission>();
		permissions.addAll(ctx.getSite().getPermissions());
		
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject("permissions", permissions);
		
		return tmav;	

	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processOther(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		SiteEditorContext ctx = getEditorContext(req);
		
		// Handle the move into New Permission. 
		if(WebUtils.hasSubmitParameter(req, "_new")) {
			Permission newPermission = businessObjectFactory.newPermission(ctx.getSite());
			ctx.putObject(newPermission);
			SitePermissionCommand command = SitePermissionCommand.buildFromModel(newPermission);
							
			ModelAndView mav = new ModelAndView();
			mav.addObject(Constants.GBL_CMD_DATA, command);						
			mav.addObject("urls", ctx.getSortedUrlPatterns());
			mav.addObject("agents", ctx.getSortedAuthAgents());			
			mav.addObject("accessStatusList", accessStatusList);
			mav.addObject("permissionEditMode", true);
			mav.addObject("permission", newPermission);
			
			mav.setViewName(Constants.VIEW_SITE_PERMISSIONS);
			return mav;								
		}	
		
		// Handle editing an existing permission 
		if(WebUtils.hasSubmitParameter(req, "_edit_permission")) {
			SitePermissionCommand command = (SitePermissionCommand) comm;
			Permission permission = (Permission) ctx.getObject(Permission.class, command.getSelectedPermission());
			
			//make sure annotations are sorted
			permission.sortAnnotations();
			
			List<Annotation> oldPermissionAnnotations = new ArrayList<Annotation>();
			req.getSession().setAttribute("oldPermissionAnnotations", oldPermissionAnnotations);
			
			SitePermissionCommand c2 = SitePermissionCommand.buildFromModel(permission);
			
			ModelAndView mav = new ModelAndView();
			mav.addObject(Constants.GBL_CMD_DATA, c2);
			mav.addObject("urls", ctx.getSortedUrlPatterns());
			mav.addObject("agents", ctx.getSortedAuthAgents());	
			mav.addObject("accessStatusList", accessStatusList);
			mav.addObject("permissionEditMode", true);
			mav.addObject("permission", permission);
			mav.setViewName(Constants.VIEW_SITE_PERMISSIONS);
			return mav;				
		}	
		
		// Handle editing an existing permission 
		if(WebUtils.hasSubmitParameter(req, "_view_permission")) {
			SitePermissionCommand command = (SitePermissionCommand) comm;
			Permission permission = (Permission) ctx.getObject(Permission.class, command.getSelectedPermission());
			
			//make sure annotations are sorted
			permission.sortAnnotations();
			
			List<Annotation> oldPermissionAnnotations = new ArrayList<Annotation>();
			if(permission.getAnnotations() != null) {
				oldPermissionAnnotations.addAll(permission.getAnnotations());
			}			
			req.getSession().setAttribute("oldPermissionAnnotations", oldPermissionAnnotations);
			
			SitePermissionCommand c2 = SitePermissionCommand.buildFromModel(permission);
			
			ModelAndView mav = new ModelAndView();
			mav.addObject(Constants.GBL_CMD_DATA, c2);
			mav.addObject("urls", ctx.getSortedUrlPatterns());
			mav.addObject("agents", ctx.getSortedAuthAgents());	
			mav.addObject("accessStatusList", accessStatusList);
			mav.addObject("permissionEditMode", false);
			mav.addObject("permission", permission);
			mav.setViewName(Constants.VIEW_SITE_PERMISSIONS);
			return mav;				
	
		}
				
		if(WebUtils.hasSubmitParameter(req, "_remove_permission")) {
			SitePermissionCommand command = (SitePermissionCommand) comm;
			Site site = ctx.getSite();
			Permission permission = (Permission) ctx.getObject(Permission.class, command.getSelectedPermission());

			// Remove all URL Patterns from the permission.
			for(UrlPattern u: permission.getUrls()) {
				u.getPermissions().remove(permission);
			}
			permission.getUrls().clear();
			
			if(!permission.isNew() && siteManager.countLinkedSeeds(permission.getOid()) > 0) {
				errors.reject("site.permission.delete.linked_seeds", "There are still seeds linked to this permission. Either unlink the seeds, or transfer them to another permission.");
				// Raise error because seeds are still attached.
			}
			
			if(errors.hasErrors()) {
				// Go back to the list of permissions
				TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
				tmav.getTabStatus().setCurrentTab(currentTab);
				tmav.addObject(Constants.GBL_ERRORS, errors);
				return tmav;
				
			}
			else {
				site.removePermission(permission);
			
				// Go back to the list of permissions
				TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
				tmav.getTabStatus().setCurrentTab(currentTab);			
				return tmav;
			}
		}
		
		return null;
	}

	/**
	 * @param siteManager The siteManager to set.
	 */
	public void setSiteManager(SiteManager siteManager) {
		this.siteManager = siteManager;
	}

	/**
	 * @param accessStatusList The accessStatusList to set.
	 */
	public void setAccessStatusList(List<String> accessStatusList) {
		this.accessStatusList = accessStatusList;
	}
	
	/**
	 * @param businessObjectFactory the businessObjectFactory to set
	 */
	public void setBusinessObjectFactory(BusinessObjectFactory businessObjectFactory) {
		this.businessObjectFactory = businessObjectFactory;
	}	
}
