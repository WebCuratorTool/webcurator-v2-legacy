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

import org.webcurator.core.harvester.HarvesterType;
import org.webcurator.domain.AgencyOwnable;
import org.webcurator.domain.model.auth.Agency;

/**
 * A DTO for the efficient retrieval and display of profiles in the
 * user interface where the details the specific profile configuration
 * is not important.
 * 
 * @author bbeaumont
 */
public class ProfileDTO implements AgencyOwnable {

	/** The unique database ID of the profile. */
	private Long oid;

	/** The name of the profile. **/
	private String name;
	
	/** The description of the profile. **/
	private String description;
	
	/** The current status of the profile. **/
	private int status;
	
	/** The profile selection level required for a user to be able
	 * to use this profile on a target. */
	private int requiredLevel;
	
	/** The owningAgency */
	private Agency owningAgency;
	
	/** Whether this is the default profile */
	private boolean defaultProfile;
	
	/** The original Oid for this Profile */
	private Long origOid;

	/** Which crawler is this? */
	private String harvesterType;

	/**
	 * Constructor for the DTO.
	 * @param anOid				The database OID of the profile.
	 * @param aName				The name of the profile.
	 * @param aDescription		The description of the profile.
	 * @param aStatus			The status of the profile.
	 * @param aRequiredLevel	The privilege level required by a user before they cna assign this profile to a target. 
	 * @param anAgency			The agency that owns this profile.
	 * @param defaultProfile	True if this is the default profile; otherwise false.
	 * @param harvesterType		The type of harvester.
	 */
	public ProfileDTO(Long anOid,
				      String aName,
				      String aDescription,
				      int aStatus,
				      int aRequiredLevel,
				      Agency anAgency,
				      String harvesterType,
				      boolean defaultProfile) {
		oid = anOid;
		name = aName;
		description = aDescription;
		status = aStatus;
		requiredLevel = aRequiredLevel;
		owningAgency = anAgency;
		this.defaultProfile = defaultProfile;
		this.harvesterType = harvesterType;
		this.origOid = null;
	}

	/**
	 * Constructor for the DTO.
	 * @param anOid				The database OID of the profile.
	 * @param aName				The name of the profile.
	 * @param aDescription		The description of the profile.
	 * @param aStatus			The status of the profile.
	 * @param aRequiredLevel	The privilege level required by a user before they cna assign this profile to a target. 
	 * @param anAgency			The agency that owns this profile.
	 * @param defaultProfile	True if this is the default profile; otherwise false.
	 * @param origOid			The original database OID for a locked profile.
	 * @param harvesterType		The type of harvester.
	 */
	public ProfileDTO(Long anOid,
				      String aName,
				      String aDescription,
				      int aStatus,
				      int aRequiredLevel,
				      Agency anAgency,
					  String harvesterType,
				      boolean defaultProfile,
				      Long origOid) {
		oid = anOid;
		name = aName;
		description = aDescription;
		status = aStatus;
		requiredLevel = aRequiredLevel;
		owningAgency = anAgency;
		this.defaultProfile = defaultProfile;
		this.origOid = origOid;
		this.harvesterType = harvesterType;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the oid.
	 */
	public Long getOid() {
		return oid;
	}

	/**
	 * @param oid The oid to set.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}

	/**
	 * @return Returns the requiredLevel.
	 */
	public int getRequiredLevel() {
		return requiredLevel;
	}

	/**
	 * @param requiredLevel The requiredLevel to set.
	 */
	public void setRequiredLevel(int requiredLevel) {
		this.requiredLevel = requiredLevel;
	}

	/**
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status The status to set.
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return Returns the owningAgency.
	 */
	public Agency getOwningAgency() {
		return owningAgency;
	}

	/**
	 * @param owningAgency The owningAgency to set.
	 */
	public void setOwningAgency(Agency owningAgency) {
		this.owningAgency = owningAgency;
	}

	/**
	 * @return Returns the defaultProfile.
	 */
	public boolean isDefaultProfile() {
		return defaultProfile;
	}

	/**
	 * @param defaultProfile The defaultProfile to set.
	 */
	public void setDefaultProfile(boolean defaultProfile) {
		this.defaultProfile = defaultProfile;
	}	

	/**
	 * @return Returns true if the profile is locked.
	 */
	public boolean isLocked() {
		return (origOid != null);
	}

	/**
	 * @return Returns the original oid.
	 */
	public Long getOrigOid() {
		return origOid;
	}

	/**
	 * @param origOid The original oid to set.
	 */
	public void setOrigOid(Long origOid) {
		this.origOid = origOid;
	}

	/**
	 *
	 * @return Returns the type of the crawler
	 */
	public String getHarvesterType() {
		return harvesterType;
	}

	/**
	 *
	 * @param harvesterType The crawler type
	 */
	public void setHarvesterType(String harvesterType) {
		this.harvesterType = harvesterType;
	}
}
