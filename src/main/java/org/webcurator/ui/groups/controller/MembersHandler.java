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
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.groups.command.AddParentsCommand;
import org.webcurator.ui.groups.command.MembersCommand;
import org.webcurator.ui.groups.command.MoveTargetsCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The  tab handler for the Target Group Members Tab.
 * @author bbeaumont
 */
public class MembersHandler extends AbstractGroupTabHandler {
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

		MembersCommand command = new MembersCommand();
		command.setSelectedPageSize(currentPageSize);
		
		TargetGroup targetGroup = getEditorContext(req).getTargetGroup();
		
		Pagination  members = targetManager.getMembers(targetGroup, 0, Integer.parseInt(currentPageSize));
		
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject("members", members);
		tmav.addObject("page", members);
		tmav.addObject("subGroupSeparator", subGroupSeparator);
		tmav.addObject(Constants.GBL_CMD_DATA, command);
		
		return tmav;
	}

	@Override
	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {

		MembersCommand command = (MembersCommand) comm;

		// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(req);

		if( MembersCommand.ACTION_UNLINK_MEMBER.equals(command.getActionCmd())) {
			getEditorContext(req).getTargetGroup().getRemovedChildren().add(command.getChildOid());
		}
	
		if(!errors.hasErrors() && MembersCommand.ACTION_MOVE_TARGETS.equals(command.getActionCmd())) {
			
			long[] targetOids = command.getTargetOids();
			List<Long> targetsToMove = new ArrayList<Long>();
			if(targetOids != null)
			{
				for(int i=0; i < targetOids.length; i++)
				{
					targetsToMove.add(targetOids[i]);
				}
			}
			
			getEditorContext(req).setTargetsToMove(targetsToMove);
			
			MoveTargetsCommand cmd = new MoveTargetsCommand();
			
			cmd.setSearch("");
			cmd.setSelectedPageSize(currentPageSize);
			
			Pagination results = targetManager.getGroupDTOs(cmd.getSearch() + "%", 0, Integer.parseInt(cmd.getSelectedPageSize()));
			
			ModelAndView mav = new ModelAndView("group-move-targets");
			mav.addObject(Constants.GBL_CMD_DATA, cmd);
			mav.addObject("page", results);
			return mav;
		}
	
		// Paging command
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.getTabStatus().setCurrentTab(currentTab);
		TargetGroup targetGroup = getEditorContext(req).getTargetGroup();
		
		Pagination members = null;
		if (command.getSelectedPageSize().equals(currentPageSize)) {
			// user has left the page size unchanged..
			members = targetManager.getMembers(targetGroup, command.getPageNumber(), Integer.parseInt(command.getSelectedPageSize()));
		}
		else {
			// user has selected a new page size, so reset to first page..
			members = targetManager.getMembers(targetGroup, 0, Integer.parseInt(command.getSelectedPageSize()));
			// ..then update the page size cookie
			CookieUtils.setPageSize(res, command.getSelectedPageSize());
		}
		
		tmav.addObject("members", members);
		// Add the pagination to the "page" attribute for standard pagination footer.
		tmav.addObject("page", members);
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
	 * @param subGroupSeparator the subGroupSeparator
	 */
	public void setSubGroupSeparator(String subGroupSeparator) {
		this.subGroupSeparator = subGroupSeparator;
	}
}
