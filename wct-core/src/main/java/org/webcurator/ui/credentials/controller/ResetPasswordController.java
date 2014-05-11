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
package org.webcurator.ui.credentials.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.dao.salt.SystemWideSaltSource;
import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.domain.UserRoleDAO;
import org.webcurator.domain.model.auth.User;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.credentials.command.ResetPasswordCommand;

/** 
 * Controller for managing reseting a users password.
 * @author bprice
 */
public class ResetPasswordController extends AbstractFormController {
	/** The data access object for authorisation data. */
    private UserRoleDAO authDAO;
    /** the password encoder. */
    private PasswordEncoder encoder;
    /** the system wide salt source. */
    private SystemWideSaltSource salt;
    /** Default Constructor. */
    public ResetPasswordController() {
        this.setCommandClass(ResetPasswordCommand.class);
    }
    
    @Override
    protected ModelAndView showForm(HttpServletRequest aReq,
            HttpServletResponse aRes, BindException aBindEx) throws Exception {
        return createDefaultModelAndView(aReq);
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq,
            HttpServletResponse aRes, Object aCmd, BindException aBindEx)
            throws Exception {
        ResetPasswordCommand aPwdCommand = (ResetPasswordCommand) aCmd;
        return processPasswordChange(aReq, aRes, aPwdCommand, aBindEx);
    }

    /**
     * @return the default model and view for the password reset page. 
     */
    private ModelAndView createDefaultModelAndView(HttpServletRequest aReq) {        
        ModelAndView mav = new ModelAndView();
        mav.setViewName(Constants.VIEW_RESET_PWD);
        
        return mav;
    }
    
    /**
     * Process the change password command. 
     */
    private ModelAndView processPasswordChange(HttpServletRequest aReq,HttpServletResponse aResp, ResetPasswordCommand aCmd, BindException aErrors) throws Exception {
        ModelAndView mav = new ModelAndView();
        if (aErrors.hasErrors()) {
            mav.addObject(Constants.GBL_CMD_DATA, aErrors.getTarget());
            mav.addObject(Constants.GBL_ERRORS, aErrors);
            mav.setViewName(Constants.VIEW_RESET_PWD);

            return mav;
        }

        try {
                        
            UsernamePasswordAuthenticationToken upat = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            
            User userAccount = (User) authDAO.getUserByName(upat.getName());
            
            String sysSalt = salt.getSystemWideSalt();
            String encodedPwd = encoder.encodePassword(aCmd.getNewPwd(),sysSalt);
            
            userAccount.setPassword(encodedPwd);
            //userAccount.setPwdFailedAttempts(0);
            userAccount.setForcePasswordChange(false);

            authDAO.saveOrUpdate(userAccount);
            
            upat.setDetails(userAccount);
            
            SecurityContextHolder.getContext().setAuthentication(upat);
            
            mav.addObject(Constants.MESSAGE_TEXT, "Your password has been changed.");
            mav.setViewName(Constants.VIEW_PASSWORD_RESET_SUCCESS);

            return mav;
        }
        catch (Exception e) {
            throw new Exception("Persistance Error occurred during password change", e);
        }
    }

	/**
	 * @param authDAO the authDAO to set
	 */
	public void setAuthDAO(UserRoleDAO authDAO) {
		this.authDAO = authDAO;
	}

	/**
	 * @param encoder the encoder to set
	 */
	public void setEncoder(PasswordEncoder encoder) {
		this.encoder = encoder;
	}

	/**
	 * @param salt the salt to set
	 */
	public void setSalt(SystemWideSaltSource salt) {
		this.salt = salt;
	}
}
