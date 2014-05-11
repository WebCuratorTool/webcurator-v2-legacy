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
package org.webcurator.domain.model.report;

import java.util.Date;


/**
 * The AbstractTargetScheduleView class is used to provide
 * fast lookups for the TargetGroupSchedulesReport report class.
 * 
 * @author oakleigh_sk
 * @hibernate.class table="ABSTRACT_TARGET_SCHEDULE_VIEW" lazy="true" readonly="true"
 * @hibernate.query name="org.webcurator.domain.model.report.AbstractTargetScheduleView.getAllByUserByAgencyByType" query="SELECT t FROM AbstractTargetScheduleView t WHERE (t.state = 5 OR t.state = 9) AND ( ?='All users' OR ( ? !='All users' AND ? = t.ownerName ) ) AND ( ?='All agencies' OR ( ?!='All agencies' AND ? = t.agencyName) ) AND ( ?='All target types' OR ( ?!='All target types' AND ? = t.objectTypeDesc) )"
 * @hibernate.query name="org.webcurator.domain.model.report.AbstractTargetScheduleView.getSummaryStatsByAgency" query="SELECT t.agencyName, CASE t.scheduleType WHEN 1 THEN 'Mondays at 9:00pm' WHEN 0 THEN 'Custom' WHEN -1 THEN 'Daily' WHEN -2 THEN 'Weekly' WHEN -3 THEN 'Monthly' WHEN -4 THEN 'Bi-Monthly' WHEN -5 THEN 'Quarterly' WHEN -6 THEN 'Half-Yearly' WHEN -7 THEN 'Annually' END as scheduleDesc, COUNT(t.scheduleType) FROM AbstractTargetScheduleView t WHERE (t.state = 5 OR t.state = 9) AND ( ?='All agencies' OR ( ?!='All agencies' AND ? = t.agencyName) ) GROUP BY t.agencyName, t.scheduleType ORDER BY t.agencyName"
*/
public class AbstractTargetScheduleView {
	
	/** Query identifier for listing all records */
	public static final String QRY_GET_ALL_BY_USER_BY_AGENCY_BY_TYPE = "org.webcurator.domain.model.report.AbstractTargetScheduleView.getAllByUserByAgencyByType";
	public static final String QRY_GET_SUMMARY_STATS_BY_AGENCY = "org.webcurator.domain.model.report.AbstractTargetScheduleView.getSummaryStatsByAgency";
	
	/** The composite primary key. The abstract_target oid and the schedule oid strung together with a comma */
	private String theKey;

	///** The database OID of the record */
    //private Long oid;

    /** 
     * Identifies whether this is a Target or Group.
     */
    private String objectTypeDesc;

    /** The target (or group) name. */
    private String name;

    /** The state of the target (or group) */
    private int state; 
    
    /** The target (or group) owner name. */
    private String ownerName;

    /** The target (or group) owning agency name. */
    private String agencyName;

	/** The oid of the schedule record. */
	private Long scheduleOid;

	/** The schedule start date*/
	private Date scheduleStartDate;

	/** The schedule end date*/
	private Date scheduleEndDate;

    /** Type Identifier for schedules. */
    private int scheduleType; 
	
	/** The schedule cron pattern*/
	private String scheduleCronPattern;

	
	/**
	 * constructor for Hibernate.
	 */
	
	protected AbstractTargetScheduleView() { }

	/**
	 * Standard constructor for WCT usage.
	 * @param aUrlPattern The UrlPattern.
	 * @param aPermission The CutdownPermission.
	//public AbstractTargetScheduleView(UrlPattern aUrlPattern, CutdownPermission aPermission) {
	//	urlPattern = aUrlPattern;
	//	permission = aPermission;
	//	
	//	domain = HierarchicalPermissionMappingStrategy.calculateDomain(aUrlPattern.getPattern());
	}
	 */

    /**
     * Get the primary key of the AbstractTargetScheduleView record.
     * @return Returns the key.
     * @hibernate.id column="THEKEY" generator-class="native"
     */
	public String getTheKey() {
        return theKey;
    }

    /**
     * Hibernate method to set the the key.
     * @param aKey The key to set.
     */
	public void setTheKey(String aKey) {
		theKey = aKey;
    }

    /*
     * Get the OID of the AbstractTargetScheduleView record.
     * @return Returns the oid.
     * @hibernate.column unique="false"
     * @hibernate.id column="AT_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="General" 
     */
    /*
	public Long getOid() {
        return oid;
    }
    */

    /*
     * Hibernate method to set the OID.
     * @param aOid The oid to set.
     */
    /*
	public void setOid(Long aOid) {
        this.oid = aOid;
    }
    */	

	/**
	 * Returns the object type description ('Target' or 'Group').
	 * @return The object type description.
	 * @hibernate.property column="AT_OBJECT_TYPE_DESC"
	 */
	public String getObjectTypeDesc() {
		return objectTypeDesc;
	}
	
