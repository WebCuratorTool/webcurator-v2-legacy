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
 * ResultSet for a report that collects the history of a Harvest Agent.
 * @author beaumontb
 */
public class CrawlerActivityReportResultSet implements ResultSet {

	private static final SimpleDateFormat sdf = DateUtils.get().getDateFormat("core.common.fullDateMask");
	
	private Long oid;
	private String targetName;
	private String status;
	private Date startDate;
	private Date endDate;
	private Long crawlDuration;
	private Long bytesDownloaded;
	private String harvestAgent;
	
	// Plain text rendering for column names 
	private static final String[] columnNames = 
		{
		"Id",
		"Target Name",
		"Status",
		"Start Date",
		"End Date",
		"Crawl Duration",
		"Bytes Downloaded",
		"Harvest Agent"
		};
	
	// HTML rendering for column names
	private static final String[] columnHTMLNames = 
		{
		"Id",
		"Target Name",
		"Status",
		"Start Date",
		"End Date",
		"Crawl Duration",
		"Bytes Downloaded",
		"Harvest Agent"	
		};	
	
	public CrawlerActivityReportResultSet(Long oid, String targetName, String status, Date startDate, Date endDate, Long crawlDuration, Long bytesDownloaded, String harvestAgent) {
		super();
		this.oid = oid;
		this.targetName = targetName;
		this.status = status;
		this.startDate = startDate;
		this.endDate = endDate;
		this.crawlDuration = crawlDuration;
		this.bytesDownloaded = bytesDownloaded;
		this.harvestAgent = harvestAgent;
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
			targetName,
			status,
			sdf.format(startDate),
			endDate == null ? "" : sdf.format(endDate),
			crawlDuration.toString(),
			bytesDownloaded.toString(),
			harvestAgent
		};
	}

	public List<Object> getFields() {
		// TODO Auto-generated method stub
		return null;
	}

}
