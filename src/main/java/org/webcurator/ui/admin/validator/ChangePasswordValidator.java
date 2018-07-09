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
import org.webcurator.ui.admin.command.ChangePasswordCommand;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.credentials.command.ResetPasswordCommand;

/** 
 * Validates the save user password change action.
 * @author bprice
 */
public class ChangePasswordValidator extends AbstractBaseValidator {
	/** @see org.springframework.validation.Validator#supports(Class).*/
    public boolean supports(Class aClass) {
        return aClass.equals(ChangePasswordCommand.class);
    }

    /** @see org.springframework.validation.Validator#validate(Object, Errors). */
    public void validate(Object aCmd, Errors aErrors) {
        ChangePasswordCommand cmd = (ChangePasswordCommand) aCmd;
        
        ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, ChangePasswordCommand.PARAM_ACTION, "required", getObjectArrayForLabel(ChangePasswordCommand.PARAM_ACTION), "Action command is a required field.");
        ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, ChangePasswordCommand.PARAM_USER_OID, "required", getObjectArrayForLabel(ChangePasswordCommand.PARAM_USER_OID), "User oid is a required field.");
        if (ResetPasswordCommand.ACTION_SAVE.equals(cmd.getAction())) {            
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, ChangePasswordCommand.PARAM_NEW_PWD, "required", getObjectArrayForLabel(ChangePasswordCommand.PARAM_NEW_PWD), "Password is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, ChangePasswordCommand.PARAM_CONFIRM_PWD, "required", getObjectArrayForLabel(ChangePasswordCommand.PARAM_CONFIRM_PWD), "Confirm password is a required field.");
            
            ValidatorUtil.validateValueMatch(aErrors, cmd.getNewPwd(), cmd.getConfirmPwd(), "string.match", getObjectArrayForTwoLabels(ChangePasswordCommand.PARAM_NEW_PWD, ChangePasswordCommand.PARAM_CONFIRM_PWD), "Your confirmation password did not match your new password.");
            ValidatorUtil.validateNewPassword(aErrors,cmd.getNewPwd(),"password.strength.failure", new Object[] {}, "Your password must have at least 1 Upper case letter, 1 lower case letter and a number.");
        }
    }
}
