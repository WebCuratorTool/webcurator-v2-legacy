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
import org.webcurator.core.permissionmapping.HierarchicalPermissionMappingStrategy;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.site.command.UrlCommand;

/**
 * Validate adding a url to a harvest autorisation.
 * @author nwaight
 */
public class SiteURLsValidator extends AbstractBaseValidator {

	HierarchicalPermissionMappingStrategy strategy;
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class clazz) {		
		return clazz.equals(UrlCommand.class);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object aCmd, Errors aErrors) {
		UrlCommand cmd = (UrlCommand) aCmd;
		
		if (UrlCommand.ACTION_ADD_URL.equals(cmd.getActionCmd())) {		
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "url", "required", new Object[] {"URL"}, "URL is a required field.");
			
			if (!aErrors.hasErrors()) {
				ValidatorUtil.validateURL(aErrors, cmd.getUrl(), "invalid.url", new Object[] { cmd.getUrl()}, "The URL provided is not valid");
			}			
			
			if (!aErrors.hasErrors()) {
				String url = cmd.getUrl();
				
				if (!strategy.isValidPattern(url)) {				
					aErrors.reject("invalid.url", new Object[] {cmd.getUrl()}, "The url provided is not valid.");
				}
			}
		}
	}

	/**
	 * @param strategy the strategy to set
	 */
	public void setStrategy(HierarchicalPermissionMappingStrategy strategy) {
		this.strategy = strategy;
	}
}
