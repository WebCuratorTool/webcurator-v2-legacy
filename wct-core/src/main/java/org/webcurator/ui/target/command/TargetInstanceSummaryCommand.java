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
package org.webcurator.ui.target.command;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.webcurator.domain.model.core.TargetInstance;

/**
 * The command object for the target instance tabs.
 * @author nwaight
 */
public class TargetInstanceSummaryCommand {
	/** name of the HTTP session object containing the target instance. */
    public static final String SESSION_TI = "sessionTargetInstance";
    /** name of the HTTP session object containing the target instance. */
    public static final String SESSION_MODE = "sessionEditMode";
    /** name of the HTTP session object containing the target instance. */
    public static final String SESSION_TI_SEARCH_CRITERIA = "targetInstanceSearchCriteria";
    
    /** session objects holding data to enable return to a relevant Harvest History page. */
    public static final String SESSION_HH_TI_OID = "sessionHarvestHistoryTIOid";
    public static final String SESSION_HH_HR_OID = "sessionHarvestHistoryHROid";
    
    /** name of the user type. */
    public static final String TYPE_USER = "user";
    /** name of the harvested type. */
    public static final String TYPE_HARVESTED = "harvested";
    /** name of the target type. */
    public static final String TYPE_TARGET = "target";
    
    /** name of the model object containing a target instance. */
    public static final String MDL_INSTANCE = "instance";
    /** name of the model object containing the QA indicators */
    public static final String MDL_INDICATORS = "indicators";
    /** name of the model object containing the QA indicators for the reference crawl */
    public static final String MDL_RC_INDICATORS = "rcIndicators";
    /** name of the model object containing the seeds **/
    public static final String MDL_SEEDS = "seeds";
    /** name of the model object containing the log list **/
    public static final String MDL_LOGS = "logs";
    /** name of the model object containing the QA indicator history */
    public static final String MDL_INDICATOR_HISTORY = "indicatorHistory";
    /** name of the model object containing the list of target instance states. */
    public static final String MDL_STATES = "states";
    /** name of the model object containing the map of active harvest agents. */
    public static final String MDL_AGENTS = "harvestAgents";
    /** name of the model object containing status of the harvester. */
    public static final String MDL_STATUS = "harvesterStatus";
    /** name of the model object containing history of the harvester. */
    public static final String MDL_HISTORY = "history";
    /** name of the model object containing the list of target instance next states. */
    public static final String MDL_NEXT_STATES = "nextStates";
    /** name of the model object containing the list of owners for this agency. */
    public static final String MDL_OWNERS = "owners";
    /** name of the model object containing the owner of this object. */
    public static final String MDL_OWNER = "owner";
    /** name of the model object containing the list of agencies. */
    public static final String MDL_AGENCIES = "agencies";
    /** name of the model object containing the harvest results */
    public static final String MDL_RESULTS = "results";
    /** name of the model object containing the rejection reasons */
    public static final String MDL_REASONS = "reasons";
    /** name of the model object containing the schedules */
    public static final String MDL_SCHEDULES = "schedules";
    public static final String MDL_SCHEDULE_COMMANDS = "scheduleCommands";
    public static final String MDL_SCHEDULE_MONTH_OPTIONS = "scheduleMonthOptions";
    /** name of the model denoting that at least one schedule has been modified by the user **/
    public static final String MDL_SCHEDULE_HAS_CHANGED = "scheduleHasChanged";
    
