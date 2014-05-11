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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.webcurator.core.exceptions.WCTRuntimeException;

/**
 * The hibernate object for persisting bandwidth restrictions for the WCT.
 * @author nwaight
 * @hibernate.class table="BANDWIDTH_RESTRICTIONS" lazy="false"
 * @hibernate.query name="org.webcurator.domain.model.core.BandwidthRestriction.all" query="from org.webcurator.domain.model.core.BandwidthRestriction br order by br.dayOfWeek, br.startTime"
 * @hibernate.query name="org.webcurator.domain.model.core.BandwidthRestriction.dayTime" query="from org.webcurator.domain.model.core.BandwidthRestriction br where br.dayOfWeek = :dow and br.startTime <= :start and br.endTime > :end"
 */
public class BandwidthRestriction {
    /** name of the all bandwidth restrictions query. */
    public static final String QUERY_ALL = "org.webcurator.domain.model.core.BandwidthRestriction.all";
    /** name if the Day and Time bandwidth restrictions query. */
    public static final String QUERY_DAY_TIME = "org.webcurator.domain.model.core.BandwidthRestriction.dayTime";
    /** name if the Day and Time bandwidth restrictions query parameter day of week. */
    public static final String PARAM_DOW = "dow";
    /** name if the Day and Time bandwidth restrictions query parameter start time. */
    public static final String PARAM_START = "start";
    /** name if the Day and Time bandwidth restrictions query parameter end time. */
    public static final String PARAM_END = "end";
    /** constant value for the day of the week Monday. */
    public static final String DOW_MON = "MONDAY";
    /** constant value for the day of the week Tuesday. */
    public static final String DOW_TUES = "TUESDAY";
    /** constant value for the day of the week Wednesday. */
    public static final String DOW_WED = "WEDNESDAY";
    /** constant value for the day of the week Thursday. */
    public static final String DOW_THUR = "THURSDAY";
    /** constant value for the day of the week Friday. */
    public static final String DOW_FRI = "FRIDAY";
    /** constant value for the day of the week Saturday. */
    public static final String DOW_SAT = "SATURDAY";
    /** constant value for the day of the week Sunday. */
    public static final String DOW_SUN = "SUNDAY";
    /** constant array for days of the week. */
    public static final String[] DOW = {DOW_MON, DOW_TUES, DOW_WED, DOW_THUR, DOW_FRI, DOW_SAT, DOW_SUN};    
    /** date format for the time. */
    public static final SimpleDateFormat TIMEONLY_FORMAT = new SimpleDateFormat("HH:mm:ss");
    /** date format for the date and time. */
    public static final SimpleDateFormat FULLDATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    /** date format for fullDay. */
    public static final SimpleDateFormat FULLDAY_FORMAT = new SimpleDateFormat("EEEE", Locale.ENGLISH);
    /** The date component to use for all start and end times. */
    public static final String DEFAULT_DATE = "09/11/1972 ";
    
    /** The primary key. */
    private Long oid;
    /** the day of the week. */
    private String dayOfWeek;
    /** the start time of restriction. */ 
    private Date startTime;
    /** the end time of  the restriction. */
    private Date endTime;
    /** the bandwidth. */
    private long bandwidth;
    private boolean allowOptimize = true;
    
    /**
     * Get the OID of the object.
     * @return Returns the oid.
     * @hibernate.id column="BR_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="Bandwidth"
     */
    public Long getOid() {
        return oid;
    }
    
    /**
     * Set the OID of the object.
     * @param oid The oid to set.
     */
    public void setOid(Long oid) {
        this.oid = oid;
    }
    
    /**
     * Get the maximum bandwdith for this restriciton.
     * @return Returns the bandwidth.
     * @hibernate.property column="BR_BANDWIDTH" not-null="true"
     */
    public long getBandwidth() {
        return bandwidth;
    }
    
