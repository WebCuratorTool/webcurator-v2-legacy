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
package org.webcurator.ui.profiles.validator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.core.harvester.agent.HarvestAgent;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.profiles.command.Heritrix3ProfileCommand;
import org.webcurator.ui.profiles.command.ImportedHeritrix3ProfileCommand;
import org.webcurator.ui.util.HarvestAgentUtil;

/**
 * Validate the imported heritrix 3 profile.
 * @author hannakoppelaar
 */
public class ImportedHeritrix3ProfileValidator extends AbstractBaseValidator implements ApplicationContextAware {

	ApplicationContext applicationContext;

	public boolean supports(Class clazz) {
		return ImportedHeritrix3ProfileCommand.class.equals(clazz);
	}

	public void validate(Object comm, Errors errors) {
		ImportedHeritrix3ProfileCommand command = (ImportedHeritrix3ProfileCommand) comm;


		HarvestAgent harvestAgent = HarvestAgentUtil.getHarvestAgent(getApplicationContext());
		String rawProfile = command.getRawProfile();
		if (!harvestAgent.isValidProfile(rawProfile)) {
			Object[] vals = new Object[]{command.getProfileName()};
			errors.reject("profile.invalid", vals, "The profile is invalid.");
		}
	}


	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
