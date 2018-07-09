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
import org.webcurator.ui.target.command.TargetAnnotationCommand;
import org.webcurator.ui.target.command.TargetGeneralCommand;

/**
 * Validate adding an annotation to a target.
 * @author nwaight
 */
public class TargetAnnotationValidator extends AbstractBaseValidator {

	public boolean supports(Class clazz) {
		return TargetGeneralCommand.class.equals(clazz);
	}

	public void validate(Object comm, Errors errors) {
		TargetAnnotationCommand command = (TargetAnnotationCommand) comm;
		
		if(command.isAction(TargetAnnotationCommand.ACTION_ADD_NOTE) ||
				command.isAction(TargetAnnotationCommand.ACTION_MODIFY_NOTE)) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, TargetAnnotationCommand.PARAM_NOTE, "required", getObjectArrayForLabel(TargetAnnotationCommand.PARAM_NOTE), "Note is a required field");
			ValidatorUtil.validateStringMaxLength(errors, command.getNote(), TargetAnnotationCommand.CNST_MAX_NOTE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetAnnotationCommand.PARAM_NOTE, TargetAnnotationCommand.CNST_MAX_NOTE_LENGTH), "Annotation is too long");
		}
		else {
			ValidatorUtil.validateStringMaxLength(errors, command.getEvaluationNote(), Target.MAX_EVALUATION_NOTE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetAnnotationCommand.PARAM_EVALUATION_NOTE, Target.MAX_EVALUATION_NOTE_LENGTH), "Evaluation Note is too long");
			ValidatorUtil.validateStringMaxLength(errors, command.getSelectionNote(), Target.MAX_SELECTION_NOTE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetAnnotationCommand.PARAM_SELECTION_NOTE, Target.MAX_SELECTION_NOTE_LENGTH), "Selection Note is too long");
			ValidatorUtil.validateStringMaxLength(errors, command.getSelectionType(), Target.MAX_SELECTION_TYPE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetAnnotationCommand.PARAM_SELECTION_TYPE, Target.MAX_SELECTION_TYPE_LENGTH), "Selection Type is too long");
			ValidatorUtil.validateStringMaxLength(errors, command.getSelectionType(), Target.MAX_SELECTION_TYPE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetAnnotationCommand.PARAM_HARVEST_TYPE, Target.MAX_HARVEST_TYPE_LENGTH), "Harvest Type is too long");
		}
	}
	
	
}
