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

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.core.harvester.coordinator.HarvestLogManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.ShowHopPathCommand;
import org.webcurator.ui.target.validator.ShowHopPathValidator;

/**
 * The controller for handling the hop path viewer commands.
 * @author skillarney
 */
public class ShowHopPathController extends AbstractCommandController {

	HarvestLogManager harvestLogManager;
	
	TargetInstanceManager targetInstanceManager;
	
	public ShowHopPathController() {
		
		setCommandClass(ShowHopPathCommand.class);
		setValidator(new ShowHopPathValidator());
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractCommandController#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView handle(HttpServletRequest aReq, HttpServletResponse aResp, Object aCommand, BindException aErrors) throws Exception {
		
		ShowHopPathCommand cmd = (ShowHopPathCommand) aCommand;
		String messageText = "";
		int firstLine = 0;
		String[] lines = {"", ""};		
		
		if(aErrors.hasErrors())
		{
			Iterator it = aErrors.getAllErrors().iterator();
			while(it.hasNext())
			{
				org.springframework.validation.ObjectError err = (org.springframework.validation.ObjectError)it.next();
				if(messageText.length()>0) messageText += "; ";
				messageText += err.getDefaultMessage();
			}
		}
		else if(cmd.getTargetInstanceOid() != null)
		{
			TargetInstance ti = targetInstanceManager.getTargetInstance(cmd.getTargetInstanceOid());
			
			cmd.setTargetName(ti.getTarget().getName());
			
			lines = harvestLogManager.getHopPath(ti, cmd.getLogFileName(), cmd.getUrl());
		}
		else
		{
			messageText = "Context has been lost. Please close the Log Viewer and re-open.";
			cmd = new ShowHopPathCommand();
		}
		
		if (lines.length == 0) {
			
		}
		ModelAndView mav = new ModelAndView();
		mav.setViewName(Constants.HOP_PATH__READER);
		mav.addObject(Constants.GBL_CMD_DATA, cmd);
		if (lines.length == 0) {
			String [] problem = { "Could not determine Hop Path for Url: " + cmd.getUrl() };
			mav.addObject(ShowHopPathCommand.MDL_LINES, parseLines(problem, cmd.getShowLineNumbers(), firstLine, cmd.getNumLines()));
		} else {
			mav.addObject(ShowHopPathCommand.MDL_LINES, parseLines(lines, cmd.getShowLineNumbers(), firstLine, cmd.getNumLines()));
		}
		mav.addObject(Constants.MESSAGE_TEXT, messageText);
		
		return mav;
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
	
	private String[] parseLines(String[] inLines, boolean showLineNumbers, int firstLine, int countLines)
	{
		String[] outLines = new String[inLines.length];
		int lineNumber = firstLine;
		for(int i = 0; i < inLines.length; i++)
		{
			if(i == 0 && showLineNumbers && lineNumber > -2)
			{
				String[] subLines = inLines[i].split("\n");
				if(lineNumber == -1)
				{
					//this is a tail
					lineNumber = 1+(countLines-subLines.length);
				}
				
				outLines[i] = addNumbers(inLines, lineNumber, showLineNumbers);
			}
			else
			{
				outLines[i] = inLines[i];
			}
		}
		
		return outLines;
	}
	
	
	private String addNumbers(String[] result, Integer firstLine, boolean showLineNumbers)
	{
		StringBuilder sb = new StringBuilder();
		if(result != null && result.length == 2)
		{
			String[] lineArray = result[0].split("\n");
			for(int i = 0; i < lineArray.length; i++)
			{
				if(i == lineArray.length-1 && lineArray[i].length()==0)
				{
					continue;
				}
				
				addLine(sb, firstLine++, lineArray[i], showLineNumbers);
			}
		}

		return sb.toString();
	}
	
	private void addLine(StringBuilder sb, Integer number, String body, boolean showLineNumbers)
	{
		if(showLineNumbers)
		{
			sb.append(number);
			sb.append(". ");
		}
		
		sb.append(body);
		sb.append("\n");
	}
	
}
