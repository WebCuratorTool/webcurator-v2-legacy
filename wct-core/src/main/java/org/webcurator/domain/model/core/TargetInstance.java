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
package org.webcurator.domain.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.webcurator.core.notification.UserInTrayResource;
import org.webcurator.domain.UserOwnable;
import org.webcurator.domain.model.auth.User;

/**
 * A TargetInstance represents a particular harvest at a given date and 
 * time. It takes its initial details from a Target, but may be modified
 * to change its profile overrides, etc. 
 * 
 * 
 * @hibernate.class table="TARGET_INSTANCE" lazy="false"  
 * @hibernate.query name="org.webcurator.domain.model.core.TargetInstance.GET_LATEST_FOR_TARGET" query="select max(scheduledTime) from TargetInstance where target.oid=:targetOid and schedule.oid=:scheduleOid" 
 * @hibernate.query name="org.webcurator.domain.model.core.TargetInstance.getPurgeable" query="from TargetInstance ti where ti.purged = false and ti.archivedTime < :purgeTime and (ti.state = :archivedState or ti.state = :rejectedState)"
 * @hibernate.query name="org.webcurator.domain.model.core.TargetInstance.getPurgeableAborted" query="from TargetInstance ti where ti.purged = false and ti.actualStartTime < :purgeTime and ti.state = :abortedState"
 * @hibernate.query name="org.webcurator.domain.model.core.TargetInstance.get_harvest_history" query="select new org.webcurator.domain.model.dto.HarvestHistoryDTO(ti.oid, ti.actualStartTime, ti.state, ti.status.dataDownloaded, ti.status.urlsDownloaded, ti.status.urlsFailed, ti.status.elapsedTime, ti.status.averageKBs, ti.status.status) from TargetInstance ti where ti.target.oid=? order by ti.actualStartTime desc"
 **/
public class TargetInstance implements Annotatable, Overrideable, UserInTrayResource {
	
	/** The name of the query to retrieve the latest date for a TargetInstance related to a given Target and Schedule. */
	public static final String QUERY_GET_LATEST_FOR_TARGET = "org.webcurator.domain.model.core.TargetInstance.GET_LATEST_FOR_TARGET";	
	
	/** the name of the query to return a list of purgeable target instances. */
	public static final String QRY_GET_PURGEABLE_TIS = "org.webcurator.domain.model.core.TargetInstance.getPurgeable";

	/** the name of the query to return a list of purgeable aborted target instances. */
	public static final String QRY_GET_PURGEABLE_ABORTED_TIS = "org.webcurator.domain.model.core.TargetInstance.getPurgeableAborted";
	
	
	/** query parameter for the archived state. */
	public static final String QRY_PARAM_ARCHIVED_STATE = "archivedState";
	/** query parameter for the rejected state. */
	public static final String QRY_PARAM_REJECTED_STATE = "rejectedState";
	/** query parameter for the aborted state. */
	public static final String QRY_PARAM_ABORTED_STATE = "abortedState";

	/** the name of the purge time query parameter. */
	public static final String QRY_PARAM_PURGE_TIME = "purgeTime";
	/** The name of the query for getting the Harvest History */
	public static final String QRY_GET_HARVEST_HISTORY = "org.webcurator.domain.model.core.TargetInstance.get_harvest_history";

