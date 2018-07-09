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
import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabHandler;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/** 
 * The handler for the target instance logs tab.
 * @author nwaight
 */
public class TargetInstanceLogsHandler extends TabHandler {

    private TargetInstanceManager targetInstanceManager;
    private HarvestCoordinator harvestCoordinator;    
    
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());        
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));
        binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));
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
			// ensure that the session target instance id is consistent (it may not be if we've been directed straight to a specific tab)
    		if (WebUtils.hasSubmitParameter(req, "targetInstanceOid")) {
    			String targetInstanceOid = req.getParameter("targetInstanceOid");
				Long targetInstanceId = Long.parseLong(targetInstanceOid);
				if (!((TargetInstance)req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI)).getOid().equals(targetInstanceId)){
					ti = targetInstanceManager.getTargetInstance(targetInstanceId, true);
					req.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
				}
    		}
    		ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI); 
    		editMode = (Boolean) req.getSession().getAttribute(TargetInstanceCommand.SESSION_MODE);
    	}  
                			
		TargetInstanceCommand populatedCommand = new TargetInstanceCommand(ti);        		
		if (editMode) {
			populatedCommand.setCmd(TargetInstanceCommand.ACTION_EDIT);
		}
		
		LogFilePropertiesDTO[] arrLogs = harvestCoordinator.listLogFileAttributes(ti);
		
		List<LogFilePropertiesDTO> logs = new ArrayList<LogFilePropertiesDTO>();
		for(int i = 0; i < arrLogs.length; i++)
		{
			logs.add(arrLogs[i]);
		}
		
		tmav.addObject(TargetInstanceCommand.MDL_LOG_LIST, logs);
		tmav.addObject(Constants.GBL_CMD_DATA, populatedCommand);		
		tmav.addObject(TargetInstanceCommand.MDL_INSTANCE, ti);
		                                       
        return tmav;        
    }

    public ModelAndView processOther(TabbedController tc, Tab currentTab,
            HttpServletRequest req, HttpServletResponse res, Object comm,
            BindException errors) {
        
    	TargetInstanceCommand cmd = (TargetInstanceCommand) comm;
        
        throw new WCTRuntimeException("Unknown command " + cmd.getCmd() + " received.");
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
