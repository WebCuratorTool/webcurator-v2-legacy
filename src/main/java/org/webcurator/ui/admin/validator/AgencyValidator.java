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
import org.webcurator.ui.admin.command.AgencyCommand;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;

/**
 * Validates the Agency creation action, and checks all the fields used in creating an agency
 * @author bprice
 */
public class AgencyValidator extends AbstractBaseValidator {
	
	/** @see org.springframework.validation.Validator#supports(Class).*/
    public boolean supports(Class aClass) {    	
        return aClass.equals(AgencyCommand.class);
    }

    /** @see org.springframework.validation.Validator#validate(Object, Errors). */
    public void validate(Object aCmd, Errors aErrors) {
        AgencyCommand cmd = (AgencyCommand) aCmd;
        
        ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, AgencyCommand.PARAM_ACTION, "required", getObjectArrayForLabel(AgencyCommand.PARAM_ACTION), "Action command is a required field.");  
                
        if (AgencyCommand.ACTION_SAVE.equals(cmd.getActionCommand())) {   
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, AgencyCommand.PARAM_NAME, "required", getObjectArrayForLabel(AgencyCommand.PARAM_NAME), "Agency name is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, AgencyCommand.PARAM_ADDRESS, "required", getObjectArrayForLabel(AgencyCommand.PARAM_ADDRESS), "Agency address is a required field.");
            ValidatorUtil.validateStringMaxLength(aErrors, cmd.getName(),80,"string.maxlength",getObjectArrayForLabelAndInt(AgencyCommand.PARAM_NAME,80),"Name field too long");
            ValidatorUtil.validateStringMaxLength(aErrors, cmd.getAddress(),255,"string.maxlength",getObjectArrayForLabelAndInt(AgencyCommand.PARAM_ADDRESS,255),"Address field too long");
            
            if (cmd.getEmail() != null && cmd.getEmail().length() > 0) {
                ValidatorUtil.validateRegEx(aErrors, cmd.getEmail(), ValidatorUtil.EMAIL_VALIDATION_REGEX, "invalid.email",getObjectArrayForLabel(AgencyCommand.PARAM_EMAIL),"the email address is invalid" );
            }
            if (cmd.getAgencyURL() != null && cmd.getAgencyURL().length() > 0) {
                ValidatorUtil.validateURL(aErrors, cmd.getAgencyURL(),"invalid.url",new Object[] {cmd.getAgencyURL()},"Invalid URL");
            }
            if (cmd.getAgencyLogoURL() != null && cmd.getAgencyLogoURL().length() > 0) {
                ValidatorUtil.validateURL(aErrors, cmd.getAgencyLogoURL(),"invalid.url",new Object[] {cmd.getAgencyLogoURL()},"Invalid URL");
            }
        }
    }

}
