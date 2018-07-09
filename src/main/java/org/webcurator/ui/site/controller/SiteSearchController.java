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

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.sites.SiteManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.core.util.CookieUtils;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.SiteCriteria;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.common.editor.CustomIntegerCollectionEditor;
import org.webcurator.ui.site.command.SiteSearchCommand;

/**
 * The controller for managing searching for harvest authorisations.
 * @author bbeaumont
 */
public class SiteSearchController extends AbstractFormController {
	
	/** the site manager. */
	private SiteManager siteManager;
	/** the agency user manager. */
	private AgencyUserManager agencyUserManager;
	
    @Override
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));   
		binder.registerCustomEditor(Set.class, "states", new CustomIntegerCollectionEditor(Set.class,true));
    }
    	
	
	@Override
	protected ModelAndView showForm(HttpServletRequest req,
			HttpServletResponse resp, BindException errors) throws Exception {

		// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(req);
		
		SiteCriteria criteria = (SiteCriteria) req.getSession().getAttribute(SiteSearchCommand.SESSION_SITE_CRITERIA);		
		if (null == criteria) {
			criteria = new SiteCriteria();
			Agency usersAgency = AuthUtil.getRemoteUserObject().getAgency();
			criteria.setAgency(usersAgency.getName());
			req.getSession().setAttribute(SiteSearchCommand.SESSION_SITE_CRITERIA, criteria);
		}

		List<Agency> agencies = agencyUserManager.getAgencies();
		
		SiteSearchCommand command = new SiteSearchCommand();
		command.setSearchOid(criteria.getSearchOid());
		command.setTitle(criteria.getTitle());
		command.setAgentName(criteria.getAgentName());
		command.setOrderNo(criteria.getOrderNo());
		command.setAgency(criteria.getAgency());
		command.setShowDisabled(criteria.isShowDisabled());
		command.setPermsFileRef(criteria.getPermsFileRef());
		command.setUrlPattern(criteria.getUrlPattern());
		command.setStates(criteria.getStates());
		command.setSortorder(criteria.getSortorder());
		
		Pagination results = siteManager.search(criteria, 0, Integer.parseInt(currentPageSize));
		
		ModelAndView mav = new ModelAndView("site-search");
		mav.addObject(Constants.GBL_CMD_DATA, command);
		mav.addObject("agencies", agencies);
		mav.addObject("page", results);
		return mav;
	}

	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request,
			HttpServletResponse response, Object comm, BindException errors)
			throws Exception {

		SiteSearchCommand command = (SiteSearchCommand) comm;
		
		List<Agency> agencies = agencyUserManager.getAgencies();
		Agency usersAgency = AuthUtil.getRemoteUserObject().getAgency();

		if (SiteSearchCommand.ACTION_RESET.equals(command.getCmdAction())) {
			command.setPageNo(0);
			command.setSearchOid(null);
			command.setTitle("");
			command.setAgentName("");
			command.setOrderNo("");
			command.setAgency(usersAgency.getName());
			command.setShowDisabled(false);
			command.setPermsFileRef("");
			command.setUrlPattern("");
			command.setStates(new HashSet<Integer>());
			command.setSortorder(SiteSearchCommand.SORT_NAME_ASC);
		}
		
		SiteCriteria criteria = new SiteCriteria();
		criteria.setSearchOid(command.getSearchOid());
		criteria.setTitle(command.getTitle());
		criteria.setAgentName(command.getAgentName());
		criteria.setOrderNo(command.getOrderNo());
		criteria.setAgency(command.getAgency());
		criteria.setShowDisabled(command.isShowDisabled());	
		criteria.setPermsFileRef(command.getPermsFileRef());
		criteria.setUrlPattern(command.getUrlPattern());
		criteria.setStates(command.getStates());
		criteria.setSortorder(command.getSortorder());
		
		request.getSession().setAttribute(SiteSearchCommand.SESSION_SITE_CRITERIA, criteria);
		
		// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(request);
		
		Pagination results = null;
		if (command.getSelectedPageSize().equals(currentPageSize)) {
			// user has left the page size unchanged..
			results = siteManager.search(criteria, command.getPageNo(), Integer.parseInt(command.getSelectedPageSize()));		
		}
		else {
			// user has selected a new page size, so reset to first page..
			results = siteManager.search(criteria, 0, Integer.parseInt(command.getSelectedPageSize()));
			// ..then update the page size cookie
			CookieUtils.setPageSize(response, command.getSelectedPageSize());
		}
		ModelAndView mav = new ModelAndView("site-search");
		mav.addObject("agencies", agencies);
		mav.addObject("page", results);
		mav.addObject(Constants.GBL_CMD_DATA, command);
		return mav;
	}

	/**
	 * @param agencyUserManager The agencyUserManager to set.
	 */
	public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
		this.agencyUserManager = agencyUserManager;
	}
	
	/**
	 * @param siteManager the siteManager to set
	 */
	public void setSiteManager(SiteManager siteManager) {
		this.siteManager = siteManager;
	}
}
