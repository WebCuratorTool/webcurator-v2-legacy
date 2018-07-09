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
import org.webcurator.ui.admin.command.RoleCommand;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;

/**
 * Validates the Role Creation action, and checks all the fields used in creating a Role
 * @author bprice
 */
public class RoleValidator extends AbstractBaseValidator {
	/** @see org.springframework.validation.Validator#supports(Class).*/
    public boolean supports(Class aClass) {
        return aClass.equals(RoleCommand.class);
    }
    /** @see org.springframework.validation.Validator#validate(Object, Errors). */
    public void validate(Object aCmd, Errors aErrors) {
        RoleCommand cmd = (RoleCommand) aCmd;
        
        ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, RoleCommand.PARAM_ACTION, "required", getObjectArrayForLabel(RoleCommand.PARAM_ACTION), "Action command is a required field.");  
                
        if (RoleCommand.ACTION_SAVE.equals(cmd.getAction())) {   
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, RoleCommand.PARAM_ROLE_NAME, "required", getObjectArrayForLabel(RoleCommand.PARAM_ROLE_NAME), "Role name is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, RoleCommand.PARAM_ROLE_DESCRIPTION, "required", getObjectArrayForLabel(RoleCommand.PARAM_ROLE_DESCRIPTION), "Role description is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, RoleCommand.PARAM_AGENCY, "required", getObjectArrayForLabel(RoleCommand.PARAM_AGENCY), "Agency is a required field.");
            ValidatorUtil.validateStringMaxLength(aErrors, cmd.getDescription(), 255, "string.maxlength", getObjectArrayForLabelAndInt(RoleCommand.PARAM_ROLE_DESCRIPTION, 255),"Role description to long");
        } else if (RoleCommand.ACTION_DELETE.equals(cmd.getAction())) { 
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, RoleCommand.PARAM_OID, "required", getObjectArrayForLabel(RoleCommand.PARAM_OID), "Role id is a required field.");
        }
    }
}
