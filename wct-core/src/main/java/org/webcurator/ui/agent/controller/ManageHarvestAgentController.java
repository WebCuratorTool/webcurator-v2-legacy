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
package org.webcurator.ui.agent.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.ui.agent.command.ManageHarvestAgentCommand;
import org.webcurator.ui.common.Constants;

/**
 * The controller for displaying harvest agent data.
 * @author nwaight
 */
public class ManageHarvestAgentController extends AbstractFormController {
    /** The class the coordinates the harvest agents and holds their states. */
    private HarvestCoordinator harvestCoordinator;
    /** the logger. */
    private Log log;
    
    /** Default constructor. */    
    public ManageHarvestAgentController() {
        super();
        log = LogFactory.getLog(getClass());
        setCommandClass(ManageHarvestAgentCommand.class);       
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractFormController#showForm(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.validation.BindException)
     */
    @Override
    protected ModelAndView showForm(HttpServletRequest aReq,
            HttpServletResponse aResp, BindException aErrors) throws Exception {        
        // Show the initial manage harvests page.                        
        ModelAndView mav = processAgentSummary(aReq, aResp, new ManageHarvestAgentCommand(), aErrors);
        
        ManageHarvestAgentCommand command = new ManageHarvestAgentCommand();
        
        populateCommand(command);
		mav.addObject(Constants.GBL_CMD_DATA, command);
        
        return mav;
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq,
            HttpServletResponse aResp, Object aCmd, BindException aErrors)
            throws Exception {
        ManageHarvestAgentCommand command = (ManageHarvestAgentCommand) aCmd;
        if (log.isDebugEnabled()) {
            log.debug("process command " + command.getActionCmd());
        }
        
        ModelAndView mav = new ModelAndView();
        if (command != null && command.getActionCmd() != null) {
            if (command.getActionCmd().equals(ManageHarvestAgentCommand.ACTION_AGENT)) {
                mav = processAgentDetails(aReq, aResp, command, aErrors);
            }
            else if (command.getActionCmd().equals(ManageHarvestAgentCommand.ACTION_SUMMARY)) {
            	mav = processAgentSummary(aReq, aResp, command, aErrors);
            }
            else if (command.getActionCmd().equals(ManageHarvestAgentCommand.ACTION_HOME)) {
            	mav =  new ModelAndView("redirect:/" + Constants.CNTRL_HOME);
            }
            else if (command.getActionCmd().equals(ManageHarvestAgentCommand.ACTION_PAUSE)) {
            	mav =  processPauseAll(aReq, aResp, command, aErrors);
            }
            else if (command.getActionCmd().equals(ManageHarvestAgentCommand.ACTION_RESUME)) {
            	mav =  processResumeAll(aReq, aResp, command, aErrors);
            }
            else if (command.getActionCmd().equals(ManageHarvestAgentCommand.ACTION_PAUSEQ)) {
            	mav =  processPauseQueue(aReq, aResp, command, aErrors);
            }
            else if (command.getActionCmd().equals(ManageHarvestAgentCommand.ACTION_RESUMEQ)) {
            	mav =  processResumeQueue(aReq, aResp, command, aErrors);
            }
            else if (command.getActionCmd().equals(ManageHarvestAgentCommand.ACTION_PAUSE_AGENT)) {
            	mav =  processPauseAgent(aReq, aResp, command, aErrors);
            }
            else if (command.getActionCmd().equals(ManageHarvestAgentCommand.ACTION_RESUME_AGENT)) {
            	mav =  processResumeAgent(aReq, aResp, command, aErrors);
            }
            else if (command.getActionCmd().equals(ManageHarvestAgentCommand.ACTION_OPTIMIZE_DISABLE)) {
            	mav =  processChangeOptimization(aReq, aResp, command, aErrors, false);
            }
            else if (command.getActionCmd().equals(ManageHarvestAgentCommand.ACTION_OPTIMIZE_ENABLE)) {
            	mav =  processChangeOptimization(aReq, aResp, command, aErrors, true);
            }
            else {
                throw new WCTRuntimeException("Unknown command " + command.getActionCmd() + " recieved.");
            }

            populateCommand(command);
    		mav.addObject(Constants.GBL_CMD_DATA, command);
            
            return mav;
        }
                
        throw new WCTRuntimeException("Unknown command recieved.");
    }

	/**
     * @param aHarvestCoordinator The harvestCoordinator to set.
     */
    public void setHarvestCoordinator(HarvestCoordinator aHarvestCoordinator) {
        this.harvestCoordinator = aHarvestCoordinator;
    }
    
    /**
     * process the Show Agent Details action.
     * @see AbstractFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    private ModelAndView processAgentDetails(HttpServletRequest aReq, HttpServletResponse aResp, ManageHarvestAgentCommand aCmd, BindException aErrors) throws Exception {
        ModelAndView mav = new ModelAndView();
        
        HashMap agents = harvestCoordinator.getHarvestAgents();
        HarvestAgentStatusDTO status = (HarvestAgentStatusDTO) agents.get(aCmd.getAgentName());
                
        mav.addObject(ManageHarvestAgentCommand.MDL_HARVEST_AGENT, status);
        mav.setViewName(Constants.VIEW_AGENT);
        
        return mav;
    }
    
    /**
     * process the Show Agent Summary action.
     * @see AbstractFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    private ModelAndView processAgentSummary(HttpServletRequest aReq, HttpServletResponse aResp, ManageHarvestAgentCommand aCmd, BindException aErrors) throws Exception {
    	ModelAndView mav = getDefaultModelAndView();
        return mav;
    }
    
    /**
     * process the pause all running harvests action.
     * @see AbstractFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    private ModelAndView processPauseAll(HttpServletRequest aReq, HttpServletResponse aResp, ManageHarvestAgentCommand aCmd, BindException aErrors) throws Exception {
    	harvestCoordinator.pauseAll();
    	ModelAndView mav = getDefaultModelAndView();
        return mav;
    }
    
    /**
     * process the resume all paused harvests action.
     * @see AbstractFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    private ModelAndView processResumeAll(HttpServletRequest aReq, HttpServletResponse aResp, ManageHarvestAgentCommand aCmd, BindException aErrors) throws Exception {
    	harvestCoordinator.resumeAll();
    	ModelAndView mav = getDefaultModelAndView();
        return mav;
    }
    
    /**
     * process the halt Scheduled and Queued harvests action.
     * @see AbstractFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    private ModelAndView processPauseQueue(HttpServletRequest aReq, HttpServletResponse aResp, ManageHarvestAgentCommand aCmd, BindException aErrors) throws Exception {
    	harvestCoordinator.pauseQueue();
    	ModelAndView mav = getDefaultModelAndView();
        return mav;
    }
    
    /**
     * process the resume Scheduled and Queued harvests action.
     * @see AbstractFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    private ModelAndView processResumeQueue(HttpServletRequest aReq, HttpServletResponse aResp, ManageHarvestAgentCommand aCmd, BindException aErrors) throws Exception {
    	harvestCoordinator.resumeQueue();
    	ModelAndView mav = getDefaultModelAndView();
        return mav;
    }

    /**
     * process the halt Scheduled and Queued harvests action.
     * @see AbstractFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    private ModelAndView processPauseAgent(HttpServletRequest aReq, HttpServletResponse aResp, ManageHarvestAgentCommand aCmd, BindException aErrors) throws Exception {
    	harvestCoordinator.pauseAgent(aCmd.getAgentName());
    	ModelAndView mav = getDefaultModelAndView();
        return mav;
    }
    
    /**
     * process the resume Scheduled and Queued harvests action.
     * @see AbstractFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    private ModelAndView processResumeAgent(HttpServletRequest aReq, HttpServletResponse aResp, ManageHarvestAgentCommand aCmd, BindException aErrors) throws Exception {
    	harvestCoordinator.resumeAgent(aCmd.getAgentName());
    	ModelAndView mav = getDefaultModelAndView();
        return mav;
    }
    
    
    private ModelAndView processChangeOptimization(HttpServletRequest aReq,
			HttpServletResponse aResp, ManageHarvestAgentCommand command,
			BindException aErrors, boolean optimizationEnabled) {
    	harvestCoordinator.setHarvestOptimizationEnabled(optimizationEnabled);
    	ModelAndView mav = getDefaultModelAndView();
        return mav;
	}

	private ModelAndView getDefaultModelAndView() {
		ModelAndView mav = new ModelAndView();
        mav.addObject(ManageHarvestAgentCommand.MDL_HARVEST_AGENTS, harvestCoordinator.getHarvestAgents());
        mav.setViewName(Constants.VIEW_MNG_AGENTS);
		return mav;
	}

	private void populateCommand(ManageHarvestAgentCommand command) {
		command.setQueuePaused(harvestCoordinator.isQueuePaused());
        command.setOptimizationEnabled(harvestCoordinator.isHarvestOptimizationEnabled());
        command.setOptimizationLookaheadHours(harvestCoordinator.getHarvestOptimizationLookAheadHours());
	}



}
