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

import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.TargetGroup;

/**
 * A DTO to support efficient data transfer and presentation of groups
 * and their members.
 * 
 * @author bbeaumont
 */
public class GroupMemberDTO {
	
	/** The OID of the GroupMember object */
	private Long   oid;
	
	/** The OID of the parent object */
	private Long   parentOid;
	/** The name of the parent object */
	private String parentName;
	/** The name of the owner of the parent object */
	private String parentOwner;
	/** The name of the agency that owns the parent object */
	private String parentAgency;

	/** The OID of the child object */
	private Long   childOid;
	/** The name of the child object */
	private String childName;
	/** The type (Target/Group) of the child object */
	private int    childType;
	/** The name of the owner of the child object */
	private String childOwner;
	/** The name of the agency that owns the child object */
	private String childAgency;
	
	/** The state of the group member link */
	public static enum SAVE_STATE { ORIGINAL, NEW, DELETED };
	private SAVE_STATE saveState;
	
	/**
	 * Constructor for Hibernate queries.
	 * @param oid			The OID of the GroupMember object.	
	 * @param parentOid		The OID of the parent object.
	 * @param parentName	The name of the parent object.
	 * @param parentOwner	The name of the owner of the parent object.
	 * @param parentAgency	The name of the agency that owns the parent object.
	 * @param childOid		The OID of the child object.
	 * @param childName		The name of the child object.
	 * @param childType		The type of the child object (Target/Group). 	
	 * @param childOwner	The name of the owner of the child object.
	 * @param childAgency	The name of the agency that owns the child object.
	 */
	public GroupMemberDTO(Long oid, 
			              Long parentOid, 
			              String parentName, 
			              String parentOwner, 
			              String parentAgency, 
			              Long childOid, 
			              String childName, 
			              int childType, 
			              String childOwner, 
			              String childAgency) {
		this.oid = oid;
		this.parentOid = parentOid;
		this.parentName = parentName;
		this.parentOwner = parentOwner;
		this.parentAgency = parentAgency;
		
		this.childOid = childOid;
		this.childName = childName;
		this.childOwner = childOwner;
		this.childAgency = childAgency;
		
		this.childType = childType;
	}
	
	/**
	 * Create a GroupMemberDTO from a parent and child object.
	 * @param parent The parent object.
	 * @param child  The child object.
	 */
	public GroupMemberDTO(TargetGroup parent, AbstractTarget child) {
		parentOid = parent.getOid();
		parentName = parent.getName();
		parentOwner = parent.getOwner().getNiceName();
		parentAgency = parent.getOwner().getAgency().getName();
		
		childOid = child.getOid();
		childName = child.getName();
		childOwner = child.getOwner().getNiceName();
		childAgency = child.getOwner().getAgency().getName();
		childType = child.getObjectType();
	}
	
	public GroupMemberDTO(Long parentOid, Long childOid) { 
		this.parentOid = parentOid;
		this.childOid = childOid;
	}
	
	/**
	 * @return Returns the childAgency.
	 */
	public String getChildAgency() {
		return childAgency;
	}
	/**
	 * @return Returns the childName.
	 */
	public String getChildName() {
		return childName;
	}
	/**
	 * @return Returns the childOid.
	 */
	public Long getChildOid() {
		return childOid;
	}
	/**
	 * @return Returns the childOwner.
	 */
	public String getChildOwner() {
		return childOwner;
	}
	/**
	 * @return Returns the childType.
	 */
	public int getChildType() {
		return childType;
	}
	/**
	 * @return Returns the oid.
	 */
	public Long getOid() {
		return oid;
	}
	/**
	 * @return Returns the parentAgency.
	 */
	public String getParentAgency() {
		return parentAgency;
	}
	/**
	 * @return Returns the parentName.
	 */
	public String getParentName() {
		return parentName;
	}
	/**
	 * @return Returns the parentOid.
	 */
	public Long getParentOid() {
		return parentOid;
	}
	/**
	 * @return Returns the parentOwner.
	 */
	public String getParentOwner() {
		return parentOwner;
	}

	public SAVE_STATE getSaveState() {
		return saveState;
	}

	public void setSaveState(SAVE_STATE saveState) {
		this.saveState = saveState;
	}
	
	public boolean equals(Object other) { 
		if(!(other instanceof GroupMemberDTO)) {
			return false;
		}
		else {
			GroupMemberDTO ot = (GroupMemberDTO) other;
			return childOid.equals(ot.childOid) && parentOid.equals(ot.parentOid); 
		}
	}

	@Override
	public int hashCode() {
		return (int) (childOid + parentOid);
	}
	
	
	
	
}
