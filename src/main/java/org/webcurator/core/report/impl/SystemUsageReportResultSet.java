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
package org.webcurator.core.report.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.webcurator.core.report.ResultSet;
import org.webcurator.core.report.dto.LogonUserDTO;
import org.webcurator.core.util.Utils;
import org.webcurator.ui.util.DateUtils;


/**
 * ResultSet for a System Usage Report
 * @author mdubos
 *
 */
public class SystemUsageReportResultSet implements ResultSet {

	public static final SimpleDateFormat sdf = DateUtils.get().getDateFormat("core.common.fullDateMask");

	// Plain text renderig for column names
	private static final String[] columnNames = 
	{ 
		"Username", 
		"Account", 
		"Disable Date", 
		"Login", 
		"Duration"
	};
	
	// HTML rendering for column names
	private static final String[] columnHTMLNames = 
	{ 
		"Username", 
		"Account", 
		"Disable Date", 
		"Login<br><font size=\"1\"><i>"+sdf.toPattern()+"</i></font>", 
		"Duration<br><font size=\"1\"><i>d:hh:mm:ss</i></font>"
	};

	
	private ArrayList<Object> resultSet = new ArrayList<Object>(columnNames.length);
	
	
	private Long userID;
	private String userName;
	private String firstname;
    private String lastname;
    private Boolean activeAccount;
    private Date dateOfDisabledAccount;
    private Date logonTime; 
    private Long duration;
	
    /**
	 * Default constructor
	 */
	public SystemUsageReportResultSet(){
	}
	
	/**
	 * Constructor initialized from a <code>LogonUserDTO</code>
	 * @param logonUserDTO A logonUserDTO result from a query 
	 */
	public SystemUsageReportResultSet(LogonUserDTO logonUserDTO){
		this.userID = logonUserDTO.getUserOid();
		this.userName = logonUserDTO.getUserName();
		this.firstname = logonUserDTO.getFirstname();
		this.lastname = logonUserDTO.getLastname();
		this.activeAccount = logonUserDTO.getActiveAccount();
		this.dateOfDisabledAccount = logonUserDTO.getDateOfDisabledAccount();
		this.logonTime = logonUserDTO.getLogonTime();
		this.duration = logonUserDTO.getDuration();
	}
	
	
	/** Get column names */
	public String[] getColumnNames() {
		return columnNames;
	}

	/** Get column names in a HTML display */
	public String[] getColumnHTMLNames() {
		return columnHTMLNames;
	}

	/** Get a list of all results */
	public List<Object> getFields(){
		return resultSet;
	}
	
	/** Rendering of a row */
	public String[] getDisplayableFields(){
		return new String[]{
				
			(userName == null ? "" : userName),
			(activeAccount == null ? "" :  
				(activeAccount.booleanValue() ? "Active" : "Disabled")
			),
			(activeAccount != null && ! activeAccount.booleanValue() && dateOfDisabledAccount != null ?
					sdf.format(dateOfDisabledAccount) : ""),// display date if account is active
			(logonTime == null ? "" : sdf.format(logonTime)),
			(duration == null ? "" : Utils.getDDhhmmss(duration))

		};
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
	 * @return Returns the resultSet.
	 */
	public ArrayList<Object> getResultSet() {
		return resultSet;
	}

	/**
	 * @param resultSet The resultSet to set.
	 */
	public void setResultSet(ArrayList<Object> resultSet) {
		this.resultSet = resultSet;
	}

	/**
	 * @return Returns the userID.
	 */
	public Long getUserID() {
		return userID;
	}

	/**
	 * @param userID The userID to set.
	 */
	public void setUserID(Long userID) {
		this.userID = userID;
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
	



	
}
