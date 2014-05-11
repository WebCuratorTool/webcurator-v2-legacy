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
package org.webcurator.ui.credentials.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.credentials.command.ResetPasswordCommand;

/**
 * Validation for reseting a users password.
 * @author bprice
 */
public class ResetPasswordValidator extends AbstractBaseValidator {
	/** @see org.springframework.validation.Validator#supports(java.lang.Class) */
    public boolean supports(Class aClass) {
        return aClass.equals(ResetPasswordCommand.class);
    }

    /** @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors) */
    public void validate(Object aCmd, Errors aErrors) {
        ResetPasswordCommand cmd = (ResetPasswordCommand) aCmd;
        
        ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, ResetPasswordCommand.PARAM_ACTION, "required", getObjectArrayForLabel(ResetPasswordCommand.PARAM_ACTION), "Action command is a required field.");               
        if (ResetPasswordCommand.ACTION_SAVE.equals(cmd.getAction())) {            
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, ResetPasswordCommand.PARAM_NEW_PWD, "required", getObjectArrayForLabel(ResetPasswordCommand.PARAM_NEW_PWD), "Password is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, ResetPasswordCommand.PARAM_CONFIRM_PWD, "required", getObjectArrayForLabel(ResetPasswordCommand.PARAM_CONFIRM_PWD), "Confirm password is a required field.");
            
            ValidatorUtil.validateValueMatch(aErrors, cmd.getNewPwd(), cmd.getConfirmPwd(), "string.match", getObjectArrayForTwoLabels(ResetPasswordCommand.PARAM_NEW_PWD, ResetPasswordCommand.PARAM_CONFIRM_PWD), "Your confirmation password did not match your new password.");
            ValidatorUtil.validateNewPassword(aErrors,cmd.getNewPwd(),"password.strength.failure", new Object[] {}, "Your password must have at least 1 Upper case letter, 1 lower case letter and a number.");
        }
    }
}
