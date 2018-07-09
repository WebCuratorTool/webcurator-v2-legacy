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

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.domain.model.core.HarvesterStatus;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabHandler;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.Utils;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the target instance states tab.
 * @author nwaight
 */
public class TargetInstanceStateHandler extends TabHandler {

    private TargetInstanceManager targetInstanceManager;
    private HarvestCoordinator harvestCoordinator;
    
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));
        binder.registerCustomEditor(java.util.Date.class, DateUtils.get().getFullDateTimeEditor(true));
    }
    
    public void processTab(TabbedController tc, Tab currentTab,
            HttpServletRequest req, HttpServletResponse res, Object comm,
            BindException errors) {
        // process the submit of the tab called on change tab or save
    }

    public TabbedModelAndView preProcessNextTab(TabbedController tc,
            Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
            Object comm, BindException errors) {
        // build mav stuff b4 displaying the tab
        TabbedModelAndView tmav = tc.new TabbedModelAndView();
        Boolean editMode = false;
        TargetInstanceCommand cmd = null;
        TargetInstance ti = null;
        if (comm instanceof TargetInstanceCommand) {
        	cmd = (TargetInstanceCommand) comm;
        	if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_EDIT)) {
        		editMode = true;
        	}
        }
        
        if (req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI) == null) {
    		ti = targetInstanceManager.getTargetInstance(cmd.getTargetInstanceId(), true);
    		req.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
    		req.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, editMode);
    	}
    	else {
    		ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI); 
    		editMode = (Boolean) req.getSession().getAttribute(TargetInstanceCommand.SESSION_MODE);
    	}  
                                    
        HashMap agents = harvestCoordinator.getHarvestAgents();                    

        HarvesterStatusDTO harvester = null;
        HarvestAgentStatusDTO agent = null;
        Iterator it = agents.values().iterator();
        while (it.hasNext()) {
            agent = (HarvestAgentStatusDTO) it.next();
            if (agent.getHarvesterStatus().containsKey(ti.getJobName())) {
                harvester = (HarvesterStatusDTO) agent.getHarvesterStatus().get(ti.getJobName());
            }
        }
        
        if (harvester == null) {
        	HarvesterStatus status = ti.getStatus(); 
        	if (status != null) {
        		harvester = status.getAsDTO();
        	}
        }
               
        // tmav.addObject(Constants.GBL_CMD_DATA, cmd);  
        tmav.addObject(TargetInstanceCommand.MDL_INSTANCE, ti);
        tmav.addObject(TargetInstanceCommand.MDL_STATUS, harvester);
                
        return tmav;        
    }

    public ModelAndView processOther(TabbedController tc, Tab currentTab,
            HttpServletRequest req, HttpServletResponse res, Object comm,
            BindException errors) {
        TargetInstanceCommand cmd = (TargetInstanceCommand) comm;
        if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_HARVEST)) {
            ModelAndView mav = new ModelAndView();            
            HashMap agents = harvestCoordinator.getHarvestAgents();
            mav.addObject(Constants.GBL_CMD_DATA, cmd);
            mav.addObject(TargetInstanceCommand.MDL_AGENTS, agents);
            mav.setViewName(Constants.VIEW_HARVEST_NOW);
            
            return mav;
        }
        else {
            throw new WCTRuntimeException("Unknown command " + cmd.getCmd() + " recieved.");
        }
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
}