	/** Scheduled – The Target Instance is scheduled for harvesting. */
    public static final String STATE_SCHEDULED = "Scheduled";
    /** Queued – The Target Instances scheduled time has past but there is no capacity process the Harvest. */
    public static final String STATE_QUEUED = "Queued";
    /** Running – A Harvest Agent is running the harvest for the Target Instance. */
    public static final String STATE_RUNNING = "Running";
    /** Stopping – A Harvest Agent is stopping the harvest for the Target Instance. */
    public static final String STATE_STOPPING = "Stopping";
    /** Paused – The harvest for the Target Instance has been paused on the Harvest Agent and may be restarted after changes to the Target Instance. */    
    public static final String STATE_PAUSED = "Paused";
    /** Aborted – The harvest for the Target Instance has been aborted and will not be restarted.  Any harvest data has been deleted. */    
    public static final String STATE_ABORTED = "Aborted";
    /** Harvested – The Harvest Agent has completed the harvest for the Target Instance or the harvest has been manually stopped. */    
    public static final String STATE_HARVESTED = "Harvested";
    /** Rejected – The Harvest Result for the Target Instance has been Quality Reviewed and determined unsuitable for archiving. */    
    public static final String STATE_REJECTED = "Rejected";
    /** Endorsed – The Harvest Result for the Target Instance has been Quality Reviewed and determined suitable for archiving. */    
    public static final String STATE_ENDORSED = "Endorsed";
    /** Archived – The Harvest Result for the Target Instance has been submitted to the Digital Archive System. */    
    public static final String STATE_ARCHIVED = "Archived";
    /** Archiving - We have started the archiving, but not completed */
	public static final String STATE_ARCHIVING = "Archiving";
    /** value for a low priority target instance. */
	
    public static final int PRI_LOW = 1000;
    /** value for a normal priority target instance. */
    public static final int PRI_NRML = 100;
    /** value for a hi priority target instance. */
    public static final int PRI_HI = 0;
    /** value for a display note length. */
    public static final int MAX_DISPLAY_NOTE_LENGTH = 4000;
    /** value for a display change reason length. */
    public static final int MAX_DISPLAY_CHANGE_REASON_LENGTH = 1000;


    /** unique identifier. */
	private Long oid = -1L;
	/** list of harvest results. */
	private List<HarvestResult> harvestResults = new LinkedList<HarvestResult>();
	/** the target or group that this instance belongs to. */
    private AbstractTarget target;
    /** the schedule that this target instance belongs to. */
    private Schedule schedule;
    /** the scheduled time of the harvest. */ 
    private Date scheduledTime;
    /** the time the harvest actually started. */
    private Date actualStartTime;    
    /** the time the target instance was archived. */
    private Date archivedTime;    
    /** the priority of the target instance. */
    private int priority = PRI_NRML;
    /** The state of the target instance. */
    private String state = STATE_SCHEDULED; 
    /** the minimum percentage of the bandwidth to allocate this harvest. */
    private Integer bandwidthPercent;
    /** The last amount of bandwidth actually allocated. */
    private Long allocatedBandwidth;
    /** the status of the current or complete harvest. */
    private HarvesterStatus status;
    /** the owner of this target instance. */
    private User owner;
    /** the value for ordering the target instance on the queue and ti view. */
    private int displayOrder = 40;   
    /** the list of annotations for this target instance. */
    private List<Annotation> annotations = new LinkedList<Annotation>();
    /** the list of deleted annotations for this target instance. */
    private List<Annotation> deletedAnnotations = new LinkedList<Annotation>();
    /** the list of indicators for this target instance. */
    private List<Indicator> indicators = new LinkedList<Indicator>();
    /** the list of deleted indicators for this target instance. */
    private List<Indicator> deletedIndicators = new LinkedList<Indicator>();
    /** Flag to state if the annotations have been sorted */
    private boolean annotationsSorted = false;   
    /** Flag to state if the annotations contain any flagged as alertable, making the whole target instance alertable */
    private boolean alertable = false;        
    /** The version number of this object */
    private int version;
    /** The reference number from the archive. */
    private String referenceNumber;
    /** The server that performed this harvest. */
    private String harvestServer;
    /** The parts of the SIP generated at time of harvest. */
    private Map<String,String> sipParts = new HashMap<String, String>();
    /** The original seeds */
    private Set<String> originalSeeds = new HashSet<String>();
    /** The display status */
    private boolean display = true;
    /** The display note */
    private String displayNote = "";
    /** The display change reason */
    private String displayChangeReason = "";
    /** Is this target instance flagged*/
    private boolean flagged = false;
    /** The flag group (a coloured flag) **/
    private Flag flag = null;
    /** The QA recommendation derived from this target instance's indicators **/
    private String recommendation;
    /** profile for this target instance (if harvested)*/
    private Profile lockedProfile = null;
    
    /** The seed history **/
    private Set<SeedHistory> seedHistory = new HashSet<SeedHistory>();
    
