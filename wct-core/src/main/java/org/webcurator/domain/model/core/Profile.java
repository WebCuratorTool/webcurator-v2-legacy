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

import org.webcurator.domain.AgencyOwnable;
import org.webcurator.domain.model.auth.Agency;


/**
 * The <code>Profile</code> object contains information that enables the user
 * to control how a profile takes place. This class is tied quite directly to
 * the Heritrix XML based profiles. 
 * 
 * @author bbeaumont
 * @hibernate.class table="PROFILE" lazy="false"
 * @hibernate.query name="org.webcurator.domain.model.core.Profile.getAllDTOs" query="SELECT new org.webcurator.domain.model.dto.ProfileDTO(p.oid, p.name, p.description, p.status, p.requiredLevel, p.owningAgency, p.defaultProfile, p.origOid) FROM Profile p order by p.owningAgency, upper(p.name)"
 * @hibernate.query name="org.webcurator.domain.model.core.Profile.getActiveDTOs" query="SELECT new org.webcurator.domain.model.dto.ProfileDTO(p.oid, p.name, p.description, p.status, p.requiredLevel, p.owningAgency, p.defaultProfile, p.origOid) FROM Profile p WHERE p.status = 1 order by p.owningAgency, upper(p.name)"
 * @hibernate.query name="org.webcurator.domain.model.core.Profile.getAvailableProfileDTOs" query="SELECT new org.webcurator.domain.model.dto.ProfileDTO(p.oid, p.name, p.description, p.status, p.requiredLevel, p.owningAgency, p.defaultProfile, p.origOid) FROM Profile p WHERE p.owningAgency.oid=:agencyOid AND p.status = 1 AND (p.requiredLevel <= :requiredLevel OR p.defaultProfile=:default OR p.oid=:currentProfileOid) ORDER BY p.name"
 * @hibernate.query name="org.webcurator.domain.model.core.Profile.getDTO" query="SELECT new org.webcurator.domain.model.dto.ProfileDTO(p.oid, p.name, p.description, p.status, p.requiredLevel, p.owningAgency, p.defaultProfile, p.origOid) FROM Profile p where p.oid = :oid" 
 * @hibernate.query name="org.webcurator.domain.model.core.Profile.getAgencyDTOs" query="SELECT new org.webcurator.domain.model.dto.ProfileDTO(p.oid, p.name, p.description, p.status, p.requiredLevel, p.owningAgency, p.defaultProfile, p.origOid) FROM Profile p WHERE p.owningAgency.oid = :agencyOid order by p.owningAgency, upper(p.name)"
 * @hibernate.query name="org.webcurator.domain.model.core.Profile.getActiveAgencyDTOs" query="SELECT new org.webcurator.domain.model.dto.ProfileDTO(p.oid, p.name, p.description, p.status, p.requiredLevel, p.owningAgency, p.defaultProfile, p.origOid) FROM Profile p WHERE p.owningAgency.oid = :agencyOid AND p.status = 1 order by p.owningAgency, upper(p.name)"
 * @hibernate.query name="org.webcurator.domain.model.core.Profile.getLockedDTO" query="SELECT new org.webcurator.domain.model.dto.ProfileDTO(p.oid, p.name, p.description, p.status, p.requiredLevel, p.owningAgency, p.defaultProfile, p.origOid) FROM Profile p WHERE p.origOid = :origOid and p.version = :version"
 */
public class Profile implements AgencyOwnable {
	
	/** Constant for named query to retrieve all DTOs for the profiles */
	public static final String QRY_GET_ALL_DTOS = "org.webcurator.domain.model.core.Profile.getAllDTOs";
	
	/** Constant for named query to retrieve all active DTOs. */
	public static final String QRY_GET_ACTIVE_DTOS = "org.webcurator.domain.model.core.Profile.getActiveDTOs";
	
	/** Constant for named query to retrieve all DTOs for a specified agency. */
	public static final String QRY_GET_AGENCY_DTOS = "org.webcurator.domain.model.core.Profile.getAgencyDTOs";
	/** Constant for named query to retrieve all active DTOs for a specified agency. */
	public static final String QRY_GET_ACTIVE_AGENCY_DTOS = "org.webcurator.domain.model.core.Profile.getActiveAgencyDTOs";
	
	public static final String QRY_GET_AVAIL_DTOS = "org.webcurator.domain.model.core.Profile.getAvailableProfileDTOs";
	
	public static final String QRY_GET_DTO = "org.webcurator.domain.model.core.Profile.getDTO";

	public static final String QRY_GET_LOCKED_DTO = "org.webcurator.domain.model.core.Profile.getLockedDTO";
	
	/** Status constant for inactive profiles **/
	public static final int STATUS_INACTIVE = 0;
	
	/** Status constant for active profiles **/
	public static final int STATUS_ACTIVE = 1;
	
	/** Status constant for locked profiles **/
	public static final int STATUS_LOCKED = 2;
	
	/** the maximum length of the name field. */
	public static final int MAX_LEN_NAME = 255;
	
	/** the maximum length of the description field. */
	public static final int MAX_LEN_DESC = 255;
		
	/** The unique database ID of the profile. */
	private Long oid;

	/** The name of the profile. **/
	private String name;
	
