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

import java.util.Date;

import org.webcurator.core.util.AuthUtil;

/**
 * A SchedulePattern defines a simple pattern that can be
 * used rather than using the Cron Expressions.
 * 
 * @author bbeaumont
 */
public class SchedulePattern {
	/** The unique ID of the pattern. */
	private int scheduleType;
	/** The description of the pattern. */
	private String description;
	/** The cron pattern. */
	private String cronPattern;
	
	/**
	 * Get the cron pattern.
	 * @return Returns the cronPattern.
	 */
	public String getCronPattern() {
		return cronPattern;
	}
	
	/**
	 * Set the cron pattern.
	 * @param cronPattern The cronPattern to set.
	 */
	public void setCronPattern(String cronPattern) {
		this.cronPattern = cronPattern;
	}
	
	/**
	 * Get the description of the schedule pattern (i.e. 9am on Mondays).
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the description of the schedule pattern.
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Get the schedule type, which is a unique identifier for the predefined
	 * schedule. The type number must be greater than zero.
	 * @return Returns the scheduleType.
	 */
	public int getScheduleType() {
		return scheduleType;
	}
	
	/**
	 * Set the schedule type.
	 * @see #getScheduleType()
	 * @param scheduleType The scheduleType to set.
	 */
	public void setScheduleType(int scheduleType) {
		this.scheduleType = scheduleType;
	}
	
	/**
	 * Build a new schedule from the pattern.
	 * @param startDate The start date for the schedule.
	 * @param endDate The end date of the schedule.
	 * @return A real schedule object.
	 */
	public Schedule makeSchedule(BusinessObjectFactory factory, AbstractTarget parent, Date startDate, Date endDate) {
		Schedule s = factory.newSchedule(parent);
		s.setScheduleType(scheduleType);
		s.setCronPattern(cronPattern);
		s.setStartDate(startDate);
		s.setEndDate(endDate);
		s.setOwningUser(AuthUtil.getRemoteUserObject());
		return s;
	}
	
	
}
