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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.ProfileOverrides;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.target.command.TargetInstanceProfileCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the target instance profiles tab.
 * @author nwaight
 */
public class TargetInstanceProfileHandler extends AbstractOverrideTabHandler {	
	
    private static Log log = LogFactory.getLog(TargetInstanceProfileHandler.class);
	
    //Work out edit mode based on session mode and target instance state 
    private Boolean getEditMode(HttpServletRequest req)
    {
		TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI);
		Boolean editMode = (Boolean) req.getSession().getAttribute(TargetInstanceCommand.SESSION_MODE);		
		
		if (!ti.getState().equals(TargetInstance.STATE_SCHEDULED) && !ti.getState().equals(TargetInstance.STATE_QUEUED) && !ti.getState().equals(TargetInstance.STATE_PAUSED)) {
			editMode = new Boolean(false);
		}
		
		return editMode;
    }
    
	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#preProcessNextTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
				
		log.debug("preProcessNextTab");
		TabbedModelAndView tmav = super.preProcessNextTab(tc, nextTabID, req, res, comm, errors); 

		// Add the objects to the model.		
		TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI);
		Boolean editMode = getEditMode(req);		
		
		// FIXME do authority checks here.
		tmav.addObject("ownable", ti);
		tmav.addObject("privlege", Privilege.MANAGE_TARGET_INSTANCES);		
		tmav.addObject("editMode", editMode);
						
		return tmav;	
	}
	
	@Override
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI);
		Boolean editMode = getEditMode(req);		
		TargetInstanceProfileCommand command = (TargetInstanceProfileCommand) comm;
		
		// If we are not to override the target, set the overrides to null.
		if(!command.isOverrideTarget()) {
			ti.setOverrides(null);
		}
		else {
			ProfileOverrides overrides = null;
			
			if(ti.getOverrides() == null) {
				overrides = ti.getTarget().getOverrides().copy();
				ti.setOverrides(overrides);
			}
			else {
				overrides = ti.getOverrides();
			}
			
			if(editMode)
			{
				//Only do this if we are in edit mode - otherwise the overrides get overwritten as null
				command.updateOverrides(overrides);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processOther(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		TargetInstanceProfileCommand command = (TargetInstanceProfileCommand) comm;
		TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI);
		
		if(command.getActionCmd().equals("toggleOverride")) {
			if(command.isOverrideTarget()) { 
				ti.setOverrides(ti.getTarget().getOverrides().copy());
			}
			else {
				ti.setOverrides(null);
			}
			
			// Process the main tab.
			TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
			tmav.getTabStatus().setCurrentTab(currentTab);
			return tmav;
		}
		else {
			return super.processOther(tc, currentTab, req, res, comm, errors);
		}
		
	}
}
