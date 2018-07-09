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

import java.text.ParseException;
import java.util.Date;
import java.util.Set;

import org.quartz.CronExpression;
import org.webcurator.core.util.DateUtils;
import org.webcurator.domain.UserOwnable;
import org.webcurator.domain.model.auth.User;

/**
 * A schedule determines how often a Target or TargetGroup will be harvested.
 * This Schedule object is dependent on the CronExpression class from Quartz
 *
 * Schedules may be custom schedules (dependent on a custom cron pattern) or 
 * may be picked from some predefined schedules. 
 * 
 * @see org.webcurator.domain.model.core.SchedulePattern
 * 
 * @author Brett Beaumont
 * @hibernate.class table="SCHEDULE" lazy="false"
 */
public class Schedule extends AbstractIdentityObject implements UserOwnable {
	
	/** Constant for a custom schedule */
	public static final int CUSTOM_SCHEDULE = 0;
	
	public static final int TYPE_DAILY = -1;
	public static final int TYPE_WEEKLY = -2;
	public static final int TYPE_MONTHLY = -3;
	public static final int TYPE_BI_MONTHLY = -4;
	public static final int TYPE_QUARTERLY = -5;
	public static final int TYPE_HALF_YEARLY = -6;
	public static final int TYPE_ANNUALLY = -7;
	
    /** The primary key. */
    private Long oid;
    /** The start date and time of the schedule. */
    private Date startDate;
    /** the end date of the schedule. */
    private Date endDate;
    /** The pattern for deciding how often to run the schedule. */
    private String cronPattern;
    /** the target the schedule is related to. */
    private AbstractTarget target;
    /** Set of related target instances */
    private Set<TargetInstance> targetInstances;
    /** Type Identifier for quick schedules. */
    private int scheduleType = CUSTOM_SCHEDULE; 
    /** The owner of the schedule */
    private User owner;
    /** The first date after the currently assigned period on which this schedule should run */
    private Date nextScheduleAfterPeriod;
    /**  */
    private Date lastProcessedDate;
    /** The first date after the currently assigned period on which this schedule should run */
    private boolean savedInThisSession = false;
    
    /**
     * Protected constructor - all schedules should be constructed by 
     * the BusinessObjectFactory.
     */
    protected Schedule() {}
    
    /**
     * Gets the database OID of the schedule.
     * @return Returns the oid.
     * @hibernate.id column="S_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="General" 
     */
    public Long getOid() {
        return oid;
    }
    
    /**
     * Sets the database oid of the schedule.
     * @param aOid The oid to set.
     */
    public void setOid(Long aOid) {
        this.oid = aOid;
    }
    
    /**
     * Gets the Cron Pattern string.
     * @return Returns the cronPattern.
     * @hibernate.property column="S_CRON" length="255" not-null="true"
     */
    public String getCronPattern() {
        return cronPattern;
    }
    
    /**
     * Gets the cron pattern string without the seconds component. This is 
     * useful for the user interface display.
     * @return The cron pattern string without the seconds component.
     */
    public String getCronPatternWithoutSeconds() {
    	int ix = cronPattern.indexOf(' ');
    	return cronPattern.substring(ix+1);
    }
    
    /**
     * Sets the cron pattern for this schedule.
     * @param aCronPattern The cronPattern to set.
     */
    public void setCronPattern(String aCronPattern) {
        this.cronPattern = aCronPattern;
    }
    
    /**
     * Returns the date at which scheduling will start.
     * @return Returns the start date.
     * @hibernate.property type="timestamp" 
     * @hibernate.column name="S_START" not-null="true" sql-type="TIMESTAMP(9)"
     */
    public Date getStartDate() {
        return startDate;
    }
    
    /**
     * Sets the date at which to start scheduling.
     * @param aStartDate The date to start scheduling.
     */
    public void setStartDate(Date aStartDate) {
        this.startDate = aStartDate;
    }
    
    /**
     * Gets the date to end scheduling. 
	 * @return Returns the end date of the schedule.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="S_END" sql-type="TIMESTAMP(9)"
	 */
	public Date getEndDate() {
		return endDate;
	}
	
	/**
	 * Sets the date to end scheduling.
	 * @param endDate The end date to set.
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Get the target that this schedule belongs to.
     * @return Returns the target.
     * @hibernate.many-to-one column="S_TARGET_ID" foreign-key="FK_S_TARGET_ID"
     */
    public AbstractTarget getTarget() {
        return target;
    }
    
    /**
     * Set the target that owns this schedule.
     * @param aTarget The target to set.
     */
    public void setTarget(AbstractTarget aTarget) {
        this.target = aTarget;
    }

    /**
     * Get the list of target instances associated with this schedule.
	 * @return Returns the targetInstances.
     * @hibernate.set cascade="save-update"
     * @hibernate.collection-key column="TI_SCHEDULE_ID"
     * @hibernate.collection-one-to-many class="org.webcurator.domain.model.core.TargetInstance"
  
	 */
	public Set<TargetInstance> getTargetInstances() {
		return targetInstances;
	}
	
