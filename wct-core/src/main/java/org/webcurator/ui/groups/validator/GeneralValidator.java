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
import org.webcurator.domain.model.core.TargetGroup;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.groups.command.GeneralCommand;

/**
 * The validation for the general target group tab.
 * @author bbeaumont
 */
public class GeneralValidator extends AbstractBaseValidator {
	/** @see org.springframework.validation.Validator#supports(java.lang.Class) */
	public boolean supports(Class clazz) {
		return GeneralCommand.class.equals(clazz);
	}

	/** @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors) */
	public void validate(Object comm, Errors errors) {
		GeneralCommand command = (GeneralCommand) comm;
		
		if(command.isEditMode()) {
			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required", getObjectArrayForLabel(GeneralCommand.PARAM_NAME), "Name is a required field");
			if(command.getName() != null)
			{
				ValidatorUtil.validateValueNotContained(errors, command.getName(), command.getSubGroupSeparator(), "string.contains", getObjectArrayForLabelAndValue(GeneralCommand.PARAM_NAME, command.getSubGroupSeparator()), "'"+command.getSubGroupSeparator()+"' cannot be a sub-string of Name");
			}
			
			if(command.getSubGroupType().equals(command.getType()) && !GeneralCommand.ACTION_ADD_PARENT.equals(command.getAction()))
			{
				//Attempt to move page without setting a parent
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "parentOid", "required", getObjectArrayForLabel(GeneralCommand.PARAM_PARENT_OID), "Parent Group is a required field");
			}
						
			ValidatorUtil.validateStringMaxLength(errors, command.getName(), GeneralCommand.CNST_MAX_LEN_NAME, "string.maxlength", getObjectArrayForLabelAndInt(GeneralCommand.PARAM_NAME, GeneralCommand.CNST_MAX_LEN_NAME), "Name is too long.");
			ValidatorUtil.validateStringMaxLength(errors, command.getDescription(), GeneralCommand.CNST_MAX_LEN_DESC, "string.maxlength", getObjectArrayForLabelAndInt(GeneralCommand.PARAM_DESCRIPTION, GeneralCommand.CNST_MAX_LEN_DESC), "Description is too long.");
			ValidatorUtil.validateStringMaxLength(errors, command.getOwnershipMetaData(), GeneralCommand.CNST_MAX_LEN_OWNER_INFO, "string.maxlength", getObjectArrayForLabelAndInt(GeneralCommand.PARAM_OWNER_INFO, GeneralCommand.CNST_MAX_LEN_OWNER_INFO), "Owner info is too long.");
			ValidatorUtil.validateStringMaxLength(errors, command.getType(), TargetGroup.MAX_TYPE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(GeneralCommand.PARAM_TYPE, TargetGroup.MAX_TYPE_LENGTH), "Group Type is too long.");
			
			if (!errors.hasErrors()) {
				if (command.getFromDate() != null && command.getToDate() != null) {
					ValidatorUtil.validateStartBeforeEndTime(errors, command.getFromDate(), command.getToDate(), "time.range", new Object[] {"To date", "From date"}, "To date is before from date");
				}				
			}
		}
	}	
}
