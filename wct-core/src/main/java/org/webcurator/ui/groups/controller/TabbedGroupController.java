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
package org.webcurator.ui.groups.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.groups.command.DefaultCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;

/**
 * The controller for handling the Target Group Tabs.
 * @author bbeaumont
 */
public class TabbedGroupController extends TabbedController {
	/** The constant name of the group editor context. */
	public static final String EDITOR_CONTEXT = "groupEditorContext";
	/** the target groups business object factory. */
	private BusinessObjectFactory businessObjectFactory = null;
	/** the group search controller. */
	private GroupSearchController searchController = null;
	/** the message source. */
	private MessageSource messageSource = null;
	/** the target manager. */
	private TargetManager targetManager = null;
	
	private AuthorityManager authorityManager = null;
	
	@Override
	protected void switchToEditMode(HttpServletRequest req) {
		getEditorContext(req).setEditMode(true);
		bindEditorContext(req, getEditorContext(req));
	};
	
	/**
	 * Bind the group context editor to the session.
	 * @param req the request to get the session from
	 * @param context the context to bind
	 */
	public void bindEditorContext(HttpServletRequest req, GroupsEditorContext context) {
		req.getSession().setAttribute(TabbedGroupController.EDITOR_CONTEXT, context);
		req.getSession().setAttribute(Constants.GBL_SESS_EDIT_MODE, context.isEditMode());
		req.getSession().setAttribute(Constants.GBL_SESS_CAN_EDIT, context.isCanEdit());
	}

	/** 
	 * unbind the group context editor from the session.
	 * @param req the request to get the session from
	 */
	public void unbindEditorContext(HttpServletRequest req) {
		req.getSession().removeAttribute(TabbedGroupController.EDITOR_CONTEXT);
		req.getSession().removeAttribute(Constants.GBL_SESS_EDIT_MODE);
		req.getSession().removeAttribute(Constants.GBL_SESS_CAN_EDIT);
	}	
	
	/**
	 * Return the editor context from the session.
	 * @param req the request to get the session from
	 * @return the context editor
	 */
	public GroupsEditorContext getEditorContext(HttpServletRequest req) {
		GroupsEditorContext ctx = (GroupsEditorContext) req.getSession().getAttribute(TabbedGroupController.EDITOR_CONTEXT);
		if( ctx == null) {
			throw new IllegalStateException("groupEditorContext not yet bound to the session");
		}
		return ctx;
	}		
	
	@Override
	protected ModelAndView processSave(Tab currentTab, HttpServletRequest req,
			HttpServletResponse res, Object comm, BindException errors) {
		GroupsEditorContext ctx = getEditorContext(req);
		TargetGroup group = ctx.getTargetGroup();
		
		// Check that the group name is okay.
		if(!targetManager.isNameOk(group)) {
			errors.reject("target.errors.duplicatename");
		}
		
		if(errors.hasErrors()) {
			TabbedModelAndView tmav = currentTab.getTabHandler().preProcessNextTab(this, currentTab, req, res, comm ,errors);
			tmav.getTabStatus().setCurrentTab(currentTab);
			tmav.addObject(Constants.GBL_CMD_DATA, comm);
			tmav.addObject(Constants.GBL_ERRORS, errors);
			return tmav;
		}
		else {
			try {
				targetManager.save(ctx.getTargetGroup(), ctx.getParents());
				
				ModelAndView mav = searchController.prepareSearchView(req, res, errors);
				mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("group.saved", new Object[] { ctx.getTargetGroup().getName() }, Locale.getDefault()));
				
				// Remove from the session and go back to search.
				unbindEditorContext(req);
				
				return mav;
				
			}
			catch(DataIntegrityViolationException ex) {
				// Probably due to an error with the name of the target. The
				// name has already been checked, but concurrency problems
				// could lead us back here.
				errors.reject("target.errors.duplicatename");
				
				// Redirect back to the general tab.
				Tab tab = getTabConfig().getTabByID("GENERAL");
				TabbedModelAndView tmav = tab.getTabHandler().preProcessNextTab(this, tab, req, res, comm ,errors);
				tmav.getTabStatus().setCurrentTab(tab);
				tmav.addObject(Constants.GBL_ERRORS, errors);
				return tmav;
			}
			
		}
		
	}

	@Override
	protected ModelAndView processCancel(Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		searchController.prepareSearchView(req, res, errors);
		
		// Remove from the session and go back to search.
		unbindEditorContext(req);
		return new ModelAndView("redirect:/curator/groups/search.html");
	}

	@Override
	protected ModelAndView processInitial(HttpServletRequest req,
			HttpServletResponse res, Object comm, BindException errors) {
		Tab general = getTabConfig().getTabByID("GENERAL");
		TabbedModelAndView tmav = general.getTabHandler().preProcessNextTab(this, general, req, res, null, errors);
		tmav.getTabStatus().setCurrentTab(general);
		return tmav;
	}

	@Override
	protected ModelAndView showForm(HttpServletRequest req,
			HttpServletResponse res, Object comm, BindException errors)
			throws Exception {
		
		DefaultCommand command = (DefaultCommand) comm;
		if( command != null && command.getTargetGroupOid() != null) {
			TargetGroup aTargetGroup = targetManager.loadGroup(command.getTargetGroupOid(), true);
			
			if(command.isCopyMode()) {
				TargetGroup newGroup = targetManager.copy(aTargetGroup);
				GroupsEditorContext ctx = new GroupsEditorContext(newGroup, true);
				bindEditorContext(req, ctx);
			}
			else {
				GroupsEditorContext ctx = new GroupsEditorContext(aTargetGroup, command.isEditMode());
				
				//Code to display edit button (in layouts\tabbed-new.jsp) driven by session vars
				ctx.setCanEdit(false);
				
				if (!command.isEditMode())
				{
					if (authorityManager.hasPrivilege(aTargetGroup,Privilege.CREATE_GROUP))
					{
						ctx.setCanEdit(true);
					}	
				}
				
				bindEditorContext(req, ctx);
			}
			
			
			
		}
		else {
			TargetGroup aTargetGroup = businessObjectFactory.newTargetGroup();
			GroupsEditorContext ctx = new GroupsEditorContext(aTargetGroup, true);
			bindEditorContext(req, ctx);
		}
		
		Tab general = getTabConfig().getTabByID("GENERAL");
		TabbedModelAndView tmav = general.getTabHandler().preProcessNextTab(this, general, req, res, null, errors);
		tmav.getTabStatus().setCurrentTab(general);
		return tmav;
	}

	/**
	 * @param targetManager The targetManager to set.
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}

	/**
	 * @param businessObjectFactory The businessObjectFactory to set.
	 */
	public void setBusinessObjectFactory(BusinessObjectFactory businessObjectFactory) {
		this.businessObjectFactory = businessObjectFactory;
	}

	/**
	 * @param searchController The searchController to set.
	 */
	public void setSearchController(GroupSearchController searchController) {
		this.searchController = searchController;
	}

	/**
	 * @param messageSource The messageSource to set.
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	/**
	 * @param authorityManager the authorityManager to set
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}
}
