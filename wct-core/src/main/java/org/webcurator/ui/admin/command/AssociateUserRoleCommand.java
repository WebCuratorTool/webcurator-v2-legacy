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
 * The command object for associating users to roles.
 * @author bprice
 */
public class AssociateUserRoleCommand {
	/** the name of the assigned roles model object. */
    public static final String MDL_ASSIGNED_ROLES = "assignedRoles";
    /** the name of the unassigned roles model object. */
    public static final String MDL_UNASSIGNED_ROLES = "unassignedRoles";
    /** the name of the selected user model object. */
    public static final String MDL_USER = "selectedUser";
    
    /** The name of the save user role association action. */
    public static final String ACTION_ASSOCIATE_SAVE = "save";
    /** The name of the view user role association action. */
    public static final String ACTION_ASSOCIATE_VIEW = "view";
    
    /** the name of the user oid parameter. */
    public static final String PARAM_USER_OID = "choosenUserOid";
    /** the name of the action command parameter. */
    public static final String PARAM_ACTION = "actionCmd";
    /** the name of the choosen user parameter. */
    public static final String PARAM_USERNAME = "choosenUser";
    /** the name of the associated roles parameter. */
    public static final String PARAM_ASSOCIATED_ROLES = "associatedRoles";
    /** the name of the all roles parameter. */
    public static final String PARAM_ALL_ROLES = "allRoles";
    /** the name of the selected roles parameter. */
    public static final String PARAM_SELECTED_ROLES = "selectedRoles";

    /** the name of the chosen user field. */
    private Long choosenUserOid;
    /** the name of the action command field. */
    private String actionCmd;
    /** the name of the role name field. */
    private String roleName;
    /** the name of the chosen user field. */
    private String choosenUser;
    /** the name of the selected roles field. */
    private String selectedRoles;
    
	/**
	 * @return the actionCmd
	 */
	public String getActionCmd() {
		return actionCmd;
	}
	/**
	 * @param actionCmd the actionCmd to set
	 */
	public void setActionCmd(String actionCmd) {
		this.actionCmd = actionCmd;
	}
	/**
	 * @return the choosenUser
	 */
	public String getChoosenUser() {
		return choosenUser;
	}
	/**
	 * @param choosenUser the choosenUser to set
	 */
	public void setChoosenUser(String choosenUser) {
		this.choosenUser = choosenUser;
	}
	/**
	 * @return the choosenUserOid
	 */
	public Long getChoosenUserOid() {
		return choosenUserOid;
	}
	/**
	 * @param choosenUserOid the choosenUserOid to set
	 */
	public void setChoosenUserOid(Long choosenUserOid) {
		this.choosenUserOid = choosenUserOid;
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
	 * @return the selectedRoles
	 */
	public String getSelectedRoles() {
		return selectedRoles;
	}
	/**
	 * @param selectedRoles the selectedRoles to set
	 */
	public void setSelectedRoles(String selectedRoles) {
		this.selectedRoles = selectedRoles;
	}
    
    
}