    /**
     * Set the bandwidth.
     * @param bandwidth The bandwidth to set.
     */
    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
    }
    /**
     * Get the day of the week that this bandwidth restrcition applies.
     * See DOW_xxx contants.
     * @return Returns the dayOfWeek.
     * @hibernate.property column="BR_DAY" not-null="true" length="9"
     */
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    /**
     * Set the day of the week that this bandwidth restrction applies.
     * @param dayOfWeek The dayOfWeek to set.
     */
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    /**
     * Get the end time of the bandwidth restrction.
     * @return Returns the endTime.
     * @hibernate.property column="BR_END_TIME" type="timestamp" not-null="true"
     */
    public Date getEndTime() {
        return endTime;
    }
    
    /**
     * Set the end time for this bandwidth restriction.
     * @param aEndTime The endTime to set.
     */
    public void setEndTime(Date aEndTime) {
        try {
        	// Make sure that the date is always 9/11/1972 so that the time 
        	// components can be compared without taking the date component
        	// into consideration (since dates are not important for the 
        	// bandwidth restrictions; only times).
            String time = TIMEONLY_FORMAT.format(aEndTime);
            Date date = FULLDATE_FORMAT.parse(DEFAULT_DATE + time);            
            this.endTime = date;
        }
        catch (ParseException e) {
            throw new WCTRuntimeException("Failed to set end time " + e.getMessage(), e);
        }
    }    
    
    /**
     * toString implementation.
     * @return A String representation of the Bandwidth Restriction.
     */
    public String toString() {
    	StringBuffer buff = new StringBuffer();
    	buff.append(dayOfWeek);
    	buff.append(" - ");
    	buff.append(TIMEONLY_FORMAT.format(startTime));
    	buff.append(" to ");
    	buff.append(TIMEONLY_FORMAT.format(endTime));
    	buff.append(" - ");
    	buff.append(bandwidth);
    	buff.append("KBps");
    	
    	return buff.toString();
    }
    
    /**
     * Get the start time for this bandwidth restriction.
     * @return Returns the startTime.
     * @hibernate.property column="BR_START_TIME" type="timestamp" not-null="true"
     */
    public Date getStartTime() {
        return startTime;
    }
    
    /**
     * Set the start time for this bandwidth restriction.
     * @param aStartTime The startTime to set.
     */
    public void setStartTime(Date aStartTime) {
        try {
        	// Make sure that the date is always 9/11/1972 so that the time 
        	// components can be compared without taking the date component
        	// into consideration (since dates are not important for the 
        	// bandwidth restrictions; only times).        	
            String time = TIMEONLY_FORMAT.format(aStartTime);
            Date date = FULLDATE_FORMAT.parse(DEFAULT_DATE + time);            
            this.startTime = date;
        }
        catch (ParseException e) {
            throw new WCTRuntimeException("Failed to set start time " + e.getMessage(), e);
        }
    }
    
    /**
     * Get the percentage of the day that this bandwidth applies to. This is 
     * used for the user interface.
     * @return Returns the dayPercentage.
     */
    public int getDayPercentage() {
        if (null == endTime || null == startTime) {
            return 0;
        }
        
        Double start = new Double(startTime.getTime());
        Double end = new Double(endTime.getTime());
        
        Double p = ((end - start) / 86400000) * 100; 
        return p.intValue();
    }    
    
    /**
     * Returns the start of the day.
     * @return the default days start time
     */
    public static Date getDefaultStartTime() {
        try {
            return FULLDATE_FORMAT.parse(DEFAULT_DATE + "00:00:00");
        }
        catch (ParseException e) {
            throw new WCTRuntimeException("Failed to get default start time " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns the end of the day.
     * @return the default days end time.
     */
    public static Date getDefaultEndTime() {
        try {
            return FULLDATE_FORMAT.parse(DEFAULT_DATE + "23:59:59");
        }
        catch (ParseException e) {
            throw new WCTRuntimeException("Failed to get default end time " + e.getMessage(), e);
        }
    }

    /**
     * Flag to indicate whether harvest optimization is permitted during this bandwidth
     * restriction period
     * @return true if harvest optimization is allowed, false otherwise.
     * @hibernate.property column="BR_OPTIMIZATION_ALLOWED"
     */
	public boolean isAllowOptimize() {
		return allowOptimize;
	}

	public void setAllowOptimize(boolean allowOptimize) {
		this.allowOptimize = allowOptimize;
	}


}
