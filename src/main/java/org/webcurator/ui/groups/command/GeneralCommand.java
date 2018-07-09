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
package org.webcurator.ui.groups.command;

import java.util.Date;

import org.webcurator.domain.model.core.TargetGroup;

/**
 * The General Target Group Tab Command object.
 * @author bbeaumont
 */
public class GeneralCommand {
	/** The constraint value for the maximum length of the name field. */
	public static final int CNST_MAX_LEN_NAME = 255;
	/** The constraint value for the maximum length of the escription field. */	
	public static final int CNST_MAX_LEN_DESC = 4000;
	/** The constraint value for the maximum length of the owner info field. */
	public static final int CNST_MAX_LEN_OWNER_INFO = 255;
	
	public static final String ACTION_ADD_PARENT = "AddParent";
	
	public static final String PARAM_ACTION = "action";
	/** The constant name of the name field.*/	
	public static final String PARAM_NAME = "name";
	/** The constant name of the desription field.*/
	public static final String PARAM_DESCRIPTION = "description";
	/** The constant name of the ownership information field.*/
	public static final String PARAM_OWNER_INFO = "ownershipMetaData";
	/** The constant name of the from date field.*/
	public static final String PARAM_FROM_DATE = "fromDate";
	/** The constant name of the editMode field.*/
	public static final String PARAM_EDIT_MODE = "editMode";
	/** The constant name of the type field.*/
	public static final String PARAM_TYPE = "type";
	/** The constant name of the subGroupType field.*/
	public static final String PARAM_SUBGROUP_TYPE = "subGroupType";
	/** The constant name of the parentOid field.*/	
	public static final String PARAM_PARENT_OID = "parentOid";
	/** The constant name of the parentName field.*/	
	public static final String PARAM_PARENT_NAME = "parentName";
	/** The constant name of the subGroupSeparator field.*/	
	public static final String PARAM_SUBGROUP_SEPARATOR = "subGroupSeparator";
	
	
	private String action = "";
    /** The targets name. */
    private String name;
    /** the targets description. */
    private String description;
    /** The target's state */
    private int state;
    /** The ID of the owner */
    private Long ownerOid;
    /** The name of the parent */
    private String parentName;
    /** The ID of the parent */
    private String parentOid;
    /** SIP Type */
    private int sipType = TargetGroup.MANY_SIP;
    /** Ownership Meta Data */
    private String ownershipMetaData;
    /** The start date of the group */
    private Date fromDate;
    /** The end date of the group */
    private Date toDate;
    /** The edit mode */
    private boolean editMode;
    /** the reference number for the group. */
    private String reference;    
    /** The type of the group */
    private String type;
    /** The subGroup Type name */
	private String subGroupType;
    /** The subGroup separator */
	private String subGroupSeparator;
    
    


	/**
     * Populate the command object from the TargetGroup model object.
     * @param model the Group to use to populate the command
	 * @param subGroupTypeName 
     * @return the populated command
     */
	public static GeneralCommand buildFromModel(TargetGroup model, String subGroupTypeName, String subGroupSeparator) {
    	GeneralCommand command = new GeneralCommand();
    	command.name = model.getName();
    	command.description = model.getDescription();
    	command.state = model.getState();
    	command.ownerOid = model.getOwner().getOid();
    	command.sipType = model.getSipType();
    	command.ownershipMetaData = model.getOwnershipMetaData();
    	command.fromDate = model.getFromDate();
    	command.toDate = model.getToDate();
    	command.reference = model.getReferenceNumber();
    	command.type = model.getType();
    	command.subGroupType = subGroupTypeName;
    	command.subGroupSeparator = subGroupSeparator;
    	
    	return command;		
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
	 * @return Returns the ownerOid.
	 */
	public Long getOwnerOid() {
		return ownerOid;
	}

	/**
	 * @param ownerOid The ownerOid to set.
	 */
	public void setOwnerOid(Long ownerOid) {
		this.ownerOid = ownerOid;
	}

	/**
	 * @return Returns the state.
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state The state to set.
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * @return Returns the sipType.
	 */
	public int getSipType() {
		return sipType;
	}

	/**
	 * @param sipType The sipType to set.
	 */
	public void setSipType(int sipType) {
		this.sipType = sipType;
	}

	/**
	 * @return Returns the ownerhsipMetaData.
	 */
	public String getOwnershipMetaData() {
		return ownershipMetaData;
	}

	/**
	 * @param ownerhsipMetaData The ownerhsipMetaData to set.
	 */
	public void setOwnershipMetaData(String ownerhsipMetaData) {
		this.ownershipMetaData = ownerhsipMetaData;
	}    
	
	/**
	 * @return Returns the fromDate.
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate The fromDate to set.
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return Returns the toDate.
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * @param toDate The toDate to set.
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	/**
	 * @return Returns the editMode.
	 */
	public boolean isEditMode() {
		return editMode;
	}

	/**
	 * @param editMode The editMode to set.
	 */
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}	

    /**
	 * @return Returns the type.
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

    /**
	 * @return Returns the subGroupType.
	 */
	public String getSubGroupType() {
		return subGroupType;
	}

	/**
	 * @param subGroupType The subGroupType to set.
	 */
	public void setSubGroupType(String subGroupType) {
		this.subGroupType = subGroupType;
	}

	/**
	 * @param parentOid The parentOid to set.
	 */
	public void setParentOid(String parentOid) {
		this.parentOid = parentOid;
	}

	/**
	 * @return Returns the parentOid.
	 */
	public String getParentOid() {
		return parentOid;
	}

	/**
	 * @param parentName The parentName to set.
	 */
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	/**
	 * @return Returns the parentName.
	 */
	public String getParentName() {
		return parentName;
	}

	/**
	 * @param action The action to set.
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return Returns the action.
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param subGroupSeparator The subGroupSeparator to set.
	 */
	public void setSubGroupSeparator(String subGroupSeparator) {
		this.subGroupSeparator = subGroupSeparator;
	}

	/**
	 * @return Returns the subGroupSeparator.
	 */
	public String getSubGroupSeparator() {
		return subGroupSeparator;
	}	
}
