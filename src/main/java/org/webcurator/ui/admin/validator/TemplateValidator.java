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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.admin.command.AgencyCommand;
import org.webcurator.ui.admin.command.TemplateCommand;
import org.webcurator.ui.admin.controller.TemplateController;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.domain.model.core.PermissionTemplate;

/**
 * Validates the Template Creation action, and checks all the fields used in creating a Permission Template
 * @author bprice
 */
public class TemplateValidator extends AbstractBaseValidator {

    
	/** @see org.springframework.validation.Validator#supports(Class).*/
    public boolean supports(Class aClass) {
        return aClass.equals(TemplateCommand.class);
    }
    /** @see org.springframework.validation.Validator#validate(Object, Errors). */
    public void validate(Object aCmd, Errors aErrors) {
        TemplateCommand tempCmd = (TemplateCommand) aCmd;
        
        ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TemplateCommand.PARAM_ACTION, "required", getObjectArrayForLabel(TemplateCommand.PARAM_ACTION), "Action command is a required field.");  
                
        if (AgencyCommand.ACTION_SAVE.equals(tempCmd.getAction())) {   
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TemplateCommand.PARAM_AGENCY_OID, "required", getObjectArrayForLabel(TemplateCommand.PARAM_AGENCY_OID), "A specified Agency is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TemplateCommand.PARAM_TEMPLATE_NAME, "required", getObjectArrayForLabel(TemplateCommand.PARAM_TEMPLATE_NAME), "The template name is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TemplateCommand.PARAM_TEMPLATE_TYPE, "required", getObjectArrayForLabel(TemplateCommand.PARAM_TEMPLATE_TYPE), "The template type is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TemplateCommand.PARAM_TEMPLATE_TEXT, "required", getObjectArrayForLabel(TemplateCommand.PARAM_TEMPLATE_TEXT), "The template text is a required field.");
            
            if (tempCmd.getTemplateType().equals(PermissionTemplate.EMAIL_TYPE_TEMPLATE))
            {
	            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TemplateCommand.PARAM_TEMPLATE_SUBJECT, "required", getObjectArrayForLabel(TemplateCommand.PARAM_TEMPLATE_SUBJECT), "The template subject is a required field.");
	            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TemplateCommand.PARAM_TEMPLATE_OVERWRITE_FROM, "required", getObjectArrayForLabel(TemplateCommand.PARAM_TEMPLATE_OVERWRITE_FROM), "The template overwrite from is a required field.");
	            if (tempCmd.getTemplateOverwriteFrom())
	            	ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TemplateCommand.PARAM_TEMPLATE_FROM, "required", getObjectArrayForLabel(TemplateCommand.PARAM_TEMPLATE_FROM), "The template from is a required field when the template overwrite option is selected.");
	        
	            if (tempCmd.getTemplateFrom() != null && tempCmd.getTemplateFrom().length() > 0) {
	                ValidatorUtil.validateRegEx(aErrors, tempCmd.getTemplateFrom(), ValidatorUtil.EMAIL_VALIDATION_REGEX, "invalid.email",getObjectArrayForLabel(TemplateCommand.PARAM_TEMPLATE_FROM),"the template from email address is invalid" );
	            }
	            
	            checkEmails(aErrors, tempCmd.getTemplateCc(), TemplateCommand.PARAM_TEMPLATE_CC, "template CC");
	            checkEmails(aErrors, tempCmd.getTemplateBcc(), TemplateCommand.PARAM_TEMPLATE_BCC, "template BCC");
            }
            
            TemplateValidatorHelper validatorHelper = new TemplateValidatorHelper(tempCmd.getTemplateText(), tempCmd.getTemplateType());
            validatorHelper.parseForErrors(aErrors);
        }
    }
    
	private void checkEmails(Errors aErrors, String emails,
			String fieldConstant, String label) {
		if (emails != null && emails.length() > 0) {
			if (emails.contains(";"))
			{
		    	for (String email: emails.split(";"))
		    	{
		    		ValidatorUtil.validateRegEx(aErrors, email, ValidatorUtil.EMAIL_VALIDATION_REGEX, "invalid.email",getObjectArrayForLabel(fieldConstant),"the "+label+" email address is invalid" );
		    	}
			}
			else
			{
				ValidatorUtil.validateRegEx(aErrors, emails, ValidatorUtil.EMAIL_VALIDATION_REGEX, "invalid.email",getObjectArrayForLabel(fieldConstant),"the "+label+" email address is invalid" );
			}
		}
	}
}