	/**
	 * setObjectTypeDesc is required for Hibernate. It is not used
	 * elsewhere.
	 * @param type The object type.
	 */
	@SuppressWarnings("unused")
	private void setObjectTypeDesc(String aObjectTypeDesc) {
		objectTypeDesc = aObjectTypeDesc;
	}

    /**
     * Returns the name of the AbstractTarget.
     * @return the name of the AbstractTarget.
     * @hibernate.property column="AT_NAME" length="255" unique="true"
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the abstract target.
     * @param aName The name to set.
     */
    public void setName(String aName) {
    	name = aName;
    }    

	/**
	 * Returns the state (STATE_APPROVED, STATE_REJECTED etc). This can be used
	 * instead of instanceof, which is useful if the object isn't fully 
	 * initialised by Hibernate.
	 * @return STATE_APPROVED, STATE_REJECTED etc
	 * @hibernate.property column="AT_STATE"
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * setState is required for Hibernate. It is not used
	 * elsewhere.
	 * @param state The state.
	 */
	@SuppressWarnings("unused")
	private void setState(int aState) {
		state = aState;
	}
    
    /**
     * Returns the name of the owner of the AbstractTarget.
     * @return the name of the owner of the AbstractTarget.
     * @hibernate.property column="USR_USERNAME" length="80"
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Sets the name of the owner of the abstract target.
     * @param aOwnerName The owner name to set.
     */
    public void setOwnerName(String aOwnerName) {
    	ownerName = aOwnerName;
    }    

    /**
     * Returns the name of the agency of the owner of the AbstractTarget.
     * @return the name of the agency of the owner of the AbstractTarget.
     * @hibernate.property column="AGC_NAME" length="80"
     */
    public String getAgencyName() {
        return agencyName;
    }

    /**
     * Sets the name of the owner of the abstract target.
     * @param aName The name to set.
     */
    public void setAgencyName(String aName) {
    	agencyName = aName;
    }    

	/**
	 * Returns the schedule oid.
	 * @return the schedule oid.
	 * @hibernate.property column="S_OID"
	 */
	public Long getScheduleOid() {
		return scheduleOid;
	}
	
	/**
	 * setScheduleOid is required for Hibernate.
	 * It is not used elsewhere.
	 * @param aScheduleOid The schedule oid.
	 */
	@SuppressWarnings("unused")
	private void setScheduleOid(Long aScheduleOid) {
		scheduleOid = aScheduleOid;
	}

    
    /**
     * Returns the date at which scheduling will start.
     * @return Returns the start date.
     * @hibernate.property type="timestamp" 
     * @hibernate.column name="S_START" not-null="true" sql-type="TIMESTAMP(9)"
     */
    public Date getScheduleStartDate() {
        return scheduleStartDate;
    }
    
    /**
     * Sets the date at which to start scheduling.
     * @param aStartDate The date to start scheduling.
     */
    public void setScheduleStartDate(Date aStartDate) {
        this.scheduleStartDate = aStartDate;
    }
    
    /**
     * Gets the date to end scheduling. 
	 * @return Returns the end date of the schedule.
     * @hibernate.property type="timestamp"
     * @hibernate.column name="S_END" sql-type="TIMESTAMP(9)"
	 */
	public Date getScheduleEndDate() {
		return scheduleEndDate;
	}
	
	/**
	 * Sets the date to end scheduling.
	 * @param endDate The end date to set.
	 */
	public void setScheduleEndDate(Date endDate) {
		this.scheduleEndDate = endDate;
	}

	/**
	 * Returns the schedule type (CUSTOM_SCHEDULE, TYPE_DAILY, etc).
	 * @return the schedule type (CUSTOM_SCHEDULE, TYPE_DAILY, etc) a positive or negative integer.
	 * @hibernate.property column="S_TYPE"
	 */
	public int getScheduleType() {
		return scheduleType;
	}
	
	/**
	 * setScheduleType is required for Hibernate.
	 * It is not used elsewhere.
	 * @param aScheduleType The schedule type.
	 */
	@SuppressWarnings("unused")
	private void setScheduleType(int aScheduleType) {
		scheduleType = aScheduleType;
	}

	/**
	 * Returns the schedules cron pattern
	 * @return Returns the schedules cron pattern.
	 * @hibernate.property column="S_CRON" length="255"
	 */
	public String getScheduleCronPattern() {
		return scheduleCronPattern;
	}

	/**
	 * Sets the schedules cron pattern.
	 * Private as this should only be called from Hibernate.
	 * @param aScheduleCronPattern The scheduleCronPattern to set. 
	 */
	@SuppressWarnings("unused")
	private void setScheduleCronPattern(String aScheduleCronPattern) {
		scheduleCronPattern = aScheduleCronPattern;
	}
	
}
