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
import org.webcurator.domain.model.core.Permission;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.site.command.SitePermissionCommand;

/**
 * Validate saving a harvest permission.
 * @author nwaight
 */
public class SitePermissionValidator extends AbstractBaseValidator {

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class clazz) {
		return SitePermissionCommand.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object aCmd, Errors aErrors) {
		SitePermissionCommand cmd = (SitePermissionCommand) aCmd;
		
		if(cmd.isAction(SitePermissionCommand.ACTION_SAVE) || cmd.isAction(SitePermissionCommand.ACTION_ADD_EXCLUSION)) {
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "authorisingAgent", "required", new Object[] {"Authorising Agent"}, "Authorising Agent is a required field.");
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "startDate", "required", new Object[] {"Start date"}, "Start date is a required field.");
			if (cmd.isQuickPick()) {
				ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "displayName", "required", new Object[] {"Display name"}, "Display name is a required field.");
			}
			
			if (cmd.getUrls() == null || cmd.getUrls().isEmpty()) {		
				//aErrors.reject("required", new Object[] {"Urls"}, "Urls is a required field.");
				aErrors.reject("", new Object[] {"Urls"}, "Choose at least one Url pattern.");
			}
			
			if(cmd.isCreateSeekPermissionTask() && cmd.getStatus() != Permission.STATUS_PENDING) {
				aErrors.reject("permission.errors.create_task");
			}
			
			if (!aErrors.hasErrors()) {
				ValidatorUtil.validateStartBeforeEndTime(aErrors, cmd.getStartDate(), cmd.getEndDate(), "time.range", new Object[] {"End date", "Start date"}, "The end date is before the start date");
				ValidatorUtil.validateStringMaxLength(aErrors, cmd.getAuthResponse(), 32000, "string.maxlength", new Object[] {"Auth. Agency Response", "32000"}, "Auth. Agency Response is too long");
				ValidatorUtil.validateStringMaxLength(aErrors, cmd.getSpecialRequirements(), 2048, "string.maxlength", new Object[] {"Special Requirements", "2048"}, "Special Requirements is too long");
				ValidatorUtil.validateStringMaxLength(aErrors, cmd.getDisplayName(), 32, "string.maxlength", new Object[] {"Display name", "32"}, "Display name is too long");
				ValidatorUtil.validateStringMaxLength(aErrors, cmd.getCopyrightStatement(), 2048, "string.maxlength", new Object[] {"Copyright Statement", "2048"}, "Copyright Statement is too long");
				ValidatorUtil.validateStringMaxLength(aErrors, cmd.getCopyrightUrl(), 2048, "string.maxlength", new Object[] {"Copyright URL", "2048"}, "Copyright URL is too long");
				ValidatorUtil.validateStringMaxLength(aErrors, cmd.getFileReference(), Permission.MAX_FILE_REF_LENGTH, "string.maxlength", new Object[] {"File Reference", Permission.MAX_FILE_REF_LENGTH}, "File Reference is too long");
			}
		}
		
		if(cmd.isAction(SitePermissionCommand.ACTION_ADD_EXCLUSION)) {
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "exclusionUrl", "required", new Object[] {"Exclusion URL"}, "Exclusion URL is a required field.");
			ValidatorUtil.validateStringMaxLength(aErrors, cmd.getExclusionUrl(), 1024, "string.maxlength", new Object[] {"Exclusion URL", "1024"}, "Exclusion URL is too long");
			ValidatorUtil.validateStringMaxLength(aErrors, cmd.getExclusionReason(), 1024, "string.maxlength", new Object[] {"Exclusion Reason", "1024"}, "Exclusion Reason is too long");			
		}
		
		if(cmd.isAction(SitePermissionCommand.ACTION_ADD_NOTE) ||
				cmd.isAction(SitePermissionCommand.ACTION_MODIFY_NOTE)) {
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "note", "required", new Object[] {"Annotation"}, "Annotation is a required field.");
			ValidatorUtil.validateStringMaxLength(aErrors, cmd.getNote(), SitePermissionCommand.CNST_MAX_NOTE_LENGTH, "string.maxlength", getObjectArrayForLabelAndInt(SitePermissionCommand.PARAM_NOTE, SitePermissionCommand.CNST_MAX_NOTE_LENGTH), "Annotation is too long");
		}
	}
}
