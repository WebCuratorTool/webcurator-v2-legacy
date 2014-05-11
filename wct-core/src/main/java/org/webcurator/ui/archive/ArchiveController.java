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
package org.webcurator.ui.archive;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.webcurator.core.archive.ArchiveAdapter;
import org.webcurator.core.archive.SipBuilder;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.TargetInstanceCommand;

/**
 * The Controller for managing the archiving of target instances.
 * @author aparker
 */
public class ArchiveController extends AbstractCommandController {
	/** The archive adapter to use to archive the target instance. */ 
	private ArchiveAdapter archiveAdapter = null;
	/** the target instance mamager used to access the target instance. */
	private TargetInstanceManager targetInstanceManager = null;
	/** The SIP Builder (for backwards compatibility */
	private SipBuilder sipBuilder = null;
	/** the url for the web curator. */
	private String webCuratorUrl = null;
	/** the version number of the heritrix harvester. */
	private String heritrixVersion = null;
	/** the target manager to use to get Target data. */
	private TargetManager targetManager = null;
	

	/** Default Constructor. */
	public ArchiveController() {
		setCommandClass(ArchiveCommand.class);
	}
		
	protected String buildSip(HttpServletRequest request, HttpServletResponse response, int harvestNumber) throws ServletException, IOException {
		TargetInstance instance = targetInstanceManager.getTargetInstance(Long.parseLong(request.getParameter("instanceID")));
		AbstractTarget target = (AbstractTarget)instance.getTarget();
		
		//Beware of lazy loaded AbstractTargets - check and reload if necessary
		if(target.getObjectType() == AbstractTarget.TYPE_TARGET &&
			!(target instanceof Target))
		{
			target = targetManager.load(target.getOid());
		}
		else if(target.getObjectType() == AbstractTarget.TYPE_GROUP &&
			!(target instanceof TargetGroup))
		{
			target = targetManager.loadGroup(target.getOid());
		}
				
		request.setAttribute("now",new Date());
		request.setAttribute("instance",instance); 
		request.setAttribute("target",target); 
		request.setAttribute("instanceAnnotations",targetInstanceManager.getAnnotations(instance));
		request.setAttribute("targetAnnotations",targetManager.getAnnotations(target));
		request.setAttribute("result",instance.getHarvestResults().get(harvestNumber-1));
		request.setAttribute("webCuratorUrl",webCuratorUrl);
		request.setAttribute("heritrixVersion",heritrixVersion);
		request.setAttribute("user",org.webcurator.core.util.AuthUtil.getRemoteUserObject());		
		request.setAttribute("groups", targetManager.getActiveParentGroups(instance));
		
		// Get the Sip Sections.
		Map<String,String> sipSections = instance.getSipParts();
		
		// If the SIP sections is null/empty, then we must have harvested this
		// before v1.2. In this case, generate the sections now.
		if(sipSections == null || sipSections.size() == 0) {
			sipSections = sipBuilder.buildSipSections(instance);
		}
		
		//We may need to update the target reference number
		if(sipBuilder.updateTargetReference(target, sipSections))
		{
			//We changed the sip parts so save the changes
			instance.setSipParts(sipSections);
			targetInstanceManager.save(instance);
		}
		
		request.setAttribute("sipSections", sipSections);
		
		HarvestResult curResult = instance.getHarvestResults().get(harvestNumber-1);
		LinkedList<HarvestResult> resultChain = new LinkedList<HarvestResult>();
		resultChain.add(curResult);
		while(curResult.getDerivedFrom() != null) {
			curResult = instance.getHarvestResults().get(curResult.getDerivedFrom() -1);
			resultChain.add(curResult);
		}
		
		request.setAttribute("resultChain", resultChain);
		
		RequestDispatcher rd = request.getRequestDispatcher("/jsp/createSIPXML.jsp");
		rd.include(request,response);	
		
		return (String)request.getAttribute("xmlData");
	}
	
