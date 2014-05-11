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
package org.webcurator.ui.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * A <code>TabHandler</code> responds to actions for a specific tab in a 
 * tab set. 
 * 
 * @see org.webcurator.ui.util.TabbedController
 * 
 * @author bbeaumont
 *
 */
public abstract class TabHandler {
	/**
	 * Process the tab submission. This method should perform any validation
	 * that could not have been performed in the validator, check authorisation,
	 * and update the business objects. It should not save the state to the 
	 * database as this is done by the tabbed controller on a save action.
	 * 
	 * @param tc		 The TabbedController delegating the request.
	 * @param currentTab The currently selected tab.
	 * @param req	     The HttpServletRequest object.
	 * @param res		 The HttpServletResponse object.
	 * @param comm 		 The Spring command object.
	 * @param errors	 The Spring errors object.
	 */
	public abstract void processTab(TabbedController tc, Tab currentTab, HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors);
	
	/**
	 * Prepare the model for showing the tab. This method should create a new
	 * tabbed model and view using <code>tc.new TabbedModelAndView</code> and
	 * set the necessary model object.
	 * 
	 * @param tc		 The TabbedController delegating the request.
	 * @param nextTabID	 The next tab to be shown.
	 * @param req	     The HttpServletRequest object.
	 * @param res		 The HttpServletResponse object.
	 * @param comm 		 The Spring command object.
	 * @param errors	 The Spring errors object.
	 * @return The model and view.
	 */	
	public abstract TabbedModelAndView preProcessNextTab(TabbedController tc, Tab nextTabID, HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors);
	
	/**
	 * Process an action that the TabbedController cannot interpret. These actions
	 * are generally sub-actions on the tab.
	 * 
	 * @param tc		 The TabbedController delegating the request.
	 * @param currentTab The current tab.
	 * @param req	     The HttpServletRequest object.
	 * @param res		 The HttpServletResponse object.
	 * @param comm 		 The Spring command object.
	 * @param errors	 The Spring errors object.
	 * @return The model and view.
	 */		
	public abstract ModelAndView processOther(TabbedController tc, Tab currentTab, HttpServletRequest req, HttpServletResponse res, Object comm, BindException errors);
	
	/**
	 * Initialise the binder for this handler. It may be necessary for the 
	 * handler to initialise its own set of binders depending on the command
	 * class.
	 * 
	 * @param request The HttpServletRequest.
	 * @param binder  The binder.
	 * @throws Exception if there are errors.
	 */
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {        
    }
}
