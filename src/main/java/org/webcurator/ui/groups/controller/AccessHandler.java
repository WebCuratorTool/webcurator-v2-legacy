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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.target.command.TargetAccessCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the groups access tab.
 * @author oakleigh_sk
 */
public class AccessHandler extends AbstractGroupTabHandler {
	
	private AuthorityManager authorityManager = null;

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		TargetAccessCommand command = (TargetAccessCommand) comm;
		GroupsEditorContext ctx = getEditorContext(req);
		
		TargetGroup targetGroup = ctx.getTargetGroup();
		
		if(ctx.isEditMode()) {
			if( authorityManager.hasAtLeastOnePrivilege(targetGroup, Privilege.MANAGE_GROUP, Privilege.CREATE_GROUP)) {
				targetGroup.setDisplayTarget(command.isDisplayTarget());
				targetGroup.setAccessZone(command.getAccessZone());
				targetGroup.setDisplayNote(command.getDisplayNote());
				targetGroup.setDisplayChangeReason(command.getDisplayChangeReason());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#preProcessNextTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
		
		TargetGroup aTargetGroup = getEditorContext(req).getTargetGroup();
		
    	TargetAccessCommand command = new TargetAccessCommand();
    	command.setTabType(TargetAccessCommand.GROUP_TYPE);
    	command.setDisplayTarget(aTargetGroup.isDisplayTarget());
    	command.setDisplayNote(aTargetGroup.getDisplayNote());
    	command.setDisplayChangeReason(aTargetGroup.getDisplayChangeReason());
    	command.setAccessZone(aTargetGroup.getAccessZone());
		
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject(Constants.GBL_CMD_DATA, command);
		tmav.addObject("ownable", aTargetGroup);
		tmav.addObject("privleges", Privilege.MANAGE_GROUP + ";" + Privilege.CREATE_GROUP);
		tmav.addObject("editMode", getEditorContext(req).isEditMode());
		tmav.addObject("displayTarget", aTargetGroup.isDisplayTarget());
		tmav.addObject("accessZone", aTargetGroup.getAccessZone());
		tmav.addObject("displayNote", aTargetGroup.getDisplayNote());
		tmav.addObject("displayChangeReason", aTargetGroup.getDisplayChangeReason());
		
		return tmav;		
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processOther(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}
}
