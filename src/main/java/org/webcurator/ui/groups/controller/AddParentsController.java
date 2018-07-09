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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.CookieUtils;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.dto.GroupMemberDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO.SAVE_STATE;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.groups.command.AddParentsCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * This controller manages the process of adding members to a Target Group.
 * @author bbeaumont
 */
public class AddParentsController extends AbstractCommandController {
	/** the manager for Target and Group data. */
	private TargetManager targetManager = null;
	/** the parent controller for this handler. */
	private TabbedGroupController groupsController = null;
	/** the manager for checking privleges. */
	private AuthorityManager authorityManager = null;
	
	
	
	/** Default COnstructor. */
	public AddParentsController() {
		setCommandClass(AddParentsCommand.class);
	}
	
	/**
	 * Retrive the editor context for the groups controller.
	 * @param req The HttpServletRequest so the session can be retrieved.
	 * @return The editor context.
	 */
	public GroupsEditorContext getEditorContext(HttpServletRequest req) {
		GroupsEditorContext ctx = (GroupsEditorContext) req.getSession().getAttribute(TabbedGroupController.EDITOR_CONTEXT);
		if( ctx == null) {
			throw new IllegalStateException("tabEditorContext not yet bound to the session");
		}
		
		return ctx;		
	}	
	
	private List<GroupMemberDTO> getParents(HttpServletRequest req) {
		return targetManager.getParents(getEditorContext(req).getTargetGroup());
	}	
	
	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object comm, BindException errors) throws Exception {
		
		AddParentsCommand command = (AddParentsCommand) comm;
		TargetGroup target = getEditorContext(request).getTargetGroup();

		if( AddParentsCommand.ACTION_ADD_PARENTS.equals(command.getActionCmd())) 
		{
			List<GroupMemberDTO> parents = getParents(request);
			GroupMemberDTO newDTO = null;
			if(command.getParentOids() != null && command.getParentOids().length == 1)
			{
				newDTO = targetManager.createGroupMemberDTO(target, command.getParentOids()[0]);
				newDTO.setSaveState(SAVE_STATE.NEW);
				
				if(parents.contains(newDTO))
				{
					// Trying to add a duplicate.
					String name = newDTO.getParentName();
					errors.reject("target.error.duplicate_parent", new Object[] { name }, "This target is already in this group");
				}
			}
			else
			{
				errors.reject("groups.errors.addparents.must_select", null, "You must select a parent group");
			}
			
			if(errors.hasErrors()) {
				return doSearch(request, response, comm, errors);
			}
			else {
				Tab generalTab = groupsController.getTabConfig().getTabByID("GENERAL");
				TabbedModelAndView tmav = generalTab.getTabHandler().preProcessNextTab(groupsController, generalTab, request, response, command, errors);
				tmav.getTabStatus().setCurrentTab(generalTab);
				return tmav;
			}
		}
		else if( AddParentsCommand.ACTION_CANCEL.equals(command.getActionCmd())) {
			// Go back to the Members tab on the groups controller.
			Tab generalTab = groupsController.getTabConfig().getTabByID("GENERAL");
			TabbedModelAndView tmav = generalTab.getTabHandler().preProcessNextTab(groupsController, generalTab, request, response, command, errors);
			tmav.getTabStatus().setCurrentTab(generalTab);
			return tmav;
		}
		else 
		{
			return doSearch(request, response, comm, errors);
		}
	}
	
	/** 
	 * Perform the search for Group members. 
	 */
	private ModelAndView doSearch(HttpServletRequest request, HttpServletResponse response, Object comm, BindException errors) throws Exception {
		
		// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(request);

		AddParentsCommand command = (AddParentsCommand) comm;
		
		if(command.getSearch() == null) {
			command.setSearch("");
			command.setSelectedPageSize(currentPageSize);
		}
		
		Pagination results = null;
		if (command.getSelectedPageSize().equals(currentPageSize)) {
			// user has left the page size unchanged..
			results = targetManager.getSubGroupParentDTOs(command.getSearch() + "%", command.getPageNumber(), Integer.parseInt(command.getSelectedPageSize()));
		}
		else {
			// user has selected a new page size, so reset to first page..
			results = targetManager.getSubGroupParentDTOs(command.getSearch() + "%", 0, Integer.parseInt(command.getSelectedPageSize()));
			// ..then update the page size cookie
			CookieUtils.setPageSize(response, command.getSelectedPageSize());
		}
		
		ModelAndView mav = new ModelAndView("group-add-parents");
		mav.addObject("page", results);
		mav.addObject(Constants.GBL_CMD_DATA, command);
		if(errors.hasErrors()) { mav.addObject(Constants.GBL_ERRORS, errors); }
		return mav;		
	}


	/**
	 * @param groupsController The groupsController to set.
	 */
	public void setGroupsController(TabbedGroupController groupsController) {
		this.groupsController = groupsController;
	}


	/**
	 * @param targetManager The targetManager to set.
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}


	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}
}
