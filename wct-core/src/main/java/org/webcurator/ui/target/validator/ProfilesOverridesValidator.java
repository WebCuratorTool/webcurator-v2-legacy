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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.core.harvester.agent.HarvestAgent;
import org.webcurator.core.profiles.ProfileManager;
import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.domain.model.core.Target;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.profiles.command.ImportedHeritrix3ProfileCommand;
import org.webcurator.ui.target.command.ProfileCommand;
import org.webcurator.ui.target.command.TargetAnnotationCommand;
import org.webcurator.ui.util.HarvestAgentUtil;

/**
 * Validate the profile ovverides tab.
 * @author nwaight
 */
public class ProfilesOverridesValidator extends AbstractBaseValidator implements ApplicationContextAware {
	private Log log = LogFactory.getLog(ProfilesOverridesValidator.class);

	ApplicationContext applicationContext;

	public boolean supports(Class clazz) {
		return ProfileCommand.class.equals(clazz);
	}

	public void validate(Object comm, Errors errors) {
		ProfileCommand command = (ProfileCommand) comm;

		// If we are overriding an imported H3RawProfile, then we only validate it and nothing else
		// We validate the profile whether or not it's being overridden
		if (command.isImported()) {
			String h3RawProfile = command.getH3RawProfile();
			String validationType = (command.isOverrideH3RawProfile() ?
					"overridden imported profile" :
					"existing imported profile");

			HarvestAgent harvestAgent = HarvestAgentUtil.getHarvestAgent(getApplicationContext());
			if (!harvestAgent.isValidProfile(h3RawProfile)) {
				log.info("isImported, validating " + validationType + ": validation failed.");
				Object[] vals = new Object[]{"'unnamed " + validationType + "'"};
				errors.reject("profile.invalid", vals, "The profile is invalid.");
			} else {
				log.info("isImported, validating " + validationType + ": validation succeeded.");
			}
		} else {
			log.info("Validating non-imported profile.");

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
		}

		// All profiles have profile notes
		ValidatorUtil.validateStringMaxLength(errors, command.getProfileNote(), AbstractTarget.MAX_PROFILE_NOTE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(ProfileCommand.PARAM_PROFILE_NOTE, AbstractTarget.MAX_PROFILE_NOTE_LENGTH), "Evaluation Note is too long");
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
