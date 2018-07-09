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
package org.webcurator.domain.model.dto;

import java.util.Date;

/**
 * DTO for efficient retrieval and display of Target Instance objects.
 * 
 * @author nwaight
 */
public class TargetInstanceDTO {
	/** unique identifier. */
	private Long oid;
    /** the scheduled time of the harvest. */ 
    private Date scheduledTime;       
    /** the priority of the target instance. */
    private int priority;
    /** The state of the target instance. */
    private String state; 
    /** The oid of the owning user. */
    private Long ownerOid;
    
    /**
     * Constructor for Hibernate queries.
     * @param oid				The OID of the target instance.
     * @param scheduledTime		The time the target instance is scheduled to start.
     * @param priority			The priority of the target instance.
     * @param state				The state of the target instance.
     * @param ownerOid 		    The oid of the owning user
     */
	public TargetInstanceDTO(Long oid, Date scheduledTime, int priority, String state, long ownerOid) {
		super();
		this.oid = oid;
		this.scheduledTime = scheduledTime;
		this.priority = priority;
		this.state = state;
		this.ownerOid = ownerOid;
	}
	
	/**
	 * @return the oid
	 */
	public Long getOid() {
		return oid;
	}
	/**
	 * @param oid the oid to set
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	/**
	 * @return the scheduledTime
	 */
	public Date getScheduledTime() {
		return scheduledTime;
	}
	/**
	 * @param scheduledTime the scheduledTime to set
	 */
	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the ownerOid
	 */
	public Long getOwnerOid() {
		return ownerOid;
	}
	/**
	 * @param ownerOid the ownerOid to set
	 */
	public void setOwnerOid(Long ownerOid) {
		this.ownerOid = ownerOid;
	}
}
