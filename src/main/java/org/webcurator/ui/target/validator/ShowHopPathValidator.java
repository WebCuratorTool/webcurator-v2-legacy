package org.webcurator.ui.target.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.target.command.ShowHopPathCommand;
import org.webcurator.ui.common.validation.ValidatorUtil;

public class ShowHopPathValidator extends AbstractBaseValidator {
	public boolean supports(Class clazz) {
		return ShowHopPathCommand.class.equals(clazz);
	}

	public void validate(Object comm, Errors errors) {
		ShowHopPathCommand command = (ShowHopPathCommand) comm;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, ShowHopPathCommand.PARAM_LINES, "required", getObjectArrayForLabel(ShowHopPathCommand.PARAM_LINES), "Number of Lines is a required field");
		ValidatorUtil.validateRegEx(errors, command.getNoOfLines(), "^[0-9]*$", "typeMismatch.java.lang.Integer",getObjectArrayForLabel(ShowHopPathCommand.PARAM_LINES),"Number of Lines must be an integer");
		
		if(ShowHopPathCommand.VALUE_FROM_LINE.equals(command.getFilterType())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, ShowHopPathCommand.PARAM_FILTER, "required", getObjectArrayForLabel(ShowHopPathCommand.PARAM_FILTER), "Line Number is a required field");
			ValidatorUtil.validateRegEx(errors, command.getFilter(), "^[0-9]*$", "typeMismatch.java.lang.Integer",getObjectArrayForLabel(ShowHopPathCommand.PARAM_FILTER),"Line Number must be an integer");
		}
		if(ShowHopPathCommand.VALUE_TIMESTAMP.equals(command.getFilterType())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, ShowHopPathCommand.PARAM_FILTER, "required", getObjectArrayForLabel(ShowHopPathCommand.PARAM_FILTER), "Date/Time is a required field");
		}
		if(ShowHopPathCommand.VALUE_REGEX_MATCH.equals(command.getFilterType()) ||
				ShowHopPathCommand.VALUE_REGEX_CONTAIN.equals(command.getFilterType()) ||
				ShowHopPathCommand.VALUE_REGEX_INDENT.equals(command.getFilterType()) ) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, ShowHopPathCommand.PARAM_FILTER, "required", getObjectArrayForLabel(ShowHopPathCommand.PARAM_FILTER), "Regular Expression is a required field");
		}
	}

}