    /** 
     * flag to indicate that the list of annotations has been loaded for this instance. 
     * This is a transient flag used in the view. 
     */
    private boolean annotationsSet = false;
    /** The profile overrides for this target instance. */
    private ProfileOverrides overrides;
    private String archiveIdentifier; 
    /** Flag to indicate that this instances digital assets have been purged from the store. */
    private boolean purged = false;
	
    /** Flag to indicate that this target instance is the first to be scheduled from its owning Target. */
    private boolean firstFromTarget = false;

    /** Use Automated Quality Assurance on Harvests derived from this Target Instance */
    private boolean useAQA = false;

	private boolean allowOptimize;
    
    /**
     * The job name used by the harvester for this target instance
     * @return the job name
     */
    public String getJobName() {
        return oid.toString();
    }
    
	/**
	 * Get the database OID of the TargetInstance.
	 * @return the primary key
     * @hibernate.id column="TI_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="General" 
	 */	
	public Long getOid() {
		return oid;
	}
	
	/**
	 * Set the database oid of the target instance.
	 * @param oid The new database oid.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}
	
	/**
	 * Get the list of harvest results (completed harvests) associated with 
	 * this target instance.
	 * @hibernate.list cascade="none" lazy="true"
	 * @hibernate.collection-key column="HR_TARGET_INSTANCE_ID"
	 * @hibernate.collection-index column="HR_INDEX"
	 * @hibernate.collection-one-to-many class="org.webcurator.domain.model.core.HarvestResult"
	 * @return The HarvestResults
	 */
	public List<HarvestResult> getHarvestResults() {
		return harvestResults;
	}
	
	/**
	 * Set the list of HarvestResults associated with this target instance.
	 * @param harvestResults
	 */
	public void setHarvestResults(List<HarvestResult> harvestResults) {
		this.harvestResults = harvestResults;
	}
	
	/**
	 * Fetch the <code>Indicator</code>s for this <code>TargetInstance</code> 
	 * @hibernate.list cascade="all" lazy="true"
	 * @hibernate.collection-key column="I_TI_OID"
	 * @hibernate.collection-index column="I_INDEX"
	 * @hibernate.collection-one-to-many class="org.webcurator.domain.model.core.Indicator"
	 * @return A <code>List</code> of <code>Indicator</code>s for the specified <code>TargetInstance</code> 
	 */
	public List<Indicator> getIndicators() {
		return indicators;
	}
	
	/**
	 * Sets the <code>Indicator</code>s for the <code>TargetInstance<code>
	 * @param indicators
	 */
	public void setIndicators(List<Indicator> indicators) {		
		this.indicators = indicators;
	}
	

	
	/**
	 * Fetch the HarvestResult whose harvestNumber is specified.
	 * @param harvestNumber the harvest number to fetch
	 * @return the HarvestResult, or null if not found
	 */
	public HarvestResult getHarvestResult(int harvestNumber)
	{
		if(harvestResults == null) return null;

		List<HarvestResult> results = getHarvestResults(); //Trigger hibernate fetch if necessary
		for(HarvestResult result:results) {
			if(harvestNumber == result.getHarvestNumber()) {
				return result;
			}
		}
		return null;
	}
	
    /**
     * Gets the schedule that created this target instance.
     * @return Returns the schedule.
     * @hibernate.many-to-one column="TI_SCHEDULE_ID" foreign-key="FK_TI_SCHEDULE_ID" 
     */
    public Schedule getSchedule() {
        return schedule;
    }
    
    /**
     * Sets the schedule that created this target instance.
     * @param aSchedule The schedule to set.
     */
    public void setSchedule(Schedule aSchedule) {
        this.schedule = aSchedule;
    }
    
    /**
     * Returns the AbstractTarget that this target instance was created by.
     * @return Returns the target.
     * @hibernate.many-to-one column="TI_TARGET_ID" foreign-key="FK_TI_TARGET_ID"
     */
    public AbstractTarget getTarget() {
        return target;
    }
    
    /**
     * Sets the AbstractTarget that created this instance.
     * @param aTarget The target to set.
     */
    public void setTarget(AbstractTarget aTarget) {
        this.target = aTarget;
    }
    