	/**
	 * Set the targetinstances associated with this schedule.
	 * @param targetInstances The targetInstances to set.
	 */
	public void setTargetInstances(Set<TargetInstance> targetInstances) {
		this.targetInstances = targetInstances;
	}
	
	/**
     * Retrieves the next execution time based on the schedule. This
     * method delegates to getNextExecutionDate(Date) assuming the 
     * current date.
     * @return The next execution time.
     */
    public Date getNextExecutionDate() {
    	return getNextExecutionDate(DateUtils.latestDate(new Date(), getStartDate()));
    }
    
    
    /**
     * Retrieves the next execution time based on the schedule and
     * the supplied date.
     * @param after The date to get the next invocation after.
     * @return The next execution time.
     */
    public Date getNextExecutionDate(Date after) {
    	try {
    		
	    	CronExpression expression = new CronExpression(this.getCronPattern());
	    	Date next = expression.getNextValidTimeAfter(DateUtils.latestDate(after, new Date()));
	    	if(next == null) { 
	    		return null; 
	    	}
	    	else if(endDate != null && next.after(endDate)) {
	    		return null;
	    	}
	    	else {
	    		return next;
	    	}
    	}
    	catch(ParseException ex) {
        	System.out.println(" Encountered ParseException for cron expression: " + this.getCronPattern() + " in schedule: " + this.getOid());
    		return null;
    	}
    }
    

    
    /**
     * Check for equality. Two objects are equal if, and only if, 
     * their cronPatterns are the same, or if both objects have a 
     * null cronPattern.
     * @param o The other object.
     */
    public boolean equals(Object o) { 
    	return o instanceof Schedule && 
    	       ( cronPattern == null && ((Schedule)o).cronPattern == null ||
    	         ((Schedule)o).cronPattern.equals(cronPattern)
    	       );
    }
    

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
    	return cronPattern == null ? 0 : cronPattern.hashCode();
    }
    
	/**
	 * Gets the type of the schedule (custom or predefined).
	 * @return Returns the scheduleType.
     * @hibernate.property column="S_TYPE" not-null="true"
     * @see org.webcurator.domain.model.core.SchedulePattern
	 */
	public int getScheduleType() {
		return scheduleType;
	}
	
	/**
	 * Sets the type of the schedule (custom or predefined).
	 * @param scheduleType The scheduleType to set.
     * @see org.webcurator.domain.model.core.SchedulePattern
	 */
	public void setScheduleType(int scheduleType) {
		this.scheduleType = scheduleType;
	}
	
	/**
	 * Sets the owner of a schedule.
	 * @param anOwner The owner of the schedule.
	 */
	public void setOwningUser(User anOwner) {
		this.owner = anOwner;
	}
	
	/**
	 * Gets the user that owns the schedule.
	 * @return Returns the owner.
	 * @hibernate.many-to-one column="S_OWNER_OID" foreign-key="FK_S_OWNER_OID"
	 */	
	public User getOwningUser() {
		return owner;
	}

	/**
	 * Gets the next scheduled time after the "number of days to schedule" 
	 * setting. This is a management piece of information used to identify 
	 * when the scheduling next needs to consider this schedule. It is not
	 * for general use. 
	 * 
	 * @return Returns the nextScheduleAfterPeriod.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="S_NEXT_SCHEDULE_TIME" sql-type="TIMESTAMP(9)"
	 */
	public Date getNextScheduleAfterPeriod() {
		return nextScheduleAfterPeriod;
	}

	/**
	 * Sets the next scheduled time after the "number of days to schedule" 
	 * setting. This is a management piece of information used to identify 
	 * when the scheduling next needs to consider this schedule. It is not
	 * for general use. 
	 * @param nextScheduleAfterPeriod The nextScheduleAfterPeriod to set.
	 */
	public void setNextScheduleAfterPeriod(Date nextScheduleAfterPeriod) {
		this.nextScheduleAfterPeriod = nextScheduleAfterPeriod;
	}

	/**
	 * @return Returns the lastProcessedDate.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="S_LAST_PROCESSED_DATE" sql-type="TIMESTAMP(9)"
	 */
	public Date getLastProcessedDate() {
		return lastProcessedDate;
	}

	/**
	 * Sets the next scheduled time after the "number of days to schedule" 
	 * setting. This is a management piece of information used to identify 
	 * when the scheduling next needs to consider this schedule. It is not
	 * for general use. 
	 * @param nextScheduleAfterPeriod The nextScheduleAfterPeriod to set.
	 */
	public void setLastProcessedDate(Date lastProcessedDate) {
		this.lastProcessedDate = lastProcessedDate;
	}

	public boolean isSavedInThisSession() {
		return savedInThisSession;
	}

	/**
	 * Sets whether the schedule has been processed for generating
	 * Target Instances. Used when a Target Instance is saved via the 
	 * Annotations screen, to prevent a duplicate scheduling bug. 
	 */
	public void setSavedInThisSession(boolean savedInThisSession) {
		this.savedInThisSession = savedInThisSession;
	}
}
