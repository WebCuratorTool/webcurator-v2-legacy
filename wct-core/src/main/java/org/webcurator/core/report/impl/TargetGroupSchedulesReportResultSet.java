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
import java.util.Date;
import java.util.List;

import org.webcurator.core.report.ResultSet;
import org.webcurator.ui.util.DateUtils;

/**
 * ResultSet for a Target/Groups Schedules Report.
 * @author oakleigh_sk
 */
public class TargetGroupSchedulesReportResultSet implements ResultSet {

	private static final SimpleDateFormat sdf = DateUtils.get().getDateFormat("core.common.fullDateMask");
	
	private Long oid;
	private String targetOrGroup;
	private String name;
	private String agency;
	private String owner;
	private Date fromDate;
	private Date toDate;
	private String scheduleType;
	private String time;
	private String dayOfWeek;
	private String dayOfMonth;
	private String month;
	
	// Plain text rendering for column names 
	private static final String[] columnNames = 
	{
		"ID",
		"Target/Group",
		"Name",
		"Agency",
		"Owner",
		"From Date",
		"To Date",
		"Schedule Type",
		"Time",
		"Day of Week",
		"Day of Month",
		"Month"
	};
	
	// HTML rendering for column names
	private static final String[] columnHTMLNames = 
	{
		"ID",
		"Target/Group",
		"Name",
		"Agency",
		"Owner",
		"From Date<br><font size=\"1\"><i>"+ sdf.toPattern()+"</i></font>",
		"To Date<br><font size=\"1\"><i>"+ sdf.toPattern()+"</i></font>",
		"Schedule Type",
		"Time",
		"Day of Week",
		"Day of Month",
		"Month"
	};
	
	public TargetGroupSchedulesReportResultSet(Long oid, String targetOrGroup, String name, String agency, String owner, Date fromDate, Date toDate, String scheduleType, String time, String dayOfWeek, String dayOfMonth, String month) {
		super();
		this.oid = oid;
		this.targetOrGroup = targetOrGroup;
		this.name = name;
		this.agency = agency;
		this.owner = owner;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.scheduleType = scheduleType;
		this.time = time;
		this.dayOfWeek = dayOfWeek;
		this.dayOfMonth = dayOfMonth;
		this.month = month;
	}

	public String[] getColumnHTMLNames() {
		return columnHTMLNames;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public String[] getDisplayableFields() {
		return new String[] {
			oid.toString(),
			targetOrGroup,
			name,
			agency,
			owner,
			sdf.format(fromDate),
			(toDate == null ? "" : sdf.format(toDate)),
			scheduleType,
			time,
			dayOfWeek,
			dayOfMonth.toString(),
			month
		};
	}

	public List<Object> getFields() {
		// TODO Auto-generated method stub
		return null;
	}

}
