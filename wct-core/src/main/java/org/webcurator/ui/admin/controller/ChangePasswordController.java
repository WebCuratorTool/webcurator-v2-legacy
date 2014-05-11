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
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.providers.dao.salt.SystemWideSaltSource;
import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.ui.admin.command.ChangePasswordCommand;
import org.webcurator.ui.admin.command.UserCommand;
import org.webcurator.ui.common.Constants;

/**
 * Manage the view for changing a users password.
 * @author bprice
 */
public class ChangePasswordController extends AbstractFormController {
	/** the agency user manager. */
    private AgencyUserManager agencyUserManager = null;
    /** the authority manager */
    private AuthorityManager authorityManager = null;
    /** the password encoder. */
    private PasswordEncoder encoder;
    /** the system wide encoding salt. */
    private SystemWideSaltSource salt;
    /** the message source. */
    private MessageSource messageSource;
    
    /** Default Constructor. */
    public ChangePasswordController() {
        this.setCommandClass(ChangePasswordCommand.class);
    }
    
    @Override
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));   
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest aReq,
            HttpServletResponse aRes, BindException aBindEx) throws Exception {
     
        return null;
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq,
            HttpServletResponse aRes, Object aCmd, BindException aBindEx)
            throws Exception {
        ChangePasswordCommand pwdCmd = (ChangePasswordCommand) aCmd;
        if (ChangePasswordCommand.ACTION_SAVE.equals(pwdCmd.getAction())) {
            //Save the Change of password action
            ChangePasswordCommand aPwdCommand = (ChangePasswordCommand) aCmd;
            return processPasswordChange(aReq, aRes, aPwdCommand, aBindEx);
        } else {
            //Display the change password form
            return createDefaultModelAndView(aReq, pwdCmd);           
        }
    }
    
    /** 
     * Process the command tp change the users password. 
     */
    private ModelAndView processPasswordChange(HttpServletRequest aReq,HttpServletResponse aResp, ChangePasswordCommand aCmd, BindException aErrors) throws Exception {
        ModelAndView mav = new ModelAndView();
        if (aErrors.hasErrors()) {
            mav.addObject(Constants.GBL_CMD_DATA, aErrors.getTarget());
            mav.addObject(Constants.GBL_ERRORS, aErrors);
            mav.setViewName("change-password");

            return mav;
        }

        try {

            User userAccount = agencyUserManager.getUserByOid(aCmd.getUserOid());
            
            String sysSalt = salt.getSystemWideSalt();
            String encodedPwd = encoder.encodePassword(aCmd.getNewPwd(),sysSalt);
            
            userAccount.setPassword(encodedPwd);

            agencyUserManager.updateUser(userAccount, true);
            
            mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("user.password.change", new Object[] { userAccount.getUsername() }, Locale.getDefault()));
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

            return mav;
        }
        catch (Exception e) {
            throw new Exception("Persistance Error occurred during password change", e);
        }
    }
    
    /**
     * Generate a default model and view. 
     */
    private ModelAndView createDefaultModelAndView(HttpServletRequest aReq, ChangePasswordCommand pwdCmd) {        
        ModelAndView mav = new ModelAndView();
        mav.addObject(Constants.GBL_CMD_DATA, pwdCmd);
        mav.setViewName("change-password");
        
        return mav;
    }

    /**  
     * @param encoder set the password encoder.
     */
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    /** 
     * @param salt the system wide salt.
     */
    public void setSalt(SystemWideSaltSource salt) {
        this.salt = salt;
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
