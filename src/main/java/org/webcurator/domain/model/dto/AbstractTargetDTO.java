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

import java.util.Comparator;
import java.util.Date;

/**
 * This DTO represents a Target or Target Group so that they can be
 * readily displayed on the user interface.
 * 
 * Note that setter methods do not affect the underlying object.
 * 
 * @author bbeaumont
 */
public class AbstractTargetDTO {
	/** the primary key of the Target. */
    private Long oid;
    /** The targets name. */
    private String name;
    /** The oid of the owner. */
    private Long ownerOid;    
    /** The name of the owner. */
    private String username;
    /** The name of the agency. */
    private String agencyName;
    /** The state of the target */
    private int state;
    /** The creation date of the target */
    private Date creationDate;
    /** The oid of the profile */
    private Long profileOid;
    /** The type of the target */
    private int objectType;
    /** The type of the group */
    private String groupType = "";
    
    
    /**
     * Constructor for Hibernate query results. 
     * @param oid			The database OID of the AbstractTarget.
     * @param name			The name of the AbstractTarget.
     * @param ownerOid		The OID of the owner of the AbstractTarget.
     * @param username		The username of the owner of the AbstractTarget.
     * @param agencyName	The name of the agency that owns the AbstractTarget.
     * @param state			The current state of the AbstractTarget.
     * @param profileOid	The OID of the profile associated with the AbstractTarget.
     * @param objectType	Whether the AbstractTarget is a Target or a Group.
     */
	public AbstractTargetDTO(Long oid, String name, Long ownerOid, String username, String agencyName, int state, Long profileOid, int objectType) {
		super();
		this.oid = oid;
		this.name = name;
		this.ownerOid = ownerOid;
		this.username = username;
		this.agencyName = agencyName;
		this.state = state;
		this.profileOid = profileOid;
		this.objectType = objectType;
	}
	
    /**
     * Constructor for Hibernate query results. 
     * @param oid			The database OID of the AbstractTarget.
     * @param name			The name of the AbstractTarget.
     * @param ownerOid		The OID of the owner of the AbstractTarget.
     * @param username		The username of the owner of the AbstractTarget.
     * @param agencyName	The name of the agency that owns the AbstractTarget.
     * @param state			The current state of the AbstractTarget.
     * @param profileOid	The OID of the profile associated with the AbstractTarget.
     * @param objectType	Whether the AbstractTarget is a Target or a Group.
     * @param groupType		The type of the Group (or "" for a target).
     */
	public AbstractTargetDTO(Long oid, String name, Long ownerOid, String username, String agencyName, int state, Long profileOid, int objectType, String groupType) {
		super();
		this.oid = oid;
		this.name = name;
		this.ownerOid = ownerOid;
		this.username = username;
		this.agencyName = agencyName;
		this.state = state;
		this.profileOid = profileOid;
		this.objectType = objectType;
		this.setGroupType(groupType);
	}
	
    /**
     * Constructor for Hibernate query results. 
     * @param oid			The database OID of the AbstractTarget.
     * @param name			The name of the AbstractTarget.
     * @param ownerOid		The OID of the owner of the AbstractTarget.
     * @param username		The username of the owner of the AbstractTarget.
     * @param agencyName	The name of the agency that owns the AbstractTarget.
     * @param state			The current state of the AbstractTarget.
     * @param creationDate	The creation date of the AbstractTarget.
     * @param profileOid	The OID of the profile associated with the AbstractTarget.
     * @param objectType	Whether the AbstractTarget is a Target or a Group.
     */
	public AbstractTargetDTO(Long oid, String name, Long ownerOid, String username, String agencyName, int state, Date creationDate, Long profileOid, int objectType) {
		super();
		this.oid = oid;
		this.name = name;
		this.ownerOid = ownerOid;
		this.username = username;
		this.agencyName = agencyName;
		this.state = state;
		this.creationDate = creationDate;
		this.profileOid = profileOid;
		this.objectType = objectType;
	}

	/**
	 * Get the name of the agency that owns this AbstractTarget.
	 * @return Returns the agencyName.
	 */
	public String getAgencyName() {
		return agencyName;
	}
	
	/**
	 * Set the name of the agency that owns this AbstractTarget.
	 * @param agencyName The agencyName to set.
	 */
	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}
	
	/**
	 * Get the name of the AbstractTarget.
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * set the name of the AbstractTarget.
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * get the database OID of the AbstractTarget.
	 * @return Returns the oid.
	 */
	public Long getOid() {
		return oid;
	}
	
	/**
	 * Set the database OID of the AbstractTarget.
	 * @param oid The oid to set.
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}
	
	/**
	 * Get the state of the AbstractTarget.
	 * @return Returns the state.
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * Set the state of the AbstractTarget.
	 * @param state The state to set.
	 */
	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * Get the username of the owner that owns this AbstractTarget.
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Set the username of the owner that owns this AbstractTarget.
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Get the object type.
	 * @return Returns the objectType.
	 */
	public int getObjectType() {
		return objectType;
	}
	
	/**
	 * Sets the object type.
	 * @param objectType The objectType to set.
	 */
	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}
	
	/**
	 * Get the OID of the owning user.
	 * @return Returns the ownerOid.
	 */
	public Long getOwnerOid() {
		return ownerOid;
	}
	
	/**
	 * Set the OID of the owning user.
	 * @param ownerOid The ownerOid to set.
	 */
	public void setOwnerOid(Long ownerOid) {
		this.ownerOid = ownerOid;
	}
	
	/**
	 * Return the OID of the associated profile.
	 * @return Returns the profileOid.
	 */
	public Long getProfileOid() {
		return profileOid;
	}
	
	/**
	 * Set the OID of the associated profile.
	 * @param profileOid The profileOid to set.
	 */
	public void setProfileOid(Long profileOid) {
		this.profileOid = profileOid;
	}
    
	/**
	 * Get the date that the AbstractTarget was created.
	 * @return Returns the creation date.
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	
	/**
	 * Set the date the object was created.
	 * @param creationDate The creationDate to set.
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public boolean equals(Object other) { 
		return other instanceof AbstractTargetDTO && ((AbstractTargetDTO) other).oid.equals(this.oid);
	}
	
	public int hashCode() {
		return oid.hashCode();
	}
	
	/**
	 * Set the groupType of the group.
	 * @param groupType The groupType to set.
	 */
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	/**
	 * Return the groupType of group or "" for a target.
	 * @return Returns the groupType.
	 */
	public String getGroupType() {
		return groupType;
	}

	public static class NameComparator implements Comparator<AbstractTargetDTO> {

		public int compare(AbstractTargetDTO dto1, AbstractTargetDTO dto2) {
			return dto1.getName().compareToIgnoreCase(dto2.getName());
		}
		
	}
    
}
