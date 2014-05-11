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
import java.util.List;
import java.util.Set;

import org.webcurator.domain.model.core.TargetInstance;

/**
 * The command object for the target instance tabs.
 * @author nwaight
 */
public class TargetInstanceCommand {
	/** name of the HTTP session object containing the target instance. */
    public static final String SESSION_TI = "sessionTargetInstance";
    /** name of the HTTP session object containing the target instance. */
    public static final String SESSION_MODE = "sessionEditMode";
    /** name of the HTTP session object containing the target instance. */
    public static final String SESSION_TI_SEARCH_CRITERIA = "targetInstanceSearchCriteria";
    
    /** session objects holding data to enable return to a relevant Harvest History page. */
    public static final String SESSION_HH_TI_OID = "sessionHarvestHistoryTIOid";
    public static final String SESSION_HH_HR_OID = "sessionHarvestHistoryHROid";
    
    /** name of the http request parameter type. */
    public static final String REQ_TYPE = "type";
    /** name of the http request parameter showSubmittedMsg. */
    public static final String REQ_SHOW_SUBMITTED_MSG = "showSubmittedMsg";
    /** name of the queue type. */
    public static final String TYPE_QUEUE = "queue";
    /** name of the user type. */
    public static final String TYPE_USER = "user";
    /** name of the harvested type. */
    public static final String TYPE_HARVESTED = "harvested";
    /** name of the target type. */
    public static final String TYPE_TARGET = "target";
    /** name of the target type name parameter. */
    public static final String PARAM_TARGET_NAME = "targetname";
    
    /** name of the model object containing a list of target instances. */
    public static final String MDL_INSTANCES = "targetInstances";
    /** name of the model object containing a target instance. */
    public static final String MDL_INSTANCE = "instance";
    /** name of the model object containing the list of target instance states. */
    public static final String MDL_STATES = "states";
    /** name of the model object containing the map of active harvest agents. */
    public static final String MDL_AGENTS = "harvestAgents";
    /** name of the model object containing status of the harvester. */
    public static final String MDL_STATUS = "harvesterStatus";
    /** name of the model object containing the list of target instance next states. */
    public static final String MDL_NEXT_STATES = "nextStates";
    /** name of the model object containing the list of owners for this agency. */
    public static final String MDL_OWNERS = "owners";
    /** name of the model object containing the owner of this object. */
    public static final String MDL_OWNER = "owner";
    /** name of the model object containing the list of agencies. */
    public static final String MDL_AGENCIES = "agencies";
    /** name of the model object containing the list of priorities. */
    public static final String MDL_PRIORITIES = "priorities";
    /** name of the model object containing the list of log file names. */
    public static final String MDL_LOG_LIST = "logList";
    /** name of the model object containing the QA indicators */
    public static final String MDL_INDICATORS = "indicators";
    public static final String MDL_FUTURE_SCHEDULE_COUNT = "futureScheduleCount";
    /** name of the model object containing the rejection reasons */
    public static final String MDL_REASONS = "reasons";
    
    /** The name of the edit action. */
    public static final String ACTION_EDIT = "edit";
    /** The name of the save action. */
    public static final String ACTION_SAVE = "save";
    /** The name of the delete action. */
    public static final String ACTION_DELETE = "delete";
    /** The name of the view action. */
    public static final String ACTION_VIEW = "view";
    /** The name of the add note. */
    public static final String ACTION_ADD_NOTE = "addNote";
    /** The name of the modify note. */
    public static final String ACTION_MODIFY_NOTE = "modifyNote";
    /** The name of the modify note. */
    public static final String ACTION_DELETE_NOTE = "deleteNote";
    /** The name of the harvest now action. */
    public static final String ACTION_HARVEST = "harvest";
    /** The name of the endorse harvest action. */
    public static final String ACTION_ENDORSE = "endorse";
    /** The name of the unendorse harvest action. */
    public static final String ACTION_UNENDORSE = "unendorse";
    /** The name of the reject harvest action. */
    public static final String ACTION_REJECT = "reject";
    /** The name of the re-index harvest action. */
    public static final String ACTION_REINDEX = "reindex";
    /** The name of the archive harvest action. */
    public static final String ACTION_ARCHIVE = "archive";
    /** The name of the delist target instance action. */
    public static final String ACTION_DELIST = "delist";
    /** The name of the filter target instances action. */
    public static final String ACTION_FILTER = "filter";
    /** The name of the reset search criteria target instances action. */
    public static final String ACTION_RESET = "reset";
    /** The name of the show next page action. */
    public static final String ACTION_NEXT = "nextPage";
    /** The name of the show previous page action. */
    public static final String ACTION_PREV = "prevPage";
    /** The name of the show custom page action. */
    public static final String ACTION_SHOW_PAGE = "showPage";
    /** The name of the pause job action. */
    public static final String ACTION_PAUSE = "pause";
    /** The name of the resume job action. */
    public static final String ACTION_RESUME = "resume";    
    /** The name of the abort job action. */
    public static final String ACTION_ABORT = "abort";
    /** The name of the stop job action. */
    public static final String ACTION_STOP = "stop";
    
