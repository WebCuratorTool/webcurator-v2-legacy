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
package org.webcurator.ui.management.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.management.command.ManagementCommand;

/**
 * Controller to render the management "menu" tab. 
 * @author bprice
 */
public class ManagementController extends AbstractFormController {

	/** enables the Management page (QA version) when true **/
	private boolean enableQaModule = false;
	
    public ManagementController() {
        super();
        setCommandClass(ManagementCommand.class);
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest aReq, HttpServletResponse aRes, BindException aError) throws Exception {
        ModelAndView mav = new ModelAndView();
        
        if (!enableQaModule) {
        	mav.setViewName(Constants.VIEW_MANAGEMENT);
        } else {
        	mav.setViewName(Constants.VIEW_QA_MANAGEMENT);
        }
        return mav;
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq, HttpServletResponse aRes, Object aCmd, BindException aError) throws Exception {
        // TODO Implement this if we require a POST version of the management screen
        return null;
    }
    
	/**
	 * Enable/disable the new QA Module (disabled by default)
	 * @param enableQaModule Enables the QA module.
	 */
	public void setEnableQaModule(Boolean enableQaModule) {
		this.enableQaModule = enableQaModule;
	}

}
