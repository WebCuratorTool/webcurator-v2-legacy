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
import org.webcurator.domain.model.core.Profile;
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
		
/*		// Name is required and must be less than 255 characters.
		if(command.getName() != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required", getObjectArrayForLabel("name"), "Name is a required field");
			ValidatorUtil.validateStringMaxLength(errors, command.getName(), Profile.MAX_LEN_NAME, "string.maxlength", getObjectArrayForLabelAndInt("name", Profile.MAX_LEN_NAME), "Name is too long");
		}
		
		// Description must be less than 255 characters.
		if(command.getDescription() != null) {
			ValidatorUtil.validateStringMaxLength(errors, command.getDescription(), Profile.MAX_LEN_DESC, "string.maxlength", getObjectArrayForLabelAndInt("description", Profile.MAX_LEN_DESC), "Description is too long");
		}*/
	}
}
