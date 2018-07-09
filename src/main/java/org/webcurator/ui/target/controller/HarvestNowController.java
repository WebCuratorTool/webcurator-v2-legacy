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
package org.webcurator.ui.target.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.TargetInstanceCommand;

/**
 * This controller is responsible for allocating a TargetInstance to a 
 * Harvest Agent immediatly.
 * @author nwaight
 */
public class HarvestNowController extends AbstractFormController {
    /** The manager to use to access the target instance. */
    private TargetInstanceManager targetInstanceManager;
    /** The harvest coordinator for looking at the harvesters. */
    private HarvestCoordinator harvestCoordinator;
    /** the message source. */
    private MessageSource messageSource = null;
    /** the logger. */
    private Log log;
    
    /** 
     * Constructor to set the command class for this controller.
     */
    public HarvestNowController() {
        super();
        setCommandClass(TargetInstanceCommand.class);
        log = LogFactory.getLog(HarvestNowController.class);
    }
    
    @Override
    protected ModelAndView showForm(HttpServletRequest aReq,
            HttpServletResponse aResp, BindException aErrs) throws Exception {
        if (log.isWarnEnabled()) {
            log.warn("the showForm method is not supported in this class.");
        }
        
        return null;
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq,
            HttpServletResponse aResp, Object aCmd, BindException aErrs)
            throws Exception {
        
    	TargetInstanceCommand cmd = (TargetInstanceCommand) aCmd;    	
    	if (!cmd.getCmd().equals(TargetInstanceCommand.ACTION_HARVEST)) {
            aReq.getSession().removeAttribute(TargetInstanceCommand.SESSION_TI);
            return new ModelAndView("redirect:/" + Constants.CNTRL_TI_QUEUE);
    	}
    	    	
    	HashMap<String, HarvestAgentStatusDTO> agents = harvestCoordinator.getHarvestAgents();
        TargetInstance ti = targetInstanceManager.getTargetInstance(cmd.getTargetInstanceId());            
        String instanceAgency = ti.getOwner().getAgency().getName(); 
        
        String key = "";
        HarvestAgentStatusDTO agent = null;
        HashMap<String, HarvestAgentStatusDTO> allowedAgents = new HashMap<String, HarvestAgentStatusDTO>();
        Iterator<String> it = agents.keySet().iterator();
        while (it.hasNext()) {
			key = (String) it.next();
			agent = agents.get(key);				
			if (agent.getAllowedAgencies().contains(instanceAgency)
				|| agent.getAllowedAgencies().isEmpty()) {
				allowedAgents.put(key, agent);
			}
		}    	
    	
        if (aErrs.hasErrors()) {
        	ModelAndView mav = new ModelAndView();
            mav.addObject(Constants.GBL_CMD_DATA, aCmd);                
            mav.addObject(TargetInstanceCommand.MDL_AGENTS, allowedAgents);            
        	mav.addObject(Constants.GBL_ERRORS, aErrs);
            mav.addObject(TargetInstanceCommand.MDL_INSTANCE, ti);
            mav.setViewName(Constants.VIEW_HARVEST_NOW);
            
            return mav;
        }
        
        ModelAndView mav = new ModelAndView("redirect:/" + Constants.CNTRL_TI_QUEUE);
        // Get the TargetInstance and the HarvestAgentStatusDTO
        HarvestAgentStatusDTO has = (HarvestAgentStatusDTO) harvestCoordinator.getHarvestAgents().get(cmd.getAgent());
        
        //Is the queue paused?
        if(harvestCoordinator.isQueuePaused())
        {
			// Display a global message and return to queue
			mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("target.instance.queue.paused", new Object[] { ti.getOid() }, Locale.getDefault()));
        }
        else if(!has.isAcceptTasks()) {
			// Display a global message and return to queue
			mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("target.instance.agent.paused", new Object[] { ti.getOid(), has.getName() }, Locale.getDefault()));
            mav.addObject(Constants.GBL_CMD_DATA, aCmd);                
            mav.addObject(TargetInstanceCommand.MDL_AGENTS, allowedAgents);            
        	mav.addObject(Constants.GBL_ERRORS, aErrs);
            mav.addObject(TargetInstanceCommand.MDL_INSTANCE, ti);
            mav.setViewName(Constants.VIEW_HARVEST_NOW);
        	
        }
        else if(has.getMemoryWarning())
        {
			// Display a global message and return to queue
			mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("target.instance.agent.notaccept", new Object[] { ti.getOid(), has.getName() }, Locale.getDefault()));
        }
        else
        {
	        try {
				harvestCoordinator.harvest(ti, has);
			} 
	        catch (HibernateOptimisticLockingFailureException e) {
				ti = targetInstanceManager.getTargetInstance(ti.getOid());
				if (ti.getState().equals(TargetInstance.STATE_RUNNING)
					|| ti.getState().equals(TargetInstance.STATE_STOPPING)) {
					// Display a global message and return to queue
					mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("target.instance.run.by.other", new Object[] { ti.getOid() }, Locale.getDefault()));
				}
			}
        }
        return mav;
    }

    /**
     * @param harvestCoordinator The harvestCoordinator to set.
     */
    public void setHarvestCoordinator(HarvestCoordinator harvestCoordinator) {
        this.harvestCoordinator = harvestCoordinator;
    }

    /**
     * @param targetInstanceManager The targetInstanceManager to set.
     */
    public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
        this.targetInstanceManager = targetInstanceManager;
    }

	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
