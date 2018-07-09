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
package org.webcurator.core.report.parameter;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.webcurator.ui.report.command.ReportCommand;

/**
 * Utility class containing the algorithm for parsing a <code>ReportCommand</code>.<br>
 * <br>
 * Used for building Parameters as well as for validating each Parameter. Actually 
 * the validation attempts to create parameters that will individually auto-validate
 * themselves. The parameters resulting from the validation cannot be kept for the 
 * <code>ReportController</code> so the <code>ReportController</code> has to perform 
 * a second building of parameters.
 *  
 * @author MDubos
 *
 */
public class ReportCommandParsing {
	
	/** Index of a Parameter <code>name</code> attribute  */
	public static final int NAME =	0;
	/** Index of a Parameter <code>value</code> attribute  */
	public static final int VALUE = 1;
	/** Index of a Parameter <code>type</code> attribute  */
	public static final int TYPE = 2;
	/** Index of a Parameter <code>description</code> attribute  */
	public static final int DESCRIPTION = 3;
	/** Index of a Parameter <code>optional</code> attribute  */
	public static final int OPTIONAL = 4;

	
	private Log log = LogFactory.getLog(ReportCommandParsing.class);
		
		
	private ReportCommand reportCommand;
	private Errors errors;
	
	
	/**
	 * Constructor for parsing the <code>ReportCommand</code> without 
	 * the need of validation 
	 *
	 * @param reportCommand ReportCommand to parse 
	 */
	public ReportCommandParsing(ReportCommand reportCommand) {
		this.reportCommand = reportCommand;
	}
	
	
	/**
	 * Constructor for parsing the <code>ReportCommand</code> and
	 * perform a validation
	 * @param reportCommand ReporCommand to parse
	 * @param errors Object handling errors
	 */
	public ReportCommandParsing(ReportCommand reportCommand, Errors errors) {
		this.reportCommand = reportCommand;
		this.errors = errors;
	}
		

	/**
	 * Parse all <code>String</code> parameters contained in a <code>ReportCommand</code>.
	 * Each parameter is read one by one. Order of input fields matters and has to be 
	 * name/value/type/description/optional in the ReportCommand.
	 * 
	 */
	public void parse() {
			
		String chosenReport = reportCommand.getSelectedReport();
		List<String> params = (List<String>) reportCommand.getParameters();
		Object[] parameterProperties = new Object[5];
		
		if(params != null){
			String name = null;
			String value = null;
			String type = null;
			String description = null;
			String optional = null;
			boolean go = false;
			for(String paramsElt : params){
				
				if(paramsElt.equals(chosenReport)){
					log.debug("report: " + paramsElt);
					go = true;
				}
					
				else if(go){	
					log.debug("process " + paramsElt);
					
					if(name == null){
						name = paramsElt;
						parameterProperties[NAME] = paramsElt;
						doOnName(paramsElt);
					}
					else if(value == null){
						value = paramsElt;
						parameterProperties[VALUE] = paramsElt;
						doOnValue(paramsElt);
					}
					else if(type == null){
						type = paramsElt;
						parameterProperties[TYPE] = paramsElt;
						doOnType(paramsElt);
					}
					else if(description == null){
						description = paramsElt;
						parameterProperties[DESCRIPTION] = paramsElt;
						doOnDescription(paramsElt);
					}
					else if(optional == null){
						optional = paramsElt;
						parameterProperties[OPTIONAL] = new Boolean((String)paramsElt);
						doOnOptional(paramsElt);
						
						doOnEndOfParameterParsing(reportCommand, parameterProperties, errors);
						
						name = null;
						value = null;
						type = null;
						description = null;
						optional = null;
						
						go = false;
					}
					
				}
			
			}
		}
	
		doOnEnd(reportCommand);
	}

	
	/**
	 * Action to perform after have read all properties of a parameter
	 * 
	 * @param command ReportCommand parsed
	 * @param parameterProperties Properties read: name/value/type/description/optional. 
	 * These values can be indexed with the constants defined in this class: 
	 * parameterProperties[NAME], parameterProperties[VALUE], ...
	 * @param errors <code>Errors</code> object handling errors. 
	 */
	public void doOnEndOfParameterParsing(ReportCommand command, Object[] parameterProperties, Errors errors){
		// To customize
	}
	
	/**
	 * Action to perform when finishing the parsing 
	 * @param reportCommand ReportComand parsed
	 */
	public void doOnEnd(ReportCommand reportCommand){
		// To customize
	}
	
	/**
	 * Action to perform when the chosenReport is known
	 * @param chosenReport Selected report
	 */
	public void doOnChosenReport(Object chosenReport){
		// To customize
	}
	
	/**
	 * Action to perform when a name of parameter is known
	 * @param name Name value of a parameter
	 */
	public void doOnName(Object name){
		// To customize
	}	
	
	/**
	 * Action to perform when the value of a parameter is known
	 * @param value Value of the parameter
	 */
	public void doOnValue(Object value){
		// To customize
	}
	
	/**
	 * Action to perform when the type of a parameter is known 
	 * @param type Type of a parameter
	 */
	public void doOnType(Object type){
		// To customize
	}
	
	/**
	 * Action to perform when the description of a parameter is known
	 * @param description Descrption of a parameter
	 */
	public void doOnDescription(Object description){
		// To customize
	}
	
	/**
	 * Action to perform when the optional attribute of a parameter is known
	 * @param optional
	 */
	public void doOnOptional(Object optional){
		// To customize
	}
	
}