    /** Name of the multi-archive action for target instances **/
    public static final String ACTION_MULTI_ARCHIVE = "multi-archive";
    /** Name of the multi-endorse action for target instances **/
    public static final String ACTION_MULTI_ENDORSE = "multi-endorse";
    /** Name of the multi-delete action for target instances **/
    public static final String ACTION_MULTI_DELETE = "multi-delete";
    /** Name of the multi-reject action for target instances **/
    public static final String ACTION_MULTI_REJECT = "multi-reject";
    /** Name of the multi-delist action for target instances **/
    public static final String ACTION_MULTI_DELIST = "multi-delist";
    
	public static final String SORT_DEFAULT = "default";
	public static final String SORT_NAME_ASC = "nameasc";
	public static final String SORT_NAME_DESC = "namedesc";
	public static final String SORT_DATE_ASC = "dateasc";
	public static final String SORT_DATE_DESC = "datedesc";
	public static final String SORT_STATE_ASC = "stateasc";
	public static final String SORT_STATE_DESC = "statedesc";
	public static final String SORT_ELAPSEDTIME_ASC = "elapsedtimeasc";
	public static final String SORT_ELAPSEDTIME_DESC = "elapsedtimedesc";
	public static final String SORT_DATADOWNLOADED_ASC = "datadownloadedasc";
	public static final String SORT_DATADOWNLOADED_DESC = "datadownloadeddesc";
	public static final String SORT_URLSSUCCEEDED_ASC = "urlssucceededasc";
	public static final String SORT_URLSSUCCEEDED_DESC = "urlssucceededdesc";
	public static final String SORT_PERCENTAGEURLSFAILED_ASC = "percentageurlsfailedasc";
	public static final String SORT_PERCENTAGEURLSFAILED_DESC = "percentageurlsfaileddesc";
	public static final String SORT_CRAWLS_ASC = "crawlsasc";
	public static final String SORT_CRAWLS_DESC = "crawlsdesc";
	public static final String SORT_DATE_DESC_BY_TARGET_OID = "datedescbytargetoid";
	
    public static final String PARAM_OID = "targetInstanceId";
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
    public static final String PARAM_FLAG = "flag";
    public static final String PARAM_REASON = "reason";
    public static final String PARAM_REJREASON_ID = "rejReasonId";
    public static final String PARAM_HISTORY_TI_OID = "historyTIOid";
    public static final String PARAM_HISTORY_RESULT_ID = "historyResultId";
    public static final String PARAM_USE_AQA = "useAQA";
        
	private Long targetInstanceId;
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
    private Set<String> recommendationFilter;
    private String note;
    private int noteIndex;
    private boolean alertable;
    private String _tab_current_page;
    private Long harvestResultId;
    private String name;
    private Long searchOid;
    private String username;
    private boolean display;
    private String displayNote;
    private String displayChangeReason;
    private boolean flagged = false;
    private Long flagOid;
    private boolean queuePaused = false;
    private boolean nondisplayonly = false;
	private String sortorder; 
    private Long rejReasonId;
    /** Whether to use Automated Quality Assurance */
    private boolean useAQA = false;
    
    private String historyTIOid = "";
    private String historyResultId = "";
    private List<String> multiselect = null;

    public TargetInstanceCommand() {
        super();
		username = org.webcurator.core.util.AuthUtil.getRemoteUser();
    }
    
    public TargetInstanceCommand(TargetInstance aTargetInstance) {
        super();
        targetInstanceId = aTargetInstance.getOid();
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
    
	public Long getTargetInstanceId() {
		return targetInstanceId;
	}

	public void setTargetInstanceId(Long targetInstanceId) {
		this.targetInstanceId = targetInstanceId;
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

	public Long getSearchOid() {
		return searchOid;
	}

	public void setSearchOid(Long searchOid) {
		this.searchOid = searchOid;
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

	public Long getFlagOid() {
		return flagOid;
	}

	public void setFlagOid(Long flagOid) {
		this.flagOid = flagOid;
	}

	public Set<String> getRecommendationFilter() {
		return recommendationFilter;
	}

	public void setRecommendationFilter(Set<String> recommendationFilter) {
		this.recommendationFilter = recommendationFilter;
	}
}
