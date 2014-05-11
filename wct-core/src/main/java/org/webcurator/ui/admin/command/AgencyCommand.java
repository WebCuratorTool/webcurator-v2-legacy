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

import org.webcurator.core.permissionmapping.UrlUtils;

/**
 * Command object for editing an agency.
 * @author bprice
 */
public class AgencyCommand {
    /** The name of the edit agency action. */
    public static final String ACTION_EDIT = "edit";
    /** The name of the save agency action. */
    public static final String ACTION_SAVE = "save";
        /** The name of the view agencies action. */
    public static final String ACTION_VIEW = "view";
    /** The name of the New Agency action */
    public static final String ACTION_NEW ="new";
    
    /** constant name for the oid field. */ 
    public static final String PARAM_OID = "oid";
    /** constant name for the action command field. */
    public static final String PARAM_ACTION = "actionCommand";
    /** constant name for the name field. */
    public static final String PARAM_NAME = "name";
    /** constant name for the address field. */
    public static final String PARAM_ADDRESS = "address";
    /** constant name for the phone field. */
    public static final String PARAM_PHONE = "phone";
    /** constant name for the email field. */
    public static final String PARAM_EMAIL = "email";
    /** constant name for the fax field. */
    public static final String PARAM_FAX = "fax";
    /** constant name for the agency url field. */
    public static final String PARAM_AGENCY_URL = "agencyURL";
    /** constant name for the agency logo field. */
    public static final String PARAM_AGENCY_LOGO_URL = "agencyLogoURL";
    /** constant name for the agency logo field. */
    public static final String PARAM_SHOW_TASKS = "showTasks";
    /** constant name for the agencies field. */
    public static final String MDL_AGENCIES = "agencies";
    public static final String PARAM_DESCRIPTION_TYPE = "descriptionType";

    /** the action command field. */
    private String actionCommand;
    /** the agency oid field. */
    private Long oid;
    /** the name field. */
    private String name;
    /** the address field. */
    private String address;
    /** the phone field. */
    private String phone;
    /** the email field. */
    private String email;
    /** the fax field. */
    private String fax;
    /** the agency url field. */
    private String agencyURL;
    /** the agency logo url field. */
    private String agencyLogoURL;
    /** the agency show flags field. */
    private boolean showTasks;
    /** edit mode flag */
    private boolean viewOnlyMode = false;
    private String descriptionType = null;
    
	/**
	 * @return the actionCommand
	 */
	public String getActionCommand() {
		return actionCommand;
	}
	/**
	 * @param actionCommand the actionCommand to set
	 */
	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
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
	 * @return the agencyLogoURL
	 */
	public String getAgencyLogoURL() {
		return agencyLogoURL;
	}
	/**
	 * @param agencyLogoURL the agencyLogoURL to set
	 */
	public void setAgencyLogoURL(String agencyLogoURL) {
		this.agencyLogoURL = UrlUtils.fixUrl(agencyLogoURL);
	}
	/**
	 * @return the agencyURL
	 */
	public String getAgencyURL() {
		return agencyURL;
	}
	/**
	 * @param agencyURL the agencyURL to set
	 */
	public void setAgencyURL(String agencyURL) {
		this.agencyURL = UrlUtils.fixUrl(agencyURL);
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
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}
	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return true if tasks should be shown in the intray, false otherwise
	 */
	public boolean getShowTasks() {
		return showTasks;
	}

	/**
	 * @param showFlags whether to show tasks in the intray or not 
	 */
	public void setShowTasks(boolean showTasks) {
		this.showTasks = showTasks;
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
	public String getDescriptionType() {
		return descriptionType;
	}
	public void setDescriptionType(String descriptionType) {
		this.descriptionType = descriptionType;
	}
}

