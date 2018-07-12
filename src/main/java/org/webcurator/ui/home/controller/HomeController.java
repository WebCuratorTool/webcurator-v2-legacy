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
package org.webcurator.ui.home.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.webcurator.ui.common.Constants;

/**
 * The home controller is responsible for rendering the home page. 
 */
public class HomeController extends AbstractController {

	/** enables the new Qa Home page **/
	private boolean enableQaModule = false;
	
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ModelAndView mav = new ModelAndView();

        if (!enableQaModule) {
        	mav.setViewName(Constants.VIEW_HOME);
        } else {
        	mav.setViewName(Constants.VIEW_QA_HOME);
        }
        
        return mav;
    }

    /**
	 * Enable/disable the new QA Module (disabled by default)
	 * @param enableQaModule Enables the QA module.
	 */
	public void setEnableQaModule(Boolean enableQaModule) {
		this.enableQaModule = enableQaModule;
	}
}
