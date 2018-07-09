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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.webcurator.core.util.Utils;
import org.webcurator.domain.model.dto.GroupMemberDTO;

/**
 * A TargetGroup contains a number of child targets or target groups.
 * 
 * @hibernate.joined-subclass table="TARGET_GROUP" lazy="true"
 * @hibernate.joined-subclass-key column="TG_AT_OID"
 * @hibernate.query name="org.webcurator.domain.model.core.TargetGroup.getGroupDTOsByNameAndType" query="SELECT new org.webcurator.domain.model.dto.AbstractTargetDTO(t.oid, t.name, t.owner.oid, t.owner.username, t.owner.agency.name, t.state, t.profile.oid, t.objectType, t.type) FROM TargetGroup t where lower(t.name) like lower(:name) and t.type IN (:types) ORDER BY UPPER(t.name), t.type"
 * @hibernate.query name="org.webcurator.domain.model.core.TargetGroup.cntGroupDTOsByNameAndType" query="SELECT count(*) FROM TargetGroup t where lower(t.name) like lower(:name) and t.type IN (:types)"
 * 
 */
		 
public class TargetGroup extends AbstractTarget {
	
	/** The maximum length of the type field. */
	public static final int MAX_TYPE_LENGTH = 255;
	
	/** Query identifier for retrieving Group DTOs by name */
	public static final String QUERY_GROUP_DTOS_BY_NAME_AND_TYPE = "org.webcurator.domain.model.core.TargetGroup.getGroupDTOsByNameAndType";
	public static final String QUERY_CNT_GROUP_DTOS_BY_NAME_AND_TYPE = "org.webcurator.domain.model.core.TargetGroup.cntGroupDTOsByNameAndType";	
	
	
	/** The TargetGroup is Active - at least one child can be scheduled */
	public static final int STATE_ACTIVE = 9;
	/** The TargetGroup is inactive - the TargetGroup has reached its end date or all of its children have reached their end dates */
	public static final int STATE_INACTIVE = 10;
	/** The TargetGroup is Pending - none of its children are active */
	public static final int STATE_PENDING = 8;
	
	/** The type constant for a One SIP group - a group that gets harvested as a single target instance */
	public static final int ONE_SIP = 1;
	/** The type constant for a Many SIP group - a group that results in creating a target instance per child */
	public static final int MANY_SIP = 2;
	
	/** The type of the Group; one sip or many sip */
	private int sipType = ONE_SIP;
	/** Date at which the Group's membership starts for meta-data purposes. */
	private Date fromDate = null;
	/** Date at which the Group's membership ends for meta-data purposes. */
	private Date toDate   = null;
	/** The ownership meta data. */
	private String ownershipMetaData = null;
	/** Children */
	private Set<GroupMember> children = new HashSet<GroupMember>();
	
	/** Unpersisted - List of new children */
	private List<GroupMemberDTO> newChildren = new LinkedList<GroupMemberDTO>();
	/** Set of children that have been removed */
	private Set<Long> removedChildren = new HashSet<Long>();
	/** The type of the group */
	private String type;

	/**
	 * Protected constructor to ensure instantiation is through the 
	 * BusinessObjectFactory.
	 */
	protected TargetGroup() {
		super(AbstractTarget.TYPE_GROUP);
	}

	/* (non-Javadoc)
	 * @see org.webcurator.domain.model.core.AbstractTarget#getSeeds()
	 */
	@Override
	public Set<Seed> getSeeds() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets whether the TargetGroup is one SIP or many SIP.
	 * @return Returns the type - either ONE_SIP or MANY_SIP.
	 * @hibernate.property column="TG_SIP_TYPE"
	 */
	public int getSipType() {
		return sipType;
	}

	/**
	 * Sets whether the TargetGroup is ONE_SIP or MANY_SIP.
	 * @param type The type to set.
	 */
	public void setSipType(int type) {
		this.sipType = type;
	}

	/**
	 * Get the date that the group becomes active.
	 * @return Returns the fromDate.
     * @hibernate.property type="date" 
     * @hibernate.column name="TG_START_DATE" sql-type="DATE"
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * Set the date the group becomes active.
	 * @param fromDate The fromDate to set.
	 */
	public void setFromDate(Date fromDate) {
		if(fromDate == null) {
			this.fromDate = null;
		}
		else { 
			this.fromDate = Utils.clearTime(fromDate);
		}
	}

	/**
	 * Get the date the group becomes inactive.
	 * @return Returns the toDate.
     * @hibernate.property type="date" 
     * @hibernate.column name="TG_END_DATE" sql-type="DATE"
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * Set the date the group becomes inactive.
	 * @param toDate The toDate to set.
	 */
	public void setToDate(Date toDate) {
		if(toDate == null) {
			this.toDate = null;
		}
		else { 
			this.toDate = Utils.endOfDay(toDate);
		}
	}

	/**
	 * Get the ownership Meta Data for the TargetGroup. This allows additional
	 * information about ownership to be added to the group, which is important
	 * since a group can only have a single owner.
	 * 
	 * @return Returns the ownerhsipMetaData.
	 * @hibernate.property column="TG_OWNERSHIP_METADATA" length="255"
	 */
	public String getOwnershipMetaData() {
		return ownershipMetaData;
	}

	/**
	 * Set the ownership meta data for the TargetGroup.
	 * @see #getOwnershipMetaData()
	 * @param ownerhsipMetaData The ownerhsipMetaData to set.
	 */
	public void setOwnershipMetaData(String ownerhsipMetaData) {
		this.ownershipMetaData = ownerhsipMetaData;
	}

	
	/**
	 * Get the list of children that have been added to the group after it
	 * was loaded from the database.
	 * @return A List of GroupMemberDTO objects.
	 */
	public List<GroupMemberDTO> getNewChildren() {
		return newChildren;
	}
	
	
	
	/**
	 * Gets a set of all the children.
	 * @return Returns the children.
     * @hibernate.set cascade="none"
     * @hibernate.collection-key column="GM_PARENT_ID"
     * @hibernate.collection-one-to-many class="org.webcurator.domain.model.core.GroupMember"
     */	
	public Set<GroupMember> getChildren() {
		return children;
	}

	/**
	 * Sets the set of children.
	 * @param children The children to set.
	 */
	public void setChildren(Set<GroupMember> children) {
		this.children = children;
	}

	/**
	 * Gets the set of OIDs for children that have been removed since the 
	 * TargetGroup was loaded from the database.
	 * @return Returns the removedChildren.
	 */
	public Set<Long> getRemovedChildren() {
		return removedChildren;
	}
	
	/**
	 * Is the target now schedulable?
	 * @return True if the new state of the target is schedulable.
	 */
	public boolean isSchedulable() {
		return getState() == TargetGroup.STATE_ACTIVE;
	}

	/**
	 * @return Returns the type.
	 * @hibernate.property column="TG_TYPE" length="255"
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
}
