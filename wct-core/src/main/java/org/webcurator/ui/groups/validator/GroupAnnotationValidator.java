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
package org.webcurator.ui.groups.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.groups.command.GroupAnnotationCommand;
import org.webcurator.ui.target.command.TargetAnnotationCommand;

/**
 * Validate adding an annotation to a target group.
 * @author nwaight
 */
public class GroupAnnotationValidator extends AbstractBaseValidator {

	public boolean supports(Class clazz) {
		return GroupAnnotationCommand.class.equals(clazz);
	}

	public void validate(Object comm, Errors errors) {
		GroupAnnotationCommand command = (GroupAnnotationCommand) comm;
		
		if(command.isAction(TargetAnnotationCommand.ACTION_ADD_NOTE) ||
				command.isAction(TargetAnnotationCommand.ACTION_MODIFY_NOTE)) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, GroupAnnotationCommand.PARAM_NOTE, "required", getObjectArrayForLabel(TargetAnnotationCommand.PARAM_NOTE), "Note is a required field");
			ValidatorUtil.validateStringMaxLength(errors, command.getNote(), GroupAnnotationCommand.CNST_MAX_NOTE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(GroupAnnotationCommand.PARAM_NOTE, GroupAnnotationCommand.CNST_MAX_NOTE_LENGTH), "Annotation is too long");
		}		
	}
}
