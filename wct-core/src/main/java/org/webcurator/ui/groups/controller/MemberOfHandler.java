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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.CookieUtils;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.dto.GroupMemberDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO.SAVE_STATE;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.groups.command.MemberOfCommand;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The  tab handler for the Target Group 'Member Of' Tab.
 * @author oakleigh_sk
 */
public class MemberOfHandler extends AbstractGroupTabHandler {
	/** the manager for accessing target and group data. */
	private TargetManager targetManager = null;
	private String subGroupSeparator;
	
	/**
	 * @see org.webcurator.ui.util.TabHandler#initBinder(javax.servlet.http.HttpServletRequest, org.springframework.web.bind.ServletRequestDataBinder)
	 */
	@Override
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);

		// Register a number binder.
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, nf, true));
	}

	@Override
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		// Not supported.
	}

	@Override
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
		
		// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(req);

		MemberOfCommand command = new MemberOfCommand();
		command.setSelectedPageSize(currentPageSize);
		
		// Get the parent DTOs
		Pagination memberOfGroups = getParents(req, 0, Integer.parseInt(currentPageSize));
		
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject("memberof", memberOfGroups);
		tmav.addObject("page", memberOfGroups);
		tmav.addObject("subGroupSeparator", subGroupSeparator);
		tmav.addObject(Constants.GBL_CMD_DATA, command);
		
		return tmav;
	}

	private List<GroupMemberDTO> getParents(HttpServletRequest req) {
		// Load the Editor Context
		GroupsEditorContext ctx = getEditorContext(req);
		TargetGroup target = ctx.getTargetGroup();
		
		
		List<GroupMemberDTO> parents = ctx.getParents();
		if(parents == null) { 
			parents = targetManager.getParents(target);
			ctx.setParents(parents);
		}		
		
		return parents;
	}

	Pagination getParents(HttpServletRequest req, int page, int pageSize)
	{
		List<GroupMemberDTO> activeParents = new ArrayList<GroupMemberDTO>();
		List<GroupMemberDTO> parents = getParents(req);
		Iterator<GroupMemberDTO> it = parents.iterator();
		while(it.hasNext())
		{
			GroupMemberDTO parent = it.next();
			if(parent.getSaveState() != SAVE_STATE.DELETED)
			{
				activeParents.add(parent);
			}
		}
		
		return new Pagination(activeParents, page, pageSize);
	}
	
	@Override
	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {

		MemberOfCommand command = (MemberOfCommand) comm;

		// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(req);
		
		if( MemberOfCommand.ACTION_UNLINK_FROM_GROUP.equals(command.getActionCmd())) {
			getEditorContext(req).getTargetGroup().getRemovedChildren().add(command.getChildOid());
		}
	
		// Paging command
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.getTabStatus().setCurrentTab(currentTab);
		
		List<GroupMemberDTO> parents = getParents(req);
		
		Pagination memberOfGroups = null;
		if (command.getSelectedPageSize().equals(currentPageSize)) {
			// user has left the page size unchanged..
			memberOfGroups = getParents(req, command.getPageNumber(), Integer.parseInt(command.getSelectedPageSize())); 
		}
		else {
			// user has selected a new page size, so reset to first page..
			memberOfGroups = getParents(req, 0, Integer.parseInt(command.getSelectedPageSize())); 
			// ..then update the page size cookie
			CookieUtils.setPageSize(res, command.getSelectedPageSize());
		}
		
		tmav.addObject("memberof", memberOfGroups);
		
		// Add the pagination to the "page" attribute for standard pagination footer.
		tmav.addObject("page", memberOfGroups);
		tmav.addObject("subGroupSeparator", subGroupSeparator);
		tmav.addObject(Constants.GBL_CMD_DATA, command);
		return tmav;
	}

	/** 
	 * @param aTargetManager the manager for accessing target and group data.
	 */
	public void setTargetManager(TargetManager aTargetManager) {
		this.targetManager = aTargetManager;
	}

	/** 
	 * @param subGroupSeparator the subGroupSeparator.
	 */
	public void setSubGroupSeparator(String subGroupSeparator) {
		this.subGroupSeparator = subGroupSeparator;
	}
}
