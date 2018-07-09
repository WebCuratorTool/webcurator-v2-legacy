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

import java.util.Iterator;
import java.util.List;

import org.webcurator.core.report.parameter.Parameter;

/**
 * Generic Report
 * 
 * @author MDubos
 */
public class Report {

	// Name of the report
	private String name;
	
	// Description of the report
	private String description;

	// Information
	private String info;
	
	// Parameters of the report
	private List<Parameter> parameters;
	
	// Interface for generating report's data
	private ReportGenerator reportGenerator;
	
	
	/**
	 * Get the report's data generator 
	 * @return ReportGenerator
	 */
	public ReportGenerator getReportGenerator() {
		return reportGenerator;
	}

	/**
	 * Set the report's data generator
	 * @param reportGenerator ReportGenerator
	 */
	public void setReportGenerator(ReportGenerator reportGenerator) {
		this.reportGenerator = reportGenerator;
	}

	/**
	 * Get the name of the report
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set name of report
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the description of the report
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set description of report
	 * @param name
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the report's parameters
	 * @return The list of Parameters
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}


	/**
	 * Set report's parameters
	 * @param parameters
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Get the additional information
	 * @return The additional information
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * Set additional information to be displayed to the user
	 * @param information A textual information
	 */
	public void setInfo(String information) {
		this.info = information;
	}

	/**
	 * Convenient method for displaying all parameters of the report
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(getName() + "\n");
		for(Parameter param : getParameters()){
			sb.append(param.toString() + "\n");
		}		
		return sb.toString();
	}
	
	
	/**
	 * Returns the Report name of the Report containing at least 
	 * one Parameter which has been	previously recorded, i.e one 
	 * parameter that has its property 
	 * {@link Parameter#getSelectedValue()} not <code>null</code>.
	 * 
	 * @return A Report name of a Report from the list that 
	 * contains at leat one parameter previoulsy selected.
	 * Returns an empty <code>String</code> otherwise
	 */
	public static String getSelectedReport(List reports) {
		String result = "";
		for(Iterator itReports = reports.iterator(); "".equals(result) && itReports.hasNext(); ){
			Report report = (Report)itReports.next();			
			for(Iterator itParameters = report.getParameters().iterator(); "".equals(result) && itParameters.hasNext(); ) {
				Parameter prmt = (Parameter)itParameters.next();
				if(prmt.getSelectedValue() != null){
					result = report.getName();
				}
			}
		}			
		return result;
	}
}
