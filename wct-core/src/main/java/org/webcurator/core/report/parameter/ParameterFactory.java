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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;

/**
 * Utility method for building a Parameter from
 * properties found in the ReportCommand. Two
 * different constructors allow a building with 
 * or without the need to handle errors 
 * 
 * @author MDubos
 *
 */
public class ParameterFactory {
	
	private static Log log = LogFactory.getLog(ParameterFactory.class);
	
	
	/**
	 * Create a {@link Parameter} from the parameters 
	 * extracted from the {@link org.webcurator.ui.report.command.ReportCommand}
	 * 
	 * @param params Properties for building a Parameter.<br> They 
	 * are: name/value/type/description/optional and can be accessed 
	 * via constants defined in {@link ReportCommandParsing}:<br> 
	 * <br>
	 * name = parameterProperties[ReportCommandParsing.NAME]<br>
	 * value = parameterProperties[ReportCommandParsing.VALUE]<br>
	 * ...
	 * @return An instance of a {@link Parameter}
	 */
	public static Parameter buildParameter(Object[] params) {
		return buildParameter(params, null);
	}
	
	/**
	 * Create a {@link Parameter} from the parameters 
	 * extracted from the {@link org.webcurator.ui.report.command.ReportCommand} indicating errors
	 * of some properties are not valid
	 * 
	 * @param params Properties for building a Parameter.<br> They 
	 * are: name/value/type/description/optional and can be accessed 
	 * via constants defined in {@link ReportCommandParsing}:<br> 
	 * <br>
	 * name = parameterProperties[ReportCommandParsing.NAME]<br>
	 * value = parameterProperties[ReportCommandParsing.VALUE]<br>
	 * ...
	 * @param errors Object handling errors
	 * @return An instance of a {@link Parameter}
	 */
	public static Parameter buildParameter(Object[] params, Errors errors) {
		
//		log.debug("n=" + params[ReportCommandParsing.NAME] );
//		log.debug("v=" + (params[ReportCommandParsing.VALUE] == null ? "null" : params[1]) );
//		log.debug("t=" + (String)params[ReportCommandParsing.TYPE] );
//		log.debug("d=" + (String)params[ReportCommandParsing.DESCRIPTION] );
//		log.debug("o=" + ((Boolean)params[ReportCommandParsing.OPTIONAL]).booleanValue() );
		
		
		String className = (String) params[ReportCommandParsing.TYPE];
		log.debug("Creating a " + className);

		Errors anyErrors = errors;
		if(anyErrors == null){
			anyErrors = new SimpleErrors();
		}
				
		Parameter parameter = null;		
		try {
			
			// Parameter with temp value
			Class parameterClass = Class.forName(className);
			parameter = (Parameter) parameterClass.newInstance();
			parameter.setName( (String) params[ReportCommandParsing.NAME]);
			parameter.setValue( null );
			// type auto set by the parameter
			parameter.setDescription( (String) params[ReportCommandParsing.DESCRIPTION]);
			parameter.setOptional( (Boolean) params[ReportCommandParsing.OPTIONAL]);
						
			parameter.validate(
					anyErrors,	// for calling aErrors.reject(..)
					(String)params[ReportCommandParsing.NAME],
					params[ReportCommandParsing.VALUE],
					(String)params[ReportCommandParsing.DESCRIPTION],
					(Boolean)params[ReportCommandParsing.OPTIONAL]
			);

			if(!anyErrors.hasErrors()){
				parameter.setValue( params[ReportCommandParsing.VALUE] );
			}
			
		} catch (Exception e) {
			// Exceptions thrown if here is a pb to instantiate a Parameter...which should not occur
			log.error("Error in instantiating parameter: " + 
					"n=" + (params[ReportCommandParsing.NAME] != null ? (String)params[ReportCommandParsing.NAME] : "null name") +
					" ex=" + e.getMessage());
			e.printStackTrace();
		}
		
		return parameter;
	}
	
}
