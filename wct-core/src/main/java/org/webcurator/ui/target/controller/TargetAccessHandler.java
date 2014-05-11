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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.Target;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.TargetAccessCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the target access tab.
 * @author oakleigh_sk
 */
public class TargetAccessHandler extends AbstractTargetTabHandler {
	
	private AuthorityManager authorityManager = null;

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		TargetAccessCommand command = (TargetAccessCommand) comm;
		TargetEditorContext ctx = getEditorContext(req);
		
		Target target = ctx.getTarget();
		
		if(ctx.isEditMode()) {
			if( authorityManager.hasAtLeastOnePrivilege(target, Privilege.MODIFY_TARGET, Privilege.CREATE_TARGET)) {
				target.setDisplayTarget(command.isDisplayTarget());
				target.setAccessZone(command.getAccessZone());
				target.setDisplayNote(command.getDisplayNote());
				target.setDisplayChangeReason(command.getDisplayChangeReason());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#preProcessNextTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
		
		Target aTarget = getEditorContext(req).getTarget();
		
		TargetAccessCommand command = TargetAccessCommand.buildFromModel(aTarget);
    	command.setTabType(TargetAccessCommand.TARGET_TYPE);
		
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject(Constants.GBL_CMD_DATA, command);
		tmav.addObject("ownable", aTarget);
		tmav.addObject("privleges", Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET);
		tmav.addObject("editMode", getEditorContext(req).isEditMode());
		tmav.addObject("displayTarget", aTarget.isDisplayTarget());
		tmav.addObject("accessZone", aTarget.getAccessZone());
		tmav.addObject("displayNote", aTarget.getDisplayNote());
		tmav.addObject("displayChangeReason", aTarget.getDisplayChangeReason());
		
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
