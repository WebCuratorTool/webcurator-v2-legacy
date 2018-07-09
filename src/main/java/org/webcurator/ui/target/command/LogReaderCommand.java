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
package org.webcurator.ui.target.command;

import java.util.regex.*;

/**
 * The command object for the log viewer.
 * @author nwaight
 */
public class LogReaderCommand {
	/** The name of the log file noOfLines model object. */
	public static final String MDL_LINES = "lines";
	public static final String MDL_MISSINGELEMENTS = "missingElements";
	public static final String MDL_FILTER_NAMES = "filterNames";
	public static final String MDL_FILTER_TYPES = "filterTypes";
	
	/** The name of the primary key field. */
	public static final String PARAM_OID = "targetInstanceOid";
	/** The name of the log file name field. */
	public static final String PARAM_LOGFILE = "logFileName";
	/** the name of the number of noOfLines to display field. */
	public static final String PARAM_LINES = "noOfLines";
	/** The name of the filter field. */
	public static final String PARAM_FILTER = "filter";	
	/** The name of the filter type field. */
	public static final String PARAM_FILTER_TYPE = "filterType";
	/** The name of the number of lines in the file field. */
	public static final String PARAM_NUM_LINES = "numLines";
	/** The name of the line numbers toggle. */
	public static final String PARAM_SHOW_LINE_NUMBERS = "showLineNumbers";
	
	/** The name of the head value. */
	public static final String VALUE_HEAD = "head";
	/** The name of the tail value. */
	public static final String VALUE_TAIL = "tail";
	/** The name of the line value. */
	public static final String VALUE_FROM_LINE = "fromLine";
	/** The name of the match value. */
	public static final String VALUE_REGEX_MATCH = "match";
	/** The name of the contain value. */
	public static final String VALUE_REGEX_CONTAIN = "contain";
	/** The name of the indent value. */
	public static final String VALUE_REGEX_INDENT = "indent";
	/** The name of the indent value. */
	public static final String VALUE_TIMESTAMP = "timestamp";
	
	/** The primary key of the target instance. */
	private Long targetInstanceOid = null;
	/** The name of the target. */
	private String targetName = null;
	/** The name of the log file to display. */
	private String logFileName = "";
	/** the number of noOfLines to display. */
	private String noOfLines = "0";
	/** The filter to use to filter the file. */
	private String filter = "";
	/** Number of lines in the file. */
	private Integer numLines = -1;
	/** Show line numbers. */
	private boolean showLineNumbers = false;
	/** regex type field. */
	private String filterType = VALUE_TAIL;

	/**
	 * @return the logFileName
	 */
	public String getLogFileName() {
		return logFileName;
	}

	/**
	 * @param logFileName the logFileName to set
	 */
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}

	/**
	 * @return true if timestamp should be shown
	 */
	public boolean getShowTimestamp()
	{
		return logFileName.endsWith(".log");
	}
	
	/**
	 * @return the targetInstanceOid
	 */
	public Long getTargetInstanceOid() {
		return targetInstanceOid;
	}

	/**
	 * @param targetInstanceOid the targetInstanceOid to set
	 */
	public void setTargetInstanceOid(Long targetInstanceOid) {
		this.targetInstanceOid = targetInstanceOid;
	}

	/**
	 * @return the noOfLines
	 */
	public String getNoOfLines() {
		return noOfLines;
	}

	/**
	 * @return the noOfLines
	 */
	public int getNoOfLinesInt() {
		return new Integer(noOfLines).intValue();
	}

	/**
	 * @param lines the number of lines to set
	 */
	public void setNoOfLines(String lines) {
		this.noOfLines = lines;
	}

	/**
	 * @param lines the number of lines to set
	 */
	public void setNoOfLines(Integer lines) {
		this.noOfLines = lines.toString();
	}

	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * @return the number of lines
	 */
	public Integer getNumLines() {
		return numLines;
	}

	/**
	 * @param numLines the number of lines to set
	 */
	public void setNumLines(Integer numLines) {
		this.numLines = numLines;
	}

	/**
	 * @return the showLineNumbers flag
	 */
	public boolean getShowLineNumbers() {
		return showLineNumbers;
	}

	/**
	 * @param showLineNumbers the flag to set
	 */
	public void setShowLineNumbers(boolean showLineNumbers) {
		this.showLineNumbers = showLineNumbers;
	}

	/**
	 * @return the timestamp
	 */
	public Long getLongTimestamp() {
		
		 Pattern logPattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
		 Pattern logDatePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
		 Pattern dateTimePattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}");
		 Pattern datePattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
		 Pattern longPattern = Pattern.compile("\\d{14}");
		 StringBuilder sb = new StringBuilder();
		 
		 if(logPattern.matcher(filter).matches())
		 {
			 sb.append(filter.substring(0, 4));
			 sb.append(filter.substring(5, 7));
			 sb.append(filter.substring(8, 10));
			 sb.append(filter.substring(11, 13));
			 sb.append(filter.substring(14, 16));
			 sb.append(filter.substring(17, 19));
			 return new Long(sb.toString());
		 }
		 else if(logDatePattern.matcher(filter).matches())
		 {
			 sb.append(filter.substring(0, 4));
			 sb.append(filter.substring(5, 7));
			 sb.append(filter.substring(8, 10));
			 sb.append("000000");
			 return new Long(sb.toString());
		 }
		 else if(dateTimePattern.matcher(filter).matches())
		 {
			 sb.append(filter.substring(6, 10));
			 sb.append(filter.substring(3, 5));
			 sb.append(filter.substring(0, 2));
			 sb.append(filter.substring(11, 13));
			 sb.append(filter.substring(14, 16));
			 sb.append(filter.substring(17, 19));
			 return new Long(sb.toString());
		 }
		 else if(datePattern.matcher(filter).matches())
		 {
			 sb.append(filter.substring(6, 10));
			 sb.append(filter.substring(3, 5));
			 sb.append(filter.substring(0, 2));
			 sb.append("000000");
			 return new Long(sb.toString());
		 }
		 else if(longPattern.matcher(filter).matches())
		 {
			 return new Long(filter);
		 }
		 else 
		 {
			 return -1L;
		 }
	}

	/**
	 * @return the filterType
	 */
	public String getFilterType() {
		return filterType;
	}

	/**
	 * @param filterType the filterType to set
	 */
	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getTargetName() {
		return targetName;
	}
	
}
