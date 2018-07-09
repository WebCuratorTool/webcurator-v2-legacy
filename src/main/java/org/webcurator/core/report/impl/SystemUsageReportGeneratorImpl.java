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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.webcurator.core.report.OperationalReport;
import org.webcurator.core.report.ReportGenerator;
import org.webcurator.core.report.ResultSet;
import org.webcurator.core.report.dto.LogonUserDTO;
import org.webcurator.core.report.parameter.DateParameter;
import org.webcurator.core.report.parameter.Parameter;
import org.webcurator.core.report.parameter.StringParameter;
import org.webcurator.domain.model.report.LogonDuration;

/**
 * ReportGenerator implementation for a System Usage Report
 * 
 * @author MDubos
 */
public class SystemUsageReportGeneratorImpl extends HibernateDaoSupport 
	implements ReportGenerator {
	
	private Log log = LogFactory.getLog(SystemUsageReportGeneratorImpl.class);
	
	/**
	 * Generate report's data
	 * @param operationalReport An OperationReport
	 * @return Report's data as a <code>List</code> of <code>ResultSet</code>
	 */
	public List<ResultSet> generateData(OperationalReport operationalReport) {
				
		// Parameters
		List<Parameter> parameters = operationalReport.getParameters();
		DateParameter startDate =	(DateParameter)	parameters.get(0);
		DateParameter endDate =		(DateParameter) parameters.get(1);
		StringParameter agency = 	(StringParameter) parameters.get(2);
		
		SimpleDateFormat sdf = startDate.getDateFormat();
		log.debug(" startDate=" + sdf.format(startDate.getValue()));
		log.debug(" endDate=" + sdf.format(endDate.getValue()));
		log.debug(" agency=" + (agency == null ? "null" : agency.getValue()) );
		
		List<ResultSet> res = getSystemUsageReport(startDate.getValue(), endDate.getValue(), agency.getValue());
		
		return res;
	}
	
	
	/**
	 * Generate the System Usage Report's data
	 * 
	 * @param startDate Start date<br>Is inclusive and value is mandatory
	 * @param endDate End date.<br>Is exclusive and value is mandatory
	 * @param agencyName Name of Agency<br><code>null</code> value is accepted
	 * @return A <code>List</code> of <code>SystemUsageReportResultSet</code>
	 */
	protected List<ResultSet> getSystemUsageReport(Date startDate, Date endDate, String agencyName){
		
		Calendar now = Calendar.getInstance();
		String query = LogonDuration.QRY_LOGGED_USERS_BY_PERIOD_BY_AGENCY;
		Object[] params = new Object[] { 
				startDate, 
				endDate,
				now.getTime(),
				endDate,
				endDate,
				agencyName, agencyName, agencyName };
		
		
		// Get all logged users 		
		List results = getHibernateTemplate().findByNamedQuery(query, params);
		
		log.debug("results=" + results.size());
		
		ArrayList<ResultSet> resultSets = new ArrayList<ResultSet>();
		for(Iterator it = results.iterator(); it.hasNext(); ){
			
			LogonUserDTO lud = (LogonUserDTO) it.next();
			
			// late user who is still logged in
			if(lud.getDuration() == null){
				Long duration = LogonDuration.computeDuration(lud.getLogonTime(), now.getTime());//(long)(now.getTime()/1000 - lud.getLogonTime().getTime()/1000);
				lud.setDuration(duration);
				log.debug("    still logged in, duration to now=" + duration.toString() );
			}
			
			// Wrap into SystemUsageReportResultSet
			SystemUsageReportResultSet rs = new SystemUsageReportResultSet(lud);
			resultSets.add(rs);
		}
		
		return resultSets;
	}
	

}
