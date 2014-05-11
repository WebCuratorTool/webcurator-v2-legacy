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

import java.util.List;

/**
 * The create user command object.
 * @author bprice
 */
public class CreateUserCommand {
	/** the name of the agencies model object. */
    public static final String MDL_AGENCIES = "agencies";
	/** the name of the assigned roles model object. */
    public static final String MDL_ASSIGNED_ROLES = "assignedRoles";
    /** The name of the save role action. */
    public static final String ACTION_SAVE = "save";
    /** The name of the New Role action */
    public static final String ACTION_NEW ="new";
    /** The name of the View user action */
    public static final String ACTION_VIEW ="view";
    /** The name of the Edit user action */
    public static final String ACTION_EDIT ="edit";
    
    /** the constant name of the user oid object. */
    public static final String PARAM_OID = "oid";
    /** the constant name of the action command object. */
    public static final String PARAM_ACTION = "action";
    /** the constant name of the user name object. */
    public static final String PARAM_USERNAME = "username";
    /** the constant name of the emial address object. */
    public static final String PARAM_EMAIL = "email";
    /** the constant name of the notify by email flag object. */
    public static final String PARAM_NOTIFICATIONS_BY_EMAIL = "notificationsByEmail";
    /** the constant name of the notify by email flag object. */
    public static final String PARAM_TASKS_BY_EMAIL = "tasksByEmail";    
    /** the constant name of the title object. */
    public static final String PARAM_TITLE = "title";
    /** the constant name of the first name object. */
    public static final String PARAM_FIRSTNAME = "firstname";
    /** the constant name of the last name object. */
    public static final String PARAM_LASTNAME = "lastname";
    /** the constant name of the active object. */
    public static final String PARAM_ACTIVE = "active";
    /** the constant name of the external authorisation flag object. */
    public static final String PARAM_EXTERNAL_AUTH = "externalAuth";
    /** the constant name of the password object. */
    public static final String PARAM_PASSWORD = "password";
    /** the constant name of the confirm password object. */
    public static final String PARAM_CONFIRM_PASSWORD = "confirmPassword";
    /** the constant name of the phone number object. */
    public static final String PARAM_PHONE = "phone";
    /** the constant name of the address object. */
    public static final String PARAM_ADDRESS = "address";
    /** the constant name of the agency oid object. */
    public static final String PARAM_AGENCY_OID = "agencyOid";
    /** the constant name of the mode object. */
    public static final String PARAM_MODE = "mode";
    
    /** the unique user oid field. */
    private Long oid;
    /** the action field. */
    private String action;
    /** the user name field. */
    private String username;
    /** the email address field. */
    private String email;
    /** the notify by email flag field. */
    private boolean notificationsByEmail;
    /** the tasks by email flag field. */
    private boolean tasksByEmail;    
    /** the title field. */
    private String title;
    /** the first name field. */
    private String firstname;
    /** the last name field. */
    private String lastname;
    /** the user is active field. */
    private boolean active;
    /** the external authorisation flag field. */
    private boolean externalAuth;
    /** the password field. */
    private String password;
    /** the password confirmation field. */
    private String confirmPassword;
    /** the phone number field. */
    private String phone;
    /** the address field. */
    private String address;
    /** the users agency oid field. */
    private Long agencyOid;
    /** the agencies field. */
    private List agencies;
    /** the mode field. */
    private String mode;
    /** the command field. */
    /** Enable notifications for changes to objects the user owns */
    private boolean notifyOnGeneral = false;
    /** Enable notifications for harvester warnings. */
    private boolean notifyOnHarvestWarnings = false;    
    
    private CreateUserCommand command;
    
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
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the agencies
	 */
	public List getAgencies() {
		return agencies;
	}
	/**
	 * @param agencies the agencies to set
	 */
	public void setAgencies(List agencies) {
		this.agencies = agencies;
	}
	/**
	 * @return the agencyOid
	 */
	public Long getAgencyOid() {
		return agencyOid;
	}
	/**
	 * @param agencyOid the agencyOid to set
	 */
	public void setAgencyOid(Long agencyOid) {
		this.agencyOid = agencyOid;
	}
	/**
	 * @return the command
	 */
	public CreateUserCommand getCommand() {
		return command;
	}
	/**
	 * @param command the command to set
	 */
	public void setCommand(CreateUserCommand command) {
		this.command = command;
	}
	/**
	 * @return the confirmPassword
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}
	/**
	 * @param confirmPassword the confirmPassword to set
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the externalAuth
	 */
	public boolean isExternalAuth() {
		return externalAuth;
	}
	/**
	 * @param externalAuth the externalAuth to set
	 */
	public void setExternalAuth(boolean externalAuth) {
		this.externalAuth = externalAuth;
	}
	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}
	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}
	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	/**
	 * @return the notificationsByEmail
	 */
	public boolean isNotificationsByEmail() {
		return notificationsByEmail;
	}
	/**
	 * @param notificationsByEmail the notificationsByEmail to set
	 */
	public void setNotificationsByEmail(boolean notifyByEmail) {
		this.notificationsByEmail = notifyByEmail;
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean isTasksByEmail() {
		return tasksByEmail;
	}
	public void setTasksByEmail(boolean tasksByEmail) {
		this.tasksByEmail = tasksByEmail;
	}
	public boolean isNotifyOnGeneral() {
		return notifyOnGeneral;
	}
	public void setNotifyOnGeneral(boolean notifyOnChanges) {
		this.notifyOnGeneral = notifyOnChanges;
	}
	public boolean isNotifyOnHarvestWarnings() {
		return notifyOnHarvestWarnings;
	}
	public void setNotifyOnHarvestWarnings(boolean notifyOnHarvestWarnings) {
		this.notifyOnHarvestWarnings = notifyOnHarvestWarnings;
	}
    
    
}
