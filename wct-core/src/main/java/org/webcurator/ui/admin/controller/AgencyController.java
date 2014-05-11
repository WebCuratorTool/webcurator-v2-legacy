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
import java.util.List;
import java.util.Locale;

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
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.common.WCTTreeSet;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.ui.admin.command.AgencyCommand;
import org.webcurator.ui.common.Constants;

/**
 * Manages the Agency Administration view and the actions associated with a Agency
 * @author bprice
 */
public class AgencyController extends AbstractFormController {
	/** the logger. */
    private Log log = null;
    /** the user manager. */    
    private AgencyUserManager agencyUserManager = null;
    /** the message source. */
    private MessageSource messageSource = null;
    /** Default Constructor. */
    
	private WCTTreeSet typeList = null;

    public AgencyController() {
        log = LogFactory.getLog(AgencyController.class);
        setCommandClass(AgencyCommand.class);
    }
       
    @Override
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));   
    }
    
    @Override
    protected ModelAndView showForm(HttpServletRequest aReq,
            HttpServletResponse aRes, BindException aError) throws Exception {
        
        return populateAgencyList();        
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq,
            HttpServletResponse aRes, Object aCommand, BindException aError)
            throws Exception {
        
        ModelAndView mav = null;
        AgencyCommand agencyCmd = (AgencyCommand) aCommand;
        if (agencyCmd != null) {
            if (aError.hasErrors()) {
                mav = new ModelAndView();
                mav = populateAgencyList();
                mav.addObject(Constants.GBL_CMD_DATA, aError.getTarget());
                mav.addObject(Constants.GBL_ERRORS, aError);
                mav.setViewName("newAgency");
                
            } else if (AgencyCommand.ACTION_NEW.equals(agencyCmd.getActionCommand())) {
                //Display the Create Agency screen
                
                mav = populateAgencyList();
                mav.setViewName("newAgency");
            } else if (AgencyCommand.ACTION_SAVE.equals(agencyCmd.getActionCommand())) {
                //Save the new Agency details
                boolean update = false;
                Agency agency = new Agency();
                
                if (agencyCmd.getOid() != null) {
                    update = true;
                    agency.setOid(agencyCmd.getOid());
                }
                
                agency.setName(agencyCmd.getName());
                agency.setAddress(agencyCmd.getAddress());
                agency.setPhone(agencyCmd.getPhone());
                agency.setFax(agencyCmd.getFax());
                agency.setEmail(agencyCmd.getEmail());
                agency.setAgencyURL(agencyCmd.getAgencyURL());
                agency.setAgencyLogoURL(agencyCmd.getAgencyLogoURL());
                agency.setShowTasks(agencyCmd.getShowTasks());
                agency.setDefaultDescriptionType(agencyCmd.getDescriptionType());
                
                agencyUserManager.updateAgency(agency, update);
                mav = populateAgencyList();
                if (update == true) {
                    mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("agency.updated", new Object[] { agencyCmd.getName() }, Locale.getDefault()));
                } else {
                    mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("agency.created", new Object[] { agencyCmd.getName() }, Locale.getDefault()));
                }
            } else if (AgencyCommand.ACTION_VIEW.equals(agencyCmd.getActionCommand()) ||
            		AgencyCommand.ACTION_EDIT.equals(agencyCmd.getActionCommand())) {
                
                Agency agency = agencyUserManager.getAgencyByOid(agencyCmd.getOid());
                AgencyCommand populatedCmd = new AgencyCommand();
                populatedCmd.setOid(agency.getOid());
                populatedCmd.setName(agency.getName());
                populatedCmd.setAddress(agency.getAddress());
                populatedCmd.setPhone(agency.getPhone());
                populatedCmd.setFax(agency.getFax());
                populatedCmd.setEmail(agency.getEmail());
                populatedCmd.setAgencyURL(agency.getAgencyURL());
                populatedCmd.setAgencyLogoURL(agency.getAgencyLogoURL());
                populatedCmd.setShowTasks(agency.getShowTasks());
                populatedCmd.setViewOnlyMode(AgencyCommand.ACTION_VIEW.equals(agencyCmd.getActionCommand()));
                populatedCmd.setDescriptionType(agency.getDefaultDescriptionType());
                
                mav = new ModelAndView();
                mav = populateAgencyList();
                mav.addObject(Constants.GBL_CMD_DATA, populatedCmd);
                mav.setViewName("newAgency");
            }
        } else {
            log.warn("No Action provided for AgencyController.");
            mav = populateAgencyList();
        }
        
		mav.addObject("descriptionTypes", typeList);
        return mav;
    }

    /**  
     * @return a model and view populated with the list of agencies.
     */
    private ModelAndView populateAgencyList() {
        List agencyList = agencyUserManager.getAgenciesForLoggedInUser();
        
        ModelAndView mav = new ModelAndView();
        mav.addObject(AgencyCommand.MDL_AGENCIES, agencyList);
        mav.setViewName("viewAgencies");
        return mav;
    }

    /** 
     * @param agencyUserManager the agency manager
     */
    public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
        this.agencyUserManager = agencyUserManager;
    }

    /**  
     * @param messageSource the message source to use
     */
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

	public void setTypeList(WCTTreeSet typeList) {
		this.typeList = typeList;
	}


}

