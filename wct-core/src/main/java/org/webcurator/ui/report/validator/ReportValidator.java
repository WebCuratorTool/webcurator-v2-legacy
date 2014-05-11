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
package org.webcurator.ui.report.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.webcurator.core.report.parameter.Parameter;
import org.webcurator.core.report.parameter.ParameterFactory;
import org.webcurator.core.report.parameter.ReportCommandParsing;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.report.command.ReportCommand;

public class ReportValidator extends AbstractBaseValidator {
	
	private Log log = LogFactory.getLog(ReportValidator.class);

	/**
	 * See {@link org.springframework.validation.Validator#supports(java.lang.Class)}
	 */
	public boolean supports(Class clazz) {
		return ReportCommand.class.equals(clazz);
	}

	/**
	 * See {@link org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)}
	 */
	public void validate(Object aCmd, Errors aErrors) {
		ReportCommand cmd = (ReportCommand) aCmd;
				
		try {
			
			ReportCommandParsing rcp = new ReportCommandParsing(cmd, aErrors){				
				public void doOnEndOfParameterParsing(ReportCommand command, Object[] parameterProperties, Errors errors){
					// Try to construct parameter. It will auto-validate itself
					Parameter parameter = ParameterFactory.buildParameter(parameterProperties, errors);
				}
				
			};
			rcp.parse();
			
		} catch (Exception e) {
			log.debug("Unable to parse parameters: " + e.getMessage());
			e.printStackTrace();
		}
		

	}
	
	

}
