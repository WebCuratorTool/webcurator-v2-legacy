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
package org.webcurator.ui.site.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.site.command.TransferSeedsCommand;

/**
 * Validate transfering seeds from one permission to another.
 * @author bbeaumont
 */
public class TransferSeedsValidator extends AbstractBaseValidator {

	public boolean supports(Class clazz) {
		return clazz.equals(TransferSeedsCommand.class);
	}

	public void validate(Object aCommand, Errors aErrors) {
		TransferSeedsCommand command = (TransferSeedsCommand) aCommand;
		if(TransferSeedsCommand.ACTION_TRANSFER.equals(command.getActionCmd())) {
			ValidationUtils.rejectIfEmpty(aErrors, TransferSeedsCommand.PARAM_TO_PERMISSION_OID, "required", getObjectArrayForLabel(TransferSeedsCommand.PARAM_TO_PERMISSION_OID), "Target permission is required");
		}
		
	}
}
