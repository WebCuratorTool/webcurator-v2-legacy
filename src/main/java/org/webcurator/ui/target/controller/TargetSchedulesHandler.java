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
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.CronExpression;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.DateUtils;
import org.webcurator.domain.SchedulePatternFactory;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.domain.model.core.SchedulePattern;
import org.webcurator.domain.model.core.Target;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.AbstractTargetEditorContext;
import org.webcurator.ui.target.command.TargetSchedulesCommand;
import org.webcurator.ui.util.Tab;
import org.webcurator.ui.util.TabHandler;
import org.webcurator.ui.util.TabbedController;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;
import org.webcurator.ui.util.Utils;

/**
 * The handler for the target schedules tab.
 * @author bbeaumont
 */
public class TargetSchedulesHandler extends TabHandler {
	/** The pattern factory */
	private SchedulePatternFactory patternFactory = null;
	
	private BusinessObjectFactory businessObjectFactory = null;
	
	private AuthorityManager authorityManager = null;
	
    private TargetManager targetManager;
	
	private String contextSessionKey = null;
	
	private String privilegeString = null;
	
	private String editControllerUrl = null;
		
	public AbstractTargetEditorContext getEditorContext(HttpServletRequest req) {
		return (AbstractTargetEditorContext) req.getSession().getAttribute(contextSessionKey);
	}
	
	/**
	 * @param patternFactory The patternFactory to set.
	 */
	public void setPatternFactory(SchedulePatternFactory patternFactory) {
		this.patternFactory = patternFactory;
	}

