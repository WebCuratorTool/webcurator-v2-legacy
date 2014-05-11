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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.Role;
import org.webcurator.domain.model.auth.RolePrivilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.ui.admin.command.RoleCommand;
import org.webcurator.ui.admin.command.UserCommand;
import org.webcurator.ui.common.Constants;

/**
 * The Controller for creation and management of Roles within the
 * WCT.
 * @author bprice
 */
public class RoleController extends AbstractFormController {
	/** The agency user manager. */
    private AgencyUserManager agencyUserManager = null;
    /** the message source. */    
    private MessageSource messageSource = null;
    /** the authority manager. */
    private AuthorityManager authorityManager = null;
    /** the logger. */
    private Log log = null;
    /** Default Constructor. */
    public RoleController() {
        setCommandClass(RoleCommand.class);
        log = LogFactory.getLog(RoleController.class);
    }
    
    @Override
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));   
    }
    
    /** 
     * @return the default model and view.
     */
    public ModelAndView defaultView(String agencyFilter) {
        List roles = agencyUserManager.getRolesForLoggedInUser();
        List agencies = null;
        if (authorityManager.hasPrivilege(Privilege.MANAGE_ROLES, Privilege.SCOPE_ALL)) {
        	agencies = agencyUserManager.getAgencies();
        } else {
            User loggedInUser = AuthUtil.getRemoteUserObject();
            Agency usersAgency = loggedInUser.getAgency();
            agencies = new ArrayList<Agency>();
            agencies.add(usersAgency);
        }
        
        RoleCommand aRoleCmd = new RoleCommand();
        
        aRoleCmd.setAgencyFilter(agencyFilter);
        
        ModelAndView mav = new ModelAndView();
        mav.addObject(RoleCommand.MDL_ROLES, roles);
        mav.addObject(RoleCommand.MDL_AGENCIES, agencies);
        mav.addObject(Constants.GBL_CMD_DATA, aRoleCmd);
        mav.setViewName("Roles");
                
        return mav;
    }
    
    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq, HttpServletResponse aRes, Object aCmd, BindException aError) throws Exception {
                
        RoleCommand roleCmd = (RoleCommand) aCmd;
        if (roleCmd != null) {
            if (aError.hasErrors()) {
                ModelAndView mav = new ModelAndView();
                List agencies = agencyUserManager.getAgenciesForLoggedInUser();
                mav.addObject(RoleCommand.MDL_AGENCIES, agencies);
                mav.addObject(Constants.GBL_CMD_DATA, aError.getTarget());
                mav.addObject(Constants.GBL_ERRORS, aError);
                mav.setViewName("AddRole");
                return mav;
                
            } else if (RoleCommand.ACTION_NEW.equals(roleCmd.getAction())) {
                List agencies = agencyUserManager.getAgenciesForLoggedInUser();
                ModelAndView mav = new ModelAndView();
                mav.addObject(RoleCommand.MDL_AGENCIES, agencies);
                mav.setViewName("AddRole");
                return mav;
            } else if (RoleCommand.ACTION_SAVE.equals(roleCmd.getAction())) {
                return saveRole(aReq, aRes, roleCmd, aError);
            } else if (RoleCommand.ACTION_DELETE.equals(roleCmd.getAction())) {
                return deleteRole(aReq, aRes, roleCmd, aError);
            } else if (RoleCommand.ACTION_VIEW.equals(roleCmd.getAction())||
            		RoleCommand.ACTION_EDIT.equals(roleCmd.getAction())) {
                return editRole(aReq, aRes, roleCmd, aError);
            } else if (RoleCommand.ACTION_FILTER.equals(roleCmd.getAction())){
            	aReq.getSession().setAttribute(RoleCommand.PARAM_AGENCY_FILTER, roleCmd.getAgencyFilter());
                return defaultView(roleCmd.getAgencyFilter());
            }
        }
                
        return null;
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest aReq, HttpServletResponse aRes, BindException aError) throws Exception {
        String agencyFilter = (String)aReq.getSession().getAttribute(RoleCommand.PARAM_AGENCY_FILTER);
        if(agencyFilter == null)
        {
        	agencyFilter = AuthUtil.getRemoteUserObject().getAgency().getName();
        }
        return defaultView(agencyFilter);
    }

    /**
     * Save the new role or updates an existing role
     * to the database with all the associated privileges
     * @param aReq the HTTP Request
     * @param aRes the HTTP response
     * @param aRoleCmd the RoleCommand holding the defined role parameters 
     * @param aError the Error object
     * @param mav the ModelAndView to return
     * @throws Exception
     */
    private ModelAndView saveRole(HttpServletRequest aReq, HttpServletResponse aRes, RoleCommand aRoleCmd, BindException aError) throws Exception {
        boolean update = false;
        
        Role role = new Role();
        if (aRoleCmd.getOid() != null) {
            //handle an update action
            update = true;
            role = agencyUserManager.getRoleByOid(aRoleCmd.getOid());
        }
        role.setName(aRoleCmd.getRoleName());
        role.setDescription(aRoleCmd.getDescription());
        Agency agency = agencyUserManager.getAgencyByOid(aRoleCmd.getAgency());
        role.setAgency(agency);
        
        
        Map<String,RolePrivilege> selectedPrivs = new HashMap<String,RolePrivilege>();
        
        //iterate through each of the selected privileges
        if (aRoleCmd.getPrivileges() != null && aRoleCmd.getPrivileges().length > 0) {
            for (int j = 0; j < aRoleCmd.getPrivileges().length; j++) {
                String privCode = aRoleCmd.getPrivileges()[j];
                RolePrivilege aPriv = new RolePrivilege();
                aPriv.setPrivilege(privCode);
                aPriv.setPrivilegeScope(Privilege.SCOPE_ALL);
                aPriv.setRole(role);
                selectedPrivs.put(privCode,aPriv);
            }
        }
        
        //work out the scope selected for each privilege
        log.debug("Display the ScopedPrivilege array "+aRoleCmd.getScopedPrivileges());
        Set <RolePrivilege>selectedPrivScopes = new HashSet<RolePrivilege>(aRoleCmd.getScopedPrivileges().length);
        for (int j = 0; j < aRoleCmd.getScopedPrivileges().length; j++) {
            String privCodeKVP = aRoleCmd.getScopedPrivileges()[j];
            String privCode = getPrivKey(privCodeKVP);
            log.debug("privKey ="+privCode);
            int privScope = getPrivScope(privCodeKVP);
            log.debug("privScope ="+privScope);
            
            if (selectedPrivs.containsKey(privCode)) {
                //only add privilege scope if the privilege has been selected
                log.debug("A match was found on privilege "+privCode);
                RolePrivilege aPriv = new RolePrivilege();
                aPriv.setPrivilege(privCode);
                aPriv.setPrivilegeScope(privScope);
                aPriv.setRole(role);
                selectedPrivScopes.add(aPriv);
            }
        }
        role.clearRolePrivileges();
        role.addRolePrivileges(selectedPrivScopes);
        agencyUserManager.updateRole(role, update);
        
        String agencyFilter = (String)aReq.getSession().getAttribute(RoleCommand.PARAM_AGENCY_FILTER);
        if(agencyFilter == null)
        {
        	agencyFilter = AuthUtil.getRemoteUserObject().getAgency().getName();
        }
        ModelAndView mav = defaultView(agencyFilter);
        if (update == true) {
            mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("role.updated", new Object[] { aRoleCmd.getRoleName() }, Locale.getDefault()));
        } else {
            mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("role.created", new Object[] { aRoleCmd.getRoleName() }, Locale.getDefault()));
        }
        return mav;
    }

    /**
     * Process the delete role command. 
     */
    private ModelAndView deleteRole(HttpServletRequest aReq, HttpServletResponse aRes, RoleCommand aRoleCmd, BindException aError) throws Exception {
        Role role = agencyUserManager.getRoleByOid(aRoleCmd.getOid());
        String roleName = role.getName();
        
        Set allUsers = role.getUsers();
        Iterator it = allUsers.iterator();
        while (it.hasNext()) {
            User user = (User) it.next();
            user.removeRole(role);
        }
        agencyUserManager.deleteRole(role);
        
        String agencyFilter = (String)aReq.getSession().getAttribute(RoleCommand.PARAM_AGENCY_FILTER);
        if(agencyFilter == null)
        {
        	agencyFilter = AuthUtil.getRemoteUserObject().getAgency().getName();
        }
        ModelAndView mav = defaultView(agencyFilter);
        mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("role.deleted", new Object[] { roleName }, Locale.getDefault()));
        return mav;
    }
    
    /** 
     * Process the edit role command. 
     */
    private ModelAndView editRole(HttpServletRequest aReq, HttpServletResponse aRes, RoleCommand aRoleCmd, BindException aError) throws Exception {
        List agencies = agencyUserManager.getAgenciesForLoggedInUser();
        ModelAndView mav = new ModelAndView();
        mav.addObject(RoleCommand.MDL_AGENCIES, agencies);
        
        Role role = agencyUserManager.getRoleByOid(aRoleCmd.getOid());
        Set rolePrivileges = role.getRolePrivileges();
        int roleNumber = rolePrivileges.size();
        String[] privCodes = new String[roleNumber];
        String[] privScopes = new String[roleNumber];
        int offset=0;
        Iterator it = rolePrivileges.iterator();
        while (it.hasNext()) {
            RolePrivilege rp = (RolePrivilege)it.next();
            privCodes[offset]=rp.getPrivilege();
            privScopes[offset]=rp.getPrivilege()+Constants.PRIVILEGE_DELIMITER+rp.getPrivilegeScope();
            offset++;
        }
        
        aRoleCmd.setOid(role.getOid());
        aRoleCmd.setRoleName(role.getName());
        aRoleCmd.setDescription(role.getDescription());
        aRoleCmd.setAgency(role.getAgency().getOid());
        aRoleCmd.setViewOnlyMode(RoleCommand.ACTION_VIEW.equals(aRoleCmd.getAction()));
        aRoleCmd.setPrivileges(privCodes);
        log.debug("selected roles for edit are "+privCodes);
        aRoleCmd.setScopedPrivileges(privScopes);
        log.debug("selected scoped roles for edit are "+privScopes);
        mav.addObject(Constants.GBL_CMD_DATA, aRoleCmd);
        mav.setViewName("AddRole");
        return mav;
    }
    
    /**
     * Return the privlege code.
     * @param kvp the key value pair
     * @return the privilege code
     */
    private String getPrivKey(String kvp) {
        if (kvp != null) {
            int index = kvp.indexOf(Constants.PRIVILEGE_DELIMITER);
            return kvp.substring(0,index);
        } else {
            return "";
        }
    }
    
    /** 
     * Return the privilege scope.
     * @param kvp the key value pair
     * @return the scope
     */
    private int getPrivScope(String kvp) {
        if (kvp != null) {
            int index = kvp.indexOf(Constants.PRIVILEGE_DELIMITER);
            String scope = kvp.substring(index+1,kvp.length());
            int scopeInt = Integer.parseInt(scope);
            return scopeInt;
        } else {
            return Privilege.SCOPE_OWNER;
        }
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
