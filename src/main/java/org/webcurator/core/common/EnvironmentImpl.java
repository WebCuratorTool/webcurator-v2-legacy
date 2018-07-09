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
package org.webcurator.core.common;

/**
 * The Implemenation of the core environment.
 * @author bbeaumont
 */
public class EnvironmentImpl implements Environment {
	/** The number of days in advance to create target instances for. */
	private int daysToSchedule = 10;
	/** The number of schedules to be processed per batch when the createNewTargetInstancesTrigger is fired. */
	private int schedulesPerBatch = 10;
    /** the WCT version. */
    private String applicationVersion;
    /** the Heritrix version. */
    private String heritrixVersion;
	
	/** Default Constructor. */
	public EnvironmentImpl() {
	}
	
	/**
	 * @return Returns the daysToSchedule.
	 */
	public int getDaysToSchedule() {
		return daysToSchedule;
	}

	/**
	 * @param daysToSchedule The daysToSchedule to set.
	 */
	public void setDaysToSchedule(int daysToSchedule) {
		this.daysToSchedule = daysToSchedule;
	}

	/**
	 * @return Returns the schedulesPerBatch.
	 */
	public int getSchedulesPerBatch() {
		return schedulesPerBatch;
	}

	/**
	 * @param schedulesPerBatch The schedulesPerBatch to set.
	 */
	public void setSchedulesPerBatch(int schedulesPerBatch) {
		this.schedulesPerBatch = schedulesPerBatch;
	}

	/**
	 * @return the applicationVersion
	 */
	public String getApplicationVersion() {
		return applicationVersion;
	}

	/**
	 * @param applicationVersion the applicationVersion to set
	 */
	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	/**
	 * @param heritrixVersion the heritrixVersion to set
	 */
	public void setHeritrixVersion(String heritrixVersion) {
		this.heritrixVersion = heritrixVersion;
	}

	/**
	 * @return the heritrixVersion
	 */
	public String getHeritrixVersion() {
		return heritrixVersion;
	}
}
