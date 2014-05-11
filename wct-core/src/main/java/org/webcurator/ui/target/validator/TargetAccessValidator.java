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
import org.webcurator.domain.model.core.Target;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.target.command.TargetAccessCommand;

/**
 * Validate the targets access tab.
 * @author oakleigh_sk
 */
public class TargetAccessValidator extends AbstractBaseValidator {

	public boolean supports(Class clazz) {
		return TargetAccessCommand.class.equals(clazz);
	}

	public void validate(Object comm, Errors errors) {
		TargetAccessCommand command = (TargetAccessCommand) comm;
		
		// Display Note must be less than or equal 4000 characters.
		if(command.getDisplayNote() != null) {
			ValidatorUtil.validateStringMaxLength(errors, command.getDisplayNote(), Target.MAX_DISPLAY_NOTE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetAccessCommand.PARAM_DISPLAY_NOTE, Target.MAX_DISPLAY_NOTE_LENGTH), "Display Note is too long");
		}

		// Display Change Reason must be less than or equal 1000 characters.
		if(command.getDisplayChangeReason() != null) {
			ValidatorUtil.validateStringMaxLength(errors, command.getDisplayChangeReason(), Target.MAX_DISPLAY_CHANGE_REASON_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetAccessCommand.PARAM_DISPLAY_CHANGE_REASON, Target.MAX_DISPLAY_CHANGE_REASON_LENGTH), "Display Note is too long");
		}
		
	}
}
