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
package org.webcurator.ui.tools.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.HarvestHistoryDTO;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.tools.command.HarvestHistoryCommand;

/**
 * Controller for the HarvestHistory QR tool.
 * @author beaumontb
 */
public class HarvestHistoryController  extends AbstractCommandController {

	private TargetInstanceManager targetInstanceManager;
	
	
	public HarvestHistoryController() {
		setCommandClass(HarvestHistoryCommand.class);
	}
	
	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		HarvestHistoryCommand cmd = (HarvestHistoryCommand) command;
		TargetInstance ti = targetInstanceManager.getTargetInstance(cmd.getTargetInstanceOid());
		List<HarvestHistoryDTO> history = targetInstanceManager.getHarvestHistory(ti.getTarget().getOid());
		
		//Set the session target instance because it is overwritten by the TargetInstanceController
		//upon viewing any history item.  The URL query string parameters are ignored.
		request.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
		ModelAndView mav = new ModelAndView("harvest-history");
		mav.addObject("history", history);
		mav.addObject("ti_oid", cmd.getTargetInstanceOid());
		mav.addObject("harvestResultId", cmd.getHarvestResultId());
		
		return mav;
	}

	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}

}
