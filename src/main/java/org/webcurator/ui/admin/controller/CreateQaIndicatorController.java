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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.IndicatorCriteria;
import org.webcurator.ui.admin.command.CreateQaIndicatorCommand;
import org.webcurator.ui.admin.command.QaIndicatorCommand;
import org.webcurator.ui.common.Constants;

/**
 * Manages the creation flow for a QA Indicator within WCT
 * @author oakleigh_sk
 */
public class CreateQaIndicatorController extends AbstractFormController {
	/** the logger. */
    private Log log = null;
    /** the agency user manager. */
    private AgencyUserManager agencyUserManager = null;
    /** the authority manager. */
    private AuthorityManager authorityManager = null;
    /** the message source. */
    private MessageSource messageSource = null;
    
    /** Default Constructor. */
    public CreateQaIndicatorController() {
    	super();
        log = LogFactory.getLog(CreateQaIndicatorController.class);
        setCommandClass(CreateQaIndicatorCommand.class);
    }
    
    @Override
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    	// enable null values for long and float fields
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        NumberFormat floatFormat = new DecimalFormat("############.##");
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));   
        binder.registerCustomEditor(java.lang.Float.class, new CustomNumberEditor(java.lang.Float.class, floatFormat, true));   
    }
    
    @Override
    protected ModelAndView showForm(HttpServletRequest arg0,
            HttpServletResponse arg1, BindException arg2) throws Exception {
        
        return null;
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq,
            HttpServletResponse aRes, Object aCommand, BindException aError)
            throws Exception {

        ModelAndView mav = null;
        CreateQaIndicatorCommand indicatorCmd = (CreateQaIndicatorCommand) aCommand;
        
            
        if (indicatorCmd != null) {
            if (aError.hasErrors()) {
                mav = new ModelAndView();
                List agencies = agencyUserManager.getAgenciesForLoggedInUser();
                mav.addObject(CreateQaIndicatorCommand.MDL_AGENCIES, agencies);
                mav.addObject(CreateQaIndicatorCommand.MDL_UNITS, IndicatorCriteria.UNITS);

                String mode = indicatorCmd.getMode();
                if (CreateQaIndicatorCommand.ACTION_EDIT.equals(mode)) {
                    mav.addObject(CreateQaIndicatorCommand.ACTION_EDIT, mode);
                }
                mav.addObject(Constants.GBL_CMD_DATA, aError.getTarget());
                mav.addObject(Constants.GBL_ERRORS, aError);
                mav.setViewName("newIndicator");
                
            } else if (CreateQaIndicatorCommand.ACTION_NEW.equals(indicatorCmd.getAction())) {
                mav = new ModelAndView();
                List agencies = agencyUserManager.getAgenciesForLoggedInUser();
                mav.addObject(CreateQaIndicatorCommand.MDL_AGENCIES, agencies);
                mav.addObject(CreateQaIndicatorCommand.MDL_UNITS, IndicatorCriteria.UNITS);
                mav.setViewName("newIndicator");
                
            } else if (CreateQaIndicatorCommand.ACTION_VIEW.equals(indicatorCmd.getAction()) ||
            		CreateQaIndicatorCommand.ACTION_EDIT.equals(indicatorCmd.getAction())) {
                //View/Edit an existing indicator
                mav = new ModelAndView();
                Long indicatorOid = indicatorCmd.getOid(); 
                IndicatorCriteria indicator = agencyUserManager.getIndicatorCriteriaByOid(indicatorOid);
                CreateQaIndicatorCommand editCmd = new CreateQaIndicatorCommand();
                editCmd.setOid(indicatorOid);
                editCmd.setAgencyOid(indicator.getAgency().getOid());
                editCmd.setName(indicator.getName());
                editCmd.setDescription(indicator.getDescription());
                editCmd.setUpperLimit(indicator.getUpperLimit());
                editCmd.setLowerLimit(indicator.getLowerLimit());
                editCmd.setUpperLimitPercentage(indicator.getUpperLimitPercentage());
                editCmd.setLowerLimitPercentage(indicator.getLowerLimitPercentage());
                editCmd.setUnit(indicator.getUnit());
                editCmd.setShowDelta(indicator.getShowDelta());
                editCmd.setEnableReport(indicator.getEnableReport());
                editCmd.setMode(indicatorCmd.getAction());
                
                List agencies = agencyUserManager.getAgenciesForLoggedInUser();
                mav.addObject(CreateQaIndicatorCommand.MDL_AGENCIES, agencies);
                mav.addObject(CreateQaIndicatorCommand.MDL_UNITS, IndicatorCriteria.UNITS);
                mav.addObject(Constants.GBL_CMD_DATA, editCmd);
                mav.setViewName("newIndicator");
                
            } else if (CreateQaIndicatorCommand.ACTION_SAVE.equals(indicatorCmd.getAction())) {
                
                
                    try {
                        IndicatorCriteria indicator = new IndicatorCriteria();
                        boolean update = (indicatorCmd.getOid() != null);
                        if (update == true) {
                            // Update an existing indicator object by loading it in first
                        	indicator = agencyUserManager.getIndicatorCriteriaByOid(indicatorCmd.getOid());
                        } else {
                        	// Save the newly created indicator object
                        
                            //load Agency
                            Long agencyOid = indicatorCmd.getAgencyOid();
                            Agency agency = agencyUserManager.getAgencyByOid(agencyOid);
                            indicator.setAgency(agency);
                        }
                        
                        indicator.setName(indicatorCmd.getName());
                        indicator.setDescription(indicatorCmd.getDescription());
                        indicator.setUpperLimit(indicatorCmd.getUpperLimit());
                        indicator.setLowerLimit(indicatorCmd.getLowerLimit());
                        indicator.setUpperLimitPercentage(indicatorCmd.getUpperLimitPercentage());
                        indicator.setLowerLimitPercentage(indicatorCmd.getLowerLimitPercentage());
                        
                        indicator.setUnit(indicatorCmd.getUnit());
                        Boolean showDelta = indicatorCmd.getShowDelta();
                        if (showDelta == null) showDelta = false;
                        indicator.setShowDelta(showDelta);
                        Boolean enableReport = indicatorCmd.getEnableReport();
                        if (enableReport == null) enableReport = false;
                        indicator.setEnableReport(enableReport);
                        
                        agencyUserManager.updateIndicatorCriteria(indicator, update);
                        
                        List indicators = agencyUserManager.getIndicatorCriteriaForLoggedInUser();
                        List agencies = null;
                        if (authorityManager.hasPrivilege(Privilege.MANAGE_INDICATORS, Privilege.SCOPE_ALL)) {
                        	agencies = agencyUserManager.getAgencies();
                        } else {
                            User loggedInUser = AuthUtil.getRemoteUserObject();
                            Agency usersAgency = loggedInUser.getAgency();
                            agencies = new ArrayList<Agency>();
                            agencies.add(usersAgency);
                        }
                        
                        mav = new ModelAndView();
                        String message;
                        if (update == true) {
                            message = messageSource.getMessage("indicator.updated", new Object[] { indicatorCmd.getName() }, Locale.getDefault());
                        } else {
                            message = messageSource.getMessage("indicator.created", new Object[] { indicatorCmd.getName() }, Locale.getDefault());
                        }
                        String agencyFilter = (String)aReq.getSession().getAttribute(QaIndicatorCommand.MDL_AGENCYFILTER);
                        if(agencyFilter == null)
                        {
                        	agencyFilter = AuthUtil.getRemoteUserObject().getAgency().getName();
                        }
                        mav.addObject(QaIndicatorCommand.MDL_AGENCYFILTER, agencyFilter);
                        mav.addObject(QaIndicatorCommand.MDL_LOGGED_IN_USER, AuthUtil.getRemoteUserObject());
                        mav.addObject(QaIndicatorCommand.MDL_QA_INDICATORS, indicators);
                        mav.addObject(QaIndicatorCommand.MDL_AGENCIES, agencies);
                        mav.addObject(CreateQaIndicatorCommand.MDL_UNITS, IndicatorCriteria.UNITS);

                        mav.addObject(Constants.GBL_MESSAGES, message );

                        mav.setViewName("viewIndicators");
                    }
                    catch (DataAccessException e) {
                    	e.printStackTrace();
                    }     
                
            }
        } else {
            log.warn("No Action provided for CreateQaIndicatorController.");
        }
            
        return mav;
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
