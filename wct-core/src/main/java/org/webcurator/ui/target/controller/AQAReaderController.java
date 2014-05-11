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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.util.XMLConverter;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.LogReaderCommand;
import org.webcurator.ui.target.validator.LogReaderValidator;
import org.webcurator.ui.tools.controller.TreeToolController.AQAElement;
import org.xml.sax.SAXException;

/**
 * The controller for handling the log viewer commands.
 * @author nwaight
 */
public class AQAReaderController extends AbstractCommandController {

	HarvestCoordinator harvestCoordinator;
	
	TargetInstanceManager targetInstanceManager;
	
	public class AQAElement
	{
		private String url = "";
		private String contentFile = "";
		private String contentType = "";
		private long contentLength = 0L;
		
		
		private AQAElement(String url, String contentFile, String contentType, long contentLength)
		{
			this.url = url;
			this.contentFile = contentFile;
			this.contentType = contentType;
			this.contentLength = contentLength;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getContentFile() {
			return contentFile;
		}

		public String getContentType() {
			return contentType;
		}

		public long getContentLength() {
			return contentLength;
		}
	}
	
	public AQAReaderController() {
		setCommandClass(LogReaderCommand.class);
		setValidator(new LogReaderValidator());
		
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractCommandController#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView handle(HttpServletRequest aReq, HttpServletResponse aResp, Object aCommand, BindException aErrors) throws Exception {
		LogReaderCommand cmd = (LogReaderCommand) aCommand;
		String messageText = "";
		int firstLine = 0;
		List<AQAElement> missingElements = new ArrayList<AQAElement>();
		
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
			
			File f = harvestCoordinator.getLogfile(ti, cmd.getLogFileName());
			Document aqaResult = readXMLDocument(f);
			NodeList missingElementsNodes = aqaResult.getElementsByTagName("missingElements");
			if(missingElementsNodes.getLength() > 0)
			{
				NodeList missingElementNodes = ((Element)missingElementsNodes.item(0)).getElementsByTagName("element");
				for(int i = 0; i < missingElementNodes.getLength(); i++)
				{
					Element elementNode = (Element)missingElementNodes.item(i);
					if (elementNode.getAttribute("statuscode").equals("200")) {
						missingElements.add(new AQAElement(elementNode.getAttribute("url"),
														   elementNode.getAttribute("contentfile"),
														   elementNode.getAttribute("contentType"),
														   Long.parseLong(elementNode.getAttribute("contentLength"))));
					}

				}
			}
		}
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName(Constants.VIEW_AQA_READER);
		mav.addObject(Constants.GBL_CMD_DATA, cmd);
		mav.addObject(Constants.MESSAGE_TEXT, messageText);
		mav.addObject(LogReaderCommand.MDL_MISSINGELEMENTS, missingElements);
		
		return mav;
	}

	private Document readXMLDocument(File f) throws SAXException, IOException, ParserConfigurationException
	{
		StringBuffer sb = new StringBuffer();
        BufferedReader in = new BufferedReader(new FileReader(f));
		
        try
        {
	        String str;
	        while ((str = in.readLine()) != null) 
	        {
	               sb.append(str);
	        }
        }
        finally
        {
        	in.close();
        }
		
		return XMLConverter.StringToDocument(sb.toString());
	}
	
	/**
	 * @param harvestCoordinator the harvestCoordinator to set
	 */
	public void setHarvestCoordinator(HarvestCoordinator harvestCoordinator) {
		this.harvestCoordinator = harvestCoordinator;
	}

	/**
	 * @param targetInstanceManager the targetInstanceManager to set
	 */
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}
	
}
