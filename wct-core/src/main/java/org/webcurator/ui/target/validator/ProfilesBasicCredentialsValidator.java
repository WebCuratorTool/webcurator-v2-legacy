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
package org.webcurator.ui.target.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.target.command.BasicCredentialsCommand;

/**
 * Validate the saving of a basic credentials override.
 * @author nwaight
 */
public class ProfilesBasicCredentialsValidator extends AbstractBaseValidator {

	public boolean supports(Class clazz) {
		return BasicCredentialsCommand.class.equals(clazz);
	}

	public void validate(Object comm, Errors errors) {
		// BasicCredentialsCommand command = (BasicCredentialsCommand) comm;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "credentialsDomain", "required", getObjectArrayForLabel("credentialsDomain"), "credentialsDomain is a required field");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "realm", "required", getObjectArrayForLabel("realm"), "Realm is a required field");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required", getObjectArrayForLabel("username"), "Username is a required field");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required", getObjectArrayForLabel("password"), "Password is a required field");
	}
}
