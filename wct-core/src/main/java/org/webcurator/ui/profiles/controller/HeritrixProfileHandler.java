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
package org.webcurator.ui.profiles.controller;

import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archive.crawler.settings.SimpleType;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.core.profiles.ComplexProfileElement;
import org.webcurator.core.profiles.DuplicateNameException;
import org.webcurator.core.profiles.HeritrixProfile;
import org.webcurator.core.profiles.ProfileElement;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.profiles.renderers.AcceptAllRendererFilter;
import org.webcurator.ui.profiles.renderers.RendererFilter;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabHandler;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * Handle the submission of one of the profie editing tabs. This handles
 * all of the Heritrix specific logic.
 * @author bbeaumont
 *
 */
public class HeritrixProfileHandler extends TabHandler {
	
	/** The base attribute to start producing the tree from */
	private String baseAttribute;
	
	/** The renderer filter to use to determine which point to start */
	private RendererFilter recursionFilter;
	
	/**
	 * Default constructor to choose a recurse all filter. This is the
	 * general filter to be used for all but the base configuration
	 * tab.
	 */
	public HeritrixProfileHandler() {
		recursionFilter = new AcceptAllRendererFilter();
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		
		try {
	        // Go through all the simple parameter types.
			HeritrixProfile profile = (HeritrixProfile) req.getSession().getAttribute("heritrixProfile");
	        ProfileElement pe = profile.getElement(baseAttribute);
	        
	        profile.setAllSimpleTypes(req, (ComplexProfileElement) pe);
		}
		catch(AttributeNotFoundException ex) {
			// Thrown by the profile.getElement(...) call. This signifies that
			// the handler has been configured incorrectly.
			throw new IllegalArgumentException("HeritrixProfileHandler specifies baseAttribute of '" + baseAttribute + "' but this is illegal");
		}
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#preProcessNextTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {
		
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject("heritrixProfile", req.getSession().getAttribute("heritrixProfile"));
		tmav.addObject("baseAttribute", baseAttribute);
		tmav.addObject("recursionFilter", recursionFilter);
		return tmav;
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processOther(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest request, HttpServletResponse response, Object comm,
			BindException errors)  {
		
		HeritrixProfile profile = (HeritrixProfile) request.getSession().getAttribute("heritrixProfile");

		if( "map".equals(request.getParameter("action"))) {
	        if("up".equals(request.getParameter("subaction"))) {
	            profile.moveMapElementUp(request.getParameter("mapName"), request.getParameter("elementToMove"));
	        }
	        else if("down".equals(request.getParameter("subaction"))) {
	            profile.moveMapElementDown(request.getParameter("mapName"), request.getParameter("elementToMove"));
	        }
	        else if("remove".equals(request.getParameter("subaction"))) {
	            profile.removeMapElement(request.getParameter("mapName"), request.getParameter("elementToMove"));
	        }
	        
	    }

		if("simpleMap-add".equals(request.getParameter("action"))) {
			String elementName = request.getParameter("mapName");
			String key = request.getParameter(elementName + ".key");
			String value = request.getParameter(elementName + ".value");	
			
			if(key.startsWith("_")) {
				errors.reject("profile.error.illegalname", new Object[] { key }, key + " is an illegal name");
			}
			else {
				try {
					SimpleType child = new SimpleType(key, "", value);
					profile.addMapElement(elementName, child);
				} 
				catch (InvalidAttributeValueException e) {
					errors.reject("profile.error.illegalvalue", new Object[] { elementName }, "Illegal value for map " + elementName);
					e.printStackTrace();
				} 
				catch (DuplicateNameException ex) {
					ex.printStackTrace();				
					errors.reject("profile.error.duplicate_map_element", new Object[] { ex.getDupeName() }, "Attempt to add element to map with a duplicate name");
				}
			}
		}
		
	    if("addMapElement".equals(request.getParameter("action"))) {
			String elementName = request.getParameter("mapName");
			String key = request.getParameter("newElemName");
			String type = request.getParameter("newElemType");	
			
	    	if(key.startsWith("_")) {
				errors.reject("profile.error.illegalname", new Object[] { key }, key + " is an illegal name");
			}
			else {
		    	try {
			        profile.addMapElement(elementName, key, type);
		    	}
		    	catch(DuplicateNameException ex) {
		    		
		    		errors.reject("profile.error.duplicate_map_element", new Object[] { ex.getDupeName() }, "Attempt to add element to map with a duplicate name");
		    		//errors.addError(new ObjectError("profile", ));
		    	}
			}
	    }  
	    
	    if("changeScope".equals(request.getParameter("action"))) {
	        profile.setScopeClass(request.getParameter("scopeClass"));
	    }		
		
		// Go back to the same page.
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.getTabStatus().setCurrentTab(currentTab);
		tmav.addObject("heritrixProfile", request.getSession().getAttribute("heritrixProfile"));
		tmav.addObject("baseAttribute", baseAttribute);
		tmav.addObject("recursionFilter", recursionFilter);
		
		if(errors.hasErrors()) {
			tmav.addObject(Constants.GBL_ERRORS, errors);
		}
		
		return tmav;		
		
	}

	/**
	 * @return Returns the baseAttribute.
	 */
	public String getBaseAttribute() {
		return baseAttribute;
	}

	/**
	 * @param baseAttribute The baseAttribute to set.
	 */
	public void setBaseAttribute(String baseAttribute) {
		this.baseAttribute = baseAttribute;
	}

	/**
	 * @return Returns the recursionFilter.
	 */
	public RendererFilter getRecursionFilter() {
		return recursionFilter;
	}

	/**
	 * @param recursionFilter The recursionFilter to set.
	 */
	public void setRecursionFilter(RendererFilter recursionFilter) {
		this.recursionFilter = recursionFilter;
	}

}
