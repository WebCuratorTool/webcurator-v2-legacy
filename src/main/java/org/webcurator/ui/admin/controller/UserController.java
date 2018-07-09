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
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.ui.admin.command.UserCommand;
import org.webcurator.ui.common.Constants;
/**
 * Manages the User Administration view and the actions associated with a User
 * @author bprice
 */
public class UserController extends AbstractFormController {
	/** the logger. */
    private Log log = null;
    /** the agency user manager. */
    private AgencyUserManager agencyUserManager = null;
    /** the authority manager. */
    private AuthorityManager authorityManager = null;
    /** the message source. */
    private MessageSource messageSource = null;
    /** Default Constructor. */
    public UserController() {
        log = LogFactory.getLog(UserController.class);
        setCommandClass(UserCommand.class);
    }
    
    @Override
    protected ModelAndView showForm(HttpServletRequest aReq,
            HttpServletResponse aRes, BindException aError) throws Exception {
        ModelAndView mav = new ModelAndView();
        String agencyFilter = (String)aReq.getSession().getAttribute(UserCommand.MDL_AGENCYFILTER);
        if(agencyFilter == null)
        {
        	agencyFilter = AuthUtil.getRemoteUserObject().getAgency().getName();
        }
        mav.addObject(UserCommand.MDL_AGENCYFILTER, agencyFilter);
        populateUserList(mav);
        return mav;        
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq,
            HttpServletResponse aRes, Object aCommand, BindException aError)
            throws Exception {
        
        ModelAndView mav = new ModelAndView();
        UserCommand userCmd = (UserCommand) aCommand;
        if (userCmd != null) {
        	
            if (UserCommand.ACTION_STATUS.equals(userCmd.getCmd())) {
                //Attempt to change the status of the user
                 
                Long userOid = userCmd.getOid(); 
                User user = agencyUserManager.getUserByOid(userOid);
                agencyUserManager.modifyUserStatus(user);
                populateUserList(mav);
            } else if (UserCommand.ACTION_MANAGE_ROLES.equals(userCmd.getCmd())) {
                //Display the Manage User Roles screen
                populateUserList(mav);
            } else if (UserCommand.ACTION_DELETE.equals(userCmd.getCmd())) {
                // Attempt to delete a user from the system
                Long userOid = userCmd.getOid(); 
                User user = agencyUserManager.getUserByOid(userOid);   
                String username = user.getUsername();
                try {
                    agencyUserManager.deleteUser(user);      
                } catch (DataAccessException e) {
                    String[] codes = {"user.delete.fail"};
                    Object[] args = new Object[1];
                    args[0] = user.getUsername();
                    if (aError == null) {
                        aError = new BindException(userCmd, "command");
                    }
                    aError.addError(new ObjectError("command",codes,args,"User owns objects in the system and can't be deleted."));
                    mav.addObject(Constants.GBL_ERRORS, aError);
                    populateUserList(mav);
                    return mav;
                }
                populateUserList(mav);
                mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("user.deleted", new Object[] { username }, Locale.getDefault()));
            } else if (UserCommand.ACTION_FILTER.equals(userCmd.getCmd())) {
                //Just filtering users by agency - if we change the default, store it in a session
            	aReq.getSession().setAttribute(UserCommand.MDL_AGENCYFILTER, userCmd.getAgencyFilter());
                populateUserList(mav);
            }
        } else {
            log.warn("No Action provided for UserController.");
            populateUserList(mav);
        }
        
        String agencyFilter = (String)aReq.getSession().getAttribute(UserCommand.MDL_AGENCYFILTER);
        if(agencyFilter == null)
        {
        	agencyFilter = AuthUtil.getRemoteUserObject().getAgency().getName();
        }
        mav.addObject(UserCommand.MDL_AGENCYFILTER, agencyFilter);
        return mav;
    }
    
    /** 
     * Populate the user list model object in the model and view provided.
     * @param mav the model and view to add the user list to.
     */
    private void populateUserList(ModelAndView mav) {
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
        
        mav.addObject(UserCommand.MDL_USERS, userDTOs);
        mav.addObject(UserCommand.MDL_LOGGED_IN_USER, AuthUtil.getRemoteUserObject());
        mav.addObject(UserCommand.MDL_AGENCIES, agencies);
        mav.setViewName("viewUsers");
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
