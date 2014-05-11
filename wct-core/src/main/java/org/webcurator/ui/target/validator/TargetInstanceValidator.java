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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.target.command.TargetAnnotationCommand;
import org.webcurator.ui.target.command.TargetInstanceCommand;
import org.webcurator.domain.model.core.TargetInstance;

/**
 * Validate actions on a target instance.
 * @author nwaight
 */
public class TargetInstanceValidator extends AbstractBaseValidator {

	/** Logger to use with this class. */
    private Log log;
    
    /** default constructor. */
    public TargetInstanceValidator() {
        super();
        log = LogFactory.getLog(TargetInstanceValidator.class);
    }
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class aClass) {
		return aClass.equals(TargetInstanceCommand.class);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object aCommand, Errors aErrors) {
		TargetInstanceCommand cmd = (TargetInstanceCommand) aCommand;
		if (log.isDebugEnabled()) {
            log.debug("Validating target instance command.");
        }
		
		if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_EDIT) && cmd.get_tab_current_page().equals("GENERAL")) {
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TargetInstanceCommand.PARAM_OWNER, "required", getObjectArrayForLabel(TargetInstanceCommand.PARAM_OWNER), "Owner is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TargetInstanceCommand.PARAM_OID, "required", getObjectArrayForLabel(TargetInstanceCommand.PARAM_OID), "Target Instance Id is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TargetInstanceCommand.PARAM_TIME, "required", getObjectArrayForLabel(TargetInstanceCommand.PARAM_TIME), "Scheduled Time is a required field.");
            
            if (!aErrors.hasErrors()) {
            	if (cmd != null && cmd.getBandwidthPercent() != null) {
            		ValidatorUtil.validateMaxBandwidthPercentage(aErrors, cmd.getBandwidthPercent(), "max.bandwidth.exeeded");
            	}
            }
		}
		else if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_ADD_NOTE) ||
				 cmd.getCmd().equals(TargetInstanceCommand.ACTION_MODIFY_NOTE)) {
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TargetInstanceCommand.PARAM_OID, "required", getObjectArrayForLabel(TargetInstanceCommand.PARAM_OID), "Target Instance Id is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TargetInstanceCommand.PARAM_NOTE, "required", getObjectArrayForLabel(TargetInstanceCommand.PARAM_NOTE), "Annotation is a required field.");
            ValidatorUtil.validateStringMaxLength(aErrors, cmd.getNote(), TargetAnnotationCommand.CNST_MAX_NOTE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetAnnotationCommand.PARAM_NOTE, TargetAnnotationCommand.CNST_MAX_NOTE_LENGTH), "Annotation is too long");
		}
		else if (cmd.getCmd().equals(TargetInstanceCommand.ACTION_EDIT) && cmd.get_tab_current_page().equals("DISPLAY")) {
           ValidatorUtil.validateStringMaxLength(aErrors, cmd.getDisplayNote(), TargetInstance.MAX_DISPLAY_NOTE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetInstanceCommand.PARAM_DISPLAY_NOTE, TargetInstance.MAX_DISPLAY_NOTE_LENGTH), "Display note is too long");
           ValidatorUtil.validateStringMaxLength(aErrors, cmd.getDisplayChangeReason(), TargetInstance.MAX_DISPLAY_CHANGE_REASON_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetInstanceCommand.PARAM_DISPLAY_CHANGE_REASON, TargetInstance.MAX_DISPLAY_CHANGE_REASON_LENGTH), "Display Change Reason is too long");
		}
	}
}
