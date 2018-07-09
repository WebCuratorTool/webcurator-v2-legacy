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

import java.beans.PropertyEditorSupport;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.common.Environment;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;
import org.webcurator.core.profiles.ProfileManager;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.store.DigitalAssetStore;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.BusinessObjectFactory;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;
import org.webcurator.domain.model.core.DublinCore;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.Indicator;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.domain.model.core.RejReason;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.HarvestHistoryDTO;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.target.command.ProfileCommand;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.ui.target.command.TargetInstanceSummaryCommand;
import org.webcurator.ui.target.command.TargetSchedulesCommand;
import org.webcurator.ui.target.command.Time;
import org.webcurator.ui.util.DateUtils;
import org.webcurator.ui.util.TabbedController.TabbedModelAndView;

/**
 * The controller for displaying the Target Instance QA Summary Page.
 * @author twoods
 */
public class QaTiSummaryController extends AbstractFormController {
    /** The manager to use to access the target instance. */
    private TargetInstanceManager targetInstanceManager;
    /** The harvest coordinator for looking at the harvesters. */
    private Environment environment;  
    /** The manager to use for user, role and agency data. */
    private AgencyUserManager agencyUserManager;
    /** The component that will provide an <code>Indicator</code> based QA recommendation **/
    private HarvestCoordinator harvestCoordinator;
    /** the logger. */
    private Log log;
	/** the profile manager to use. */
	private ProfileManager profileManager;
	/** Authority Manager */
	private AuthorityManager authorityManager = null;
	/** Business object factory */
	private BusinessObjectFactory businessObjectFactory;
	/** used to retrieve a complete target **/
	private TargetManager targetManager;
    /** the message source. */
    private MessageSource messageSource = null;
    
    private DigitalAssetStore digitalAssetStore;
    
    private static Map<String,Integer> monthMap = new HashMap<String,Integer>(20);
    private static Map<String,Integer> dayMap = new HashMap<String,Integer>(60);

    /** static Map of months used by the schedule panel **/
	static {
        monthMap.put("JAN", new Integer(0));
        monthMap.put("FEB", new Integer(1));
        monthMap.put("MAR", new Integer(2));
        monthMap.put("APR", new Integer(3));
        monthMap.put("MAY", new Integer(4));
        monthMap.put("JUN", new Integer(5));
        monthMap.put("JUL", new Integer(6));
        monthMap.put("AUG", new Integer(7));
        monthMap.put("SEP", new Integer(8));
        monthMap.put("OCT", new Integer(9));
        monthMap.put("NOV", new Integer(10));
        monthMap.put("DEC", new Integer(11));

        dayMap.put("SUN", new Integer(1));
        dayMap.put("MON", new Integer(2));
        dayMap.put("TUE", new Integer(3));
        dayMap.put("WED", new Integer(4));
        dayMap.put("THU", new Integer(5));
        dayMap.put("FRI", new Integer(6));
        dayMap.put("SAT", new Integer(7));
    }	

