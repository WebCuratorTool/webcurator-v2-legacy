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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.webcurator.core.report.OperationalReport;
import org.webcurator.core.report.ReportGenerator;
import org.webcurator.core.report.ResultSet;
import org.webcurator.core.report.parameter.Parameter;
import org.webcurator.core.report.parameter.StringParameter;
import org.webcurator.domain.model.report.AbstractTargetScheduleView;

/**
 * Summary Report of Target/Group Schedules
 * @author oakleigh_sk
 *
 */
public class SummaryTargetSchedulesReport extends HibernateDaoSupport implements ReportGenerator {

	private static String[] schedules = {"Mondays at 9:00pm", "Custom", "Daily", "Weekly", "Monthly", "Bi-Monthly", "Quarterly", "Half-Yearly", "Annually" };

    private Log log = LogFactory.getLog(SummaryTargetSchedulesReport.class);

	/**
	 * Generate report's data
	 * @param operationalReport An OperationReport
	 * @return Report's data as a <code>List</code> of <code>ResultSet</code>
	 */
	public List<ResultSet> generateData(OperationalReport operationalReport) {
		
		List<Parameter> parameters = operationalReport.getParameters();
		StringParameter agency	   = (StringParameter) parameters.get(0);
		
		log.debug(" agency=" + (agency == null ? "null" : agency.getValue()) );
		
		return runReport(agency.getValue());
	}
	

	/**
	 * Generate the Summary Target/Groups Schedules Report
	 * 
	 * @param agencyName Name of Agency<br><code>null</code> value is accepted
	 * @return A <code>List</code> of <code>SummaryTargetSchedulesReportResultSet</code>
	 */
	@SuppressWarnings("unchecked")
	protected List<ResultSet> runReport(final String agencyName) {

		String query = AbstractTargetScheduleView.QRY_GET_SUMMARY_STATS_BY_AGENCY;
		Object[] params = new Object[] { agencyName, agencyName, agencyName };
		
		
		// Get the results 		
		List results = getHibernateTemplate().findByNamedQuery(query, params);
		
		// Iterate over results identifying the number of distinct 
		// agencies. Store them in agencies list.
		List<String> agencies = new ArrayList<String>();
    	for(Iterator it = results.iterator(); it.hasNext(); ){
    		Object[] obj = (Object[])it.next();
    		String agency   = (String)obj[0];
    		if (!agencies.contains(agency)) {
    			agencies.add(agency);
    		}
    	}
    	
    	// create columnNames
    	String[] columnNames = new String[agencies.size()+1];
    	columnNames[0] = "ScheduleType";
    	for (Integer i = 1; i < (agencies.size()+1); i++) {
    		columnNames[i] = "count"+i.toString();
    	}
    	
    	// create columnHTMLNames
    	String[] columnHTMLNames = new String[agencies.size()+1];
    	columnHTMLNames[0] = "Schedule Type";
    	Iterator it = agencies.iterator();
    	for (Integer i = 1; i < (agencies.size()+1); i++) {
    		
    		columnHTMLNames[i] = (String) it.next();
    	}
    	
		
    	List realResults = new ArrayList<SummaryTargetSchedulesReportResultSet>();
		
		for (int i = 0; i < schedules.length; i++ ) {
	    	
			// create displayableFields array
	    	String[] displayableFields = new String[agencies.size()+1];
	    	displayableFields[0] = schedules[i];
	    	for (Integer j = 1; j < (agencies.size()+1); j++) {
	    		
	    		displayableFields[j] = "0";
	    	}
	    	populateCounts(displayableFields, agencies, results);
			
	    	realResults.add(
					new SummaryTargetSchedulesReportResultSet(
							columnNames,
							columnHTMLNames,
							displayableFields));
		}

		return realResults;
		
    }    
	
	private void populateCounts(String[] displayableFields, List<String> agencies, List results) {
		
		// Iterate over results 'rows' and populate elements in displayableFields..
    	for(Iterator it = results.iterator(); it.hasNext(); ) {
    		
    		Object[] obj    = (Object[])it.next();
    		String agency   = (String)obj[0];
    		String schedule = (String)obj[1];
    		Integer count   = (Integer)obj[2];
    		
    		if (schedule.equals(displayableFields[0])) {
    			int col = agencies.indexOf(agency);
    			displayableFields[col+1] = count.toString();
    		}
    	}
		
	}
}
