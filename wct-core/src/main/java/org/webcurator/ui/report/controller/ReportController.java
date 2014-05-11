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
package org.webcurator.ui.report.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.core.report.OperationalReport;
import org.webcurator.core.report.Report;
import org.webcurator.core.report.ReportGenerator;
import org.webcurator.core.report.ReportManager;
import org.webcurator.core.report.parameter.Parameter;
import org.webcurator.core.report.parameter.ParameterFactory;
import org.webcurator.core.report.parameter.ReportCommandParsing;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.report.command.ReportCommand;

/**
 * ReportController
 * @author MDubos
 *
 */
public class ReportController extends AbstractFormController {
	
	private Log log = LogFactory.getLog(ReportController.class);
	
	private ReportManager reportMngr;
		
    /**
     * Default constructor
     *
     */
    public ReportController() {
        setCommandClass(ReportController.class);
    }
	
	@Override
	protected ModelAndView showForm(HttpServletRequest req,
			HttpServletResponse resp, BindException exc) throws Exception {
	
		// Initialise parameters selectValues
		for(Report report : getReportMngr().getReports()){
			for(Parameter parameter : report.getParameters()){
				parameter.setSelectedValue(null);
			}
		}
		
		req.getSession().setAttribute("selectedRunReport", "");
		ModelAndView mav = new ModelAndView();
		mav.addObject("reports", getReportMngr().getReports());
		mav.setViewName("reporting");
		
		log.debug("nb reports=" + getReportMngr().getReports().size());
				
		return mav;
	}
	

	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request,
			HttpServletResponse response, Object comm, BindException errors)
			throws Exception {
			
		ReportCommand com = (ReportCommand) comm;
		
		// Build parameters
		long start = System.currentTimeMillis();
		ArrayList<Parameter> parameters = parseParameters(com);		
		log.debug("Parsing parameters took " + (System.currentTimeMillis()-start) + "ms");
				
		request.getSession().setAttribute("selectedRunReport", com.getSelectedReport());
		ModelAndView mav = new ModelAndView();
		if (errors.hasErrors()) {
			
			mav.addObject(Constants.GBL_ERRORS, errors);
			mav.addObject("reports", getReportMngr().getReports());
			mav.setViewName("reporting");
			
		} else {
					
			// Retreive reportGenerator & info  
			String info = "";
			ReportGenerator reportGenerator = null;
			for(Report rep : reportMngr.getReports()){
				if(rep.getName().equals(com.getSelectedReport())){
					info = rep.getInfo();
					reportGenerator = rep.getReportGenerator();
				}
			}
			
			// Build an AbstractReport
			OperationalReport operationalReport = new OperationalReport(
					com.getSelectedReport(), info, parameters, reportGenerator);
			
			// Store in session
			request.getSession().setAttribute("operationalReport", operationalReport);
			
			mav.setViewName("reporting-preview");

		}
		
		return mav;
	}
	
//	/**
//	 * Custom editor for certain fields
//	 */
//	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
//		super.initBinder(request, binder);
//		binder.registerCustomEditor(List.class, "parameters", new ParameterEditor(List.class,true));
//		//binder.registerCustomEditor(List.class, "parameters", new ParameterEditor(List.class,true));
//	}

	
	/**
	 * @return Returns the ReportManager
	 */
	public ReportManager getReportMngr() {
		return reportMngr;
	}

	/**
	 * 
	 * @param reportMngr Returns the ReportManager
	 */
	public void setReportMngr(ReportManager reportMngr) {
		this.reportMngr = reportMngr;
	}


	/**
	 * Build a List of {@link Parameter} from a {@link ReportCommand}
	 * @param reportCommand The ReportCommand to parse
	 * @return List of parameters
	 */
	protected ArrayList<Parameter> parseParameters(ReportCommand reportCommand){
		final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		
		ReportCommandParsing rcp = new ReportCommandParsing(reportCommand){
						
			public void doOnEndOfParameterParsing(ReportCommand command, Object[] parameterProperties, Errors errors){
				// Create + validate
				Parameter parameter = ParameterFactory.buildParameter(parameterProperties, errors);
				parameters.add(parameter);
				// Record if valid & not optional (null otherwise)
				Object selected = parameter.getValue();
				// Update the reportMngr.reports
				for(Report report : reportMngr.getReports()){
					if(command.getSelectedReport().equals(report.getName())){
						for(Parameter parameterInReports : report.getParameters()){
							if(parameter.getName().equals(parameterInReports.getName()) 
									//&& !parameter.getOptional().booleanValue()
									){
								parameterInReports.setSelectedValue(selected);
							}
						}
					}
				}
				
			}
			
			public void doOnEnd(ReportCommand reportCommand){
				// Set to null all other parameters.selectedValue into reportMngr.reports
				for(Report report : reportMngr.getReports()){
					if(!reportCommand.getSelectedReport().equals(report.getName())){
						for(Parameter parameterInReports : report.getParameters()){
							parameterInReports.setSelectedValue(null);
						}
					}
				}
			}
			
		};
		
		try {
			rcp.parse();
		} catch (Exception e) {
			log.error("Cannot parse parameters: " + e.getMessage());
		}
		return parameters; 
	}
}
