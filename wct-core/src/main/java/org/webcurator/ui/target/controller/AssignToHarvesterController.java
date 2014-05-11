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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.TargetInstanceCommand;

public class AssignToHarvesterController extends AbstractCommandController {
	
	/** The target instance manager */
	private TargetInstanceManager targetInstanceManager;
	/* The Harvest Coordinator */
	private HarvestCoordinator harvestCoordinator;
	
	/**
	 * Create the controller object and set the command class.
	 */
	public AssignToHarvesterController() {
		setCommandClass(TargetInstanceCommand.class);
	}
	
	
	@Override
	protected ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) throws Exception {
		TargetInstanceCommand command = (TargetInstanceCommand) comm;
		
        HashMap<String, HarvestAgentStatusDTO> agents = harvestCoordinator.getHarvestAgents();
        TargetInstance ti = targetInstanceManager.getTargetInstance(command.getTargetInstanceId());            
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
        
        
        
        ModelAndView mav = new ModelAndView();
        mav.addObject(TargetInstanceCommand.SESSION_TI, ti);
        mav.addObject(TargetInstanceCommand.MDL_INSTANCE, ti);
        mav.addObject(Constants.GBL_CMD_DATA, command);
        mav.addObject(TargetInstanceCommand.MDL_AGENTS, allowedAgents);
        mav.setViewName(Constants.VIEW_HARVEST_NOW);
        
        return mav;		
	}


	public HarvestCoordinator getHarvestCoordinator() {
		return harvestCoordinator;
	}


	public void setHarvestCoordinator(HarvestCoordinator harvestCoordinator) {
		this.harvestCoordinator = harvestCoordinator;
	}


	public TargetInstanceManager getTargetInstanceManager() {
		return targetInstanceManager;
	}


	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}
	
	

}
