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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.sites.SiteManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.AuthorisingAgent;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.core.UrlPattern;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.ui.site.command.DefaultSiteCommand;
import org.webcurator.ui.site.command.SiteCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;

/** 
 * The Controller for managing the Harvest Autorisation tabs.
 * @author bbeaumont
 */
public class SiteController extends TabbedController {
	
	public static final String EDITOR_CONTEXT = "siteEditorContext";
	
	/** Logger for the Siteontroller. **/
	private static Log log = LogFactory.getLog(SiteController.class);
	
	private SiteManager siteManager = null;
	
	private AuthorityManager authorityManager = null;
	
	/** BusinessObjectFactory */
	private BusinessObjectFactory businessObjectFactory = null;
	
	/** The message source for localised messages. */
	private MessageSource messageSource = null;
	
	/** The site search controller */
	private SiteSearchController siteSearchController = null; 
	
	public SiteController() {
		this.setCommandClass(SiteCommand.class);
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabbedController#initBinder(javax.servlet.http.HttpServletRequest, org.springframework.web.bind.ServletRequestDataBinder)
	 */
	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
		
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(Long.class, "siteOid", new CustomNumberEditor(Long.class, nf, true));
	}	
	
	
	public void bindEditorContext(HttpServletRequest req, SiteEditorContext context) {
		req.getSession().setAttribute(EDITOR_CONTEXT, context);
		req.getSession().setAttribute(Constants.GBL_SESS_EDIT_MODE, context.isEditMode());
		req.getSession().setAttribute(Constants.GBL_SESS_CAN_EDIT, context.isCanEdit());
	}

	public void unbindEditorContext(HttpServletRequest req) {
		req.getSession().removeAttribute(EDITOR_CONTEXT);
		req.getSession().removeAttribute(Constants.GBL_SESS_EDIT_MODE);		
		req.getSession().removeAttribute(Constants.GBL_SESS_CAN_EDIT);
	}	
	
	public SiteEditorContext getEditorContext(HttpServletRequest req) {
		SiteEditorContext ctx = (SiteEditorContext) req.getSession().getAttribute(EDITOR_CONTEXT);
		if( ctx == null) {
			throw new IllegalStateException("siteEditorContext not yet bound to the session");
		}
		
		return ctx;
	}
	
	@Override
	protected void switchToEditMode(HttpServletRequest req) {
		getEditorContext(req).setEditMode(true);
		bindEditorContext(req, getEditorContext(req));
	};
	
	@Override
	protected ModelAndView showForm(HttpServletRequest req, HttpServletResponse res, Object comm, BindException bex) throws Exception {
		log.debug("------------------------showForm-------------------------------");
		return processInitial(req, res, comm, bex);
	}

	
	private void checkAuthAgencyNamesUnique(HttpServletRequest request, BindException errors) {
		SiteEditorContext ctx = getEditorContext(request);
		for(AuthorisingAgent agent: ctx.getSite().getAuthorisingAgents()) {
			if( !siteManager.isAuthAgencyNameUnique( agent.isNew() ? null : agent.getOid(), agent.getName())) {
				errors.reject("site.errors.authagent.duplicatename", new Object[] { agent.getName() }, "");
			}
		}
	}
	
	public void checkSave(HttpServletRequest req, BindException errors) { 
		if (!errors.hasErrors() && !siteManager.isSiteTitleUnique(getEditorContext(req).getSite())) {
			errors.reject("site.errors.duplicatename", new Object[] {getEditorContext(req).getSite().getTitle()} ,"");
		}
		
		checkAuthAgencyNamesUnique(req, errors);
	}
	
	public ModelAndView getErrorsView(Tab currentTab, HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) {
		TabbedModelAndView tmav = currentTab.getTabHandler().preProcessNextTab(this, currentTab, req, res, comm ,errors);
		tmav.getTabStatus().setCurrentTab(currentTab);
		tmav.addObject(Constants.GBL_CMD_DATA,  comm);
		tmav.addObject(Constants.GBL_ERRORS, errors);
		return tmav;		
	}