    /** re-run the QA Analysis to update the Indicators **/
    public static final String ACTION_RERUN_QA = "runqa";
    /** denote a target instance as a reference crawl **/
    public static final String ACTION_DENOTE_REF_CRAWL = "refcrawl";
    /** The name of the edit action. */
    public static final String ACTION_EDIT = "edit";
    /** The name of the save action. */
    public static final String ACTION_SAVE = "save";
    /** The name of the save profile action. */
    public static final String ACTION_SAVE_PROFILE = "saveProfile";
    /** The name of the delete action. */
    public static final String ACTION_DELETE = "delete";
    /** The name of the view action. */
    public static final String ACTION_VIEW = "view";
    /** The name of the add note. */
    public static final String ACTION_ADD_NOTE = "addNote";
    /** The name of the harvest now action. */
    public static final String ACTION_HARVEST = "harvest";
    /** The name of the endorse harvest action. */
    public static final String ACTION_ENDORSE = "endorse";
    /** The name of the unendorse harvest action. */
    public static final String ACTION_UNENDORSE = "unendorse";
    /** The name of the reject harvest action. */
    public static final String ACTION_REJECT = "reject";
    /** The name of the archive harvest action. */
    public static final String ACTION_ARCHIVE = "archive";
    /** The name of the filter target instances action. */
    public static final String ACTION_FILTER = "filter";
    /** The name of the reset search criteria target instances action. */
    public static final String ACTION_RESET = "reset";
    /** The name of the stop job action. */
    public static final String ACTION_STOP = "stop";
    /** The name of the refresh form action. */
    public static final String ACTION_REFRESH = "refresh";
    /** the name of the save schedule action **/
    public static final String ACTION_SAVE_SCHEDULE = "saveSchedule";
    /** the name of the reset schedule action **/
    public static final String ACTION_RESET_SCHEDULE = "resetSchedule";
    /** the name of the run crawl now schedule action **/
    public static final String ACTION_RUN_TARGET_NOW = "runTargetNow";

    
    public static final String PARAM_OID = "targetInstanceOid";
    public static final String PARAM_REF_CRAWL_OID = "referenceCrawlOid";
    public static final String PARAM_CMD = "cmd";
    public static final String PARAM_TIME = "scheduledTime";
    public static final String PARAM_PRI = "priority";
    public static final String PARAM_STATE = "state";
    public static final String PARAM_BW = "bandwidthPercent";
    public static final String PARAM_AGENT = "agent";
    public static final String PARAM_FROM = "from";
    public static final String PARAM_TO = "to";
    public static final String PARAM_OWNER = "owner";
    public static final String PARAM_AGENCY = "agency";
    public static final String PARAM_PAGE = "pageNo";
	public static final String PARAM_PAGESIZE = "selectedPageSize";
    public static final String PARAM_NOTE = "note";
    public static final String PARAM_NOTE_INDEX = "noteIndex";
    public static final String PARAM_HR_ID = "harvestResultId";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_SEARCH_OID = "searchOid";
    public static final String PARAM_CURRENT_USER = "username";
    public static final String PARAM_DISPLAY = "display";
    public static final String PARAM_DISPLAY_NOTE = "displayNote";
    public static final String PARAM_DISPLAY_CHANGE_REASON = "displayChangeReason";
    public static final String PARAM_FLAGGED = "flagged";
    public static final String PARAM_REASON = "reason";
    public static final String PARAM_REJREASON_ID = "rejReasonId";
    public static final String PARAM_HISTORY_TI_OID = "historyTIOid";
    public static final String PARAM_HISTORY_RESULT_ID = "historyResultId";
    public static final String PARAM_USE_AQA = "useAQA";
    
    public static final String PARAM_MINUTES = "minutes";
    public static final String PARAM_HOURS = "hours";
    public static final String PARAM_DAYS_OF_WEEK = "daysOfWeek";
    public static final String PARAM_DAYS_OF_MONTH = "daysOfMonth";
    public static final String PARAM_MONTHS = "months";
    public static final String PARAM_YEARS = "years";
    
	/** The name of the parameter for Start Date **/
    public static final String PARAM_START_DATE = "startDate";
	/** The name of the parameter for End Date. */
    public static final String PARAM_END_DATE = "endDate";

    /** name of the profile command object **/
    public static final String CMD_PROFILE = "profileCommand";
    
    /** name of the schedule command object **/
    public static final String CMD_SCHEDULE = "scheduleCommand";
        
    private String cmd;
    private Date scheduledTime;
    private int priority;
    private String state;  
    private Integer bandwidthPercent;
    private String agent;
    private Date from;
    private Date to;
    private String owner;
    private String agency;
    private int pageNo;
	private String selectedPageSize;
    private Set<String> states;
    private String note;
    private int noteIndex;
    private boolean alertable;
    private String _tab_current_page;
    private Long harvestResultId;
    private String name;
    private Long targetInstanceOid;
    private Long referenceCrawlOid;
    private String username;
    private boolean display;
    private String displayNote;
    private String displayChangeReason;
    private boolean flagged = false;
    private boolean queuePaused = false;
    private boolean nondisplayonly = false;
	private String sortorder; 
    private Long rejReasonId;
	/** The OID of the profile to edit */
	private Long profileOid;
	private String robots;
	private boolean overrideRobots;
	
