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
import org.webcurator.ui.common.validation.ValidatorUtil;
import org.webcurator.ui.site.command.SiteCommand;

/**
 * Validate saving a harvest autorisation or adding a annotation to one.
 * @author nwaight
 */
public class SiteValidator extends AbstractBaseValidator {

	public boolean supports(Class clazz) {
		return clazz.equals(SiteCommand.class);
	}

	public void validate(Object aCommand, Errors aErrors) {
		SiteCommand command = (SiteCommand) aCommand;
		if (SiteCommand.ACTION_ADD_NOTE.equals(command.getCmdAction()) ||
				SiteCommand.ACTION_MODIFY_NOTE.equals(command.getCmdAction())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "annotation", "required", new Object[] {"Annotation"}, "Annotation is a required field.");
			if (!aErrors.hasErrors()) {
				Object[] vals = {"Annotation", "1000"};
				ValidatorUtil.validateStringMaxLength(aErrors, command.getAnnotation(), 1000, "string.maxlength", vals, "The Annotation string is too long.");
			}
		}
		else if (command.isEditMode()) {			
			ValidationUtils.rejectIfEmptyOrWhitespace(aErrors, "title", "required", new Object[] {"Title"}, "Title is a required field.");
			if (!aErrors.hasErrors()) {
				Object[] vals = new Object[] {"Name", "" + SiteCommand.CNST_MAX_LEN_TITLE};
				ValidatorUtil.validateStringMaxLength(aErrors, command.getTitle(), SiteCommand.CNST_MAX_LEN_TITLE, "string.maxlength", vals, "Title is too long");
				vals = new Object[] {"Description", "" + SiteCommand.CNST_MAX_LEN_DESC};
				ValidatorUtil.validateStringMaxLength(aErrors, command.getDescription(), SiteCommand.CNST_MAX_LEN_DESC, "string.maxlength", vals, "Description is too long");
				vals = new Object[] {"Order No.", "" + SiteCommand.CNST_MAX_LEN_ORDERNO};
				ValidatorUtil.validateStringMaxLength(aErrors, command.getLibraryOrderNo(), SiteCommand.CNST_MAX_LEN_ORDERNO, "string.maxlength", vals, "Order No is too long");				
			}
		}
	}
}
