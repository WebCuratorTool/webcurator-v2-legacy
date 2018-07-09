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
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.target.command.SeedsCommand;

/** 
 * Validate adding seeds to a target.
 * @author nwaight
 */
public class TargetSeedsValidator extends AbstractBaseValidator {
	
	public boolean supports(Class clazz) {
		return SeedsCommand.class.equals(clazz);
	}

	public void validate(Object comm, Errors errors) {
		SeedsCommand command = (SeedsCommand) comm;
		
		// Linking new seeds.
		if(command.isAction(SeedsCommand.ACTION_LINK_NEW_CONFIRM)) {
			if(command.getLinkPermIdentity() == null ||
			   command.getLinkPermIdentity().length == 0) {
				errors.reject("target.errors.link.noneselected");
			}
		}
		
		// Adding seeds.
		if(command.isAction(SeedsCommand.ACTION_ADD)) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "seed", "required", getObjectArrayForLabel("seed"), "Seed is a required field");
			
            if (command.getSeed() != null && command.getSeed().length() > 0) {
                ValidatorUtil.validateURL(errors, command.getSeed(), "target.errors.badUrl", getObjectArrayForLabel("seed"),"Invalid URL");
            }
		}
		
		// Searching for Permissions
		if( command.isAction(SeedsCommand.ACTION_LINK_NEW_SEARCH)) {
			validateLinkSearch(command, errors);
		}
	}
	
	/**
	 * Validates that the URL Search Criteria is valid.
	 * @param command The command object.
	 * @param errors  The errors object to populate.
	 * @return        true if valid; otherwise false.
	 */
	public boolean validateLinkSearch(SeedsCommand command, Errors errors) {
		if( SeedsCommand.SEARCH_URL.equals(command.getSearchType())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "urlSearchCriteria", "required", getObjectArrayForLabel("urlSearchCriteria"), "urlSearchCriteria is a required field");
            if (command.getUrlSearchCriteria() != null && command.getUrlSearchCriteria().length() > 0) {
                ValidatorUtil.validateURL(errors, command.getUrlSearchCriteria(), "target.errors.badUrl", getObjectArrayForLabel("urlSearchCriteria"),"Invalid URL");
            }			
		}
		return !errors.hasErrors();
	}

}
