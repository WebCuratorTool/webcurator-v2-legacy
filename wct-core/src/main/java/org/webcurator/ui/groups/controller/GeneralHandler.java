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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.common.WCTTreeSet;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.CookieUtils;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.DublinCore;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.dto.GroupMemberDTO;
import org.webcurator.domain.model.dto.UserDTO;
import org.webcurator.domain.model.dto.GroupMemberDTO.SAVE_STATE;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.groups.command.AddParentsCommand;
import org.webcurator.ui.groups.command.GeneralCommand;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The  tab handler for the Target Group General Tab.
 * @author bbeaumont
 */
public class GeneralHandler extends AbstractGroupTabHandler {
	/** the manager for accessing agency and user data. */
	private AgencyUserManager agencyUserManager = null;
	/** the manager for accessing privilege data. */
	private AuthorityManager  authorityManager  = null;
	
	/** The list of available group types */
	private WCTTreeSet groupTypesList = null;
	
	private String subGroupTypeName = null;
	private String subGroupSeparator = null;
	private TargetManager targetManager = null;

	
	@Override
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        // Register the binders.
        binder.registerCustomEditor(java.util.Date.class, DateUtils.get().getFullDateEditor(true));       
    }
	
	
	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}

	@Override
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		GeneralCommand command = (GeneralCommand) comm;
		GroupsEditorContext ctx = getEditorContext(req);
		
		TargetGroup target = ctx.getTargetGroup();
		
		if(ctx.isEditMode()) {
			if( authorityManager.hasPrivilege(target, Privilege.CREATE_GROUP)) {
				
				String previousName = target.getName();
				target.setName(command.getName());
				target.setDescription(command.getDescription());
				target.setSipType(command.getSipType());
				target.setOwnershipMetaData(command.getOwnershipMetaData());
				target.setFromDate(command.getFromDate());
				target.setToDate(command.getToDate());
				target.setReferenceNumber(command.getReference());				
				target.setType(command.getType());
				if(previousName == null || !previousName.equals(target.getName()))
				{
					//Name has changed - update DublinCore
					DublinCore dc = target.getDublinCoreMetaData();
					if(dc == null)
					{
						dc = new DublinCore();
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
				
				if(subGroupTypeName.equals(target.getType()))
				{
					try
					{
						Long parentOid = Long.parseLong(command.getParentOid());
						setParent(req, parentOid);
					}
					catch(NumberFormatException e)
					{
						//Do nothing
					}
					
					//Make the name prefixed
					String parentName = getParentName(req);
					if(parentName != null && !parentName.isEmpty())
					{
						String name = parentName + subGroupSeparator + command.getName();
						target.setName(name);
					}
				}
			}
			
			if(authorityManager.hasPrivilege(target, Privilege.TAKE_OWNERSHIP)) {
				User newOwner = agencyUserManager.getUserByOid(command.getOwnerOid());
				if(agencyUserManager.canGiveTo(target, newOwner)) {
					target.setOwner(newOwner);
				}
			}
		}

	}

	private void setParent(HttpServletRequest req, Long parentOid) 
	{
		// Load the Editor Context
		GroupsEditorContext ctx = getEditorContext(req);
		TargetGroup target = ctx.getTargetGroup();
		
		List<GroupMemberDTO> parents = ctx.getParents();
		if(parents == null) 
		{ 
			parents = targetManager.getParents(target);
			ctx.setParents(parents);
		}
		
		boolean entrySaved = false;
		List<GroupMemberDTO> parentsToRemove = new ArrayList<GroupMemberDTO>();
		Iterator<GroupMemberDTO> it = parents.iterator();
		while(it.hasNext())
		{
			GroupMemberDTO parent = it.next();
			if(parentOid.equals(parent.getParentOid())) 
			{
				//We are trying to set an existing state so leave it as it is unless...
				if(parent.getSaveState() == GroupMemberDTO.SAVE_STATE.DELETED)
				{
					//restore it
					parent.setSaveState(GroupMemberDTO.SAVE_STATE.ORIGINAL);
				}
				entrySaved = true;
			}
			else
			{
				//This parent is not the same as we are trying to set so delete or mark deleted
				if(parent.getSaveState() == GroupMemberDTO.SAVE_STATE.ORIGINAL)
				{
					parent.setSaveState(GroupMemberDTO.SAVE_STATE.DELETED);
				}
				else if(parent.getSaveState() == GroupMemberDTO.SAVE_STATE.NEW)
				{
					parentsToRemove.add(parent);
				}
			}
		}
			
		if(!parentsToRemove.isEmpty())
		{
			parents.removeAll(parentsToRemove);
		}
		
		if(!entrySaved)
		{
			TargetGroup newParent = targetManager.loadGroup(parentOid, false);
			GroupMemberDTO dto = new GroupMemberDTO(newParent, target);
			dto.setSaveState(SAVE_STATE.NEW);
			parents.add(dto);
			entrySaved = true;
		}
	}

	private Long getParent(HttpServletRequest req) 
	{
		// Load the Editor Context
		GroupsEditorContext ctx = getEditorContext(req);
		TargetGroup target = ctx.getTargetGroup();
		
		List<GroupMemberDTO> parents = ctx.getParents();
		if(parents == null) 
		{ 
			parents = targetManager.getParents(target);
			ctx.setParents(parents);
		}
		
		if(!parents.isEmpty())
		{
			Iterator<GroupMemberDTO> it = parents.iterator();
			while(it.hasNext())
			{
				GroupMemberDTO parent = it.next();
				if(parent.getSaveState() != GroupMemberDTO.SAVE_STATE.DELETED)
				{
					return parent.getParentOid();
				}
			}
		}

		return null;
	}

	private String getParentName(HttpServletRequest req) 
	{
		// Load the Editor Context
		GroupsEditorContext ctx = getEditorContext(req);
		TargetGroup target = ctx.getTargetGroup();
		
		List<GroupMemberDTO> parents = ctx.getParents();
		if(parents == null) 
		{ 
			parents = targetManager.getParents(target);
			ctx.setParents(parents);
		}
		
		if(!parents.isEmpty())
		{
			TargetGroup parentGroup = targetManager.loadGroup(getParent(req), false);
			return parentGroup.getName();
		}
		else
		{
			return "";
		}
	}
	
	private WCTTreeSet getGroupTypes(HttpServletRequest req)
	{
		GroupsEditorContext ctx = getEditorContext(req);
		
		TargetGroup target = ctx.getTargetGroup();
		if(target.isNew() && (target.getType() == null || target.getType().length() == 0))
		{
			return groupTypesList;
		}
		else if(subGroupTypeName.equals(target.getType()))
		{
			WCTTreeSet types = new WCTTreeSet(new ArrayList<String>(1), subGroupTypeName.length());
			
			types.add(subGroupTypeName);
			
			return types;
		}
		else
		{
			List<String> typesList = new ArrayList<String>();
			Iterator<String> it = groupTypesList.iterator();
			int maxLength = 1;
			while(it.hasNext())
			{
				String type = it.next();
				
				if(type.length() > maxLength)
				{
					maxLength = type.length();
				}
				
				if(!subGroupTypeName.equals(type))
				{
					typesList.add(type);
				}
			}
			
			return new WCTTreeSet(typesList, maxLength);
		}
	}

	@Override
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
	
		TargetGroup aTargetGroup = getEditorContext(req).getTargetGroup();
		
		GeneralCommand command = GeneralCommand.buildFromModel(aTargetGroup, subGroupTypeName, subGroupSeparator);
		if(comm instanceof AddParentsCommand)
		{
			AddParentsCommand addParentsCommand = (AddParentsCommand)comm;
			if(AddParentsCommand.ACTION_ADD_PARENTS.equals(addParentsCommand.getActionCmd()))
			{
				long[] parentOids = addParentsCommand.getParentOids();
				if(parentOids != null && parentOids.length > 0)
				{
					long parentOid = parentOids[0];
					TargetGroup parentGroup = targetManager.loadGroup(parentOid, false);
					command.setParentOid(Long.toString(parentOid));
					command.setParentName(parentGroup.getName());
					command.setType(subGroupTypeName);
				}
				else
				{
					command.setParentOid("");
					command.setParentName("");
					command.setType(subGroupTypeName);
				}
			}
			else if(AddParentsCommand.ACTION_CANCEL.equals(addParentsCommand.getActionCmd()))
			{
				Long parentOid = getParent(req);
				if(parentOid != null)
				{
					command.setParentOid(getParent(req).toString());
					command.setParentName(getParentName(req));
				}
				else
				{
					command.setParentOid("");
					command.setParentName("");
				}
				
				command.setType(subGroupTypeName);
			}
		}
		else
		{
			Long parentOid = getParent(req);
			
			command.setParentOid(parentOid == null?"":parentOid.toString());
			
			String parentName = getParentName(req);
			command.setParentName(parentName);
		}
		
    	if(subGroupTypeName.equals(aTargetGroup.getType()))
    	{
    		//Remove the name prefix
    		if(aTargetGroup.getName().contains(subGroupSeparator))
    		{
    			command.setName(aTargetGroup.getName().substring(aTargetGroup.getName().indexOf(subGroupSeparator)+subGroupSeparator.length()));
    		}
    	}
		
		List<UserDTO> users = agencyUserManager.getPossibleOwners(aTargetGroup);
		
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject(Constants.GBL_CMD_DATA, command);
		tmav.addObject("allUsers", users);
		tmav.addObject("groupTypesList", getGroupTypes(req));
		
		
		return tmav;				
	}

	@Override
	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
        TabbedModelAndView nextView = null;

        //Save the tab data before moving to parents screen
        processTab(tc, currentTab, req, res, comm, errors);      
    	nextView = preProcessNextTab(tc, currentTab, req, res, comm, errors);
        nextView.getTabStatus().setCurrentTab(currentTab);
        
		GeneralCommand command = (GeneralCommand) comm;
    	
		if(!errors.hasErrors() && command.getAction().equals(GeneralCommand.ACTION_ADD_PARENT))
		{
			// get value of page size cookie
			String currentPageSize = CookieUtils.getPageSize(req);
			
			AddParentsCommand cmd = new AddParentsCommand();
			
			cmd.setSearch("");
			cmd.setSelectedPageSize(currentPageSize);
			
			Pagination results = targetManager.getSubGroupParentDTOs(cmd.getSearch() + "%", 0, Integer.parseInt(cmd.getSelectedPageSize()));
			
			ModelAndView mav = new ModelAndView("group-add-parents");
			mav.addObject(Constants.GBL_CMD_DATA, cmd);
			mav.addObject("page", results);
			return mav;		
		}
		
		return nextView;				
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
		this.targetManager  = targetManager;
	}

	/**
	 * @param groupTypesList The groupTypesList to set.
	 */
	public void setGroupTypesList(WCTTreeSet groupTypesList) {
		this.groupTypesList = groupTypesList;
	}


	/**
	 * @param subGroupTypeName The subGroupTypeName to set.
	 */
	public void setSubGroupTypeName(String subGroupTypeName) {
		this.subGroupTypeName = subGroupTypeName;
	}

	/**
	 * @param subGroupSeparator The subGroupSeparator to set.
	 */
	public void setSubGroupSeparator(String subGroupSeparator) {
		this.subGroupSeparator = subGroupSeparator;
	}
}
