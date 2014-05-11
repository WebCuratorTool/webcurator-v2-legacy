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
package org.webcurator.ui.agent.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.agent.command.BandwidthRestrictionsCommand;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;

/**
 * Validator to use for validating Bandwidth restriction data.
 * @author nwaight
 */
public class BandwidthRestrictionValidator extends AbstractBaseValidator {

    /** Logger to use with this class. */
    private Log log;
    
    /** default constructor. */
    public BandwidthRestrictionValidator() {
        super();
        log = LogFactory.getLog(BandwidthRestrictionValidator.class);
    }

    /** @see org.springframework.validation.Validator#supports(java.lang.Class) */
    public boolean supports(Class aClass) {
        return aClass.equals(BandwidthRestrictionsCommand.class);
    }

    /** @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors) */
    public void validate(Object aCommand, Errors aErrors) {
        BandwidthRestrictionsCommand cmd = (BandwidthRestrictionsCommand) aCommand;
        if (log.isDebugEnabled()) {
            log.debug("Validating bandwidth restrictions command.");
        }
        
        ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, BandwidthRestrictionsCommand.PARAM_ACTION, "required", getObjectArrayForLabel(BandwidthRestrictionsCommand.PARAM_ACTION), "Action command is a required field.");               
        if (BandwidthRestrictionsCommand.ACTION_EDIT.equals(cmd.getActionCmd())) {            
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, BandwidthRestrictionsCommand.PARAM_DAY, "required", getObjectArrayForLabel(BandwidthRestrictionsCommand.PARAM_DAY), "Day of the week is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, BandwidthRestrictionsCommand.PARAM_START, "required", getObjectArrayForLabel(BandwidthRestrictionsCommand.PARAM_START), "Start Time is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, BandwidthRestrictionsCommand.PARAM_END, "required", getObjectArrayForLabel(BandwidthRestrictionsCommand.PARAM_END), "End Time is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, BandwidthRestrictionsCommand.PARAM_LIMIT, "required", getObjectArrayForLabel(BandwidthRestrictionsCommand.PARAM_LIMIT), "Bandwidth limit is a required field.");            
        }
        else if (BandwidthRestrictionsCommand.ACTION_SAVE.equals(cmd.getActionCmd())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, BandwidthRestrictionsCommand.PARAM_DAY, "required", getObjectArrayForLabel(BandwidthRestrictionsCommand.PARAM_DAY), "Day of the week is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, BandwidthRestrictionsCommand.PARAM_START, "required", getObjectArrayForLabel(BandwidthRestrictionsCommand.PARAM_START), "Start Time is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, BandwidthRestrictionsCommand.PARAM_END, "required", getObjectArrayForLabel(BandwidthRestrictionsCommand.PARAM_END), "End Time is a required field.");
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, BandwidthRestrictionsCommand.PARAM_LIMIT, "required", getObjectArrayForLabel(BandwidthRestrictionsCommand.PARAM_LIMIT), "Bandwidth limit is a required field.");
            ValidatorUtil.validateMinNumber(aErrors, cmd.getLimit(), BandwidthRestrictionsCommand.CNSNT_LOW_LIMIT, "positive.number", getObjectArrayForLabelAndValue(BandwidthRestrictionsCommand.PARAM_LIMIT, cmd.getLimit().toString()), "Bandwidth is set too low");            
            if (!aErrors.hasErrors()) {
                ValidatorUtil.validateStartBeforeEndTime(aErrors, cmd.getStart(), cmd.getEnd(), "time.range", getObjectArrayForTwoLabels(BandwidthRestrictionsCommand.PARAM_START, BandwidthRestrictionsCommand.PARAM_END), "The end time is before the start time.");
            }
            
            if (!aErrors.hasErrors()) {        
                ValidatorUtil.validateNoBandwidthPeriodOverlaps(aErrors, cmd, "time.overlap", getObjectArrayForTwoLabels(BandwidthRestrictionsCommand.PARAM_START, BandwidthRestrictionsCommand.PARAM_END), "The specified time period overlaps with an existing one.");
            }
        }
        else if (BandwidthRestrictionsCommand.ACTION_DELETE.equals(cmd.getActionCmd())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, BandwidthRestrictionsCommand.PARAM_OID, "required", getObjectArrayForLabel(BandwidthRestrictionsCommand.PARAM_OID), "oid is a required field.");
        }
    }
}