    /** Default constructor. */    
    public QaTiSummaryController() {
        super();
        setCommandClass(TargetInstanceSummaryCommand.class);
        log = LogFactory.getLog(getClass());
    }
    
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(java.util.Date.class, DateUtils.get().getFullDateTimeEditor(true));
        
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));   
        
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {     
        	public void setAsText(String value) {
        		try {             
        			setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));         
        		} catch(ParseException e) {             
        			setValue(null);         
        		}     
        	}      
        	public String getAsText() {         
        		Date value = (Date) getValue();
        		if (value != null) {
        			return new SimpleDateFormat("dd/MM/yyyy").format(value); 
        		} else {
        			return "";
        		}
        	}          
        }); 
    }
   
    /**
     * binds the data to the <code>ModelAndView</code>
     * @param mav	the <code>ModelAndView</code> to bind to
     * @param ti	the <code>TargetInstance</code> whose data should be bound to the <code>ModelAndView</code>
     */
    private ModelAndView buildModelAndView(HttpServletRequest request, TargetInstance ti, TargetInstance rcti, TargetInstanceSummaryCommand command, BindException errors) {
    	
       	ModelAndView mav = new ModelAndView(Constants.VIEW_TARGET_INSTANCE_QA_SUMMARY);
    	
        mav.addObject(TargetInstanceSummaryCommand.MDL_INSTANCE, ti);
        
        if (command != null)
        	mav.addObject(Constants.GBL_CMD_DATA, command);
        
        // fetch any QA indicators (and retain the order of insertion - ie: the original sort order)
        LinkedHashSet<Indicator> indicators = new LinkedHashSet<Indicator>(ti.getIndicators());
        mav.addObject(TargetInstanceSummaryCommand.MDL_INDICATORS, indicators);
        
        // fetch any QA indicators for the reference crawl if defined
        HashMap<String, Indicator> rcIndicators = new HashMap<String, Indicator>();
        if (rcti != null) {
        	List<Indicator> rcis = rcti.getIndicators();
        
	        Iterator<Indicator> rciit = rcis.iterator();
	        // add them to the HashMap so that we can pull them out directly in the ui
	        while (rciit.hasNext()) {
	        	Indicator rci = rciit.next();
	        	rcIndicators.put(rci.getName(), rci);
	        }
        }
        mav.addObject(TargetInstanceSummaryCommand.MDL_RC_INDICATORS, rcIndicators);
	
        // bind the HarvesterStatus
        mav.addObject(TargetInstanceSummaryCommand.MDL_STATUS, ti.getStatus());
        
        // bind the HarvestHistory
        List<HarvestHistoryDTO> history = targetInstanceManager.getHarvestHistory(ti.getTarget().getOid());
        mav.addObject(TargetInstanceSummaryCommand.MDL_HISTORY, history);
        
        // add the seeds
        Set<String> seeds = ti.getOriginalSeeds();
        mav.addObject(TargetInstanceSummaryCommand.MDL_SEEDS, seeds);
        
        // add the log list
        LogFilePropertiesDTO[] arrLogs = harvestCoordinator.listLogFileAttributes(ti);
        mav.addObject(TargetInstanceSummaryCommand.MDL_LOGS, arrLogs);
        
        // add the harvest results
        mav.addObject(TargetInstanceSummaryCommand.MDL_RESULTS, ti.getHarvestResults());
        
    	// fetch the valid rejection reasons for targets 
    	// (used to populate the rejection reason drop-down)
		User user = AuthUtil.getRemoteUserObject();
        List<RejReason> rejectionReasons = agencyUserManager.getValidRejReasonsForTIs(user.getAgency().getOid());
        mav.addObject(TargetInstanceSummaryCommand.MDL_REASONS, rejectionReasons);
        
        // the annotations are not recovered by hibernate during the ti fetch so we need to add them
        ti.setAnnotations(targetInstanceManager.getAnnotations(ti));
        
        // add profile information to support overrides
        buildProfile(mav, request, ti, command);
        
        // add schedule info to support schedule management
        buildSchedule(mav, request, ti, command, errors);
        
        includeCustomFormDetails(request, mav, ti);
        
        return mav;
    }
    
    private void includeCustomFormDetails(HttpServletRequest request, ModelAndView mav, TargetInstance ti) {
		boolean customDepositFormRequired = false;
		if (TargetInstance.STATE_ENDORSED.equals(ti.getState())) {
			try {
				User user = org.webcurator.core.util.AuthUtil.getRemoteUserObject();
				Agency agency = user.getAgency();
				CustomDepositFormCriteriaDTO criteria = new CustomDepositFormCriteriaDTO();
				criteria.setUserId(user.getUsername());
				DublinCore dc = ti.getTarget().getDublinCoreMetaData();
				if (dc != null)
					criteria.setTargetType(ti.getTarget().getDublinCoreMetaData().getType());
				if (agency != null)
					criteria.setAgencyId(String.valueOf(agency.getOid()));
				criteria.setAgencyName(agency.getName());
				CustomDepositFormResultDTO response = digitalAssetStore.getCustomDepositFormDetails(criteria);
				if (response != null && response.isCustomDepositFormRequired()) {
					String customDepositFormHTMLContent = response.getHTMLForCustomDepositForm();
					String customDepositFormURL = response.getUrlForCustomDepositForm();
					if (customDepositFormURL != null) {
						customDepositFormRequired = true;
					}
					if (customDepositFormHTMLContent != null) {
						customDepositFormRequired = true;
					}
				}
			} catch (Exception e) {
				throw new WCTRuntimeException("Exception when trying to determine the custom deposit form details: " + e.getMessage(), e);
			}
		}
		mav.addObject("customDepositFormRequired", customDepositFormRequired);
	}

	private void buildSchedule(	ModelAndView mav, 
    							HttpServletRequest request, 
    							TargetInstance ti, 
    							TargetInstanceSummaryCommand command,
    							BindException errors) {
    	
    	// add schedule info to support schedule management
    	mav.addObject(TargetInstanceSummaryCommand.MDL_SCHEDULES, ti.getTarget().getSchedules());
   	
    	// model for the schedule subform
    	LinkedHashMap<Long, TargetSchedulesCommand> scheduleCommands = new LinkedHashMap<Long, TargetSchedulesCommand>();
    	
    	// the model schedules
    	Set<Schedule> schedules = ti.getTarget().getSchedules();
    	
    	// the month options for each schedule
    	LinkedHashMap<Long, Map<String, String>> scheduleMonthOptions = new LinkedHashMap<Long, Map<String, String>>();
    	
    	Iterator<Schedule> it = schedules.iterator();
    	
    	Boolean scheduleHasChanged = false;

    	// iterate thorough the schedules and set the command values to their corresponding schedule values
    	while (it.hasNext()) {
    		Schedule schedule = it.next();
    		TargetSchedulesCommand scheduleCommand = null;
    		
    		// update the form command if an update to the schedule has been posted from the ui
        	if (command != null && command.getScheduleOid() != null) {
        		
        		int scheduleIndex = command.getScheduleOid().indexOf(schedule.getOid().toString());
        		  
        		if (scheduleIndex != -1) {
        			scheduleCommand = buildScheduleCommand(mav, command, scheduleIndex, errors);
        		} else {
        			// build a new schedule command
        			scheduleCommand = TargetSchedulesCommand.buildFromModel(schedule);
        		}
        	} else {
        	
    			// build a new schedule command
    			scheduleCommand = TargetSchedulesCommand.buildFromModel(schedule);
        	}
        	
        	scheduleCommands.put(schedule.getOid(), scheduleCommand);
        	
        	// get the month options
        	scheduleMonthOptions.put(schedule.getOid(), EditScheduleController.getMonthOptionsByType(scheduleCommand.getScheduleType()));
        	
        	// if the schedule has changed then note this so that the action buttons can be enabled/disabled in the ui
        	if (!scheduleHasChanged && (!schedule.getCronPattern().equals(scheduleCommand.getCronExpression())
        			|| schedule.getScheduleType() != scheduleCommand.getScheduleType())) {
        		scheduleHasChanged = true;
        	}
    	}
    	
    	mav.addObject(TargetInstanceSummaryCommand.MDL_SCHEDULE_COMMANDS, scheduleCommands);
    	mav.addObject(TargetInstanceSummaryCommand.MDL_SCHEDULE_MONTH_OPTIONS, scheduleMonthOptions);
    	mav.addObject(TargetInstanceSummaryCommand.MDL_SCHEDULE_HAS_CHANGED, scheduleHasChanged.toString());
    	
    }
    
    private TargetSchedulesCommand buildScheduleCommand(ModelAndView mav,
    													TargetInstanceSummaryCommand command, 
    													int scheduleIndex, 
    													BindException errors) {
    	TargetSchedulesCommand scheduleCommand = new TargetSchedulesCommand();
		scheduleCommand.setScheduleType(Integer.parseInt(command.getScheduleType().get(scheduleIndex)));

		try {
			if (!command.getStartDate().get(scheduleIndex).equals("") || command.getStartDate().get(scheduleIndex) == null) {
				scheduleCommand.setStartDate(new SimpleDateFormat("d/M/yyyy").parse(command.getStartDate().get(scheduleIndex)));
			} else {
				mav.addObject(Constants.GBL_MESSAGES, "From Date is a required field");
			}
		} catch (ParseException e) {
			mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("typeMismatch.java.util.Date", new Object[] { command.getStartDate().get(scheduleIndex) }, Locale.getDefault()));
		}
		try {
			if (!command.getEndDate().get(scheduleIndex).equals("")) {
				scheduleCommand.setEndDate(new SimpleDateFormat("d/M/yyyy").parse(command.getEndDate().get(scheduleIndex)));
				if (scheduleCommand.getStartDate() != null && scheduleCommand.getEndDate() != null && scheduleCommand.getStartDate().getTime() > scheduleCommand.getEndDate().getTime()) {
					mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("time.range", new Object[] { command.getEndDate().get(scheduleIndex), command.getStartDate().get(scheduleIndex) }, Locale.getDefault()));
				}
			}
		} catch (ParseException e) {
			mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("typeMismatch.java.util.Date", new Object[] { command.getEndDate().get(scheduleIndex) }, Locale.getDefault()));
		}
		scheduleCommand.setMinutes(command.getMinutes().get(scheduleIndex));
		scheduleCommand.setHours(command.getHours().get(scheduleIndex));
		scheduleCommand.setDaysOfMonth(command.getDaysOfMonth().get(scheduleIndex));
		scheduleCommand.setMonths(command.getMonths().get(scheduleIndex));
		scheduleCommand.setDaysOfWeek(command.getDaysOfWeek().get(scheduleIndex));
		scheduleCommand.setYears(command.getYears().get(scheduleIndex));
    	if(Integer.parseInt(command.getScheduleType().get(scheduleIndex)) < 0) { 
    		int hours = Integer.parseInt(command.getHours().get(scheduleIndex));
    		int minutes = Integer.parseInt(command.getMinutes().get(scheduleIndex));
    		scheduleCommand.setTime(new Time(hours, minutes));
    	}
    	
    	return scheduleCommand;
    }

    private void buildProfile(ModelAndView mav, HttpServletRequest request, TargetInstance ti, TargetInstanceSummaryCommand command) {
        // setup the model attributes to support profile override editing
		mav.addObject("ownable", ti.getTarget());
		mav.addObject("privlege", Privilege.MODIFY_TARGET + ";" + Privilege.CREATE_TARGET);		
		mav.addObject("editMode", Boolean.toString(true));
        mav.addObject("profiles", profileManager.getAvailableProfiles(ti.getTarget().getProfile().getOid()));	
        
		Profile newProfile = profileManager.load(ti.getTarget().getProfile().getOid());
		
		// The user can set the profile if they have a high enough level
		// or if the profile is marked as the default.
		int userLevel = authorityManager.getProfileLevel();
		if( newProfile.getRequiredLevel() <= userLevel || 
		    newProfile.isDefaultProfile()) {
			ti.getTarget().setProfile(newProfile);
		}
		
		// now fetch the profile overrides and populate a ProfileCommand object (used to render the form)
		ProfileCommand profileCommand = new ProfileCommand();
		profileCommand.setProfileOid(ti.getTarget().getProfile().getOid());
		profileCommand.setFromOverrides(ti.getTarget().getProfileOverrides());
		
		mav.addObject(TargetInstanceSummaryCommand.CMD_PROFILE, profileCommand);
    }

    
    @Override
    protected ModelAndView showForm(HttpServletRequest request, 
    								HttpServletResponse response, 
    								BindException errors) throws Exception {
    	
		if( request.getParameter(TargetInstanceSummaryCommand.PARAM_OID) != null) {
			// fetch the ti
	    	Long tiOid = new Long(request.getParameter(TargetInstanceSummaryCommand.PARAM_OID));
			TargetInstance ti = targetInstanceManager.getTargetInstance(tiOid);
			
			// bind the ti to the session
			request.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
			
			// put the session into edit mode (so that edit mode it rendered if the user clicks through to the harvest results)
			request.getSession().setAttribute(TargetInstanceCommand.SESSION_MODE, true);
			request.getSession().setAttribute(Constants.GBL_SESS_EDIT_MODE, true); 
			
			// fetch the reference crawl
			Long refCrawlOid = ti.getTarget().getReferenceCrawlOid();
			TargetInstance rcti = null;
			if (refCrawlOid != null)
				rcti = targetInstanceManager.getTargetInstance(refCrawlOid);
			return buildModelAndView(request, ti, rcti, null, errors);
		}
		
        return null;
    }

    @Override
    protected ModelAndView processFormSubmission(	HttpServletRequest request, 
    												HttpServletResponse response, 
    												Object cmd, 
    												BindException error) throws Exception {
        
    	TargetInstanceSummaryCommand command = (TargetInstanceSummaryCommand) cmd;
        
    	if (log.isDebugEnabled()) {
            log.debug("process command " + command.getCmd());
        }
    	
    	// fetch the ti
    	TargetInstance ti = targetInstanceManager.getTargetInstance(command.getTargetInstanceOid());
    	
		// fetch the reference crawl
		Long refCrawlOid = ti.getTarget().getReferenceCrawlOid();
		TargetInstance rcti = null;
		if (refCrawlOid != null)
			rcti = targetInstanceManager.getTargetInstance(refCrawlOid);
        
		// resolve and execute the form action
        if (command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_RERUN_QA)) {
        	
        	processRunQa(request, response, command, error);
    		return buildModelAndView(request, ti, rcti, command, error);
    		
        } else if (command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_DENOTE_REF_CRAWL)) {
        	
        	processDenoteReferenceCrawl(request, response, command, error);
			return buildModelAndView(request, ti, rcti, command, error);
			
        } else if (command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_ARCHIVE)) {
			// redirect to the ArchiveController for processing
			StringBuilder queryString = new StringBuilder("redirect:/curator/archive/submit.html?");
			queryString.append("instanceID=");
			queryString.append(command.getTargetInstanceOid());
			return new ModelAndView(queryString.toString());
        	
        } else if (command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_ENDORSE)) {
        	
    		// set the ti to endorsed
    		processEndorse(request, response, command, error);
        	return buildModelAndView(request, ti, rcti, command, error);
        	
        } else if (command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_REJECT)) {
        	
        	processReject(request, response, command, error);
        	return buildModelAndView(request, ti, rcti, command, error);
        	
        } else if (command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_ADD_NOTE)) {
        	
        	if (log.isDebugEnabled()) {
        		log.debug("Processing add annotation.");
        	}
        	if (!error.hasErrors()) {
	        	Annotation annotation = new Annotation();
	        	annotation.setDate(new Date());
	        	annotation.setNote(command.getNote());
	        	annotation.setAlertable(command.isAlertable());
	        	annotation.setUser(AuthUtil.getRemoteUserObject());
	        	annotation.setObjectType(TargetInstance.class.getName());
	        	annotation.setObjectOid(command.getTargetInstanceOid());
	        	
	        	ti.addAnnotation(annotation);
	        	targetInstanceManager.save(ti);
        	}
        	return buildModelAndView(request, ti, rcti, command, error);
        	
        } else if (command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_SAVE_PROFILE)) {
        	
        	processSaveProfile(ti, request, response, command, error);
        	return buildModelAndView(request, ti, rcti, command, error);
        	
        } else if (command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_REFRESH)) {
        	
        	// we just refresh view (only performed for schedule management)
        	return buildModelAndView(request, ti, rcti, command, error);
        	
        } else if (command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_SAVE_SCHEDULE)) {

        	ModelAndView mav = buildModelAndView(request, ti, rcti, command, error);
        	processSaveSchedule(mav, ti, request, error, command);
        	return buildModelAndView(request, ti, rcti, command, error);
        	
        } else if (command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_RESET_SCHEDULE)) {
        	
        	command = null;
        	return buildModelAndView(request, ti, rcti, command, error);
        	
        } else if (command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_RUN_TARGET_NOW)) {
        	
        	Target target = targetManager.load(ti.getTarget().getOid(), true);
        	ModelAndView mav = buildModelAndView(request, ti, rcti, command, error);
        	// check if the target can be run
    		if(command != null && command.getRunTargetNow() && target.getState() != Target.STATE_APPROVED && target.getState() != Target.STATE_COMPLETED) {
                mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("target.error.notapproved", new Object[] {  }, Locale.getDefault()));
    		} else {
    			if(command != null)
    			{
    				if (command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_RUN_TARGET_NOW)) {
    					int beforeSaveState = target.getState();
					
    					target.setHarvestNow(true);
    					if (target.getState() == Target.STATE_COMPLETED) {
    						target.changeState(Target.STATE_APPROVED);
    					}
						targetManager.save(target, null);
    					int afterSaveState = target.getState();
    					
    					if( beforeSaveState == afterSaveState) {
    						if (target.isHarvestNow() && afterSaveState == Target.STATE_APPROVED) {
    							mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("target.saved.schedulednow", new Object[] { target.getName() }, Locale.getDefault()));
    						}
    						else {
    							mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("target.saved", new Object[] { target.getName() }, Locale.getDefault()));
    						}
    					}
    					else if(afterSaveState == Target.STATE_NOMINATED) {
    						mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("target.saved.nominated", new Object[] { target.getName() }, Locale.getDefault()));
    					}
    					else if(afterSaveState == Target.STATE_REINSTATED) {
    						mav.addObject(Constants.GBL_MESSAGES, messageSource.getMessage("target.saved.reinstated", new Object[] { target.getName() }, Locale.getDefault()));
    					}

    				}
    			}
    		}
        
        	return mav;
        }
        else {
            throw new WCTRuntimeException("Unknown command " + command.getCmd() + " recieved.");
        }
                
    }

    /**
     * Examines changes made to a <code>Schedule</code> for a <code>Target</code> by the user and saves any changes.<p/>
     * The changed <code>Schedule</code>'s end date is set to the current date, and a new <code>Schedule</code> is added to represent the changes.
     * @param ti the <code>TargetInstance</code> associated with the <code>Target</code> to which this <code>Schedule</code> belongs
     * @param request the <code>HttpServletRequest</code> that holds the <code>Schedule</code> changes
     * @param errors any errors
     * @throws Exception
     */
    private void processSaveSchedule(	ModelAndView mav,
    									TargetInstance ti,
										HttpServletRequest request, 
										BindException errors,
										TargetInstanceSummaryCommand command) throws Exception {
    	
    	// fetch the schedule changes submitted by the user
    	LinkedHashMap<Long, TargetSchedulesCommand> scheduleCommands = new LinkedHashMap<Long, TargetSchedulesCommand>();
    	
    	// fetch the original schedules for comparison (array avoid concurrent modifications)
   		// iterate over the schedules and determine if any changes have been made
		Iterator<Schedule> it = new ArrayIterator(ti.getTarget().getSchedules().toArray());

    	if (scheduleCommands != null) {
     		
    		while (it.hasNext()) {
    			Schedule schedule = it.next();

    			
    			// fetch the stored schedule modified by the ui
    			int scheduleIndex = command.getScheduleOid().indexOf(schedule.getOid().toString());
    			
    			if (scheduleIndex != -1) {
    				
    				TargetSchedulesCommand scheduleCommand = buildScheduleCommand(mav, command, scheduleIndex, errors);
    				
    				// validate the schedule
    				validateschedule(scheduleCommand, errors);
    				
    				// if the schedule has changed
    				if (!schedule.getCronPattern().equals(scheduleCommand.getCronExpression())) {
    				
	    				Schedule oldSchedule = businessObjectFactory.newSchedule(ti.getTarget());
	    				oldSchedule.setCronPattern(schedule.getCronPattern());
	    				oldSchedule.setStartDate(schedule.getStartDate());
	    				oldSchedule.setEndDate(schedule.getEndDate());
	    				oldSchedule.setScheduleType(schedule.getScheduleType());
	    				
	    				// remove the changed schedule
	    				ti.getTarget().removeSchedule(schedule);
	    				
	    				// save everything
	    		    	targetInstanceManager.save(ti);
	    		    	
	    				// set the end date of the schedule to the current date
	    				oldSchedule.setEndDate(new Date());
	    				
	    				// store the original schedule with the modified end date
	    				ti.getTarget().addSchedule(oldSchedule);
	    				
	    				// save everything
	    		    	targetInstanceManager.save(ti);
	    		    	
	    				// generate a new schedule to represent the changes posted from the ui
	    				Schedule newSchedule = businessObjectFactory.newSchedule(ti.getTarget());
	    				newSchedule.setCronPattern(scheduleCommand.getCronExpression());
	    				newSchedule.setStartDate(scheduleCommand.getStartDate());
	    				newSchedule.setEndDate(scheduleCommand.getEndDate());
	    				newSchedule.setScheduleType(scheduleCommand.getScheduleType());
	    				
	    				// store the new schedule
	    				ti.getTarget().addSchedule(newSchedule);
	    				
	    				// save everything
	    		    	targetInstanceManager.save(ti);
    				} else {
    					// the schedule start and end dates are stored as sql timestamps in the dao, whereas the 
    					// scheduleCommand stores its dates as Date()
    					// we therefore need to compare them using a date independent format (ie: milliseconds)
    					Long scheduleStartDate = 0L;
    					Long scheduleEndDate = 0L;
    					Long scheduleCommandStartDate = 0L;
    					Long scheduleCommandEndDate = 0L;

    					if (schedule.getStartDate() != null) 
    						scheduleStartDate = schedule.getStartDate().getTime();
    					if (schedule.getEndDate() != null) 
    						scheduleEndDate = schedule.getEndDate().getTime();
    					if (scheduleCommand.getStartDate() != null) 
    						scheduleCommandStartDate = scheduleCommand.getStartDate().getTime();
    					if (scheduleCommand.getEndDate() != null) 
    						scheduleCommandEndDate = scheduleCommand.getEndDate().getTime();
    					
    					// if the start or end date has changed
    					if (!scheduleStartDate.equals(scheduleCommandStartDate) || !scheduleEndDate.equals(scheduleCommandEndDate)) {
    	    				Schedule oldSchedule = businessObjectFactory.newSchedule(ti.getTarget());
    	    				oldSchedule.setCronPattern(schedule.getCronPattern());
    	    				oldSchedule.setStartDate(scheduleCommand.getStartDate());
    	    				oldSchedule.setEndDate(scheduleCommand.getEndDate());
    	    				oldSchedule.setScheduleType(scheduleCommand.getScheduleType());
    	    				
    	    				// remove the changed schedule
    	    				ti.getTarget().removeSchedule(schedule);
    	    				
    	    				// save everything
    	    		    	targetInstanceManager.save(ti);
    	    		    	
    	    				// store the original schedule with the modified dates
    	    				ti.getTarget().addSchedule(oldSchedule);
    	    				
    	    				// save everything
    	    		    	targetInstanceManager.save(ti);
    	    		    	
    					}
    				}
    			}
    		}
    	}
    	
    }
    
    private void validateschedule(TargetSchedulesCommand scheduleCommand, BindException errors) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "", getObjectArrayForLabel(TargetInstanceSummaryCommand.PARAM_START_DATE), "From Date is a required field");
		ValidatorUtil.validateStartBeforeOrEqualEndTime(errors, scheduleCommand.getStartDate(), scheduleCommand.getEndDate(), "time.range", getObjectArrayForTwoLabels(TargetInstanceSummaryCommand.PARAM_START_DATE, TargetInstanceSummaryCommand.PARAM_END_DATE), "The start time must be before the end time.");

    }
    
    private void processSaveProfile(	TargetInstance ti,
    									HttpServletRequest request, 
										HttpServletResponse response, 
										TargetInstanceSummaryCommand command, 
										BindException errors) throws Exception {
    	ProfileCommand profileCommand = new ProfileCommand();
    	profileCommand.setFromSummaryCommand(command);
    	profileCommand.updateOverrides(ti.getTarget().getProfileOverrides());
    	
    	// update the profile used by the target if it has changed
    	if (command.getProfileOid() != ti.getTarget().getProfile().getOid()) {
    		Profile newProfile = profileManager.load(profileCommand.getProfileOid());
    		ti.getTarget().setProfile(newProfile);
    	}
    	
    	// save everything
    	targetInstanceManager.save(ti);
    }

    private void processDenoteReferenceCrawl(	HttpServletRequest request, 
												HttpServletResponse response, 
												TargetInstanceSummaryCommand command, 
												BindException errors) throws Exception {
									       	
    	TargetInstance ti = targetInstanceManager.getTargetInstance(command.getTargetInstanceOid());
       	ti.getTarget().setReferenceCrawlOid(command.getReferenceCrawlOid());
    	// save everything
    	targetInstanceManager.save(ti);
    }
    
    private void processRunQa(	HttpServletRequest request, 
    							HttpServletResponse response, 
    							TargetInstanceSummaryCommand command, 
    							BindException errors) throws Exception {
    	
        TargetInstance ti = targetInstanceManager.getTargetInstance(command.getTargetInstanceOid());
		harvestCoordinator.runQaRecommentationService(ti);
    }

    /**
     * @param aTargetInstanceManager The targetInstanceManager to set.
     */
    public void setTargetInstanceManager(TargetInstanceManager aTargetInstanceManager) {
        targetInstanceManager = aTargetInstanceManager;
    }
    
    /**
     * process the endorse target instance action.
     * @see AbstractFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    private void processEndorse(HttpServletRequest aReq, HttpServletResponse aResp, TargetInstanceSummaryCommand aCmd, BindException aErrors) throws Exception {
    	// set the ti state and the hr states
    	TargetInstance ti = targetInstanceManager.getTargetInstance(aCmd.getTargetInstanceOid());
    	ti.setState(TargetInstance.STATE_ENDORSED);
    	
    	for (HarvestResult hr : ti.getHarvestResults()) {
			if (hr.getOid().equals(aCmd.getHarvestResultId())) {
				hr.setState(HarvestResult.STATE_ENDORSED);
			}
			else {
				if(hr.getState() != HarvestResult.STATE_REJECTED)
				{
					hr.setState(HarvestResult.STATE_REJECTED);
	        		harvestCoordinator.removeIndexes(hr);
				}
			}
			
			targetInstanceManager.save(hr);
		}
    	
    	targetInstanceManager.save(ti);
    	
    	aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
    }
    
    /**
     * process the reject target instance action.
     * @see AbstractFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    private void processReject(HttpServletRequest aReq, HttpServletResponse aResp, TargetInstanceSummaryCommand aCmd, BindException aErrors) throws Exception {
       	//	set the ti state and the hr states
    	TargetInstance ti = targetInstanceManager.getTargetInstance(aCmd.getTargetInstanceOid()); 
    	for (HarvestResult hr : ti.getHarvestResults()) {
			if (hr.getOid().equals(aCmd.getHarvestResultId())) {
				if(hr.getState() != HarvestResult.STATE_REJECTED)
				{
					hr.setState(HarvestResult.STATE_REJECTED);
					RejReason rejReason = agencyUserManager.getRejReasonByOid(aCmd.getRejReasonId());
					hr.setRejReason(rejReason);
	        		harvestCoordinator.removeIndexes(hr);
				}
				
				targetInstanceManager.save(hr);
			}
		}

    	boolean allRejected = true;
    	for (HarvestResult hr : ti.getHarvestResults()) {        		
			if ((HarvestResult.STATE_REJECTED != hr.getState()) &&
					(HarvestResult.STATE_ABORTED != hr.getState())) {
				allRejected = false;
				break;
			}
		}
    	
    	if (allRejected) {
    		ti.setState(TargetInstance.STATE_REJECTED);
    		ti.setArchivedTime(new Date());
    	}
    	
		targetInstanceManager.save(ti);
		aReq.getSession().setAttribute(TargetInstanceCommand.SESSION_TI, ti);
    }
    
    /**
     * Helper method to return the last harvest result oid for a specified harvest instance
     * @param targetInstanceOid the harvest instance oid that contains the list of harvest results
     * @return the last harvest result oid
     */
    private final Long getLastHarvestResultOid(Long targetInstanceOid) {
    	// get the harvest results for the specified target instance oid
		int results = targetInstanceManager.getHarvestResults(targetInstanceOid).size();
		// return the last harvest result
		return targetInstanceManager.getHarvestResults(targetInstanceOid).get(results-1).getOid();
    }
    

	/**
	 * Retrurn the Object array containing the specified label.
	 * @param aLabel the label to add to the array
	 * @return the object array
	 */
	protected Object[] getObjectArrayForLabel(String aLabel) {
		return new Object[] {new DefaultMessageSourceResolvable(new String[] {Constants.GBL_CMD_DATA + "." + aLabel})};
	}
    
	/**
	 * Return an Object array containing two specific labels.
	 * @param aLabel1 the first label
	 * @param aLabel2 the second label
	 * @return the Object array
	 */
	protected Object[] getObjectArrayForTwoLabels(String aLabel1, String aLabel2) {
		return new Object[] {new DefaultMessageSourceResolvable(new String[] {Constants.GBL_CMD_DATA + "." + aLabel1}), new DefaultMessageSourceResolvable(new String[] {Constants.GBL_CMD_DATA + "." + aLabel2})};
	}
	
    /**
     * @param harvestCoordinator The harvestCoordinator to set.
     */
    public void setHarvestCoordinator(HarvestCoordinator harvestCoordinator) {
        this.harvestCoordinator = harvestCoordinator;
    }

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * @param agencyUserManager the agencyUserManager to set
	 */
	public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
		this.agencyUserManager = agencyUserManager;
	}
	
	/**
	 * @param profileManager the profileManager to set
	 */
	public void setProfileManager(ProfileManager profileManager) {
		this.profileManager = profileManager;
	}
	
	/**
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}
	
	/**
	 * @param businessObjectFactory The businessObjectFactory to set.
	 */
	public void setBusinessObjectFactory(BusinessObjectFactory businessObjectFactory) {
		this.businessObjectFactory = businessObjectFactory;
	}
	
	/**
	 * @param targetManager The targetManager to set.
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setDigitalAssetStore(DigitalAssetStore digitalAssetStore) {
		this.digitalAssetStore = digitalAssetStore;
	}
	
}
