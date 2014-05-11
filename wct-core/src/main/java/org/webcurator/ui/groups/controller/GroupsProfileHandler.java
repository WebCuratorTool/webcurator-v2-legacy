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
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.target.controller.AbstractOverrideTabHandler;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The  tab handler for the Target Group Profile Tab.
 * @author bbeaumont
 */
public class GroupsProfileHandler extends AbstractOverrideTabHandler {	
	/**
	 * @see org.webcurator.ui.util.TabHandler#preProcessNextTab(TabbedController, Tab, HttpServletRequest, HttpServletResponse, Object, BindException).
	 */
	@Override
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
				
		TabbedModelAndView tmav = super.preProcessNextTab(tc, nextTabID, req, res, comm, errors); 

		// Add the objects to the model.		
		GroupsEditorContext ctx = (GroupsEditorContext) req.getSession().getAttribute(TabbedGroupController.EDITOR_CONTEXT);
		TargetGroup tg = ctx.getTargetGroup();
		Boolean editMode = ctx.isEditMode();		
		
		// FIXME do authority checks here.
		tmav.addObject("ownable", tg);
		tmav.addObject("privlege", Privilege.MANAGE_GROUP_OVERRIDES);		
		tmav.addObject("editMode", editMode);
						
		return tmav;	
	}
}
