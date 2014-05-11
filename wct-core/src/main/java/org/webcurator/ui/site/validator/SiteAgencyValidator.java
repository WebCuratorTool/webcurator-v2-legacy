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
import org.webcurator.core.sites.SiteManager;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.site.command.SiteAuthorisingAgencyCommand;

/**
 * Validate the saving of a harvest ahuthorisation.
 * @author nwaight
 */
public class SiteAgencyValidator extends AbstractBaseValidator {

	private SiteManager siteManager = null;
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class clazz) {		
		return SiteAuthorisingAgencyCommand.class.equals(clazz);
	}
	
	private boolean isEmpty(String aString) {
		return aString == null || aString.trim().equals("");
	}
	

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object aCmd, Errors aErrors) {
		SiteAuthorisingAgencyCommand cmd = (SiteAuthorisingAgencyCommand) aCmd;
		if ("Save".equals(cmd.getCmdAction())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "name", "required", new Object[] {"Name"}, "Name is a required field.");
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "contact", "required", new Object[] {"Contact"}, "Contact is a required field.");
			
			Long oid = null;
			if(!isEmpty(cmd.getIdentity()) && !cmd.getIdentity().startsWith("t")) {
				oid = Long.parseLong(cmd.getIdentity());
			}
		
			if( !siteManager.isAuthAgencyNameUnique( oid, cmd.getName())) {
				aErrors.reject("site.errors.authagent.duplicatename", new Object[] { cmd.getName() }, "");
			}			
			
			boolean phoneBlank = (cmd.getPhoneNumber() == null || cmd.getPhoneNumber().trim().equals(""));
			boolean emailBlank = (cmd.getEmail() == null || cmd.getEmail().trim().equals(""));
			boolean addrBlank = (cmd.getAddress() == null || cmd.getAddress().trim().equals(""));
			
			if (phoneBlank && emailBlank && addrBlank) {
				aErrors.reject("one.of.required", new Object[] {"Phone, Email and Address"}, "One of the fields Phone, Email and Address must be populated.");
			}
			
			if (!aErrors.hasErrors()) {
				ValidatorUtil.validateStringMaxLength(aErrors, cmd.getName(), 255, "string.maxlength", new Object[] {"Name", "255"}, "Name is too long");
				ValidatorUtil.validateStringMaxLength(aErrors, cmd.getDescription(), 2048, "string.maxlength", new Object[] {"Description", "2048"}, "Description is too long");
				ValidatorUtil.validateStringMaxLength(aErrors, cmd.getContact(), 255, "string.maxlength", new Object[] {"Contact", "255"}, "Contact is too long");
				ValidatorUtil.validateStringMaxLength(aErrors, cmd.getAddress(), 2048, "string.maxlength", new Object[] {"Address", "2048"}, "Address is too long");
				ValidatorUtil.validateStringMaxLength(aErrors, cmd.getPhoneNumber(), 32, "string.maxlength", new Object[] {"Phone", "32"}, "Phone is too long");
				ValidatorUtil.validateStringMaxLength(aErrors, cmd.getEmail(), 255, "string.maxlength", new Object[] {"Email", "255"}, "Email is too long");
                
			}
            
            if (!aErrors.hasErrors()) {
                ValidatorUtil.validateRegEx(aErrors,cmd.getEmail(),ValidatorUtil.EMAIL_VALIDATION_REGEX,"invalid.email",new Object[] {"Email"},"Invalid email address");
            }
		}
	}

	/**
	 * @param siteManager The siteManager to set.
	 */
	public void setSiteManager(SiteManager siteManager) {
		this.siteManager = siteManager;
	}
}
