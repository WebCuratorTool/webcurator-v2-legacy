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

import java.util.HashSet;
import java.util.Iterator;
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
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.core.util.CookieUtils;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.TargetDAO;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.Target;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.common.editor.CustomIntegerCollectionEditor;
import org.webcurator.ui.groups.command.SearchCommand;
import org.webcurator.ui.target.command.TargetSearchCommand;

/**
 * The controller for searching for Targets.
 * @author bbeaumont
 */
public class TargetSearchController extends AbstractFormController {

	private TargetDAO targetDao = null;
	
	private TargetManager targetManager = null;
	
	private AgencyUserManager agencyUserManager = null;
	

	@Override
	protected ModelAndView showForm(HttpServletRequest request,
			HttpServletResponse response, BindException errors) throws Exception {
		
		// fetch command object (if any) from session..
		TargetSearchCommand command = (TargetSearchCommand) request.getSession().getAttribute("targetSearchCommand");
		if(command == null) {
			command = getDefaultCommand();
		}

		// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(request);
		// and set page size preference..
		command.setSelectedPageSize(currentPageSize);

		return prepareSearchView(request, response, command, errors);
	}
	
	public TargetSearchCommand getDefaultCommand() {
		TargetSearchCommand command = new TargetSearchCommand();
		Agency usersAgency = AuthUtil.getRemoteUserObject().getAgency();
		command.setPageNumber(0);
		command.setAgency(usersAgency.getName());
		command.setOwner(AuthUtil.getRemoteUserObject().getUsername());
		command.setSearchOid(null);
		command.setSortorder(TargetSearchCommand.SORT_NAME_ASC);
		
		return command;
	}
	
	/** 
	 * @return The search view.
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView prepareSearchView(HttpServletRequest request, 
			                              HttpServletResponse response,
			                              BindException errors) {

		// fetch command object (if any) from session..
		TargetSearchCommand command = (TargetSearchCommand) request.getSession().getAttribute("targetSearchCommand");
		if(command == null) {
			command = getDefaultCommand();
		}
		command.setSelectedPageSize(CookieUtils.getPageSize(request));

		return prepareSearchView(request, response, command, errors);
	}

	@SuppressWarnings("unchecked")
	public ModelAndView prepareSearchView(HttpServletRequest request, 
			                              HttpServletResponse response,
			                              TargetSearchCommand command,
			                              BindException errors) {
		
		List<Agency> agencies = agencyUserManager.getAgencies();

		// We need to find the OID of the agency.
		Agency currentAgency = null;
		for(Agency a: agencies) {
			if(a.getName().equals(command.getAgency())) {
				currentAgency = a;
				break;
			}
		}

		List owners = null;
        if (currentAgency != null) {
        	owners = agencyUserManager.getUserDTOs(currentAgency.getOid());
        }
        else {
        	owners = agencyUserManager.getUserDTOs();
        }   

		// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(request);

		Pagination results = null;
		if (command.getSelectedPageSize() != null && command.getSelectedPageSize().equals(currentPageSize)) {
			// user has left the page size unchanged..
			results = targetDao.search(command.getPageNumber(), Integer.parseInt(command.getSelectedPageSize()), command.getSearchOid(), command.getName(), command.getStates(), command.getSeed(), command.getOwner(), command.getAgency(), command.getMemberOf(), command.getNondisplayonly(), command.getSortorder(), command.getDescription());
		}
		else {
			String pageSize = command.getSelectedPageSize();
			if(pageSize==null) {
				pageSize = currentPageSize;
			} else {
				CookieUtils.setPageSize(response, command.getSelectedPageSize());
			}
			// user has selected a new page size, so reset to first page..
			results = targetDao.search(0, Integer.parseInt(pageSize), command.getSearchOid(), command.getName(), command.getStates(), command.getSeed(), command.getOwner(), command.getAgency(), command.getMemberOf(), command.getNondisplayonly(), command.getSortorder(), command.getDescription());
		}
		
		// we need to populate annotations to determine if targets are alertable.
		for (Iterator<Target> i = ((List<Target>) results.getList()).iterator( ); i.hasNext(); ) {
			Target t = i.next();
			t.setAnnotations(targetManager.getAnnotations(t));
		}		

		request.getSession().setAttribute("targetSearchCommand", command);
		
        ModelAndView mav = new ModelAndView("target-search");
		mav.addObject("agencies", agencies);
		mav.addObject("owners", owners);
		mav.addObject(Constants.GBL_CMD_DATA, command);
		mav.addObject("page", results);
		
		return mav;
	}
	

	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request,
			HttpServletResponse response, Object comm, BindException errors)
			throws Exception {

		TargetSearchCommand command = (TargetSearchCommand) comm;
		
		if(TargetSearchCommand.ACTION_SEARCH.equals(command.getActionCmd())) {
			return prepareSearchView(request, response, command, errors);
		}
		else if(TargetSearchCommand.ACTION_RESET.equals(command.getActionCmd())) {
			command.setAgency("");
			command.setName("");
			command.setOwner("");
			command.setPageNumber(0);
			command.setSeed("");
			command.setStates(new HashSet<Integer>());
			command.setMemberOf("");
			command.setSearchOid(null);
			command.setSortorder(TargetSearchCommand.SORT_NAME_ASC);
			command.setDescription("");
			
			return prepareSearchView(request, response, command, errors);
		}
		else if(TargetSearchCommand.ACTION_DELETE.equals(command.getActionCmd())){
			// TODO Load target, check privileges, and delete the target.
			// Then return to the search view.
			Target aTarget = targetManager.load(command.getSelectedTargetOid());
			targetManager.deleteTarget(aTarget);
			return prepareSearchView(request, response, command, errors);
		}
		else {
			return null;
		}
	}
	
	

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest, org.springframework.web.bind.ServletRequestDataBinder)
	 */
	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		// TODO Auto-generated method stub
		super.initBinder(request, binder);
		binder.registerCustomEditor(Set.class, "states", new CustomIntegerCollectionEditor(Set.class,true));
		binder.registerCustomEditor(Long.class, "selectedTargetOid", new CustomNumberEditor(Long.class,true));
		binder.registerCustomEditor(Long.class, "searchOid", new CustomNumberEditor(Long.class,true));
	}

	/**
	 * @return Returns the targetDao.
	 */
	public TargetDAO getTargetDao() {
		return targetDao;
	}

	/**
	 * @param targetDao The targetDao to set.
	 */
	public void setTargetDao(TargetDAO targetDao) {
		this.targetDao = targetDao;
	}

	/**
	 * @param agencyUserManager The agencyUserManager to set.
	 */
	public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
		this.agencyUserManager = agencyUserManager;
	}

	/**
	 * @param targetManager The targetManager to set.
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}

}
