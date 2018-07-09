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
package org.webcurator.core.report;

import java.util.List;

import org.webcurator.core.report.impl.SystemActivityReportResultSet;
import org.webcurator.core.report.impl.SystemUsageReportResultSet;

/**
 * Generic result of a report's query.<br>
 * <br>
 * Each <code>ResultSet</code> defines results and how they are
 * rendered.<br>
 * <br>
 * All known implementing subclasses:<br>
 * <br>
 * {@link SystemActivityReportResultSet}
 * {@link SystemUsageReportResultSet}
 * {@link CrawlerActivityReportResultSet}
 * {@link TargetGroupSchedulesReportResultSet}
 * 
 * @author MDubos
 *
 */
public interface ResultSet {

	/** Get column names */
	public String[] getColumnNames();
	
	/** Get column names in a HTML display */
	public String[] getColumnHTMLNames();
	
	/** Get a list of all results */
	public List<Object> getFields();
	
	/** Rendering of a row */
	public String[] getDisplayableFields();
	
	
}