	@Override
	protected ModelAndView processSave(Tab currentTab, HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) {
		
		log.debug("------------------------processSave-------------------------------");
		checkSave(req, errors);
		
		if(errors.hasErrors()) {
			return getErrorsView(currentTab, req, res, comm, errors);
		}
		
		// Save the object.
		try {
			siteManager.save(getEditorContext(req).getSite());

			// Go back to the search screen.
			ModelAndView mav = siteSearchController.showForm(req, res, errors);
			mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("site.saved", new Object[] { getEditorContext(req).getSite().getTitle() }, Locale.getDefault()));
			return mav;
		} 
		catch (Exception ex) {
			throw new WCTRuntimeException(ex.getMessage(), ex);
		}
		finally {
			// Take the editor context out of the session.
			unbindEditorContext(req);
		}
	}

	@Override
	protected ModelAndView processCancel(Tab currentTab, HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) {
		// Take the editor context out of the session.
		log.debug("------------------------processCancel-------------------------------");
		unbindEditorContext(req);
		return new ModelAndView("redirect:/curator/site/search.html");
	}
	

    @Override
    protected ModelAndView processInitial(HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) {
    	
    	if (log.isDebugEnabled()) {
    		log.debug("Processing intial in SiteController.");
    	}
    	
    	DefaultSiteCommand command = (DefaultSiteCommand) comm;
    	Site theSite = null;

    	log.debug("------------------------hasSubmitParameter:-------------------------------");
	log.debug(WebUtils.hasSubmitParameter(req, "_tab_edit"));
    	
    	if(command.getSiteOid() != null) {
    		theSite = siteManager.getSite(command.getSiteOid(), true);
    		List<Annotation> annotations = siteManager.getAnnotations(theSite);
    		theSite.setAnnotations(annotations);
    		
    		for(Permission p: theSite.getPermissions()) {
    			p.setAnnotations(siteManager.getAnnotations(p));
    		}
    		
    	}
    	else {
    		theSite = new Site();
    		theSite.setOwningAgency(AuthUtil.getRemoteUserObject().getAgency());
    	}

    	if (command.isCopyMode()) {
    		if (log.isDebugEnabled()) {
    			log.debug("About to process copy of site " + command.getSiteOid());
    		}
    		Site siteCopy = new Site();
    		siteCopy.setOwningAgency(AuthUtil.getRemoteUserObject().getAgency());
    		siteCopy.setDescription(theSite.getDescription());
    		siteCopy.setLibraryOrderNo(theSite.getLibraryOrderNo());
    		siteCopy.setPublished(theSite.isPublished());
    		
    		HashSet<AuthorisingAgent> agents = new HashSet<AuthorisingAgent>();    
			agents.addAll(theSite.getAuthorisingAgents());
    		siteCopy.setAuthorisingAgents(agents);
    		
    		UrlPattern p = null;
    		HashSet<UrlPattern> patterns = new HashSet<UrlPattern>();
    		for (UrlPattern pattern : theSite.getUrlPatterns()) {
				p = businessObjectFactory.newUrlPattern(siteCopy);
				p.setPattern(pattern.getPattern());
				patterns.add(p);
			}    		
    		siteCopy.setUrlPatterns(patterns);
    		theSite = siteCopy;
    		command.setCopyMode(false);
    		command.setEditMode(true);
    	}
    	
		// Add the site into the editor context.
		SiteEditorContext ctx = new SiteEditorContext(theSite);
		
		if (command.isEditMode()) {
			String[] privs = {Privilege.CREATE_SITE, Privilege.MODIFY_PERMISSION, Privilege.CONFIRM_PERMISSION};		
			if (authorityManager.hasAtLeastOnePrivilege(privs)) {
				ctx.setEditMode(command.isEditMode());				
			}
			else {
				ctx.setEditAnnotations(true);
			}
		}
		
		ctx.setCanEdit(false);
		if (!command.isEditMode() && command.getSiteOid() != null)
		{
			if (theSite.getOwningAgency() != null && authorityManager.hasPrivilege(theSite, Privilege.MODIFY_SITE))
			{
				ctx.setCanEdit(true);
			}	
		}

		bindEditorContext(req, ctx);
		
		// Load the tabbed model and view.
		TabbedModelAndView tmav = new TabbedModelAndView();
		tmav.addObject("command", SiteCommand.buildFromModel(ctx.getSite()));
		tmav.addObject("editMode", ctx.isEditMode());
		tmav.getTabStatus().setCurrentTab(getTabConfig().getTabByID("GENERAL"));
		return tmav;
    	
    }

	/**
	 * @param siteManager The siteManager to set.
	 */
	public void setSiteManager(SiteManager siteManager) {
		this.siteManager = siteManager;
	}

	/**
	 * @param authorityManager the authorityManager to set
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}

	/**
	 * @param businessObjectFactory the businessObjectFactory to set
	 */
	public void setBusinessObjectFactory(BusinessObjectFactory businessObjectFactory) {
		this.businessObjectFactory = businessObjectFactory;
	}

	/**
	 * @param messageSource The messageSource to set.
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @param siteSearchController The siteSearchController to set.
	 */
	public void setSiteSearchController(SiteSearchController siteSearchController) {
		this.siteSearchController = siteSearchController;
	}
}
