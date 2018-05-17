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
package org.webcurator.ui.profiles.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.profiles.command.Heritrix3ProfileCommand;

/**
 * Validate the profile general tab.
 * @author nwaight
 */
public class Heritrix3ProfileValidator extends AbstractBaseValidator {

	public boolean supports(Class clazz) {
		return Heritrix3ProfileCommand.class.equals(clazz);
	}

	public void validate(Object comm, Errors errors) {
		Heritrix3ProfileCommand command = (Heritrix3ProfileCommand) comm;
		
		// Contact URL is required.
		if(command.getContactURL() != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactURL", "required", getObjectArrayForLabel("contactURL"), "Contact URL is a required field");
			ValidatorUtil.validateURL(errors, command.getContactURL(),"invalid.url",new Object[] {command.getContactURL()},"Invalid URL");
		}

		// User agent prefix is required.
		if(command.getUserAgent() != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userAgent", "required", getObjectArrayForLabel("userAgent"), "User Agent Prefix is a required field");
		}
	}
}
