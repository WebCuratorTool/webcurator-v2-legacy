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
import org.webcurator.domain.model.core.RejReason;
import org.webcurator.ui.admin.command.RejReasonCommand;
import org.webcurator.ui.common.Constants;
/**
 * Manages the Rejection Reason Administration view and the actions associated with a Rejection Reason
 * @author oakleigh_sk
 */
public class RejReasonController extends AbstractFormController {
	/** the logger. */
    private Log log = null;
    /** the agency user manager. */
    private AgencyUserManager agencyUserManager = null;
    /** the authority manager. */
    private AuthorityManager authorityManager = null;
    /** the message source. */
    private MessageSource messageSource = null;
    /** Default Constructor. */
    public RejReasonController() {
        log = LogFactory.getLog(RejReasonController.class);
        setCommandClass(RejReasonCommand.class);
    }
    
    @Override
    protected ModelAndView showForm(HttpServletRequest aReq,
            HttpServletResponse aRes, BindException aError) throws Exception {
        ModelAndView mav = new ModelAndView();
        String agencyFilter = (String)aReq.getSession().getAttribute(RejReasonCommand.MDL_AGENCYFILTER);
        if(agencyFilter == null)
        {
        	agencyFilter = AuthUtil.getRemoteUserObject().getAgency().getName();
        }
        mav.addObject(RejReasonCommand.MDL_AGENCYFILTER, agencyFilter);
        populateRejReasonList(mav);
        return mav;        
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq,
            HttpServletResponse aRes, Object aCommand, BindException aError)
            throws Exception {
        
        ModelAndView mav = new ModelAndView();
        RejReasonCommand reasonCmd = (RejReasonCommand) aCommand;
        if (reasonCmd != null) {
        	
        	if (RejReasonCommand.ACTION_DELETE.equals(reasonCmd.getCmd())) {
                // Attempt to delete a rejection reason from the system
                Long reasonOid = reasonCmd.getOid(); 
                RejReason reason = agencyUserManager.getRejReasonByOid(reasonOid);   
                String name = reason.getName();
                try {
                    agencyUserManager.deleteRejReason(reason);      
                } catch (DataAccessException e) {
                    String[] codes = {"rejreason.delete.fail"};
                    Object[] args = new Object[1];
                    args[0] = reason.getName();
                    if (aError == null) {
                        aError = new BindException(reasonCmd, "command");
                    }
                    aError.addError(new ObjectError("command",codes,args,"Rejection Reason owns objects in the system and can't be deleted."));
                    mav.addObject(Constants.GBL_ERRORS, aError);
                    populateRejReasonList(mav);
                    return mav;
                }
                populateRejReasonList(mav);
                mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("rejreason.deleted", new Object[] { name }, Locale.getDefault()));
            } else if (RejReasonCommand.ACTION_FILTER.equals(reasonCmd.getCmd())) {
                //Just filtering reasons by agency - if we change the default, store it in the session
            	aReq.getSession().setAttribute(RejReasonCommand.MDL_AGENCYFILTER, reasonCmd.getAgencyFilter());
            	populateRejReasonList(mav);
            }
        } else {
            log.warn("No Action provided for RejReasonController.");
            populateRejReasonList(mav);
        }
        
        String agencyFilter = (String)aReq.getSession().getAttribute(RejReasonCommand.MDL_AGENCYFILTER);
        if(agencyFilter == null)
        {
        	agencyFilter = AuthUtil.getRemoteUserObject().getAgency().getName();
        }
        mav.addObject(RejReasonCommand.MDL_AGENCYFILTER, agencyFilter);
        return mav;
    }
    
    /** 
     * Populate the rejection reason list model object in the model and view provided.
     * @param mav the model and view to add the user list to.
     */
    private void populateRejReasonList(ModelAndView mav) {
        List reasons = agencyUserManager.getRejReasonsForLoggedInUser();
        List agencies = null;
        if (authorityManager.hasPrivilege(Privilege.MANAGE_REASONS, Privilege.SCOPE_ALL)) {
        	agencies = agencyUserManager.getAgencies();
        } else {
            User loggedInUser = AuthUtil.getRemoteUserObject();
            Agency usersAgency = loggedInUser.getAgency();
            agencies = new ArrayList<Agency>();
            agencies.add(usersAgency);
        }
        
        mav.addObject(RejReasonCommand.MDL_REASONS, reasons);
        mav.addObject(RejReasonCommand.MDL_LOGGED_IN_USER, AuthUtil.getRemoteUserObject());
        mav.addObject(RejReasonCommand.MDL_AGENCIES, agencies);
        mav.setViewName("viewReasons");
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