	private Long maxHours;
	private boolean overrideMaxHours;
	
	private Long maxBytesDownload;
	private boolean overrideMaxBytesDownload;
	
	private Long maxDocuments;
	private boolean overrideMaxDocuments;
	
	private Integer maxPathDepth;
	private boolean overrideMaxPathDepth;
	
	private Integer maxHops;
	private boolean overrideMaxHops;
	
	private String excludeFilters;
	private boolean overrideExcludeFilters;
	
	private String forceAcceptFilters;
	private boolean overrideForceAcceptFilters;
	
	/** the schedule identifier for the schedule being adjusted **/
	private List<Long> scheduleOid;
	/** Type Identifier for quick schedules. */
	private List<String> scheduleType;
    /** The start date and time of the schedule. */
    private List<String> startDate;
    /** the end date of the schedule. */
    private List<String> endDate;
    private List<String> cronPattern;
    /** The minutes component of the Cron Expression. **/
    private List<String> minutes;
    /** The hours component of the Cron Expression. **/
    private List<String> hours;
    /** The daysOfWeek component of the Cron Expression. **/
    private List<String> daysOfWeek;
    /** The daysOfMonth component of the Cron Expression. **/
    private List<String> daysOfMonth;
    /** The months component of the Cron Expression. **/
    private List<String> months;
    /** The years component of the Cron Expression. **/
    private List<String> years;

    /** specifies if a crawl should be run now **/
	private Boolean runTargetNow = false;
	
    /** Whether to use Automated Quality Assurance */
    private boolean useAQA = false;
    
    private String historyTIOid = "";
    private String historyResultId = "";
    private List<String> multiselect = null;

    public TargetInstanceSummaryCommand() {
        super();
		username = org.webcurator.core.util.AuthUtil.getRemoteUser();
    }
    
    public TargetInstanceSummaryCommand(TargetInstance aTargetInstance) {
        super();
        targetInstanceOid = aTargetInstance.getOid();
        scheduledTime = aTargetInstance.getScheduledTime();
        priority = aTargetInstance.getPriority();
        state = aTargetInstance.getState();
        owner = aTargetInstance.getOwner().getUsername();
        agency = aTargetInstance.getOwner().getAgency().getName();
        bandwidthPercent = aTargetInstance.getBandwidthPercent();
		username = org.webcurator.core.util.AuthUtil.getRemoteUser();
		display = aTargetInstance.getDisplay();
		displayNote = aTargetInstance.getDisplayNote();
		displayChangeReason = aTargetInstance.getDisplayChangeReason();
		flagged = aTargetInstance.getFlagged();
		useAQA = aTargetInstance.isUseAQA();
    }
    
    /**
     * @return Returns the cmd.
     */
    public String getCmd() {
        return cmd;
    }

    /**
     * @param cmd The cmd to set.
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * @return Returns the priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority The priority to set.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return Returns the scheduledTime.
     */
    public Date getScheduledTime() {
        return scheduledTime;
    }

    /**
     * @param scheduledTime The scheduledTime to set.
     */
    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    /**
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return Returns the bandwidthPercent.
     */
    public Integer getBandwidthPercent() {
        return bandwidthPercent;
    }

    /**
     * @param bandwidthPercent The bandwidthPercent to set.
     */
    public void setBandwidthPercent(Integer bandwidthPercent) {
        this.bandwidthPercent = bandwidthPercent;
    }

    /**
     * @return Returns the agent.
     */
    public String getAgent() {
        return agent;
    }

    /**
     * @param agent The agent to set.
     */
    public void setAgent(String agent) {
        this.agent = agent;
    }

    /**
     * @return Returns the from.
     */
    public Date getFrom() {
        return from;
    }

    /**
     * @param from The from to set.
     */
    public void setFrom(Date from) {
        this.from = from;
    }

    /**
     * @return Returns the owner.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner The owner to set.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return Returns the to.
     */
    public Date getTo() {
        return to;
    }

    /**
     * @param to The to to set.
     */
    public void setTo(Date to) {
        this.to = to;
    }

	/**
	 * @return the agency
	 */
	public String getAgency() {
		return agency;
	}

