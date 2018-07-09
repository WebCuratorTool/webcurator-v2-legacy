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

import java.text.NumberFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabHandler;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the target instance annotations tab.
 * @author nwaight
 */
public class TargetInstanceAnnotationHandler extends TabHandler {

    private TargetInstanceManager targetInstanceManager;
    private static Log log = LogFactory.getLog(TargetInstanceAnnotationHandler.class);
    
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());        
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));
        binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));
        binder.registerCustomEditor(java.util.Date.class, DateUtils.get().getFullDateTimeEditor(true));
      
    }
    
    public void processTab(TabbedController tc, Tab currentTab,
            HttpServletRequest req, HttpServletResponse res, Object comm,
            BindException errors) {
        // process the submit of the tab called on change tab or save    
        if (comm instanceof TargetInstanceCommand) {
    		TargetInstanceCommand cmd = (TargetInstanceCommand) comm;        
        	cmd = (TargetInstanceCommand) comm;
        	if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_ADD_NOTE)) {
        		TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI);
        		addAnnotation(cmd, ti);
        	}
        }
    }
    
    @SuppressWarnings("unchecked")
    public TabbedModelAndView preProcessNextTab(TabbedController tc,
            Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
            Object comm, BindException errors) {
        // build mav stuff b4 displaying the tab
        TabbedModelAndView tmav = tc.new TabbedModelAndView();
        
        Boolean editMode = false;
        TargetInstanceCommand cmd = null;
        TargetInstance ti = null;
        if (comm instanceof TargetInstanceCommand) {
        	cmd = (TargetInstanceCommand) comm;
        	if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_EDIT)) {
        		editMode = true;
        	}
        }
        
        if (req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI) == null) {
    		ti = targetInstanceManager.getTargetInstance(cmd.getTargetInstanceId(), true);
    		req.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
    		req.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, editMode);
    	}
    	else {
    		ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI); 
    		editMode = (Boolean) req.getSession().getAttribute(TargetInstanceCommand.SESSION_MODE);
    	}               
        
        if (!ti.isAnnotationsSet()) {
        	ti.setAnnotations(targetInstanceManager.getAnnotations(ti));
        }
        
        //ensure annotations are sorted
        ti.sortAnnotations();
        			      
		TargetInstanceCommand populatedCommand = new TargetInstanceCommand(ti);
		if (editMode) {
			populatedCommand.setCmd(TargetInstanceCommand.ACTION_EDIT);
		}
		
		tmav.addObject(Constants.GBL_CMD_DATA, populatedCommand);
		tmav.addObject(TargetInstanceCommand.MDL_INSTANCE, ti);
                                               
        return tmav;        
    }

    public ModelAndView processOther(TabbedController tc, Tab currentTab,
            HttpServletRequest req, HttpServletResponse res, Object comm,
            BindException errors) {
        TargetInstanceCommand cmd = (TargetInstanceCommand) comm;        
        if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_ADD_NOTE)) {
        	if (log.isDebugEnabled()) {
        		log.debug("Processing add annotation.");
        	}
        	
        	TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI);
        	
        	if (!errors.hasErrors()) {
	        	addAnnotation(cmd, ti);
        	}
        }
        else if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_MODIFY_NOTE)) {
        	if (log.isDebugEnabled()) {
        		log.debug("Processing modify annotation.");
        	}
        	TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI);
        	Annotation annotation = ti.getAnnotation(cmd.getNoteIndex());
        	if(annotation != null &&
        			annotation.getUser().equals(AuthUtil.getRemoteUserObject()))
        	{
	        	annotation.setDate(new Date());
	        	annotation.setNote(cmd.getNote());
	        	annotation.setAlertable(cmd.isAlertable());
	     	}
        }
        else if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_DELETE_NOTE)) {
        	if (log.isDebugEnabled()) {
        		log.debug("Processing delete annotation.");
        	}
        	TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI);
        	Annotation annotation = ti.getAnnotation(cmd.getNoteIndex());
        	if(annotation != null &&
        			annotation.getUser().equals(AuthUtil.getRemoteUserObject()))
        	{
	        	ti.deleteAnnotation(cmd.getNoteIndex());
	     	}
        }

        TargetInstance ti = (TargetInstance) req.getSession().getAttribute(TargetInstanceCommand.SESSION_TI);

        //ensure annotations are sorted
        ti.sortAnnotations();
        
    	Tab tab = tc.getTabConfig().getTabByID("ANNOTATIONS");
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.getTabStatus().setCurrentTab(tab);
		tmav.getTabStatus().setEnabled(true);
		
		cmd.setCmd(TargetInstanceCommand.ACTION_EDIT);
    	tmav.addObject(Constants.GBL_CMD_DATA, cmd);
		tmav.addObject(TargetInstanceCommand.MDL_INSTANCE, ti);    		
    	
    	return tmav;	       
    }

	private void addAnnotation(TargetInstanceCommand cmd, TargetInstance ti) {
		Annotation annotation = new Annotation();
		annotation.setDate(new Date());
		annotation.setNote(cmd.getNote());
		annotation.setAlertable(cmd.isAlertable());
		annotation.setUser(AuthUtil.getRemoteUserObject());
		annotation.setObjectType(TargetInstance.class.getName());
		annotation.setObjectOid(cmd.getTargetInstanceId());
		
		ti.addAnnotation(annotation);
	}

    /**
     * @param targetInstanceManager The targetInstanceManager to set.
     */
    public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
        this.targetInstanceManager = targetInstanceManager;
    }
}
