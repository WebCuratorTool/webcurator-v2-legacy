package org.webcurator.ui.target.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.webcurator.ui.common.validation.AbstractBaseValidator;
import org.webcurator.ui.target.command.LogReaderCommand;
import org.webcurator.ui.common.validation.ValidatorUtil;

public class LogReaderValidator extends AbstractBaseValidator {
	public boolean supports(Class clazz) {
		return LogReaderCommand.class.equals(clazz);
	}

	public void validate(Object comm, Errors errors) {
		LogReaderCommand command = (LogReaderCommand) comm;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, LogReaderCommand.PARAM_LINES, "required", getObjectArrayForLabel(LogReaderCommand.PARAM_LINES), "Number of Lines is a required field");
		ValidatorUtil.validateRegEx(errors, command.getNoOfLines(), "^[0-9]*$", "typeMismatch.java.lang.Integer",getObjectArrayForLabel(LogReaderCommand.PARAM_LINES),"Number of Lines must be an integer");
		
		if(LogReaderCommand.VALUE_FROM_LINE.equals(command.getFilterType())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, LogReaderCommand.PARAM_FILTER, "required", getObjectArrayForLabel(LogReaderCommand.PARAM_FILTER), "Line Number is a required field");
			ValidatorUtil.validateRegEx(errors, command.getFilter(), "^[0-9]*$", "typeMismatch.java.lang.Integer",getObjectArrayForLabel(LogReaderCommand.PARAM_FILTER),"Line Number must be an integer");
		}
		if(LogReaderCommand.VALUE_TIMESTAMP.equals(command.getFilterType())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, LogReaderCommand.PARAM_FILTER, "required", getObjectArrayForLabel(LogReaderCommand.PARAM_FILTER), "Date/Time is a required field");
		}
		if(LogReaderCommand.VALUE_REGEX_MATCH.equals(command.getFilterType()) ||
				LogReaderCommand.VALUE_REGEX_CONTAIN.equals(command.getFilterType()) ||
				LogReaderCommand.VALUE_REGEX_INDENT.equals(command.getFilterType()) ) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, LogReaderCommand.PARAM_FILTER, "required", getObjectArrayForLabel(LogReaderCommand.PARAM_FILTER), "Regular Expression is a required field");
		}
	}

}
