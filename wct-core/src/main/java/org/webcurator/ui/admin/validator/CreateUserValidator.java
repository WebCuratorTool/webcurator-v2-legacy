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
import org.webcurator.ui.admin.command.CreateUserCommand;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;

/**
 * Validates the User Creation action, and checks all the fields used in creating a user
 * @author bprice
 */
public class CreateUserValidator extends AbstractBaseValidator {
	/** @see org.springframework.validation.Validator#supports(Class).*/
    public boolean supports(Class aClass) {
        return aClass.equals(CreateUserCommand.class);
    }
    /** @see org.springframework.validation.Validator#validate(Object, Errors). */
    public void validate(Object aCmd, Errors aErrors) {
        CreateUserCommand cmd = (CreateUserCommand) aCmd;
        
        ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, CreateUserCommand.PARAM_ACTION, "required", getObjectArrayForLabel(CreateUserCommand.PARAM_ACTION), "Action command is a required field.");  
                
        if (CreateUserCommand.ACTION_SAVE.equals(cmd.getAction())) {
            //If an Update or a Insert check the following
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, CreateUserCommand.PARAM_FIRSTNAME, "required", getObjectArrayForLabel(CreateUserCommand.PARAM_FIRSTNAME), "Firstname is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, CreateUserCommand.PARAM_LASTNAME, "required", getObjectArrayForLabel(CreateUserCommand.PARAM_LASTNAME), "Lastname is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, CreateUserCommand.PARAM_USERNAME, "required", getObjectArrayForLabel(CreateUserCommand.PARAM_USERNAME), "Username is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, CreateUserCommand.PARAM_AGENCY_OID, "required", getObjectArrayForLabel(CreateUserCommand.PARAM_AGENCY_OID), "Agency is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, CreateUserCommand.PARAM_EMAIL, "required", getObjectArrayForLabel(CreateUserCommand.PARAM_EMAIL), "Email is a required field.");
            ValidatorUtil.validateStringMaxLength(aErrors, cmd.getAddress() ,200,"string.maxlength",getObjectArrayForLabelAndInt(CreateUserCommand.PARAM_ADDRESS,200),"Address field too long");
            if (cmd.getEmail() != null && cmd.getEmail().length() > 0) {
                ValidatorUtil.validateRegEx(aErrors, cmd.getEmail(), ValidatorUtil.EMAIL_VALIDATION_REGEX, "invalid.email",getObjectArrayForLabel(CreateUserCommand.PARAM_EMAIL),"the email address is invalid" );
            }
            
            if (cmd.getOid() == null) {   
                //for a brand new user validate these things
                if (cmd.isExternalAuth() == false) {
                    //Only check the password fields if not using an external Authentication Source
                    ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, CreateUserCommand.PARAM_PASSWORD, "required", getObjectArrayForLabel(CreateUserCommand.PARAM_PASSWORD), "Password is a required field.");
                    ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, CreateUserCommand.PARAM_CONFIRM_PASSWORD, "required", getObjectArrayForLabel(CreateUserCommand.PARAM_CONFIRM_PASSWORD), "Confirm password is a required field.");
                
                    ValidatorUtil.validateValueMatch(aErrors, cmd.getPassword(), cmd.getConfirmPassword(), "string.match", getObjectArrayForTwoLabels(CreateUserCommand.PARAM_PASSWORD, CreateUserCommand.PARAM_CONFIRM_PASSWORD), "Your passwords did not match.");
                    ValidatorUtil.validateNewPassword(aErrors,cmd.getPassword(),"password.strength.failure", new Object[] {}, "Your password must have at least 1 Upper case letter, 1 lower case letter and a number.");
                }
            } else {
                //for existing users validate these additional things
            }
        }
    }
}