	/** The description of the profile. **/
	private String description;
	
	/** The current status of the profile. **/
	private int status = STATUS_ACTIVE;
	
	/** The profile selection level required for a user to be able
	 * to use this profile on a target. */
	private int requiredLevel = 1;
	
	/** The agency that own's this profile */
	private Agency owningAgency;

	/** The profile itself, as an XML string */
	private String profile;
	
	/** Is this the default profile for the agency */
	private boolean defaultProfile;
	
	/** The hibernate version number. */
	private Integer version;
	
	/** The original OID for this profile before it was locked */
	private Long origOid;

	/** What type of harvester is being configured by this profile? */
	private String harvesterType;
		
    /**
     * Get a clone of the profile with a null OID.
     * @return A clone of this Profile.
     */
	public Profile clone()
	{
		Profile theClone = new Profile();
		theClone.setDefaultProfile(defaultProfile);
		theClone.setDescription(description);
		theClone.setName(name);
		theClone.setOwningAgency(owningAgency);
		theClone.setProfile(profile);
		theClone.setRequiredLevel(requiredLevel);
		theClone.setStatus(status);
		theClone.setVersion(version);
		theClone.setOrigOid(origOid);
		theClone.setHarvesterType(harvesterType);
		
		return theClone;
	}
	
    /**
     * Get the OID of the profile.
     * @return Returns the oid.
     * @hibernate.id column="P_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="General" 
     */	
	public Long getOid() {
		return oid;
	}
	
	/**
	 * Set the database OID of the profile.
	 * @param oid The oid to set.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}

	/**
	 * Get the description of the profile.
	 * @return Returns the description.
     * @hibernate.property column="P_DESC" length="255" 
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the descrption of the profile.
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the name of the profile.
	 * @return Returns the name.
     * @hibernate.property column="P_NAME" length="255" 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the profile.
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the Profile XML settings.
	 * @return Returns the profile.
     * @hibernate.property column="P_PROFILE_STRING" type="text" 
	 */
	public String getProfile() {
		return profile;
	}

	/**
	 * Set the Profile XML string.
	 * @param profile The profile to set.
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

	/**
	 * Get the Profile Privilege level required so a user may use this 
	 * profile.
	 * @return Returns the requiredLevel.
     * @hibernate.property column="P_PROFILE_LEVEL" 
	 */
	public int getRequiredLevel() {
		return requiredLevel;
	}

	/**
	 * Set the Profile Privilege level required so a user may use this profile.
	 * @param requiredLevel The requiredLevel to set.
	 */
	public void setRequiredLevel(int requiredLevel) {
		this.requiredLevel = requiredLevel;
	}

	/**
	 * Get the status of this permission.
	 * @return Either STATUS_ACTIVE or STATUS_INACTIVE.
     * @hibernate.property column="P_STATUS"
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set the status of this permission.
	 * @param status The status to set.
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Returns true if this is the default profile.
	 * @return true if default; otherwise false.
	 * @hibernate.property column="P_DEFAULT"
	 */
	public boolean isDefaultProfile() {
		return defaultProfile;
	}

	/**
	 * Sets whether this is the default profile.
	 * @param defaultProfile true to make this the default; otherwise false.
	 */
	public void setDefaultProfile(boolean defaultProfile) {
		this.defaultProfile = defaultProfile;
	}

	/**
	 * Get the agency that owns this profile.
	 * @return Returns the owningAgency.
	 * @hibernate.many-to-one column="P_AGECNY_OID" foreign-key="FK_P_AGENCY_OID"
	 */
	public Agency getOwningAgency() {
		return owningAgency;
	}

	/**
	 * Set the agency that owns this profile.
	 * @param owningAgency The owningAgency to set.
	 */
	public void setOwningAgency(Agency owningAgency) {
		this.owningAgency = owningAgency;
	}

	/**
	 * Get the database version of this profile. This is used for optimistic
	 * locking and is managed directly by Hibernate.
	 * @return the version
	 * @hibernate.version column="P_VERSION"
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * Set the database version of this profile. This is used for optimistic
	 * locking and is managed directly by Hibernate.
	 * @param version the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * Returns true if this profile is locked.
	 * @return true if locked; otherwise false.
	 */
	public boolean isLocked() {
		if(this.origOid != null || this.status == STATUS_LOCKED)
		{
			this.setStatus(STATUS_LOCKED);
			return true;
		}
		
		return false;
	}

	/**
	 * Sets the originalOid for this profile.
	 * @param origOid the original oid to set.
	 */
	public void setOrigOid(Long origOid) {
		this.origOid = origOid;
		if(this.origOid != null)
		{
			this.setStatus(STATUS_LOCKED);
		}
	}

	/**
	 * Gets the originalOid for this profile.
	 * @return the original oid.
	 * @hibernate.property column="P_ORIG_OID"
	 */
	public Long getOrigOid() {
		return this.origOid;
	}


	/**
	 *
	 * @return The harvester type
	 * @hibernate.property column="P_HARVESTER_TYPE"
	 */
	public String getHarvesterType() {
		return harvesterType;
	}

	/**
	 *
	 * @param harvesterType The harvester type
	 */
	public void setHarvesterType(String harvesterType) {
		this.harvesterType = harvesterType;
	}

}
