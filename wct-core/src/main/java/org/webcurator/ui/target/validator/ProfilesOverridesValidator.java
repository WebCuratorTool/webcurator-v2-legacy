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
import org.springframework.validation.ValidationUtils;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Target;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.target.command.ProfileCommand;
import org.webcurator.ui.target.command.TargetAnnotationCommand;

/**
 * Validate the profile ovverides tab.
 * @author nwaight
 */
public class ProfilesOverridesValidator extends AbstractBaseValidator {

	public boolean supports(Class clazz) {
		return ProfileCommand.class.equals(clazz);
	}

	public void validate(Object comm, Errors errors) {
		ProfileCommand command = (ProfileCommand) comm;		
		if (command.isOverrideExcludedMimeTypes()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "excludedMimeTypes", "required", getObjectArrayForLabel("excludedMimeTypes"), "excludedMimeTypes is a required field");			
		}

		if (command.isOverrideExcludeFilters()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "excludeFilters", "required", getObjectArrayForLabel("excludeFilters"), "excludeFilters is a required field");
		}
		
		if (command.isOverrideForceAcceptFilters()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "forceAcceptFilters", "required", getObjectArrayForLabel("forceAcceptFilters"), "forceAcceptFilters is a required field");
		}
		
		if (command.isOverrideMaxBytesDownload()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "maxBytesDownload", "required", getObjectArrayForLabel("maxBytesDownload"), "maxBytesDownload is a required field");
		}
		
		if (command.isOverrideMaxDocuments()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "maxDocuments", "required", getObjectArrayForLabel("maxDocuments"), "maxDocuments is a required field");
		}

		if (command.isOverrideMaxHops()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "maxHops", "required", getObjectArrayForLabel("maxHops"), "maxHops is a required field");
		}

		if (command.isOverrideMaxHours()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "maxHours", "required", getObjectArrayForLabel("maxHours"), "maxHours is a required field");
		}
		
		if (command.isOverrideMaxPathDepth()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "maxPathDepth", "required", getObjectArrayForLabel("maxPathDepth"), "maxPathDepth is a required field");
		}

		if (command.isOverrideRobots()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "robots", "required", getObjectArrayForLabel("robots"), "robots is a required field");
		}
		
		ValidatorUtil.validateStringMaxLength(errors, command.getProfileNote(), AbstractTarget.MAX_PROFILE_NOTE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(ProfileCommand.PARAM_PROFILE_NOTE, AbstractTarget.MAX_PROFILE_NOTE_LENGTH), "Evaluation Note is too long");
	}
}
