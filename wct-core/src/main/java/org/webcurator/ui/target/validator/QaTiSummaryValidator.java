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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.target.command.TargetAnnotationCommand;
import org.webcurator.ui.target.command.TargetInstanceSummaryCommand;
import org.webcurator.ui.target.command.TargetSchedulesCommand;

/**
 * Validate actions on a target instance.
 * @author nwaight
 */
public class QaTiSummaryValidator extends AbstractBaseValidator {

	/** Logger to use with this class. */
    private Log log;
    
    /** default constructor. */
    public QaTiSummaryValidator() {
        super();
        log = LogFactory.getLog(QaTiSummaryValidator.class);
    }
    

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class aClass) {
		return aClass.equals(TargetInstanceSummaryCommand.class);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object aCommand, Errors aErrors) {
		TargetInstanceSummaryCommand command = (TargetInstanceSummaryCommand) aCommand;
		if (log.isDebugEnabled()) {
            log.debug("Validating target instance summary command.");
        }
		
		if (command.getCmd() != null && command.getCmd().equals(TargetInstanceSummaryCommand.ACTION_ADD_NOTE) ) {
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TargetInstanceSummaryCommand.PARAM_OID, "required", getObjectArrayForLabel(TargetInstanceSummaryCommand.PARAM_OID), "Target Instance Id is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TargetInstanceSummaryCommand.PARAM_NOTE, "required", getObjectArrayForLabel(TargetInstanceSummaryCommand.PARAM_NOTE), "Annotation is a required field.");
            ValidatorUtil.validateStringMaxLength(aErrors, command.getNote(), TargetAnnotationCommand.CNST_MAX_NOTE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TargetAnnotationCommand.PARAM_NOTE, TargetAnnotationCommand.CNST_MAX_NOTE_LENGTH), "Annotation is too long");
		}
		
	}
	

}
