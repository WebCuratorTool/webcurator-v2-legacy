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
import org.webcurator.domain.model.audit.Audit;
import org.webcurator.ui.util.DateUtils;

/**
 * ResultSet for the System Activity Report<br>
 * 
 * @author MDubos
 *
 */
public class SystemActivityReportResultSet implements ResultSet {
	
	private static final SimpleDateFormat sdf = DateUtils.get().getDateFormat("core.common.fullDateMask");
	
	// Plain text rendering for column names 
	private static final String[] columnNames = 
		{
		"Date",
		"Username",
		"User ID",
		"Activity",
		"Description"
		};
	
	// HTML rendering for column names
	private static final String[] columnHTMLNames = 
		{
		"Date<br><font size=\"1\"><i>"+ sdf.toPattern()+"</i></font>",
		"Username",
		"User ID",
		"Activity",
		"Description"		
		};
	
	private ArrayList<Object> resultSet = new ArrayList<Object>(columnNames.length);
	
	private String activity;
	private Date date;
	private Long userID;
	private String username;
	private String description;
	
	/**
	 * Default constructor
	 */
	public SystemActivityReportResultSet(){
	}
	
	
	/**
	 * Constructor intitialized from an <code>Audit</code>
	 * @param audit An audit object result from a query
	 */
	public SystemActivityReportResultSet(Audit audit){
		setDate(audit.getDateTime());
		setActivity(audit.getAction());
		setUsername(audit.getUserName());
		setUserID(audit.getUserOid());
		setDescription(audit.getMessage());
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
				(date == null ? "" : sdf.format(date)),
				(username == null ? "" : username),
				(userID == null ? "" : userID.toString()),
				(activity == null ? "" : activity),
				(description == null ? "" : description)
		};
	}


	/**
	 * @return Returns the activity.
	 */
	public String getActivity() {
		return activity;
	}


	/**
	 * @param activity The activity to set.
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}


	/**
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}


	/**
	 * @param date The date to set.
	 */
	public void setDate(Date date) {
		this.date = date;
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
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}


	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
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
	
}