	@Override
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		// Determine the necessary formats.
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        
        // Register the binders.
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, nf, true));
        binder.registerCustomEditor(Integer.class, "scheduleType", new CustomNumberEditor(Integer.class, nf, true));
        binder.registerCustomEditor(java.util.Date.class, org.webcurator.ui.util.DateUtils.get().getFullDateEditor(true));       
    }

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public void processTab(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		// Do nothing		
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#preProcessNextTab(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public TabbedModelAndView preProcessNextTab(TabbedController tc,
			Tab nextTabID, HttpServletRequest req, HttpServletResponse res,
			Object comm, BindException errors) {

        AbstractTargetEditorContext editorContext = getEditorContext(req);
		AbstractTarget abstractTarget = editorContext.getAbstractTarget();
		if (!abstractTarget.isAnnotationsSet()) {
        	abstractTarget.setAnnotations(targetManager.getAnnotations(abstractTarget));
        }
		
		String alertText = "";
		int count=0;
		for (Annotation ann: abstractTarget.getAnnotations()) {
			if (ann.isAlertable()) {
				count++;
				alertText+=(count+": "+ann.getNote()+"\n");
			}
		}
		
		TabbedModelAndView tmav = tc.new TabbedModelAndView();
		tmav.addObject("schedules", editorContext.getSortedSchedules());
		tmav.addObject("patternMap", patternFactory.getPatternMap());
		tmav.addObject("privilegeString", privilegeString);
		tmav.addObject("editMode", editorContext.isEditMode());
		tmav.addObject("abstractTarget", abstractTarget);
		tmav.addObject("editControllerUrl", editControllerUrl);
		tmav.addObject("alertText", alertText);
		if(abstractTarget.getObjectType() == AbstractTarget.TYPE_TARGET) {
			if(abstractTarget.getOid()!=null) {
				Target target = targetManager.load(abstractTarget.getOid());
				tmav.addObject("allowOptimize", target.isAllowOptimize());
			}
		}
		return tmav;
	}

	/* (non-Javadoc)
	 * @see org.webcurator.ui.util.TabHandler#processOther(org.webcurator.ui.util.TabbedController, org.webcurator.ui.util.Tab, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	public ModelAndView processOther(TabbedController tc, Tab currentTab,
			HttpServletRequest req, HttpServletResponse res, Object comm,
			BindException errors) {
		TargetSchedulesCommand command = (TargetSchedulesCommand) comm;
		AbstractTargetEditorContext ctx = getEditorContext(req);
		
		if(authorityManager.hasPrivilege(ctx.getAbstractTarget(), privilegeString)) {
		
			if(WebUtils.hasSubmitParameter(req, "_new")) {
				Tab tab = tc.getTabConfig().getTabByID("SCHEDULES").createSubTab("../target-schedule-edit.jsp");
				
				TabbedModelAndView tmav = tc.new TabbedModelAndView();
				tmav.getTabStatus().setCurrentTab(tab);
				tmav.getTabStatus().setEnabled(false);
				
				TargetSchedulesCommand newCommand = new TargetSchedulesCommand();
				
				tmav.addObject(Constants.GBL_CMD_DATA, newCommand);
				tmav.addObject("patterns", patternFactory.getPatterns());			
				
				return tmav;
			}
			
			if(WebUtils.hasSubmitParameter(req, "_edit")) {
				Tab tab = tc.getTabConfig().getTabByID("SCHEDULES").createSubTab("../target-schedule-edit.jsp");
				
				TabbedModelAndView tmav = tc.new TabbedModelAndView();
				tmav.getTabStatus().setCurrentTab(tab);
				tmav.getTabStatus().setEnabled(false);
				
				Schedule aSchedule = (Schedule) ctx.getObject(Schedule.class, command.getSelectedItem());
				TargetSchedulesCommand newCommand = TargetSchedulesCommand.buildFromModel(aSchedule);
				
				tmav.addObject(Constants.GBL_CMD_DATA, newCommand);
				tmav.addObject("patterns", patternFactory.getPatterns());			
				
				return tmav;
			}		
	
			if(WebUtils.hasSubmitParameter(req, "_save")) {
				
				
				if(errors.hasErrors()) {
					TabbedModelAndView tmav = getEditTabModel(tc);
					tmav.addObject(Constants.GBL_CMD_DATA, errors.getTarget());
					tmav.addObject(Constants.GBL_ERRORS, errors);
					tmav.addObject("patterns", patternFactory.getPatterns());	
					return tmav;
				}
				else {
					if( !Utils.isEmpty(command.getSelectedItem())) {
						Schedule aSchedule = (Schedule) ctx.getObject(Schedule.class, command.getSelectedItem());
						ctx.getAbstractTarget().removeSchedule(aSchedule);
					}
					
					Schedule schedule = null;
					
					if(command.getScheduleType() == Schedule.CUSTOM_SCHEDULE) {
				    	schedule = businessObjectFactory.newSchedule(ctx.getAbstractTarget());
				    	schedule.setStartDate(command.getStartDate());
				    	schedule.setEndDate(command.getEndDate());
				    	schedule.setScheduleType(command.getScheduleType());
				    	schedule.setCronPattern(command.getCronExpression());
					}
					else {
						SchedulePattern pattern = patternFactory.getPattern(command.getScheduleType()); 
						schedule = pattern.makeSchedule(businessObjectFactory, ctx.getAbstractTarget(), command.getStartDate(), command.getEndDate());
					}
					
					ctx.putObject(schedule);
					ctx.getAbstractTarget().addSchedule(schedule);
					
					TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
					tmav.getTabStatus().setCurrentTab(currentTab);
					return tmav;
				}
			}	
			
			if(WebUtils.hasSubmitParameter(req, "_cancel")) {
				TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
				tmav.getTabStatus().setCurrentTab(currentTab);
				return tmav;
			}			
			
			if(WebUtils.hasSubmitParameter(req, "_remove")) {
				//sessionModel.getSchedules().remove(command.getSelectedItem());
				Schedule scheduleToRemove = (Schedule) ctx.getObject(Schedule.class, command.getSelectedItem());
				ctx.getAbstractTarget().removeSchedule(scheduleToRemove);
				
				TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
				tmav.getTabStatus().setCurrentTab(currentTab);
				return tmav;
			}		
			
			if(WebUtils.hasSubmitParameter(req, "_test")) {
		
				if(errors.hasErrors()) {
					TabbedModelAndView tmav = getEditTabModel(tc);
					tmav.addObject("patterns", patternFactory.getPatterns());
					tmav.addObject(Constants.GBL_CMD_DATA, errors.getTarget());
					tmav.addObject(Constants.GBL_ERRORS, errors);
					return tmav;
				}
				else {			
				
					List<Date> testResults = new LinkedList<Date>();
		
					TabbedModelAndView tmav = getEditTabModel(tc);
					
					tmav.addObject("patterns", patternFactory.getPatterns());
					tmav.addObject(Constants.GBL_CMD_DATA, command);
					tmav.addObject("testResults", testResults);
					
					try {
						CronExpression expr = new CronExpression(command.getCronExpression());
						Date d = DateUtils.latestDate(new Date(), command.getStartDate());
						Date nextDate = null;
						for(int i = 0; i<10; i++) {
							nextDate = expr.getNextValidTimeAfter(d);
							if(nextDate == null || command.getEndDate() != null && nextDate.after(command.getEndDate())) {
								break;
							}
							testResults.add(nextDate);
							d = nextDate;
						}
					}
					catch(ParseException ex) {
						ex.printStackTrace();
					}
		
					return tmav;
				}
			}
		}
		
		TabbedModelAndView tmav = preProcessNextTab(tc, currentTab, req, res, comm, errors);
		tmav.getTabStatus().setCurrentTab(currentTab);
		return tmav;
	}

	private TabbedModelAndView getEditTabModel(TabbedController controller) {
		Tab tab = controller.getTabConfig().getTabByID("SCHEDULES").createSubTab("../target-schedule-edit.jsp");
		TabbedModelAndView tmav = controller.new TabbedModelAndView();
		tmav.getTabStatus().setCurrentTab(tab);
		tmav.getTabStatus().setEnabled(false);
		return tmav;
	}

	/**
	 * @param businessObjectFactory The businessObjectFactory to set.
	 */
	public void setBusinessObjectFactory(BusinessObjectFactory businessObjectFactory) {
		this.businessObjectFactory = businessObjectFactory;
	}

	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}
	
    /**
     * @param targetManager The targetManager to set.
     */
    public void setTargetManager(TargetManager targetManager) {
        this.targetManager = targetManager;
    }

	/**
	 * @return Returns the contextSessionKey.
	 */
	public String getContextSessionKey() {
		return contextSessionKey;
	}

	/**
	 * @param contextSessionKey The contextSessionKey to set.
	 */
	public void setContextSessionKey(String contextSessionKey) {
		this.contextSessionKey = contextSessionKey;
	}

	/**
	 * @return Returns the privilegeString.
	 */
	public String getPrivilegeString() {
		return privilegeString;
	}

	/**
	 * @param privilegeString The privilegeString to set.
	 */
	public void setPrivilegeString(String privilegeString) {
		this.privilegeString = privilegeString;
	}

	/**
	 * @param editControllerUrl The editControllerUrl to set.
	 */
	public void setEditControllerUrl(String editControllerUrl) {
		this.editControllerUrl = editControllerUrl;
	}
}
