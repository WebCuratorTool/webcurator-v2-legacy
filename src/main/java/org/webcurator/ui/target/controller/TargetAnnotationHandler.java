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
import org.webcurator.core.common.WCTTreeSet;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.TargetEditorContext;
import org.webcurator.ui.target.command.TargetAnnotationCommand;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.Utils;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The handler for the target annotations tab.
 * @author bbeaumont
 */
public class TargetAnnotationHandler extends AbstractTargetTabHandler {
    private static Log log = LogFactory.getLog(TargetAnnotationHandler.class);
	
    private TargetManager targetManager;
    
    /** A List of the selection types. */
    private WCTTreeSet selectionTypesList;
    /** A list of the harvest types */
    private WCTTreeSet harvestTypesList;
    

    
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());        
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));
        binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));
        binder.registerCustomEditor(java.util.Date.class, DateUtils.get().getFullDateTimeEditor(true));
    }
    
    public void processTab(TabbedController tc, Tab currentTab,
            HttpServletRequest req, HttpServletResponse res, Object comm,
            BindException errors) {
        TargetEditorContext ctx = getEditorContext(req);
        Target target = ctx.getTarget();
        
        if (ctx.isEditMode()) {
        	updateModel((TargetAnnotationCommand) comm, target);
        }
        
        TargetAnnotationCommand cmd = (TargetAnnotationCommand) comm;
        if (cmd.isAction(TargetAnnotationCommand.ACTION_ADD_NOTE)) {
        	addAnnotation(cmd, target);
        }

    }
    
    @SuppressWarnings("unchecked")
    public TabbedModelAndView preProcessNextTab(TabbedController tc,
            Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
            Object comm, BindException errors) {
        // build mav stuff b4 displaying the tab
        TabbedModelAndView tmav = tc.new TabbedModelAndView();
        TargetEditorContext ctx = getEditorContext(req);
        Target target = ctx.getTarget();
        
        if (!target.isAnnotationsSet()) {
        	target.setAnnotations(targetManager.getAnnotations(target));
        }
        			      
		TargetAnnotationCommand populatedCommand = buildFromModel(target);    
		
		tmav.addObject(Constants.GBL_CMD_DATA, populatedCommand);
		tmav.addObject("annotations", target.getSortedAnnotations());
		tmav.addObject("selectionTypesList", selectionTypesList);
		tmav.addObject("harvestTypesList", harvestTypesList);
                                               
        return tmav;        
    }
    
    /**
     * Build a command object from the business model.
     * @param aTarget The business model.
     * @return A command object.
     */
    private TargetAnnotationCommand buildFromModel(Target aTarget) {
    	TargetAnnotationCommand command = new TargetAnnotationCommand();
    	command.setEvaluationNote(aTarget.getEvaluationNote());
    	command.setSelectionDate(aTarget.getSelectionDate());
    	command.setSelectionNote(aTarget.getSelectionNote());
    	command.setSelectionType(aTarget.getSelectionType());
    	command.setHarvestType(aTarget.getHarvestType());
    	return command;
    }
    
    private void updateModel(TargetAnnotationCommand command, Target aTarget) {
    	aTarget.setEvaluationNote(command.getEvaluationNote());
    	aTarget.setSelectionNote(command.getSelectionNote());
    	aTarget.setSelectionType(command.getSelectionType());
    	aTarget.setHarvestType(command.getHarvestType());
    }

    public ModelAndView processOther(TabbedController tc, Tab currentTab,
            HttpServletRequest req, HttpServletResponse res, Object comm,
            BindException errors) {
        TargetAnnotationCommand cmd = (TargetAnnotationCommand) comm;
        TargetEditorContext ctx = getEditorContext(req);
        
        TabbedModelAndView nextView = null;
        
        if(ctx.isEditMode()) {
        	if(errors.hasErrors()) {
        		nextView = preProcessNextTab(tc, currentTab, req, res, cmd, errors);
        		nextView.getTabStatus().setCurrentTab(currentTab);
        		nextView.addObject(Constants.GBL_CMD_DATA, cmd);
        	}
        	else {
	        	Target target = ctx.getTarget();
        		
    	        if (cmd.isAction(TargetAnnotationCommand.ACTION_ADD_NOTE)) {
		        	addAnnotation(cmd, target);
    	        }
    	        if (cmd.isAction(TargetAnnotationCommand.ACTION_MODIFY_NOTE)) {
		        	if (log.isDebugEnabled()) {
		        		log.debug("Processing modify annotation.");
		        	}
		        	Annotation annotation = target.getAnnotation(cmd.getNoteIndex());
		        	if(annotation != null &&
		        			annotation.getUser().equals(AuthUtil.getRemoteUserObject()))
		        	{
			        	annotation.setDate(new Date());
			        	annotation.setNote(cmd.getNote());
			        	annotation.setAlertable(cmd.isAlertable());
			        	target.sortAnnotations();
			     	}
    	        }
    	        else if (cmd.isAction(TargetAnnotationCommand.ACTION_DELETE_NOTE)) {
		        	if (log.isDebugEnabled()) {
		        		log.debug("Processing delete annotation.");
		        	}
		        	Annotation annotation = target.getAnnotation(cmd.getNoteIndex());
		        	if(annotation != null &&
		        			annotation.getUser().equals(AuthUtil.getRemoteUserObject()))
		        	{
			        	target.deleteAnnotation(cmd.getNoteIndex());
			     	}
    	        }
    	        
	        	updateModel(cmd, target);
	        	
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

	private void addAnnotation(TargetAnnotationCommand cmd, Target target) {
		if (log.isDebugEnabled()) {
			log.debug("Processing add annotation.");
		}
		Annotation annotation = new Annotation();
		annotation.setDate(new Date());
		annotation.setNote(cmd.getNote());
		annotation.setAlertable(cmd.isAlertable());
		annotation.setUser(AuthUtil.getRemoteUserObject());
		annotation.setObjectType(Target.class.getName());
		annotation.setObjectOid(null);
		
		target.addAnnotation(annotation);
	}

    /**
     * @param targetManager The targetManager to set.
     */
    public void setTargetManager(TargetManager targetManager) {
        this.targetManager = targetManager;
    }

	/**
	 * @param selectionTypesList The selectionTypesList to set.
	 */
	public void setSelectionTypesList(WCTTreeSet selectionTypesList) {
		this.selectionTypesList = selectionTypesList;
	}

	/**
	 * @param harvestTypesList The harvestTypesList to set.
	 */
	public void setHarvestTypesList(WCTTreeSet harvestTypesList) {
		this.harvestTypesList = harvestTypesList;
	}
}
