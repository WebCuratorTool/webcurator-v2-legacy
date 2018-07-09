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

import org.springframework.validation.Errors;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.groups.command.AddMembersCommand;
import org.webcurator.ui.target.command.AddParentsCommand;

/**
 * The validation for adding members to a group.
 * @author bbeaumont
 */
public class AddParentsValidator extends AbstractBaseValidator {
	/** @see org.springframework.validation.Validator#supports(java.lang.Class) */
	public boolean supports(Class clazz) {
		return AddParentsCommand.class.equals(clazz);
	}

	/** @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors) */
	public void validate(Object comm, Errors errors) {
		AddParentsCommand command = (AddParentsCommand) comm;
		
		
		if(AddParentsCommand.ACTION_ADD_PARENTS.equals(command.getActionCmd())) {
			if((command.getParentOids() == null || command.getParentOids().length == 0) &&
					command.getSelectedCount() == 0) {
				errors.reject("target.errors.addparents.must_select");
			}
		}
	}
}
