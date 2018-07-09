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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.text.DecimalFormat;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.webcurator.core.report.OperationalReport;
import org.webcurator.core.report.ReportGenerator;
import org.webcurator.core.report.ResultSet;
import org.webcurator.core.report.parameter.Parameter;
import org.webcurator.core.report.parameter.StringParameter;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.domain.model.report.AbstractTargetScheduleView;

/**
 * Report of Target/Groups Schedules
 * @author oakleigh_sk
 *
 */
public class TargetGroupSchedulesReport extends HibernateDaoSupport implements ReportGenerator {

    private Log log = LogFactory.getLog(TargetGroupSchedulesReport.class);

	/**
	 * Generate report's data
	 * @param operationalReport An OperationReport
	 * @return Report's data as a <code>List</code> of <code>ResultSet</code>
	 */
	public List<ResultSet> generateData(OperationalReport operationalReport) {
		
		List<Parameter> parameters = operationalReport.getParameters();
		StringParameter agency		= (StringParameter) parameters.get(0);
		StringParameter user		= (StringParameter) parameters.get(1);
		StringParameter targetType	= (StringParameter) parameters.get(2);
		
		log.debug(" agency=" + (agency == null ? "null" : agency.getValue()) );
		log.debug(" user=" + (user == null ? "null" : user.getValue()) );
		log.debug(" targetType=" + (targetType == null ? "null" : targetType.getValue()) );
		
		return runReport(agency.getValue(), user.getValue(), targetType.getValue());
	}
	
	/**
	 * Generate the Target/Groups Schedules Report
	 * 
	 * @param agencyName Name of Agency<br><code>null</code> value is accepted
	 * @param userName User name<br><code>null</code> value is accepted
	 * @param targetType Type of Target<br><code>null</code> value is accepted
	 * @return A <code>List</code> of <code>TargetGroupSchedulesReportResultSet</code>
	 */
	@SuppressWarnings("unchecked")
	protected List<ResultSet> runReport(final String agencyName,
										final String userName,
										final String targetType){

		List results = (List) getHibernateTemplate().execute(new HibernateCallback() {

			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				Criteria query = session.createCriteria(AbstractTargetScheduleView.class);

				query.add(Restrictions.in("state", new Object[] {Target.STATE_APPROVED, TargetGroup.STATE_ACTIVE } ));
				
				if(!targetType.equals("All target types")) {
					if (targetType.equals("Target")) {
						query.add(Restrictions.eq("objectTypeDesc", "Target"));
					}
					else {
						query.add(Restrictions.eq("objectTypeDesc", "Group"));
					}
				}

				if(!userName.equals("All users")) {
					query.add(Restrictions.eq("ownerName", userName));
				}

				if(!agencyName.equals("All agencies")) {
					query.add(Restrictions.eq("agencyName", agencyName));
				}
				
				query.addOrder(Order.asc("name"));
				
				List results = query.list();
				
				List realResults = new ArrayList<TargetGroupSchedulesReportResultSet>(results.size());
				
		    	for(Iterator it = results.iterator(); it.hasNext(); ){
					
		    		AbstractTargetScheduleView rec = (AbstractTargetScheduleView)it.next();
		    		
					// parse Cron pattern
					String elements[]  = rec.getScheduleCronPattern().split(" ");
					// ignore seconds in elements[0];
					String mins        = elements[1];
					String hours       = elements[2];
					String daysOfMonth = elements[3];
					String months      = elements[4];
					String daysOfWeek  = elements[5];

					DecimalFormat df = new DecimalFormat("00");

					String time = df.format(Integer.parseInt(hours)) + ":" + df.format(Integer.parseInt(mins));;
					String dayOfWeek = "-";
					String dayOfMonth = "-";
					String month = "-";
					

					String scheduleType = "";
					int sType = rec.getScheduleType();
					if (sType == 1) {
						scheduleType = "Mondays at 9:00pm";
						dayOfWeek = daysOfWeek;
					}
					else if (sType == Schedule.CUSTOM_SCHEDULE) {
						scheduleType = "Custom";
						dayOfWeek = daysOfWeek;
						dayOfMonth = daysOfMonth;
						month = DecodeMonths(months);
					}
					else if (sType == Schedule.TYPE_ANNUALLY) {
						scheduleType = "Annually";
						dayOfMonth = daysOfMonth;
						month = DecodeMonths(months);
					}
					else if (sType == Schedule.TYPE_BI_MONTHLY) {
						scheduleType = "Bi-Monthly";
						dayOfMonth = daysOfMonth;
						month = DecodeMonths(months);
					}
					else if (sType == Schedule.TYPE_DAILY) {
						scheduleType = "Daily";
					}
					else if (sType == Schedule.TYPE_HALF_YEARLY) {
						scheduleType = "Half Yearly";
						dayOfMonth = daysOfMonth;
						month = DecodeMonths(months);
					}
					else if (sType == Schedule.TYPE_MONTHLY) {
						scheduleType = "Monthly";
						dayOfMonth = daysOfMonth;
					}
					else if (sType == Schedule.TYPE_QUARTERLY) {
						scheduleType = "Quarterly";
						dayOfMonth = daysOfMonth;
						month = DecodeMonths(months);
					}
					else if (sType == Schedule.TYPE_WEEKLY) {
						scheduleType = "Weekly";
						dayOfWeek = daysOfWeek;
					}
					else {
						scheduleType = "UNKNOWN";
					}

					try {
						String[] keys = rec.getTheKey().split(",");
						realResults.add(
								new TargetGroupSchedulesReportResultSet(
										Long.parseLong(keys[0]),
										rec.getObjectTypeDesc(),
										rec.getName(), 
										rec.getAgencyName(),
										rec.getOwnerName(),
										rec.getScheduleStartDate(),
										rec.getScheduleEndDate(),
										scheduleType,
										time,
										dayOfWeek,
										dayOfMonth,
										month));
					}
					catch (Exception e) {
						log.debug(e.getMessage());
					}
				}
				return realResults;
			}
		});
		
    	return results;
    }

	private String DecodeMonths(String encodedMonths) {
		
		String[] mths = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		String decoded = "";

		try {
			if (encodedMonths.indexOf(",") > 0) {
				// comma separated list means we're expecting a list of month
				// numbers to convert to month names.
				String[] mthNos = encodedMonths.split(",");
				for (int i = 0; i < mthNos.length; i++) {
					decoded = decoded + mths[Integer.parseInt(mthNos[i])-1];
					if (i != (mthNos.length-1)) {
							decoded = decoded + ",";
					};
				}
			} else if (encodedMonths.indexOf("/") > 0) {
				// slash separated list means we're expecting a month
				// number followed by a month offset to convert to month names.
				String[] mthNos = encodedMonths.split("/");
				for (int i = Integer.parseInt(mthNos[0]); i < 13; i = i + Integer.parseInt(mthNos[1])) {
					decoded = decoded + mths[i-1];
					if ( (i + Integer.parseInt(mthNos[1])) < 13 ) {
							decoded = decoded + ",";
					};
				}
			} else {
				// it must be a single month number so..
				decoded = decoded + mths[Integer.parseInt(encodedMonths)-1];
			}
		}
		catch (Exception e) {
			log.debug("exception in DecodeMonths(): " + e.getMessage() + "encodedMonths is: " + encodedMonths );
		}
		
		return decoded;
	}
}
