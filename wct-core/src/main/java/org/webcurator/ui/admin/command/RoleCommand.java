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
package org.webcurator.ui.admin.command;

/**
 * holds the Role base information gathered from the UI related to
 * User Roles within the WCT system. A Role is comprised of a
 * set of Privileges to different functions within the system.
 * @author bprice
 */
public class RoleCommand {
    /** The name of the roles model object. */
    public static final String MDL_ROLES = "roles";
    /** The name of the agencies model object. */
    public static final String MDL_AGENCIES = "agencies";
    
    /** The name of the edit role action. */
    public static final String ACTION_EDIT = "edit";
    /** The name of the save role action. */
    public static final String ACTION_SAVE = "save";
    /** The name of the delete role action. */
    public static final String ACTION_DELETE = "delete";
    /** The name of the view role action. */
    public static final String ACTION_VIEW = "view";
    /** The name of the New Role action */
    public static final String ACTION_NEW ="new";
    /** The name of the Filter Role action */
    public static final String ACTION_FILTER ="filter";

    /** the constant name of the role oid field. */ 
    public static final String PARAM_OID = "oid";
    /** the constant name of the action field. */
    public static final String PARAM_ACTION = "action";
    /** the constant name of the role name field. */
    public static final String PARAM_ROLE_NAME = "roleName";
    /** the constant name of the description field. */
    public static final String PARAM_ROLE_DESCRIPTION = "description";
    /** the constant name of the privileges field. */
    public static final String PARAM_PRIVILEGES = "privileges";
    /** the constant name of the scoped privileges field. */
    public static final String PARAM_SCOPED_PRIVILEGES = "scopedPrivileges";
    /** the constant name of the agency field. */
    public static final String PARAM_AGENCY = "agency";
    /** the constant name of the max radio groups field. */
    public static final String PARAM_RADIO_GROUP_COUNT = "maxRadioGroups";
    /** the constant name of the agency filter value. */
    public static final String PARAM_AGENCY_FILTER = "agencyFilter";

    /** the action field. */
    private String action;
    /** the role oid field. */
    private Long oid;
    /** the role name field. */
    private String roleName;
    /** the description field. */
    private String description;
    /** the privileges field. */
    private String[] privileges;
    /** the scope field. */
    private String[] scopedPrivileges;
    /** the agency oid field. */
    private Long agency;
    /** the max radio groups field. */
    private Integer maxRadioGroups;
    /** view only mode. */
    private boolean viewOnlyMode = false;
    /** agency filter value */
    private String agencyFilter = "";
    
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return the agency
	 */
	public Long getAgency() {
		return agency;
	}
	/**
	 * @param agency the agency to set
	 */
	public void setAgency(Long agency) {
		this.agency = agency;
	}
	/**
	 * @return the agency filter
	 */
	public String getAgencyFilter() {
		return agencyFilter;
	}
	/**
	 * @param agencyFilter the agencyFilter to set
	 */
	public void setAgencyFilter(String agencyFilter) {
		this.agencyFilter = agencyFilter;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the maxRadioGroups
	 */
	public Integer getMaxRadioGroups() {
		return maxRadioGroups;
	}
	/**
	 * @param maxRadioGroups the maxRadioGroups to set
	 */
	public void setMaxRadioGroups(Integer maxRadioGroups) {
		this.maxRadioGroups = maxRadioGroups;
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
	 * @return the viewOnlyMode
	 */
	public boolean getViewOnlyMode() {
		return viewOnlyMode;
	}
	/**
	 * @param viewOnlyMode the mode to set
	 */
	public void setViewOnlyMode(boolean viewOnlyMode) {
		this.viewOnlyMode = viewOnlyMode;
	}
	/**
	 * @return the privileges
	 */
	public String[] getPrivileges() {
		return privileges;
	}
	/**
	 * @param privileges the privileges to set
	 */
	public void setPrivileges(String[] privileges) {
		this.privileges = privileges;
	}
	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}
	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	/**
	 * @return the scopedPrivileges
	 */
	public String[] getScopedPrivileges() {
		return scopedPrivileges;
	}
	/**
	 * @param scopedPrivileges the scopedPrivileges to set
	 */
	public void setScopedPrivileges(String[] scopedPrivileges) {
		this.scopedPrivileges = scopedPrivileges;
	}
}
