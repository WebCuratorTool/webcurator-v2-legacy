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
package org.webcurator.ui.groups.controller;

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
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.groups.GroupsEditorContext;
import org.webcurator.ui.groups.command.GroupAnnotationCommand;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the group annotations tab.
 * @author bbeaumont
 */
public class GroupAnnotationHandler extends AbstractGroupTabHandler {
    private static Log log = LogFactory.getLog(GroupAnnotationHandler.class);
	
    private TargetManager targetManager;
        
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());        
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));
        binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));
        binder.registerCustomEditor(java.util.Date.class, DateUtils.get().getFullDateTimeEditor(true));       
    }
    
    public void processTab(TabbedController tc, Tab currentTab,
            HttpServletRequest req, HttpServletResponse res, Object comm,
            BindException errors) {
    	GroupAnnotationCommand cmd = (GroupAnnotationCommand) comm;
        if (cmd.isAction(GroupAnnotationCommand.ACTION_ADD_NOTE)) {
        	GroupsEditorContext ctx = getEditorContext(req);
        	addAnnotation(cmd, ctx);
        }
    }
    
    @SuppressWarnings("unchecked")
    public TabbedModelAndView preProcessNextTab(TabbedController tc,
            Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
            Object comm, BindException errors) {
        // build mav stuff b4 displaying the tab
        TabbedModelAndView tmav = tc.new TabbedModelAndView();
        GroupsEditorContext ctx = getEditorContext(req);
        
        TargetGroup targetGroup = ctx.getTargetGroup();        
        if (!targetGroup.isAnnotationsSet()) {
        	targetGroup.setAnnotations(targetManager.getAnnotations(targetGroup));
        }
		
		tmav.addObject(Constants.GBL_CMD_DATA, new GroupAnnotationCommand());
		tmav.addObject("annotations", targetGroup.getSortedAnnotations());
                                               
        return tmav;        
    }       

    public ModelAndView processOther(TabbedController tc, Tab currentTab,
            HttpServletRequest req, HttpServletResponse res, Object comm,
            BindException errors) {
        GroupAnnotationCommand cmd = (GroupAnnotationCommand) comm;
        GroupsEditorContext ctx = getEditorContext(req);
        
        TabbedModelAndView nextView = null;
        
        if(ctx.isEditMode()) {
        	if(errors.hasErrors()) {
        		nextView = preProcessNextTab(tc, currentTab, req, res, cmd, errors);
        		nextView.getTabStatus().setCurrentTab(currentTab);
        		nextView.addObject(Constants.GBL_CMD_DATA, cmd);
        	}
        	else {
    	        if (cmd.isAction(GroupAnnotationCommand.ACTION_ADD_NOTE)) {
		        	if (log.isDebugEnabled()) {
		        		log.debug("Processing add annotation.");
		        	}
		        	addAnnotation(cmd, ctx);
    	        }
    	        else if (cmd.isAction(GroupAnnotationCommand.ACTION_MODIFY_NOTE)) {
		        	if (log.isDebugEnabled()) {
		        		log.debug("Processing modify annotation.");
		        	}
		        	TargetGroup targetGroup = ctx.getTargetGroup();
		        	Annotation annotation = targetGroup.getAnnotation(cmd.getNoteIndex());
		        	if(annotation != null &&
		        			annotation.getUser().equals(AuthUtil.getRemoteUserObject()))
		        	{
			        	annotation.setDate(new Date());
			        	annotation.setNote(cmd.getNote());
			        	targetGroup.sortAnnotations();
			     	}
    	        }
    	        else if (cmd.isAction(GroupAnnotationCommand.ACTION_DELETE_NOTE)) {
		        	if (log.isDebugEnabled()) {
		        		log.debug("Processing delete annotation.");
		        	}
		        	TargetGroup targetGroup = ctx.getTargetGroup();
		        	Annotation annotation = targetGroup.getAnnotation(cmd.getNoteIndex());
		        	if(annotation != null &&
		        			annotation.getUser().equals(AuthUtil.getRemoteUserObject()))
		        	{
			        	targetGroup.deleteAnnotation(cmd.getNoteIndex());
			     	}
    	        }
    	        
    	        nextView = preProcessNextTab(tc, currentTab, req, res, cmd, errors);
    	        nextView.getTabStatus().setCurrentTab(currentTab);
        	}
        }
        else {
        	nextView = preProcessNextTab(tc, currentTab, req, res, cmd, errors);
	        nextView.getTabStatus().setCurrentTab(currentTab);
        }
        
        return nextView;
    }

	private void addAnnotation(GroupAnnotationCommand cmd,
			GroupsEditorContext ctx) {
		Annotation annotation = new Annotation();
		annotation.setDate(new Date());
		annotation.setNote(cmd.getNote());
		annotation.setUser(AuthUtil.getRemoteUserObject());
		annotation.setObjectType(TargetGroup.class.getName());
		annotation.setObjectOid(null);
		
		TargetGroup targetGroup = ctx.getTargetGroup();
		targetGroup.addAnnotation(annotation);
	}

    /**
     * @param targetManager The targetManager to set.
     */
    public void setTargetManager(TargetManager targetManager) {
        this.targetManager = targetManager;
    }
}
