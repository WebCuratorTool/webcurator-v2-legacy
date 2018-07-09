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
package org.webcurator.ui.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.Role;
import org.webcurator.domain.model.auth.User;
import org.webcurator.ui.admin.command.AssociateUserRoleCommand;
import org.webcurator.ui.admin.command.UserCommand;
import org.webcurator.ui.common.Constants;

/**
 * Manage the view for associating Roles to a user.
 * @author bprice
 */
public class AssociateUserRoleController extends AbstractFormController {
	/** the logger. */
    private Log log = null;
    /** the agency mananger. */
    private AgencyUserManager agencyUserManager = null;
    /** the authority manager */
    private AuthorityManager authorityManager = null;
    /** the message source. */
    private MessageSource messageSource = null;
    /** Default Constructor. */
    public AssociateUserRoleController() {
        log = LogFactory.getLog(AssociateUserRoleController.class);
        setCommandClass(AssociateUserRoleCommand.class);
    }
    
    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq, HttpServletResponse aRes, Object aCmd, BindException aErrors) throws Exception {
        AssociateUserRoleCommand command = (AssociateUserRoleCommand) aCmd;
        if (command != null && command.getActionCmd() != null) {
            if (AssociateUserRoleCommand.ACTION_ASSOCIATE_VIEW.equals(command.getActionCmd())) {
                return processUserToRoleAssoc(aReq, aRes, command, aErrors);
            }
            else if (AssociateUserRoleCommand.ACTION_ASSOCIATE_SAVE.equals(command.getActionCmd())) {
                return processSaveUserToRoleAssoc(aReq, aRes, command, aErrors);
            }
            else {
                throw new RuntimeException("Unknown action item " + command.getActionCmd());
            }
        }
        throw new RuntimeException("Unknown command.");
    }
    
    @Override
    protected ModelAndView showForm(HttpServletRequest arg0, HttpServletResponse arg1, BindException arg2) throws Exception {
        return null;
    }
    
    /**
     * Process the command to associate roles to a user. 
     */
    private ModelAndView processUserToRoleAssoc(HttpServletRequest aReq, HttpServletResponse aRes, AssociateUserRoleCommand aCmd, BindException aErrors) {
        ModelAndView mav = new ModelAndView();
        if (aCmd.getChoosenUserOid() != null) {
            List assignedRoles = agencyUserManager.getAssociatedRolesForUser(aCmd.getChoosenUserOid());
            User choosenUser = agencyUserManager.getUserByOid(aCmd.getChoosenUserOid());
            List allAgencyRoles = agencyUserManager.getRolesForUser(choosenUser);
            
            HashMap <Long,Role>roles = new HashMap<Long,Role>();
            List <Role>unassignedRoles = new ArrayList<Role>();
            Role role = null;
            Iterator it = assignedRoles.iterator();
            while (it.hasNext()) {
                role = (Role) it.next();
                roles.put(role.getOid(), role);
            }
            
            it = allAgencyRoles.iterator();
            while (it.hasNext()) {
                role = (Role) it.next();
                if (!roles.containsKey(role.getOid())) {
                    unassignedRoles.add(role);
                }
            }
            
            mav.addObject(AssociateUserRoleCommand.MDL_ASSIGNED_ROLES, assignedRoles);
            mav.addObject(AssociateUserRoleCommand.MDL_UNASSIGNED_ROLES,unassignedRoles);
            mav.addObject(AssociateUserRoleCommand.MDL_USER, choosenUser.getUsername());
            mav.addObject(Constants.GBL_CMD_DATA, aCmd);
            
            mav.setViewName("viewUserRoleAssociations");
        }
        
        return mav;
        
    }
    
    /** 
     * Process the command to save the user and role associations. 
     */
    private ModelAndView processSaveUserToRoleAssoc(HttpServletRequest aReq, HttpServletResponse aRes, AssociateUserRoleCommand aCmd, BindException aErrors) {
        ModelAndView mav = new ModelAndView();
        log.debug("start of processSaveUserToRoleAssoc()");
        if (aCmd.getChoosenUserOid() != null) {
        	// load the selected users
            User choosenUser = agencyUserManager.getUserByOid(aCmd.getChoosenUserOid());
            // remove all previous roles
            choosenUser.removeAllRoles();
            
            log.debug("About to assign roles to user");
            if (!"".equals(aCmd.getSelectedRoles())) {
                Set roleOids = parseKeysToLongs(aCmd.getSelectedRoles());
                Iterator it = roleOids.iterator();
                
                while (it.hasNext()) {
                    log.debug("Iterating through role oids");
                    Long roleOid = (Long) it.next();
                    Role role = agencyUserManager.getRoleByOid(roleOid);
                    log.debug("roleOid = "+roleOid);
                                        
                    choosenUser.addRole(role);                   
                }
            }
            agencyUserManager.updateUserRoles(choosenUser);
            
            mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("user.roles.updated", new Object[] { choosenUser.getUsername() }, Locale.getDefault()));
            List userDTOs = agencyUserManager.getUserDTOsForLoggedInUser();
            List agencies = null;
            if (authorityManager.hasPrivilege(Privilege.MANAGE_USERS, Privilege.SCOPE_ALL)) {
            	agencies = agencyUserManager.getAgencies();
            } else {
                User loggedInUser = AuthUtil.getRemoteUserObject();
                Agency usersAgency = loggedInUser.getAgency();
                agencies = new ArrayList<Agency>();
                agencies.add(usersAgency);
            }
            
            String agencyFilter = (String)aReq.getSession().getAttribute(UserCommand.MDL_AGENCYFILTER);
            if(agencyFilter == null)
            {
            	agencyFilter = AuthUtil.getRemoteUserObject().getAgency().getName();
            }
            mav.addObject(UserCommand.MDL_AGENCYFILTER, agencyFilter);
            mav.addObject(UserCommand.MDL_LOGGED_IN_USER, AuthUtil.getRemoteUserObject());
            mav.addObject(UserCommand.MDL_USERS, userDTOs);
            mav.addObject(UserCommand.MDL_AGENCIES, agencies);

            mav.setViewName("viewUsers");
        }
        
        return mav;
    }
    
    /**
     * parses a String containing a list of keys that need to be
     * converted to Longs. This is used when parsing oids in the format
     * "1|23|43|7".
     * @param aKeys the String to parse
     * @return the Set of Long objects
     */
    private Set parseKeysToLongs(String aKeys) {
        HashSet<Long> set = new HashSet<Long>();
        if (aKeys != null) {
            StringTokenizer st = new StringTokenizer(aKeys, Constants.PRIVILEGE_DELIMITER);
            while (st.hasMoreTokens()) {
                set.add(Long.valueOf(st.nextToken()));
            }
        }
        return set;
    }

    /** 
     * @param agencyUserManager the agency user manager.
     */
    public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
        this.agencyUserManager = agencyUserManager;
    }

    /**  
     * @param messageSource the message source. 
     */
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
	/**
	 * Spring setter method for the Authority Manager.
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}
}