	/**
	 * @param agency the agency to set
	 */
	public void setAgency(String agency) {
		this.agency = agency;
	}

	/**
	 * @return the pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * @return the selectedPageSize
	 */
	public String getSelectedPageSize() {
		return selectedPageSize;
	}

	/**
	 * @param selectedPageSize the selectedPageSize to set
	 */
	public void setSelectedPageSize(String selectedPageSize) {
		this.selectedPageSize = selectedPageSize;
	}

	/**
	 * @return the states
	 */
	public Set<String> getStates() {
		return states;
	}

	/**
	 * @param states the states to set
	 */
	public void setStates(Set<String> states) {
		this.states = states;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * @return the noteIndex
	 */
	public int getNoteIndex() {
		return noteIndex;
	}

	/**
	 * @param noteIndex the noteIndex to set
	 */
	public void setNoteIndex(int noteIndex) {
		this.noteIndex = noteIndex;
	}
	
	/**
	 * @return Returns the alertable option.
	 */
	public boolean isAlertable() {
		return alertable;
	}

	/**
	 * @param alertable The alertable to set.
	 */
	public void setAlertable(boolean alertable) {
		this.alertable = alertable;
	}
	
	/**
	 * @return the _tab_current_page
	 */
	public String get_tab_current_page() {
		return _tab_current_page;
	}

	/**
	 * @param _tab_current_page the _tab_current_page to set
	 */
	public void set_tab_current_page(String _tab_current_page) {
		this._tab_current_page = _tab_current_page;
	}

	/**
	 * @return the harvestResultId
	 */
	public Long getHarvestResultId() {
		return harvestResultId;
	}

	/**
	 * @param harvestResultId the harvestResultId to set
	 */
	public void setHarvestResultId(Long harvestResultId) {
		this.harvestResultId = harvestResultId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public Long getTargetInstanceOid() {
		return targetInstanceOid;
	}

	public void setTargetInstanceOid(Long targetInstanceOid) {
		this.targetInstanceOid = targetInstanceOid;
	}
	
	public boolean getDisplay()
	{
		return display;
	}
	
	public void setDisplay(boolean display)
	{
		this.display = display;
	}
	
	public String getDisplayNote()
	{
		return displayNote;
	}
	
	public void setDisplayNote(String displayNote)
	{
		this.displayNote = displayNote;
	}
	
	public String getDisplayChangeReason()
	{
		return displayChangeReason;
	}
	
	public void setDisplayChangeReason(String displayChangeReason)
	{
		this.displayChangeReason = displayChangeReason;
	}
	
	public boolean getFlagged()
	{
		return flagged;
	}
	
	public void setFlagged(boolean flagged)
	{
		this.flagged = flagged;
	}
	
	public boolean getQueuePaused()
	{
		return queuePaused;
	}
	
	public void setQueuePaused(boolean queuePaused)
	{
		this.queuePaused = queuePaused;
	}
	
	public boolean getNondisplayonly() {
		return nondisplayonly;
	}
	public void setNondisplayonly(boolean nondisplayonly) {
		this.nondisplayonly = nondisplayonly;
	}

	/**
	 * @return Returns the sortorder.
	 */
	public String getSortorder() {
		return sortorder;
	}

	/**
	 * @param sortorder The sortorder to set.
	 */

	public void setSortorder(String sortorder) {
		this.sortorder = sortorder;
	}

	/**
	 * @return the rejReasonId
	 */
	public Long getRejReasonId() {
		return rejReasonId;
	}

	/**
	 * @param rejReasonId the rejReasonId to set
	 */
	public void setRejReasonId(Long rejReasonId) {
		this.rejReasonId = rejReasonId;
	}
	
	/**
	 * @return Returns the useAQA.
	 */
	public boolean isUseAQA() {
		return useAQA;
	}

	/**
	 * @param useAQA The useAQA to set.
	 */
	public void setUseAQA(boolean useAQA) {
		this.useAQA = useAQA;
	}

	/**
	 * @return Returns the historyTIOid.
	 */
	public String getHistoryTIOid() {
		return historyTIOid;
	}

	/**
	 * @param historyTIOid The historyTIOid to set.
	 */

	public void setHistoryTIOid(String historyTIOid) {
		this.historyTIOid = historyTIOid;
	}
	
	/**
	 * @return Returns the historyResultId.
	 */
	public String getHistoryResultId() {
		return historyResultId;
	}

	/**
	 * @param historyResultId The historyResultId to set.
	 */

	public void setHistoryResultId(String historyResultId) {
		this.historyResultId = historyResultId;
	}
	
	public List<String> getMultiselect() {
		return this.multiselect;
	}
	
	public void setMultiselect(List<String> multiselect) {
		this.multiselect = multiselect;
	}
	
	/**
	 * @return Returns the profileOid.
	 */
	public Long getProfileOid() {
		return profileOid;
	}

	/**
	 * @param profileOid The profileOid to set.
	 */
	public void setProfileOid(Long profileOid) {
		this.profileOid = profileOid;
	}
	/**
	 * @return Returns the excludeFilters.
	 */
	public String getExcludeFilters() {
		return excludeFilters;
	}

	/**
	 * @param excludeFilters The excludeFilters to set.
	 */
	public void setExcludeFilters(String excludeFilters) {
		this.excludeFilters = excludeFilters;
	}

	/**
	 * @return Returns the forceAcceptFilters.
	 */
	public String getForceAcceptFilters() {
		return forceAcceptFilters;
	}

	/**
	 * @param forceAcceptFilters The forceAcceptFilters to set.
	 */
	public void setForceAcceptFilters(String forceAcceptFilters) {
		this.forceAcceptFilters = forceAcceptFilters;
	}

	/**
	 * @return Returns the maxBytesDownload.
	 */
	public Long getMaxBytesDownload() {
		return maxBytesDownload;
	}

	/**
	 * @param maxBytesDownload The maxBytesDownload to set.
	 */
	public void setMaxBytesDownload(Long maxBytesDownload) {
		this.maxBytesDownload = maxBytesDownload;
	}

	/**
	 * @return Returns the maxDocuments.
	 */
	public Long getMaxDocuments() {
		return maxDocuments;
	}

	/**
	 * @param maxDocuments The maxDocuments to set.
	 */
	public void setMaxDocuments(Long maxDocuments) {
		this.maxDocuments = maxDocuments;
	}

	/**
	 * @return Returns the maxHops.
	 */
	public Integer getMaxHops() {
		return maxHops;
	}

	/**
	 * @param maxHops The maxHops to set.
	 */
	public void setMaxHops(Integer maxHops) {
		this.maxHops = maxHops;
	}

	/**
	 * @return Returns the maxHours.
	 */
	public Long getMaxHours() {
		return maxHours;
	}

	/**
	 * @param maxHours The maxHours to set.
	 */
	public void setMaxHours(Long maxHours) {
		this.maxHours = maxHours;
	}

	/**
	 * @return Returns the maxPathDepth.
	 */
	public Integer getMaxPathDepth() {
		return maxPathDepth;
	}

	/**
	 * @param maxPathDepth The maxPathDepth to set.
	 */
	public void setMaxPathDepth(Integer maxPathDepth) {
		this.maxPathDepth = maxPathDepth;
	}

	/**
	 * @return Returns the overrideExcludeFilters.
	 */
	public boolean isOverrideExcludeFilters() {
		return overrideExcludeFilters;
	}

	/**
	 * @param overrideExcludeFilters The overrideExcludeFilters to set.
	 */
	public void setOverrideExcludeFilters(boolean overrideExcludeFilters) {
		this.overrideExcludeFilters = overrideExcludeFilters;
	}

	/**
	 * @return Returns the overrideForceAcceptFilters.
	 */
	public boolean isOverrideForceAcceptFilters() {
		return overrideForceAcceptFilters;
	}

	/**
	 * @param overrideForceAcceptFilters The overrideForceAcceptFilters to set.
	 */
	public void setOverrideForceAcceptFilters(boolean overrideForceAcceptFilters) {
		this.overrideForceAcceptFilters = overrideForceAcceptFilters;
	}

	/**
	 * @return Returns the overrideMaxBytesDownload.
	 */
	public boolean isOverrideMaxBytesDownload() {
		return overrideMaxBytesDownload;
	}

	/**
	 * @param overrideMaxBytesDownload The overrideMaxBytesDownload to set.
	 */
	public void setOverrideMaxBytesDownload(boolean overrideMaxBytesDownload) {
		this.overrideMaxBytesDownload = overrideMaxBytesDownload;
	}

	/**
	 * @return Returns the overrideMaxDocuments.
	 */
	public boolean isOverrideMaxDocuments() {
		return overrideMaxDocuments;
	}

	/**
	 * @param overrideMaxDocuments The overrideMaxDocuments to set.
	 */
	public void setOverrideMaxDocuments(boolean overrideMaxDocuments) {
		this.overrideMaxDocuments = overrideMaxDocuments;
	}

	/**
	 * @return Returns the overrideMaxHops.
	 */
	public boolean isOverrideMaxHops() {
		return overrideMaxHops;
	}

	/**
	 * @param overrideMaxHops The overrideMaxHops to set.
	 */
	public void setOverrideMaxHops(boolean overrideMaxHops) {
		this.overrideMaxHops = overrideMaxHops;
	}

	/**
	 * @return Returns the overrideMaxHours.
	 */
	public boolean isOverrideMaxHours() {
		return overrideMaxHours;
	}

	/**
	 * @param overrideMaxHours The overrideMaxHours to set.
	 */
	public void setOverrideMaxHours(boolean overrideMaxHours) {
		this.overrideMaxHours = overrideMaxHours;
	}

	/**
	 * @return Returns the overrideMaxPathDepth.
	 */
	public boolean isOverrideMaxPathDepth() {
		return overrideMaxPathDepth;
	}

	/**
	 * @param overrideMaxPathDepth The overrideMaxPathDepth to set.
	 */
	public void setOverrideMaxPathDepth(boolean overrideMaxPathDepth) {
		this.overrideMaxPathDepth = overrideMaxPathDepth;
	}

	/**
	 * @return Returns the overrideRobots.
	 */
	public boolean isOverrideRobots() {
		return overrideRobots;
	}

	/**
	 * @param overrideRobots The overrideRobots to set.
	 */
	public void setOverrideRobots(boolean overrideRobots) {
		this.overrideRobots = overrideRobots;
	}

	/**
	 * @return Returns the robots.
	 */
	public String getRobots() {
		return robots;
	}

	/**
	 * @param robots The robots to set.
	 */
	public void setRobots(String robots) {
		this.robots = robots;
	}

	public List<String> getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(List<String> scheduleType) {
		this.scheduleType = scheduleType;
	}

	public List<String> getStartDate() {
		return startDate;
	}

	public void setStartDate(List<String> startDate) {
		this.startDate = startDate;
	}

	public List<String> getEndDate() {
		return endDate;
	}

	public void setEndDate(List<String> endDate) {
		this.endDate = endDate;
	}

	public List<Long> getScheduleOid() {
		return scheduleOid;
	}

	public void setScheduleOid(List<Long> scheduleOid) {
		this.scheduleOid = scheduleOid;
	}

	public List<String> getCronPattern() {
		return cronPattern;
	}

	public void setCronPattern(List<String> cronPattern) {
		this.cronPattern = cronPattern;
	}

	public List<String> getMinutes() {
		return minutes;
	}

	public void setMinutes(List<String> minutes) {
		this.minutes = minutes;
	}

	public List<String> getHours() {
		return hours;
	}

	public void setHours(List<String> hours) {
		this.hours = hours;
	}

	public List<String> getDaysOfWeek() {
		return daysOfWeek;
	}

	public void setDaysOfWeek(List<String> daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	public List<String> getDaysOfMonth() {
		return daysOfMonth;
	}

	public void setDaysOfMonth(List<String> daysOfMonth) {
		this.daysOfMonth = daysOfMonth;
	}

	public List<String> getMonths() {
		return months;
	}

	public void setMonths(List<String> months) {
		this.months = months;
	}

	public List<String> getYears() {
		return years;
	}

	public void setYears(List<String> years) {
		this.years = years;
	}

	public Boolean getRunTargetNow() {
		return runTargetNow;
	}

	public void setRunTargetNow(Boolean runTargetNow) {
		this.runTargetNow = runTargetNow;
	}

	public Long getReferenceCrawlOid() {
		return referenceCrawlOid;
	}

	public void setReferenceCrawlOid(Long referenceCrawlOid) {
		this.referenceCrawlOid = referenceCrawlOid;
	}

}
