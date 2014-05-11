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
import org.webcurator.domain.model.core.Target;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.target.command.TargetGeneralCommand;

/**
 * Validate the targets general tab.
 * @author nwaight
 */
public class TargetGeneralValidator extends AbstractBaseValidator {

	public boolean supports(Class clazz) {
		return TargetGeneralCommand.class.equals(clazz);
	}

	public void validate(Object comm, Errors errors) {
		TargetGeneralCommand command = (TargetGeneralCommand) comm;
		
		// Name is required and must be less than 50 characters.
		if(command.getName() != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required", getObjectArrayForLabel(TargetGeneralCommand.PARAM_NAME), "Name is a required field");
			ValidatorUtil.validateStringMaxLength(errors, command.getName(), Target.MAX_NAME_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetGeneralCommand.PARAM_NAME, Target.MAX_NAME_LENGTH), "Name is too long");
		}
		
		// Description must be less than 255 characters.
		if(command.getDescription() != null) {
			ValidatorUtil.validateStringMaxLength(errors, command.getDescription(), Target.MAX_DESC_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetGeneralCommand.PARAM_DESCRIPTION, Target.MAX_DESC_LENGTH), "Description is too long");
		}
		
		if(command.getState() == -1) { 
			errors.reject("target.bad_state");
		}
	}
}