	protected Map getCustomDepositFormElementsAsMap(HttpServletRequest request) {
		if (Boolean.parseBoolean(request.getParameter("customDepositForm_customFormPopulated")) == false) {
			return null;
		}
		Enumeration parameterNames = request.getParameterNames();
		if (parameterNames == null) return null;
		Map customDepositFormElements = new HashMap();
		while (parameterNames.hasMoreElements()) {
			String parameterName = (String)parameterNames.nextElement();
			if (parameterName.startsWith("customDepositForm_")) {
				String[] values = request.getParameterValues(parameterName);
				if (values == null || values.length <=0) continue;
				customDepositFormElements.put(parameterName, values[0]);
			}
		}
		return customDepositFormElements;
	}
	
	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object comm, BindException errors) throws Exception {
		
		// fetch the list of target instance ids to archive from the request (includes multi-select)
		String[] targetInstanceOids = request.getParameterValues("instanceID");
		
		for (String targetInstanceOid : targetInstanceOids) {
			TargetInstance instance = targetInstanceManager.getTargetInstance(Long.parseLong(targetInstanceOid));
			String harvestNo = request.getParameter("harvestNumber");
			
			// ensure harvestNumber request parameter is valid (it will be invalid in the case of multi-select)
			int harvestNumber;
			if (harvestNo != null) {
				harvestNumber = Integer.parseInt(harvestNo);
			} else	{
				harvestNumber = 0;
			}
			
			// for a zeroed harvestNumber, recover the correct harvestNumber
			if (harvestNumber == 0) {
				// We have come from the 'quick archive' link on the TI list view, rather than a link
				// on the harvest results tab, so we can only proceed if there is just
				// one harvest result in the endorsed state. If there are no HRs in the
				// endorsed state (somehow?) or if there are more than one then we'll return
				// to the TI list view displaying an appropriate message.
		        List<HarvestResult> results = targetInstanceManager.getHarvestResults(instance.getOid());
		        harvestNumber = extractEndorsedHarvestNumber(results);
		        if ( harvestNumber == 0 ) {
		    		ModelAndView mav = new ModelAndView("redirect:/" + Constants.CNTRL_TI_QUEUE + "?" + TargetInstanceCommand.REQ_SHOW_SUBMITTED_MSG + "=n");
		    		return mav;		
		        }
			}
			
			String xmlData = buildSip(request, response, harvestNumber);
	
			Map customDepositFormElements = getCustomDepositFormElementsAsMap(request);
	
			try{
				// denote this target instance as the reference crawl if the option is set on the target
				Target target = targetManager.load(instance.getTarget().getOid());
		        if (target.isAutoDenoteReferenceCrawl()) {
		        	target.setReferenceCrawlOid(instance.getOid());
		        	targetManager.save(target);
		        }
		        // submit the target instance to the archive
				archiveAdapter.submitToArchive(instance, xmlData, customDepositFormElements, harvestNumber);
			}
			catch(Exception e){			
				e.printStackTrace();
				errors.reject("archive.failure", new Object[]{e.getMessage()}, "Failed to submit to archive");		
				ArchiveCommand command = (ArchiveCommand) comm;		
				ModelAndView mav = new ModelAndView("submit-to-archive2");
				mav.addObject("instance", instance);
				mav.addObject(Constants.GBL_CMD_DATA, command);
				mav.addObject("hasErrors", errors.hasErrors());
				mav.addObject(Constants.GBL_ERRORS, errors);
				return mav;		
			}
		
		}
		ModelAndView mav = new ModelAndView("redirect:/" + Constants.CNTRL_TI_QUEUE + "?" + TargetInstanceCommand.REQ_SHOW_SUBMITTED_MSG + "=y");
		return mav;		
	}
	
	private int extractEndorsedHarvestNumber(List<HarvestResult> results) {
		int count = 0;
		int returnVal = 0;
		for (Iterator<HarvestResult> it = results.iterator(); it.hasNext(); ) {
			HarvestResult hr = (HarvestResult)it.next();
			if(hr.getState() == HarvestResult.STATE_ENDORSED)
			{
				count = count + 1;
				returnVal = hr.getHarvestNumber();
			}
		}

		if (count == 1) {
			return returnVal;
		} else {
			return 0;
		}
	}

	/**  
	 * @param targetInstanceManager the target instance manager. 
	 */
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}

	/**  
	 * @param archiveAdapter the archive adaptor.
	 */
	public void setArchiveAdapter(ArchiveAdapter archiveAdapter) {
		this.archiveAdapter = archiveAdapter;
	}

	/** 
	 * @param targetManager The target manager.
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}

	/**  
	 * @param heritrixVersion The version of the harvester being used.
	 */
	public void setHeritrixVersion(String heritrixVersion) {
		this.heritrixVersion = heritrixVersion;
	}

	/**  
	 * @param webCuratorUrl The url of the web curator tool.
	 */
	public void setWebCuratorUrl(String webCuratorUrl) {
		this.webCuratorUrl = webCuratorUrl;
	}

	/**
	 * @param sipBuilder the sipBuilder to set
	 */
	public void setSipBuilder(SipBuilder sipBuilder) {
		this.sipBuilder = sipBuilder;
	}
	
}
