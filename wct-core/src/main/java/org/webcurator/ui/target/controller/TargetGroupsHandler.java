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
package org.webcurator.ui.target.controller;

import java.text.NumberFormat;
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
import org.webcurator.domain.model.dto.GroupMemberDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO.SAVE_STATE;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.TargetGroupsCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * Handler for adding groups to a Target.
 * @author beaumontb
 *
 */
public class TargetGroupsHandler extends AbstractTargetTabHandler {

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
	public TabbedModelAndView preProcessNextTab(TabbedController tc, Tab nextTabID, HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) {

		// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(req);

		// Get the parent DTOs
		List<GroupMemberDTO> parents = getParents(req);
		
		Pagination page = new Pagination(parents, 0, Integer.parseInt(currentPageSize));

		// Create the view.
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject("page", page);
		tmav.addObject("subGroupSeparator", subGroupSeparator);
		return tmav;
	}
	
	private List<GroupMemberDTO> getParents(HttpServletRequest req) {
		// Load the Editor Context
		TargetEditorContext ctx = getEditorContext(req);
		Target target = ctx.getTarget();
		
		
		List<GroupMemberDTO> parents = ctx.getParents();
		if(parents == null) { 
			parents = targetManager.getParents(target);
			ctx.setParents(parents);
		}		
		
		return parents;
	}

	@Override
	public ModelAndView processOther(TabbedController tc, Tab currentTab, HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) {
		
		// Cast the command object.
		TargetGroupsCommand cmd = (TargetGroupsCommand) comm;

		// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(req);
		
		// Load the Editor Context
		TargetEditorContext ctx = getEditorContext(req);
		Target target = ctx.getTarget();		
		
		if( TargetGroupsCommand.ACTION_UNLINK_PARENT.equals(cmd.getActionCmd())) {
			GroupMemberDTO dto = new GroupMemberDTO(cmd.getParentOid(), target.getOid());
			ctx.getParents().get(ctx.getParents().indexOf(dto)).setSaveState(SAVE_STATE.DELETED);
		}
		
		// Paging command
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.getTabStatus().setCurrentTab(currentTab);
		
		Pagination members = null;
		if (cmd.getSelectedPageSize().equals(currentPageSize)) {
			// user has left the page size unchanged..
			members = new Pagination(getParents(req), cmd.getPageNumber(), Integer.parseInt(cmd.getSelectedPageSize()));
		}
		else {
			// user has selected a new page size, so reset to first page..
			members = new Pagination(getParents(req), 0, Integer.parseInt(cmd.getSelectedPageSize()));
			// ..then update the page size cookie
			CookieUtils.setPageSize(res, cmd.getSelectedPageSize());
		}
		
		// Add the pagination to the "page" attribute for standard pagination footer.
		tmav.addObject("page", members);
		tmav.addObject("subGroupSeparator", subGroupSeparator);
		tmav.addObject(Constants.GBL_CMD_DATA, cmd);
		return tmav;
	}

	@Override
	public void processTab(TabbedController tc, Tab currentTab, HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) {

		
	}

	/**
	 * @param targetManager the targetManager to set
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}
	
	/** 
	 * @param subGroupSeparator the subGroupSeparator
	 */
	public void setSubGroupSeparator(String subGroupSeparator) {
		this.subGroupSeparator = subGroupSeparator;
	}

}
