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
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.notification.InTrayManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.sites.SiteManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.common.Constants;

/**
 * The home controller is responsible for rendering the home page. 
 */
public class HomeController extends AbstractController {
	public static final String MDL_CNT_NOTIFICATION = "notificationsCount";
	public static final String MDL_CNT_TASK = "tasksCount";
	public static final String MDL_CNT_SITE = "sitesCount";
	public static final String MDL_CNT_TARGET = "targetsCount";
	public static final String MDL_CNT_GROUPS = "targetGroupsCount";
	public static final String MDL_CNT_SCHEDULED = "scheduledCount";
	public static final String MDL_CNT_QR = "qualityReviewCount";
	
	/** The manager for getting task and notification counts. */
	InTrayManager inTrayManager;
	/** the manager for accessing privileges for a user. */
	AuthorityManager authorityManager;
	/** The manager for getting the site count. */
	SiteManager siteManager;
	/** The manager for getting a count of targets. */
	TargetManager targetManager;
	/** the manager for getting a count of target instances. */
	TargetInstanceManager targetInstanceManager;
	/** enables the new Qa Home page **/
	private boolean enableQaModule = false;
	
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ModelAndView mav = new ModelAndView();
        
        User user = AuthUtil.getRemoteUserObject();
        
        int notificationCount = inTrayManager.countNotifications(user);
        int taskCount = inTrayManager.countTasks(user);        
        int siteCount = siteManager.countSites();
        int targetCount = targetManager.countTargets(user);
        int groupCount = targetManager.countTargetGroups(user);
        
        ArrayList<String> states = new ArrayList<String>();
        states.add(TargetInstance.STATE_SCHEDULED);
        int schedCount = targetInstanceManager.countTargetInstances(user, states);
        
        states.clear();
        states.add(TargetInstance.STATE_HARVESTED);
        int qaCount = targetInstanceManager.countTargetInstances(user, states);

        mav.addObject(MDL_CNT_NOTIFICATION, new Integer(notificationCount));
        mav.addObject(MDL_CNT_TASK, new Integer(taskCount));
        mav.addObject(MDL_CNT_SITE, new Integer(siteCount));
        mav.addObject(MDL_CNT_TARGET, new Integer(targetCount));
        mav.addObject(MDL_CNT_GROUPS, new Integer(groupCount));
        mav.addObject(MDL_CNT_SCHEDULED, new Integer(schedCount));
        mav.addObject(MDL_CNT_QR, new Integer(qaCount));
        if (!enableQaModule) {
        	mav.setViewName(Constants.VIEW_HOME);
        } else {
        	mav.setViewName(Constants.VIEW_QA_HOME);
        }
        
        return mav;
    }

	/**
	 * @param inTrayManager the inTrayManager to set
	 */
	public void setInTrayManager(InTrayManager inTrayManager) {
		this.inTrayManager = inTrayManager;
	}

	/**
	 * @param authorityManager the authorityManager to set
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}

	/**
	 * @param siteManager the siteManager to set
	 */
	public void setSiteManager(SiteManager siteManager) {
		this.siteManager = siteManager;
	}

	/**
	 * @param targetManager the targetManager to set
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}

	/**
	 * @param targetInstanceManager the targetInstanceManager to set
	 */
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}
	
	/**
	 * Enable/disable the new QA Module (disabled by default)
	 * @param enableQaModule Enables the QA module.
	 */
	public void setEnableQaModule(Boolean enableQaModule) {
		this.enableQaModule = enableQaModule;
	}
}
