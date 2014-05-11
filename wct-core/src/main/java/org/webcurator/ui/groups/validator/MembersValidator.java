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
package org.webcurator.ui.groups.validator;

import org.springframework.validation.Errors;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.groups.command.MembersCommand;

/**
 * The validation for the general target group tab.
 * @author bbeaumont
 */
public class MembersValidator extends AbstractBaseValidator {
	/** @see org.springframework.validation.Validator#supports(java.lang.Class) */
	public boolean supports(Class clazz) {
		return MembersCommand.class.equals(clazz);
	}

	/** @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors) */
	public void validate(Object comm, Errors errors) {
		MembersCommand command = (MembersCommand) comm;
		
		if(MembersCommand.ACTION_MOVE_TARGETS.equals(command.getActionCmd())) {
			if(command.getTargetOids() == null || command.getTargetOids().length == 0) {
				errors.reject("groups.errors.members.must_select");
			}
		}
		
	}	
}
