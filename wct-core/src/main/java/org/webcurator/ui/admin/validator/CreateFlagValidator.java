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
package org.webcurator.ui.admin.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.admin.command.CreateFlagCommand;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;

/**
 * Validates the Flag Creation action, and checks all the fields used in creating a flag
 * @author twoods
 */
public class CreateFlagValidator extends AbstractBaseValidator {
	/** @see org.springframework.validation.Validator#supports(Class).*/
    public boolean supports(Class aClass) {
        return aClass.equals(CreateFlagCommand.class);
    }
    /** @see org.springframework.validation.Validator#validate(Object, Errors). */
    public void validate(Object aCmd, Errors aErrors) {
        CreateFlagCommand cmd = (CreateFlagCommand) aCmd;
        
        // protect the action
        ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, CreateFlagCommand.PARAM_ACTION, "required", getObjectArrayForLabel(CreateFlagCommand.PARAM_ACTION), "Action command is a required field.");  
                
        // validate the remaining form fields
        if (CreateFlagCommand.ACTION_SAVE.equals(cmd.getAction())) {
            //If an Update or a Insert check the following
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, CreateFlagCommand.PARAM_NAME, "required", getObjectArrayForLabel(CreateFlagCommand.PARAM_NAME), "Name is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, CreateFlagCommand.PARAM_AGENCY_OID, "required", getObjectArrayForLabel(CreateFlagCommand.PARAM_AGENCY_OID), "Agency is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, CreateFlagCommand.PARAM_AGENCY_OID, "required", getObjectArrayForLabel(CreateFlagCommand.PARAM_RGB), "RGB colour components is a required field.");
            ValidatorUtil.validateStringMaxLength(aErrors, cmd.getName() ,100,"string.maxlength",getObjectArrayForLabelAndInt(CreateFlagCommand.PARAM_NAME,100),"Name field too long");
          
        }
    }
}
