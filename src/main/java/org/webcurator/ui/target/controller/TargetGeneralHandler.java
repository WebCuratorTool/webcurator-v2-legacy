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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.UserRoleDAO;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.DublinCore;
import org.webcurator.domain.model.core.RejReason;
import org.webcurator.domain.model.core.Target;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.TargetGeneralCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the target general tab.
 * @author bbeaumont
 */
public class TargetGeneralHandler extends AbstractTargetTabHandler {
	private UserRoleDAO userRoleDao = null;
	
	private TargetManager targetManager = null;
	
	private AgencyUserManager agencyUserManager = null;
	
	private AuthorityManager authorityManager = null;

    /** Automatic QA Url */
    private String autoQAUrl = "";

	/**
	 * @param aUserRoleDao The UserRoleDao to set.
	 */
	public void setUserRoleDao(UserRoleDAO aUserRoleDao) {
		this.userRoleDao = aUserRoleDao;
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		TargetGeneralCommand command = (TargetGeneralCommand) comm;
		TargetEditorContext ctx = getEditorContext(req);
		
		Target target = ctx.getTarget();
		
		if(ctx.isEditMode()) {
			if( authorityManager.hasAtLeastOnePrivilege(target, Privilege.MODIFY_TARGET, Privilege.CREATE_TARGET)) {
				String previousName = target.getName();
				target.setName(command.getName());
				target.setDescription(command.getDescription());
				target.setReferenceNumber(command.getReference());
				target.setRunOnApproval(command.isRunOnApproval());
				target.setUseAQA(command.isUseAQA());
				target.setAutoPrune(command.isAutoPrune());
				target.setAutoDenoteReferenceCrawl(command.isAutoDenoteReferenceCrawl());
				target.setRequestToArchivists(command.getRequestToArchivists());
				if(previousName == null || !previousName.equals(target.getName()))
				{
					//Name has changed - update DublinCore
					DublinCore dc = target.getDublinCoreMetaData();
					if(dc == null)
					{
						dc = new DublinCore();
						dc.setType(target.getOwner().getAgency().getDefaultDescriptionType());

						target.setDublinCoreMetaData(dc);
					}

					String title = dc.getTitle();
					if(title == null ||
						title.isEmpty() ||
						title.equals(previousName))
					{
						dc.setTitle(target.getName());
					}
				}
			}
			
			if( authorityManager.hasAtLeastOnePrivilege(target, 
					Privilege.APPROVE_TARGET,
					Privilege.CREATE_TARGET,
					Privilege.MODIFY_TARGET, 
					Privilege.CANCEL_TARGET, 
					Privilege.REINSTATE_TARGET)
					&& targetManager.allowStateChange(target, command.getState())) {
						target.changeState(command.getState());
						if (command.getState()==Target.STATE_REJECTED) {
					        RejReason rejReason = agencyUserManager.getRejReasonByOid(command.getReasonOid());
							target.setRejReason(rejReason);
						}
			}
			
			if(authorityManager.hasPrivilege(target, Privilege.TAKE_OWNERSHIP)) {
				User newOwner = userRoleDao.getUserByOid(command.getOwnerOid());
				if(agencyUserManager.canGiveTo(target, newOwner)) {
					target.setOwner(newOwner);
				}
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
		
		TargetGeneralCommand command = TargetGeneralCommand.buildFromModel(aTarget);
		
		int[] nextStates = targetManager.getNextStates(aTarget);
		
		List users = agencyUserManager.getPossibleOwners(aTarget);

		User user = org.webcurator.core.util.AuthUtil.getRemoteUserObject();
		Agency agency = user.getAgency();
        List rejectionReasons = agencyUserManager.getValidRejReasonsForTargets(agency.getOid());
		
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject(Constants.GBL_CMD_DATA, command);
		tmav.addObject("allUsers", users);
		tmav.addObject("originalState", aTarget.getOriginalState());
		if(autoQAUrl != null && autoQAUrl.length() > 0) {
			tmav.addObject("showAQAOption", 1);
		} else {
			tmav.addObject("showAQAOption", 0);
		}
		tmav.addObject(TargetGeneralCommand.MDL_NEXT_STATES, nextStates);
		tmav.addObject(TargetGeneralCommand.MDL_REJ_REASONS, rejectionReasons);
		
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
	 * @param targetManager The targetManager to set.
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}

	/**
	 * @param agencyUserManager The agencyUserManager to set.
	 */
	public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
		this.agencyUserManager = agencyUserManager;
	}

	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}
	
	public void setAutoQAUrl(String autoQAUrl) {
		this.autoQAUrl = autoQAUrl;
	}

	public String getAutoQAUrl() {
		return autoQAUrl;
	}
	
}