    /**
     * Gets the priority of the target instance.
     * @return Returns the priority.
     * @hibernate.property column="TI_PRIORITY" not-null="true" 
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * @return a map of valid priority numbers and readable strings
     */
    public TreeMap<Integer, String> getPriorities() {
    	TreeMap<Integer, String> p = new TreeMap<Integer, String>();
    	p.put(new Integer(PRI_LOW), "Low");
    	p.put(new Integer(PRI_NRML), "Normal");
    	p.put(new Integer(PRI_HI), "High");
    	
    	return p;
    }
    
    /**
     * Set the priority of the target instnace.
     * @param aPriority The priority to set.
     */
    public void setPriority(int aPriority) {
        this.priority = aPriority;
    }
    
    /**
     * Get the time at which the instance is scheduled to run. This may not be
     * the same as the actual start time due to delays or advance harvesting.
     * @return Returns the scheduledTime.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="TI_SCHEDULED_TIME" not-null="true" sql-type="TIMESTAMP(9)"   
     */
    public Date getScheduledTime() {
        return scheduledTime;
    }
    
    /**
     * Set the time at which the instance is scheduled to run.
     * @param aScheduledTime The scheduledTime to set.
     */
    public void setScheduledTime(Date aScheduledTime) {
        this.scheduledTime = aScheduledTime;
    }
    
    /**
     * Get the state of the Target Instance.
     * @return Returns the state.
     * @hibernate.property length="50" not-null="true" column="TI_STATE"
     */
    public String getState() {
        return state;
    }
    
    /**
     * Set the state of the Target Instance.
     * @param aState The state to set.
     */
    public void setState(String aState) {    	
        this.state = aState;
        
        HashMap<String, Integer> states = getOrderedStates();
        setDisplayOrder(states.get(state));
    }
    
    /**
     * Get the percentage of bandwidth that this instance should attempt to use.
     * @return Returns the bandwidthPercent.
     * @hibernate.property column="TI_BANDWIDTH_PERCENT"
     */
    public Integer getBandwidthPercent() {
        return bandwidthPercent;
    }
    
    /**
     * Set the percentage of bandwidth that this instance should attempt to use.
     * @param bandwidth The bandwidthPercent to set.
     */
    public void setBandwidthPercent(Integer bandwidth) {
        this.bandwidthPercent = bandwidth;
    }
    
    /**
     * Returns the bandwidth allocated to this instance.
     * @return Returns the allocatedBandwidth.
     * @hibernate.property column="TI_ALLOCATED_BANDWIDTH"
     */
    public Long getAllocatedBandwidth() {
        return allocatedBandwidth;
    }
    
    /**
     * Set the bandwidth allocated to this instance.
     * @param allocatedBandwidth The allocatedBandwidth to set.
     */
    public void setAllocatedBandwidth(Long allocatedBandwidth) {
        this.allocatedBandwidth = allocatedBandwidth;
    }
    
    /**
     * Get the time the harvest started.
     * @return Returns the actualStartTime.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="TI_START_TIME"
     */
    public Date getActualStartTime() {
        return actualStartTime;
    }
    
