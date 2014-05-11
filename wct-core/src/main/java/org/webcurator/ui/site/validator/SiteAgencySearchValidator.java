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
package org.webcurator.ui.site.validator;

import org.springframework.validation.Errors;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.site.command.AgencySearchCommand;

/**
 * Validate adding an authorising agency to a Harvest authorisation.
 * @author bbeaumont
 */
public class SiteAgencySearchValidator  extends AbstractBaseValidator {

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class clazz) {		
		return AgencySearchCommand.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object aCmd, Errors errors) {
		AgencySearchCommand command = (AgencySearchCommand) aCmd;
		if(AgencySearchCommand.ACTION_ADD.equals(command.getActionCmd())) {
			if(command.getSelectedOids() == null || command.getSelectedOids().length == 0) {
				errors.reject("site.errors.authagencysearch.must_select");
			}
		}
	}
	
}
