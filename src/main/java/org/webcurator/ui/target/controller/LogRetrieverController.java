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

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.harvester.coordinator.HarvestLogManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.target.command.LogRetrieverCommand;

/**
 * Controller for retrieving a log file from the WCT core.
 * @author beaumontb
 *
 */
public class LogRetrieverController extends AbstractCommandController {

	private HarvestLogManager harvestLogManager = null;
	private TargetInstanceManager targetInstanceManager = null;
	
	public LogRetrieverController() {
		setCommandClass(LogRetrieverCommand.class);
	}
	
	@Override
	protected ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors) throws Exception {
		LogRetrieverCommand cmd = (LogRetrieverCommand) comm;
		
		TargetInstance ti = targetInstanceManager.getTargetInstance(cmd.getTargetInstanceOid());
		
		File f = null;
		
		try
		{
			f = harvestLogManager.getLogfile(ti, cmd.getLogFileName());
		}
		catch(WCTRuntimeException e)
		{
			
		}
		
		AttachmentView v = new AttachmentView(cmd.getLogFileName(), f, true);
		return new ModelAndView(v);
	}

	/**
	 * @param harvestLogManager the harvestLogManager to set
	 */
	public void setHarvestLogManager(HarvestLogManager harvestLogManager) {
		this.harvestLogManager = harvestLogManager;
	}

	/**
	 * @param targetInstanceManager the targetInstanceManager to set
	 */
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}
	

}
