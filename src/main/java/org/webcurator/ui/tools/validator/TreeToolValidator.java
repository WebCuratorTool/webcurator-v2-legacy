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
package org.webcurator.ui.tools.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.tools.command.TreeToolCommand;

/**
 * Validate that a pruned harvest contains a provenance note.
 * @author nwaight
 */
public class TreeToolValidator extends AbstractBaseValidator {
    /** default constructor. */
    public TreeToolValidator() {
        super();
    }
    
    /* (non-Javadoc)
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class aClass) {
        return aClass.equals(TreeToolCommand.class);
    }

    /* (non-Javadoc)
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object aCommand, Errors aErrors) {
        TreeToolCommand command = (TreeToolCommand) aCommand;
        
        if(command.isAction(TreeToolCommand.ACTION_SAVE)) {
        	ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, TreeToolCommand.PARAM_PROVENANCE_NOTE, "required", getObjectArrayForLabel(TreeToolCommand.PARAM_PROVENANCE_NOTE), "Provenance Note is a required field");
        	ValidatorUtil.validateStringMaxLength(aErrors, command.getProvenanceNote(), TreeToolCommand.CNST_MAX_PROVENANCE_NOTE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(TreeToolCommand.PARAM_PROVENANCE_NOTE, TreeToolCommand.CNST_MAX_PROVENANCE_NOTE_LENGTH), "Provenance Note is too long");
        }
    }
}