    /**
     * Set the time the harvest started.
     * @param actualStartTime The actualStartTime to set.
     */
    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }
    
    /**
     * Get the time the target instance was archived.
     * @return Returns the archivedTime.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="TI_ARCHIVED_TIME"
     */
    public Date getArchivedTime() {
        return archivedTime;
    }
    
    /**
     * Set the time the target instance was archived.
     * @param archivedTime The archivedTime to set.
     */
    public void setArchivedTime(Date archivedTime) {
        this.archivedTime = archivedTime;
    }
    
    /**
     * Get the status of the harvest.
     * @return Returns the status.
     * @hibernate.one-to-one cascade="all"
     */
    public HarvesterStatus getStatus() {
        return status;
    }
    
    /**
     * Set the status of the harvest.
     * @param status The status to set.
     */
    public void setStatus(HarvesterStatus status) {
        this.status = status;
    }      
    
    /**
     * Get a map of the states in their appropriate order.
     * @return A map of the states in their appropriate order.
     */
    public static HashMap<String, Integer> getOrderedStates() {
        HashMap<String, Integer> stateOrder = new HashMap<String, Integer>();
        stateOrder.put(STATE_ABORTED, new Integer(70));
        stateOrder.put(STATE_ARCHIVING, new Integer(89));
        stateOrder.put(STATE_ARCHIVED, new Integer(90));
        stateOrder.put(STATE_ENDORSED, new Integer(60));
        stateOrder.put(STATE_HARVESTED, new Integer(50));
        stateOrder.put(STATE_PAUSED, new Integer(20));
        stateOrder.put(STATE_QUEUED, new Integer(30));
        stateOrder.put(STATE_REJECTED, new Integer(80));
        stateOrder.put(STATE_RUNNING, new Integer(1));
        stateOrder.put(STATE_STOPPING, new Integer(10));
        stateOrder.put(STATE_SCHEDULED, new Integer(40));
        
        return stateOrder;
    }
    
    /** 
     * Return the list of possible next states for the current state.
     * @return the list of possible states
     */
    public List<String> getNextStates() {
        return getNextStates(state);
    }
    
    /** 
     * Return a list of states that may me set from the sepecifed state.
     * @param aCurrentState the current state
     * @return the list of possible states
     */
    public static List<String> getNextStates(String aCurrentState) {
        List<String> states = new ArrayList<String>();
        if (aCurrentState == null || aCurrentState.trim().equals("") || 
            aCurrentState.equals(STATE_ABORTED) ||
            aCurrentState.equals(STATE_ARCHIVED) ||
            aCurrentState.equals(STATE_REJECTED)) {
            
            if (aCurrentState != null && !aCurrentState.trim().equals("")) {
                states.add(aCurrentState);
            }
            
            return states;
        }
         
        states.add(aCurrentState);
        
        if (aCurrentState.equals(STATE_SCHEDULED)) {
            states.add(STATE_QUEUED);
            states.add(STATE_RUNNING);            
        }
        else if (aCurrentState.equals(STATE_QUEUED)) {
            states.add(STATE_RUNNING);
        }
        else if (aCurrentState.equals(STATE_RUNNING)) {
            states.add(STATE_PAUSED);
            states.add(STATE_HARVESTED);
        }
        else if (aCurrentState.equals(STATE_PAUSED)) {
            states.add(STATE_RUNNING);
            states.add(STATE_HARVESTED);
            states.add(STATE_ABORTED);
        }
        else if (aCurrentState.equals(STATE_HARVESTED)) {
            states.add(STATE_ENDORSED);
            states.add(STATE_REJECTED);
        }
        else if (aCurrentState.equals(STATE_ENDORSED)) {
            states.add(STATE_ARCHIVED);
        }
        
        return states;
    }

    /**
     * Internal comparator to allow for sorting the Target Instance 
     * objects by state and scheduled date and time.
     * @author nwaight
     */
    public static class TargetInstanceComparator implements Comparator {
        /** @see Comparator#compare(java.lang.Object, java.lang.Object). */
        public int compare(Object o1, Object o2) {
            if (o1 instanceof TargetInstance && o2 instanceof TargetInstance) {
                TargetInstance t1 = (TargetInstance) o1;
                TargetInstance t2 = (TargetInstance) o2;
                
                String state1 = t1.getState();
                String state2 = t2.getState();
                
                int result = compareStates(state1, state2);                
                if (result == 0) {
                    result = t1.scheduledTime.compareTo(t2.scheduledTime);
                }
                
                return result;
            }
            else {
                throw new ClassCastException("TargetInstanceComparator can only compare TargetInstance objects");
            }
        }
        
        /** 
         * Compare the two states provided using the order map to decide compare 
         * the values.
         * @param aState1 state string 1
         * @param aState2 state string 2
         * @return the comparsion result 1, 0 or -1
         */
        private int compareStates(String aState1, String aState2) {
            HashMap<String, Integer> stateOrder = getOrderedStates();
            if (aState1.equals(aState2)) {
                return 0;
            }
            
            Integer order1 = stateOrder.get(aState1);
            Integer order2 = stateOrder.get(aState2);
            
            return order1.compareTo(order2);
        }
    }

    /** @see UserOwnable#getOwningUser() .*/
	public User getOwningUser() {
		return owner;
	}
	/**
	 * @return the owner
	 * @hibernate.many-to-one column="TI_OWNER_ID" foreign-key="FK_TI_USER_ID"  
	 */
	public User getOwner() {
		return owner;
	}
	
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}
	/**
	 * @return the displayOrder
	 * @hibernate.property column="TI_DISPLAY_ORDER" 
	 */
	public int getDisplayOrder() {
		return displayOrder;
	}
	
	/**
	 * @param displayOrder the displayOrder to set
	 */
	private void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
		
	/** @see Annotatable#addAnnotation(Annotation). */
	public void addAnnotation(Annotation annotation) {
		annotations.add(annotation);
		annotationsSorted = false;
		calculateAlertable();
	}
	
	/** @see Annotatable#getAnnotation(int). */
	public Annotation getAnnotation(int index) {
		return annotations.get(index);	
	}
		
	/** @see Annotatable#deleteAnnotation(int). */
	public void deleteAnnotation(int index)
	{
		Annotation annotation = annotations.get(index); 
		if(annotation != null)
		{
			deletedAnnotations.add(annotation);
			annotations.remove(index);
		}
		calculateAlertable();
	}

	private void calculateAlertable() {
		alertable=false;
		for (Annotation ann: annotations) {
			if (ann.isAlertable()) {
				alertable=true;
				break;
			}
		}
	}
	
	/** @see Annotatable#getAnnotations(). */
	public List<Annotation> getAnnotations() {		
		return annotations;
	}
	
	/** @see Annotatable#getDeletedAnnotations(). */
	public List<Annotation> getDeletedAnnotations() {		
		return deletedAnnotations;
	}
	
	/** @see Annotatable#setAnnotations(List). */
	public void setAnnotations(List<Annotation> aAnnotations) {		
		annotations = aAnnotations;
		deletedAnnotations.clear();
		annotationsSet = true;
		annotationsSorted = false;
		calculateAlertable();
	}
	
	/** @see Annotatable#isAnnotationsSet() .*/
	public boolean isAnnotationsSet() {
		return annotationsSet;
	}
    
	/*(non-Javadoc)
	 * @see org.webcurator.domain.model.core.Annotatable#getSortedAnnotations()
	 */
	public List<Annotation> getSortedAnnotations()
	{
		if(!annotationsSorted)
		{
			sortAnnotations();
		}
		return getAnnotations();
	}
		
	/*(non-Javadoc)
	 * @see org.webcurator.domain.model.core.Annotatable#sortAnnotations()
	 */
	public void sortAnnotations()
	{
		Collections.sort(annotations);
		annotationsSorted = true;
	}
		
	/** 
	 * @see Overrideable#getOverrides().
	 */
	public ProfileOverrides getProfileOverrides() {		
		if (null == overrides) {
			return getTarget().getOverrides();
		}
		return overrides;
	}	

	/**
	 * Get the overrides for Hibernate ONLY. Application code should use the getProfileOverrides()
	 * method.
	 * KU 10-01-2008: because the TargetInstance does not "own" the profile overrides (they are notionally owned by the Target) there are problems when the 
	 * target instance is deleted (the override may still be referenced). I have therefore changed the cascade
	 * option for hibernate from "all" to "save-update"

	 * @hibernate.many-to-one column="TI_PROF_OVERRIDE_OID" cascade="save-update" class="org.webcurator.domain.model.core.ProfileOverrides" foreign-key="FK_TI_PROF_OVERRIDE_OID" 
	 */
	public ProfileOverrides getOverrides() {		
		return overrides;
	}
	
	/** 
	 * set the profile overrides for the target instance
	 * @param aOverrides the profile overrides
	 * @see Overrideable#getOverrides().
	 */
	public void setOverrides(ProfileOverrides aOverrides) {
		overrides = aOverrides;
	}
	
	/** @see Overrideable#getProfile().
	*/
	public Profile getProfile() {
		if(lockedProfile != null)
		{
			return lockedProfile;
		}
		else
		{
			return getTarget().getProfile();
		}
	}
	
	/** Hibernate only - get the locked profile.
	 * @hibernate.many-to-one column="TI_PROFILE_ID"
	*/
	public Profile getLockedProfile() {
		return lockedProfile;
	}
	
	/**
	 * @param profile profile to set
	 */
	public void setLockedProfile(Profile profile)
	{
		this.lockedProfile = profile;
	}

	/**
	 * Checks if the associated harvest results have been purged.
	 * @return true if the harvest results have been purged; otherwise false.
	 * @hibernate.property column="TI_PURGED" not-null="true"
	 */
	public boolean isPurged() {
		return purged;
	}

	/**
	 * Sets whether the associated harvest results have been purged.
	 * @param purged the purged to set
	 */
	public void setPurged(boolean purged) {
		this.purged = purged;
	}
	
    /* (non-Javadoc)
     * @see org.webcurator.core.notification.InTrayResource#getResourceName()
     */
    public String getResourceName() {
        return this.oid.toString();
    }

    /* (non-Javadoc)
     * @see org.webcurator.core.notification.InTrayResource#getResourceType()
     */
    public String getResourceType() {
        return TargetInstance.class.getName();
    }
    
    /**
     * gets the unique Identifier provided by the Archive system when this
     * TargetInstance was submitted
     * @return the Unique Archive Identifier
     * @hibernate.property column="TI_ARCHIVE_ID" length="40" unique="true" not-null="false"
     */
    public String getArchiveIdentifier() {
        return archiveIdentifier;
    }
 
    /**
     * Set the identifier provided by the Archive System.
     * @param archiveIdentifier The identifier provided by the Archive System.
     */
    public void setArchiveIdentifier(String archiveIdentifier) {
        this.archiveIdentifier = archiveIdentifier;
        this.referenceNumber = this.archiveIdentifier;
    }


	/**
	 * Hibernate version tracking for optimistic locking.
	 * @return the version
	 * @hibernate.version column="TI_VERSION"
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Hibernate version tracking for optimistic locking.
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Get the date by which items should be sorted.
	 * @return If the Target Instance has started (or later), then the start 
	 * 	       time; otherwise the scheduled time.
	 * @hibernate.property formula="(case when ti_start_time is null then ti_scheduled_time else ti_start_time end)"
	 */
	public Date getSortOrderDate() {
		return actualStartTime == null ? scheduledTime : actualStartTime;
	}
	
	/**
	 * Setter required for Hibernate.
	 * @param dt Ignored - required only for Hibernate.
	 */
	public void setSortOrderDate(Date dt) {
		// No need to store the date as it is calculated.
	}

	/**
	 * @return the referenceNumber
	 * @hibernate.property length="255" column="TI_REFERENCE"
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * 
	 * @return the server that performed this harvest.
	 * @hibernate.property length="255" column="TI_HARVEST_SERVER"
	 */
	public String getHarvestServer() {
		return harvestServer;
	}

	public void setHarvestServer(String harvestServer) {
		this.harvestServer = harvestServer;
	}
	
	
	
	/**
	 * @return the parts
	 * 
	 * @hibernate.map table="SIP_PART_ELEMENT" cascade="all" lazy="true"
	 * @hibernate.collection-element column="SPE_VALUE" type="text"
	 * @hibernate.collection-key column="SPE_TARGET_INSTANCE_OID"
	 * @hibernate.collection-index column="SPE_KEY" type="java.lang.String"
	 */
	public Map<String, String> getSipParts() {
		return sipParts;
	}
	
	/**
	 * @param parts the parts to set
	 */
	public void setSipParts(Map<String, String> parts) {
		this.sipParts = parts;
	}

	/**
	 * @return the originalSeeds
	 * @hibernate.set table="TARGET_INSTANCE_ORIG_SEED" cascade="all"
	 * @hibernate.collection-key column="TIOS_TI_OID"
	 * @hibernate.collection-element type="string" column="TIOS_SEED"
	 */
	public Set<String> getOriginalSeeds() {
		return originalSeeds;
	}

	/**
	 * @param originalSeeds the originalSeeds to set
	 */
	public void setOriginalSeeds(Set<String> originalSeeds) {
		this.originalSeeds = originalSeeds;
	}	
	
	public boolean equals(Object other) { 
		return other instanceof TargetInstance && ((TargetInstance)other).oid != null &&
			((TargetInstance)other).oid.equals(oid);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return oid == null ? 0 : oid.hashCode();
	}
	
	/**
	 * @return Returns the display target instance flag
	 * @hibernate.property column="TI_DISPLAY_TARGET_INSTANCE"
	 */
	public boolean getDisplay()
	{
		return display;
	}
	
	/**
	 * @param display display target instance flag
	 */
	public void setDisplay(boolean display)
	{
		this.display = display;
	}
	
    /**
     * Get the display note the Target Instance.
     * @return Returns the display note.
     * @hibernate.property length="4000" not-null="false" column="TI_DISPLAY_NOTE"
     */
	public String getDisplayNote()
	{
		return displayNote;
	}
	
	/**
	 * @param displayNote the note to set
	 */
	public void setDisplayNote(String displayNote)
	{
		this.displayNote = displayNote;
	}
	
    /**
     * Get the display change reason for the Target Instance.
     * @return Returns the display change reason.
     * @hibernate.property length="1000" not-null="false" column="TI_DISPLAY_CHG_REASON"
     */
	public String getDisplayChangeReason()
	{
		return displayChangeReason;
	}
	
	/**
	 * @param displayChangeReason the display change reason to set
	 */
	public void setDisplayChangeReason(String displayChangeReason)
	{
		this.displayChangeReason = displayChangeReason;
	}
	
	/**
	 * @return Returns the flagged flag
	 * @hibernate.property column="TI_FLAGGED"
	 */
	public boolean getFlagged()
	{
		return flagged;
	}
	
	/**
	 * @param flagged flagged flag
	 */
	public void setFlagged(boolean flagged)
	{
		this.flagged = flagged;
	}
	
	/**
	 * @return the Flag
	 * @hibernate.many-to-one column="TI_FLAG_OID" foreign-key="FK_F_OID" not-null="false"  
	 */
	public Flag getFlag() {
		return flag;
	}
	
	/**
	 * @param Flag the Flag to set
	 */
	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	/**
	 * @return Returns the useAQA.
     * @hibernate.property column="TI_USE_AQA" 
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
     * Return the Set of SeedHistory objects attached to this target instance.
	 * @return Returns the seed history.
     * @hibernate.set cascade="all" 
     * @hibernate.collection-key column="SH_TI_OID" 
     * @hibernate.collection-one-to-many class="org.webcurator.domain.model.core.SeedHistory"
	 */
	public Set<SeedHistory> getSeedHistory() {
		return seedHistory;
	}

	/**
	 * Set the seed history for this target instance.
	 * @param seedHistory The seed history to set.
	 */
	public void setSeedHistory(Set<SeedHistory> seedHistory) {
		this.seedHistory = seedHistory;
	}
	
	/**
	 * @return Returns the alertable boolean.
	 */
	public boolean getAlertable() {
		return alertable;
	}

	/**
	 * @return Returns the 'first from target' flag
	 * @hibernate.property column="TI_FIRST_FROM_TARGET"
	 */
	public boolean getFirstFromTarget()
	{
		return firstFromTarget;
	}
	
	/**
	 * @param firstFromTarget flag
	 */
	public void setFirstFromTarget(boolean firstFromTarget)
	{
		this.firstFromTarget = firstFromTarget;
	}

	/**
	 * @return Returns the QA recommendation derived from the target instance's indicators
	 * @hibernate.property column="TI_RECOMMENDATION"
	 */
	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	/**
	 * @return Flag to indicate if harvest optimization is permitted for this target instance
	 * @hibernate.property column="TI_ALLOW_OPTIMIZE"
	 */
	public boolean isAllowOptimize() {
		return allowOptimize;
	}

	public void setAllowOptimize(boolean allowOptimize) {
		this.allowOptimize = allowOptimize;
	}
}
