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
import org.webcurator.ui.target.command.LogReaderCommand;
import org.webcurator.ui.target.validator.LogReaderValidator;

/**
 * The controller for handling the log viewer commands.
 * 
 * @author nwaight
 */
public class ContentReaderController extends AbstractCommandController {

	HarvestLogManager harvestLogManager;

	TargetInstanceManager targetInstanceManager;

	public ContentReaderController() {
		setCommandClass(LogReaderCommand.class);
		setValidator(new LogReaderValidator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.mvc.AbstractCommandController#handle(
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object,
	 * org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView handle(HttpServletRequest aReq, HttpServletResponse aResp, Object aCommand, BindException aErrors)
			throws Exception {
		LogReaderCommand cmd = (LogReaderCommand) aCommand;

		TargetInstance ti = targetInstanceManager.getTargetInstance(cmd.getTargetInstanceOid());
		File f = null;

		try {
			f = harvestLogManager.getLogfile(ti, cmd.getLogFileName());
		} catch (WCTRuntimeException e) {

		}

		ContentView v = new ContentView(f, cmd.getLogFileName(), true);
		return new ModelAndView(v);
	}

	/**
	 * @param harvestLogManager
	 *            the harvestLogManager to set
	 */
	public void setHarvestLogManager(HarvestLogManager harvestLogManager) {
		this.harvestLogManager = harvestLogManager;
	}

	/**
	 * @param targetInstanceManager
	 *            the targetInstanceManager to set
	 */
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}
}
