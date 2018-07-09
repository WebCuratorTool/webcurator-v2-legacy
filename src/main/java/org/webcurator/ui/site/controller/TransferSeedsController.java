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
package org.webcurator.ui.site.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.sites.SiteManager;
import org.webcurator.core.targets.PermissionCriteria;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.site.command.TransferSeedsCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * Controls the Transfer Seeds Action.
 * @author bbeaumont
 */
public class TransferSeedsController extends AbstractCommandController {
	/** The TargetManager responsible for business operations. */
	private TargetManager targetManager = null;
	/** The SiteManager responsible for site related operations. */
	private SiteManager siteManager = null;
	/** The Site Controller that flow should return to. */
	private SiteController siteController = null;
	/** Message Source */
	private MessageSource messageSource = null;
	/** Authority Manager */
	private AuthorityManager authorityManager = null;
	
	
	/**
	 * Set up the controller by setting the command class.
	 */
	public TransferSeedsController() {
		setCommandClass(TransferSeedsCommand.class);
	}
	

	/**
	 * Pass the control of the action over to one of the specific handler
	 * methods.
	 */
	@Override
	protected ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) throws Exception {
		TransferSeedsCommand command = (TransferSeedsCommand) comm;
		
		if(TransferSeedsCommand.ACTION_CANCEL.equals(command.getActionCmd())) {
			return handleCancel(req, res, command, errors);
		}
		else if(TransferSeedsCommand.ACTION_NEXT.equals(command.getActionCmd())) {
			return handleNext(req, res, command, errors);
		}
		else if(TransferSeedsCommand.ACTION_PREV.equals(command.getActionCmd())) {
			return handlePrev(req, res, command, errors);
		}
		else if(TransferSeedsCommand.ACTION_SEARCH.equals(command.getActionCmd())) {
			return handleSearch(req, res, command, errors);
		}
		else if(TransferSeedsCommand.ACTION_TRANSFER.equals(command.getActionCmd())) {
			return handleTransfer(req, res, command, errors);
		}
		else if(TransferSeedsCommand.ACTION_INIT.equals(command.getActionCmd())) {
			// Check if there are any errors.
			siteController.checkSave(req, errors);
			if(errors.hasErrors()) {
				Tab currentTab = siteController.getTabConfig().getTabByID("PERMISSIONS");
				return siteController.getErrorsView(currentTab, req, res, comm, errors);
			}

			// Save the Site.
			try {
				siteManager.save(siteController.getEditorContext(req).getSite());
				return handleSearch(req, res, command, errors);
			} 
			catch (Exception ex) {
				throw new WCTRuntimeException(ex.getMessage(), ex);
			}
		}
		else {
			return null;
		}
	}
	
	
	/**
	 * Handle search.
	 * @param req The servlet request.
	 * @param res The servlet response.
	 * @param command The TransferSeedsCommand object.
	 * @param errors The bind errors.
	 * @return The ModelAndView to be displayed.
	 */
	protected ModelAndView handleSearch(HttpServletRequest req, HttpServletResponse res, TransferSeedsCommand command, BindException errors) {
		PermissionCriteria criteria = new PermissionCriteria();
		criteria.setAgencyOid(AuthUtil.getRemoteUserObject().getAgency().getOid());
		criteria.setSiteName(command.getSiteTitle());
		criteria.setUrlPattern(command.getUrlPattern());
		criteria.setPageNumber(0);
	
		req.getSession().setAttribute("permSearchCriteria", criteria);
		
		return getSearchView(command, criteria);
	}
	
	/**
	 * Converts the permission criteria to a command object.
	 * @param criteria The criteria to convert.
	 * @return The command object.
	 */
	private void updateCommand(TransferSeedsCommand command, PermissionCriteria criteria) {
		command.setSiteTitle(criteria.getSiteName());
		command.setUrlPattern(criteria.getUrlPattern());
	}
	
	
	/**
	 * Perform the search and build the results view.
	 * @param aCriteria The criteria to search for.
	 * @return The search results view.
	 */
	protected ModelAndView getSearchView(TransferSeedsCommand aCommand, PermissionCriteria aCriteria) {
		Pagination results = targetManager.searchPermissions(aCriteria);
		updateCommand(aCommand, aCriteria);
		
		int numberOfSeeds = siteManager.countLinkedSeeds(aCommand.getFromPermissionOid());
		
		ModelAndView mav = new ModelAndView("permission-search");
		mav.addObject("page", results);
		mav.addObject("seedCount", numberOfSeeds);
		mav.addObject(Constants.GBL_CMD_DATA, aCommand);
		
		return mav;
		
	}
	
	/**
	 * Handle transfer.
	 * @param req The servlet request.
	 * @param res The servlet response.
	 * @param command The TransferSeedsCommand object.
	 * @param errors The bind errors.
	 * @return The ModelAndView to be displayed.
	 */	
	protected ModelAndView handleTransfer(HttpServletRequest req, HttpServletResponse res, TransferSeedsCommand command, BindException errors) {
		
		if(errors.hasErrors()) {
			PermissionCriteria criteria = (PermissionCriteria) req.getSession().getAttribute("permSearchCriteria");
			ModelAndView mav = getSearchView(command, criteria);
			mav.addObject(Constants.GBL_ERRORS, errors);
			return mav;
		}
		else {
			Permission fromPermission = targetManager.loadPermission(command.getFromPermissionOid());
			Permission toPermission   = targetManager.loadPermission(command.getToPermissionOid());
			
			int seedsTransferred = 0;
			String message = null; 
			
			// Only perform the transfer if the user has the appropriate privileges.
			if(authorityManager.hasPrivilege(fromPermission, Privilege.TRANSFER_LINKED_TARGETS)) {
				seedsTransferred = targetManager.transferSeeds(fromPermission, toPermission);
				message = messageSource.getMessage("site.seeds_transferred", new Object[] { seedsTransferred }, Locale.getDefault());
			}
			else {
				message = messageSource.getMessage("site.seeds_transfer.denied", new Object[] {}, Locale.getDefault());
			}
			
			
			Tab currentTab = siteController.getTabConfig().getTabByID("PERMISSIONS");
			TabbedModelAndView tmav = currentTab.getTabHandler().preProcessNextTab(siteController, currentTab, req, res, command, errors);
			tmav.getTabStatus().setCurrentTab(currentTab);
				
			tmav.addObject(Constants.GBL_MESSAGES, message);
			
			return tmav;
		}
	}
	
	
	/**
	 * Handle next.
	 * @param req The servlet request.
	 * @param res The servlet response.
	 * @param command The TransferSeedsCommand object.
	 * @param errors The bind errors.
	 * @return The ModelAndView to be displayed.
	 */	
	protected ModelAndView handleNext(HttpServletRequest req, HttpServletResponse res, TransferSeedsCommand command, BindException errors) {
		PermissionCriteria criteria = (PermissionCriteria) req.getSession().getAttribute("permSearchCriteria");
		criteria.setPageNumber(criteria.getPageNumber() + 1);
		return getSearchView(command, criteria);
	}
	
	
	/**
	 * Handle previous.
	 * @param req The servlet request.
	 * @param res The servlet response.
	 * @param command The TransferSeedsCommand object.
	 * @param errors The bind errors.
	 * @return The ModelAndView to be displayed.
	 */	
	protected ModelAndView handlePrev(HttpServletRequest req, HttpServletResponse res, TransferSeedsCommand command, BindException errors) {
		PermissionCriteria criteria = (PermissionCriteria) req.getSession().getAttribute("permSearchCriteria");
		criteria.setPageNumber(criteria.getPageNumber() - 1);
		return getSearchView(command, criteria);
	}	

	/**
	 * Handle cancel.
	 * @param req The servlet request.
	 * @param res The servlet response.
	 * @param command The TransferSeedsCommand object.
	 * @param errors The bind errors.
	 * @return The ModelAndView to be displayed.
	 */	
	protected ModelAndView handleCancel(HttpServletRequest req, HttpServletResponse res, TransferSeedsCommand command, BindException errors) {
		Tab currentTab = siteController.getTabConfig().getTabByID("PERMISSIONS");
		TabbedModelAndView tmav = currentTab.getTabHandler().preProcessNextTab(siteController, currentTab, req, res, command, errors);
		tmav.getTabStatus().setCurrentTab(currentTab);
			
		tmav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("site.seeds_transfer_cancelled", new Object[] { }, Locale.getDefault()));
		
		return tmav;
	}


	
	/**
	 * Spring initialisation method
	 * @param targetManager The targetManager to set.
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}	
	
	/**
	 * Set the site controller for Spring initialisation.
	 * @param siteController The siteController to set.
	 */
	public void setSiteController(SiteController siteController) {
		this.siteController = siteController;
	}


	/**
	 * @param messageSource The messageSource to set.
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}


	/**
	 * @param siteManager The siteManager to set.
	 */
	public void setSiteManager(SiteManager siteManager) {
		this.siteManager = siteManager;
	}


	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}
	
}
