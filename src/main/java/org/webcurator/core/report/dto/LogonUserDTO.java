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
package org.webcurator.core.report.dto;

import java.util.Date;

/**
 * DTO for a LogonUser
 * 
 * @author MDubos
 *
 */
public class LogonUserDTO {
	
	private Long userOid;
	private String userName;
	private String firstname;
    private String lastname;
    private Boolean activeAccount;
    private Date dateOfDisabledAccount;
    private Date logonTime; 
    private Long duration;
    
    /**
     * Default constructor
     *
     */
    public LogonUserDTO(){
    }
    
    /**
     * Constructor with full parameters
     * @param userOid User ID
     * @param userName User name
     * @param firstname User first name
     * @param lastname USer last name
     * @param activeAccount If user's account is active
     * @param dateOfDisabledAccount Date of user's account disable, in case it is not active anymore
     * @param logonTime User's logon time
     * @param duration Duration of user's logon
     */
    public LogonUserDTO(Long userOid, String userName, String firstname, 
    		String lastname, Boolean activeAccount, Date dateOfDisabledAccount, 
    		Date logonTime, Long duration){
    	this.userOid = userOid;
    	this.userName = userName;
    	this.firstname = firstname;
    	this.lastname = lastname;
    	this.activeAccount = activeAccount;
    	this.dateOfDisabledAccount = dateOfDisabledAccount;
    	this.logonTime = logonTime;
    	this.duration = duration;
    }

	/**
	 * @return Returns the activeAccount.
	 */
	public Boolean getActiveAccount() {
		return activeAccount;
	}

	/**
	 * @param activeAccount The activeAccount to set.
	 */
	public void setActiveAccount(Boolean activeAccount) {
		this.activeAccount = activeAccount;
	}

	/**
	 * @return Returns the dateOfDisabledAccount.
	 */
	public Date getDateOfDisabledAccount() {
		return dateOfDisabledAccount;
	}

	/**
	 * @param dateOfDisabledAccount The dateOfDisabledAccount to set.
	 */
	public void setDateOfDisabledAccount(Date dateOfDisabledAccount) {
		this.dateOfDisabledAccount = dateOfDisabledAccount;
	}

	/**
	 * @return Returns the duration.
	 */
	public Long getDuration() {
		return duration;
	}

	/**
	 * @param duration The duration to set.
	 */
	public void setDuration(Long duration) {
		this.duration = duration;
	}

	/**
	 * @return Returns the firstname.
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname The firstname to set.
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return Returns the lastname.
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname The lastname to set.
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return Returns the logonTime.
	 */
	public Date getLogonTime() {
		return logonTime;
	}

	/**
	 * @param logonTime The logonTime to set.
	 */
	public void setLogonTime(Date logonTime) {
		this.logonTime = logonTime;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return Returns the userOid.
	 */
	public Long getUserOid() {
		return userOid;
	}

	/**
	 * @param userOid The userOid to set.
	 */
	public void setUserOid(Long userOid) {
		this.userOid = userOid;
	}
    
    
}
