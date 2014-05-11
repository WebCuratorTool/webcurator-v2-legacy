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
package org.webcurator.ui.intray.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.core.exceptions.NotOwnerRuntimeException;
import org.webcurator.core.notification.InTrayManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.core.util.CookieUtils;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Notification;
import org.webcurator.domain.model.core.Task;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.intray.command.InTrayCommand;

/**
 * The controller for managing the intray views.
 * @author bprice
 */
public class InTrayController extends AbstractFormController {
	/** the logger. */
    private Log log = null;
    /** the manager for manager tasks and notifications. */
    private InTrayManager inTrayManager = null;
    /** Default Constructor. */
    public InTrayController() {
        log = LogFactory.getLog(InTrayController.class);
        setCommandClass(InTrayCommand.class);
    }
    
    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq, HttpServletResponse aRes, Object aCmd, BindException aErrors) throws Exception {
        InTrayCommand intrayCmd = (InTrayCommand) aCmd;
        ModelAndView mav = null;
        
		// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(aReq);
		
		int taskPage = intrayCmd.getTaskPage();
		int notificationPage = intrayCmd.getNotificationPage();
		Boolean showTasks = intrayCmd.getShowTasks();
		if(intrayCmd.getSelectedPageSize()!=null) {
			if ( !intrayCmd.getSelectedPageSize().equals(currentPageSize) ) {
				// user has selected a new page size, so reset to first page..
				currentPageSize = intrayCmd.getSelectedPageSize();
				CookieUtils.setPageSize(aRes, currentPageSize);
				taskPage = 0;
				notificationPage = 0;
			}
		}
 		
		if (intrayCmd.getAction() != null) {
			int pageSize = Integer.parseInt(currentPageSize);
            if (InTrayCommand.ACTION_DELETE_NOTIFICATION.equals(intrayCmd.getAction())) {
                mav = deleteNotification(intrayCmd, pageSize, showTasks);
            } else if (InTrayCommand.ACTION_VIEW_NOTIFICATION.equals(intrayCmd.getAction())) {
                mav = viewNotification(intrayCmd, pageSize, showTasks);
            } else if (InTrayCommand.ACTION_DELETE_TASK.equals(intrayCmd.getAction())) {
                mav = deleteTask(intrayCmd, pageSize, aErrors, showTasks);
            } else if (InTrayCommand.ACTION_VIEW_TASK.equals(intrayCmd.getAction())) {
                mav = viewTask(intrayCmd, pageSize, showTasks);
            } else if (InTrayCommand.ACTION_CLAIM_TASK.equals(intrayCmd.getAction())) {
                mav = claimTask(intrayCmd, pageSize, showTasks);
            } else if (InTrayCommand.ACTION_UNCLAIM_TASK.equals(intrayCmd.getAction())) {
                mav = unclaimTask(intrayCmd, pageSize, showTasks);
            } else if (InTrayCommand.ACTION_NEXT.equals(intrayCmd.getAction()) || InTrayCommand.ACTION_PREVIOUS.equals(intrayCmd.getAction())) {
                mav = defaultView(taskPage, notificationPage, pageSize, showTasks);
            } else if (InTrayCommand.ACTION_DELETE_ALL_NOTIFICATIONS.equals(intrayCmd.getAction())) {
            	mav = deleteAllNotifications(intrayCmd, pageSize, showTasks);
            } else if (InTrayCommand.ACTION_DELETE_ALL_TASKS.equals(intrayCmd.getAction())) {
            	mav = deleteAllTasks(intrayCmd, pageSize, showTasks);
            }
        } else {
            //invalid action command so redirect to the view intray screen
            log.warn("A form was posted to the InTrayController without a valid action attribute, redirecting to the showForm flow.");
            return showForm(aReq,aRes,aErrors);
        }
        return mav;
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest aReq, HttpServletResponse aRes, BindException aErrors) throws Exception {

    	// get value of page size cookie
		String currentPageSize = CookieUtils.getPageSize(aReq);
        
		return defaultView(0, 0, Integer.parseInt(currentPageSize), null);
    }

    public void setInTrayManager(InTrayManager inTrayManager) {
        this.inTrayManager = inTrayManager;
    }
    
    private ModelAndView deleteNotification(InTrayCommand intrayCmd, int pageSize, Boolean showTasks) {
        Long notifyOid = intrayCmd.getNotificationOid();
        if (notifyOid != null) {
            inTrayManager.deleteNotification(notifyOid);
        } else {
            log.warn("A form was posted to the InTrayController without a valid notifyOid attribute, redirecting to the showForm flow.");
        }
        
		return defaultView(intrayCmd.getTaskPage(), intrayCmd.getNotificationPage(), pageSize, showTasks);
    }

    private ModelAndView viewNotification(InTrayCommand intrayCmd, int pageSize, Boolean showTasks) {
        Long notifyOid = intrayCmd.getNotificationOid();
        if (notifyOid != null) {
            Notification notify = inTrayManager.getNotification(notifyOid);
            ModelAndView mav = new ModelAndView();
            mav.addObject(InTrayCommand.MDL_NOTIFICATION, notify);
            mav.setViewName("intray-notification");
            
            return mav;
        } else {
            log.warn("A form was posted to the InTrayController without a valid notifyOid attribute, redirecting to the showForm flow.");
            return defaultView(0, 0, pageSize, showTasks);
        }
    }
    
    private ModelAndView deleteTask(InTrayCommand intrayCmd, int pageSize, BindException aErrors, Boolean showTasks) {
        Long taskOid = intrayCmd.getTaskOid();
        if (taskOid != null) {
            try {
				inTrayManager.deleteTask(taskOid);
			} 
            catch (NotOwnerRuntimeException e) {
            	aErrors.reject("task.error.delete.not.owner");
			}
        } else {
            log.warn("A form was posted to the InTrayController without a valid taskOid attribute, redirecting to the showForm flow.");
        }
        
        ModelAndView mav = defaultView(intrayCmd.getTaskPage(), intrayCmd.getNotificationPage(), pageSize, showTasks);
        if (aErrors.hasErrors()) {
        	mav.addObject(Constants.GBL_ERRORS, aErrors);
        }
        
        return mav;
    }

    private ModelAndView viewTask(InTrayCommand intrayCmd, int pageSize, Boolean showTasks) {
        Long taskOid = intrayCmd.getTaskOid();
        if (taskOid != null) {
            Task task = inTrayManager.getTask(taskOid);
            ModelAndView mav = new ModelAndView();
            mav.addObject(InTrayCommand.MDL_TASK, task);
            mav.setViewName("intray-task");
            
            return mav;
        } else {
            log.warn("A form was posted to the InTrayController without a valid taskOid attribute, redirecting to the showForm flow.");
            return defaultView(0,0,pageSize, showTasks);
        }
    }
    
    private ModelAndView claimTask(InTrayCommand intrayCmd, int pageSize, Boolean showTasks) {
        Long taskOid = intrayCmd.getTaskOid();
        User loggedInUser = AuthUtil.getRemoteUserObject();
        if (taskOid != null) {
            inTrayManager.claimTask(loggedInUser, taskOid);
            
        } else {
            log.warn("A form was posted to the InTrayController without a valid taskOid attribute, redirecting to the showForm flow.");
        }
        return defaultView(intrayCmd.getTaskPage(), intrayCmd.getNotificationPage(), pageSize, showTasks);
    }
    
    private ModelAndView unclaimTask(InTrayCommand intrayCmd, int pageSize, Boolean showTasks) {
        Long taskOid = intrayCmd.getTaskOid();
        User loggedInUser = AuthUtil.getRemoteUserObject();
        if (taskOid != null) {
            inTrayManager.unclaimTask(loggedInUser, taskOid);
            
        } else {
            log.warn("A form was posted to the InTrayController without a valid taskOid attribute, redirecting to the showForm flow.");
        }
        return defaultView(intrayCmd.getTaskPage(), intrayCmd.getNotificationPage(), pageSize, showTasks);
    }
    
    private ModelAndView defaultView(int taskPage, int notificationPage, int pageSize, Boolean showTasks) {
        ModelAndView mav = new ModelAndView();
        
        User loggedInUser = AuthUtil.getRemoteUserObject();
            
        Pagination notifications = inTrayManager.getNotifications(loggedInUser, notificationPage, pageSize);
        mav.addObject(InTrayCommand.MDL_NOTIFICATIONS, notifications);

        //Set value to agency default only if the value hasn't been set yet
		if(showTasks==null) {
	        Agency agency = loggedInUser.getAgency();
			showTasks = agency.getShowTasks();
		}

		Pagination tasks = inTrayManager.getTasks(loggedInUser, taskPage, pageSize);
       	mav.addObject(InTrayCommand.MDL_TASKS, tasks);
        
        mav.addObject(InTrayCommand.MDL_SHOW_TASKS, showTasks);
        
        mav.addObject(InTrayCommand.MDL_CURRENT_USER, loggedInUser);
        mav.setViewName("intray");
        
        return mav;
    }
    
    private ModelAndView deleteAllNotifications(InTrayCommand intrayCmd, int pageSize, Boolean showTasks) {
    	inTrayManager.deleteAllNotifications(AuthUtil.getRemoteUserObject().getOid());
    	ModelAndView mav = defaultView(intrayCmd.getTaskPage(), intrayCmd.getNotificationPage(), pageSize, showTasks);
    	return mav;
    }

    private ModelAndView deleteAllTasks(InTrayCommand intrayCmd, int pageSize, Boolean showTasks) {
    	inTrayManager.deleteAllTasks();
    	ModelAndView mav = defaultView(intrayCmd.getTaskPage(), intrayCmd.getNotificationPage(), pageSize, showTasks);
    	return mav;
    }
}
