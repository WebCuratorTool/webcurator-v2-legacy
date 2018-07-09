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
package org.webcurator.ui.report.command;

import java.util.List;


/**
 * Command for reporting.jsp <br>
 * <br>
 * When a report has been selected, allows to get the report's
 * name and its parameters. 
 * See also {@link org.webcurator.ui.report.controller.ReportController}
 * 
 * @author MDubos
 *
 */
public class ReportCommand {

	// Name of the selected report
	private String selectedReport;
	
	// List of parameters
	private List<String> parameters;	
	
	/**
	 * Get name of the report
	 * @return Name
	 */
	public String getSelectedReport() {
		return selectedReport;
	}
	
	/**
	 * Set report's name
	 * @param reportName
	 */
	public void setSelectedReport(String reportName) {
		this.selectedReport = reportName;
	}
	
	/**
	 * Get the list of parameters
	 * 
	 * @return <code>List</code> of <code>String</code> 
	 */
	public List<String> getParameters() {
		return parameters;
	}
	/**
	 * 
	 * @param parameters Set the Parameters
	 */
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}
	

}
