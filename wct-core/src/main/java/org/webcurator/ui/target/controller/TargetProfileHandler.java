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
import org.webcurator.auth.AuthorityManager;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.ProfileCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the target profile overrides tab.
 * @author bbeaumont
 */
public class TargetProfileHandler extends AbstractOverrideTabHandler {	
	/** The Authority Manager */
	private AuthorityManager authorityManager;
	
	public TargetEditorContext getEditorContext(HttpServletRequest req) {
		TargetEditorContext ctx = (TargetEditorContext) req.getSession().getAttribute(TabbedTargetController.EDITOR_CONTEXT);
		if( ctx == null) {
			throw new IllegalStateException("tabEditorContext not yet bound to the session");
		}
		
		return ctx;
	}
	
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		super.processTab(tc, currentTab, req, res, comm, errors);
		
		ProfileCommand command = (ProfileCommand) comm;
		if(command.getProfileOid() != null && overrideGetter.isOverrideableEditable(req)) {
			Profile newProfile = profileManager.load(command.getProfileOid());
			
			// The user can set the profile if they have a high enough level
			// or if the profile is marked as the default.
			int userLevel = authorityManager.getProfileLevel();
			if( newProfile.getRequiredLevel() <= userLevel || 
			    newProfile.isDefaultProfile()) {
				getEditorContext(req).getTarget().setProfile(newProfile);
			}
			
			getEditorContext(req).getTarget().setProfileNote(command.getProfileNote());
		}
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#preProcessNextTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
				
		TabbedModelAndView tmav = super.preProcessNextTab(tc, nextTabID, req, res, comm, errors); 

		// Add the objects to the model.		
		tmav.addObject("ownable", getEditorContext(req).getTarget());
		tmav.addObject("privlege", Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET);		
		tmav.addObject("editMode", Boolean.toString(getEditorContext(req).isEditMode()));
						
		return tmav;	
	}
	
	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}
}
