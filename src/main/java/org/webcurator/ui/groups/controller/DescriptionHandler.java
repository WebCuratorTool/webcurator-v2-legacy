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
import org.webcurator.core.common.WCTTreeSet;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.DublinCore;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.target.command.DescriptionCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The  tab handler for the Target Group Description Tab.
 * @author bbeaumont
 */
public class DescriptionHandler extends AbstractGroupTabHandler {
	/** the manager for accessing privilege data. */
	private AuthorityManager  authorityManager  = null;
	
	private WCTTreeSet typeList = null;	

	@Override
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		DescriptionCommand command = (DescriptionCommand) comm;
		GroupsEditorContext ctx = getEditorContext(req);
		
		TargetGroup target = ctx.getTargetGroup();
		
		if(ctx.isEditMode()) {
			if( authorityManager.hasPrivilege(target, Privilege.CREATE_GROUP)) {
				DublinCore dc = command.toModelObject();
				if (target.getDublinCoreMetaData() != null) {
					dc.setOid(target.getDublinCoreMetaData().getOid());
				}
				target.setDublinCoreMetaData(dc);
			}			
		}

	}

	@Override
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
	
		TargetGroup aTargetGroup = getEditorContext(req).getTargetGroup();
		
		DescriptionCommand command = DescriptionCommand.fromModel(aTargetGroup.getDublinCoreMetaData());
		
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject(Constants.GBL_CMD_DATA, command);
		tmav.addObject("ownable", aTargetGroup);
		tmav.addObject("privleges", Privilege.CREATE_GROUP);
		tmav.addObject("editMode", getEditorContext(req).isEditMode());
		tmav.addObject("types", typeList);
	
		return tmav;				
		
	}

	@Override
	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		return null;
	}
	
	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}

	/**
	 * @param typeList the typeList to set
	 */
	public void setTypeList(WCTTreeSet typeList) {
		this.typeList = typeList;
	}
	
	
}
