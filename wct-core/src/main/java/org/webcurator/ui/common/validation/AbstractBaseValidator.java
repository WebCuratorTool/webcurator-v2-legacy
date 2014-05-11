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
package org.webcurator.ui.common.validation;

import java.util.regex.Pattern;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Validator;
import org.webcurator.ui.common.Constants;

/**
 * Abstract base class to be extended by all WCT Validators.
 * This base class provides some useful methods for building object arrays
 * for the i8n messages.
 * @author nwaight
 */
public abstract class AbstractBaseValidator implements Validator {

	/** Regular Expression used to validate an email address. */
	public static final String EMAIL_REGEX = "^[a-zA-Z0-9]+([_\\.-][a-zA-Z0-9]+)*@([a-zA-Z0-9]+([\\.-][a-zA-Z0-9]+)*)+\\.[a-zA-Z]{2,}$";

	/** Default COnstructor. */
	public AbstractBaseValidator() {
		super();
	}

	/**
	 * Retrurn the Object array containing the specified label.
	 * @param aLabel the label to add to the array
	 * @return the object array
	 */
	protected Object[] getObjectArrayForLabel(String aLabel) {
		return new Object[] {new DefaultMessageSourceResolvable(new String[] {Constants.GBL_CMD_DATA + "." + aLabel})};
	}

	/**
	 * Return an Object array containing the specified label and int value.
	 * @param aLabel the label to add to the array
	 * @param aInt the int value to add to the array
	 * @return the Object array
	 */
	protected Object[] getObjectArrayForLabelAndInt(String aLabel, int aInt) {
		return new Object[] {new DefaultMessageSourceResolvable(new String[] {Constants.GBL_CMD_DATA + "." + aLabel}), Integer.toString(aInt)};
	}

	/**
	 * Return an Object array containing the specified label and String value.
	 * @param aLabel the label to add to the array
	 * @param aValue the String value to add to the array
	 * @return the Object array
	 */
	protected Object[] getObjectArrayForLabelAndValue(String aLabel, String aValue) {
		return new Object[] {new DefaultMessageSourceResolvable(new String[] {Constants.GBL_CMD_DATA + "." + aLabel}), aValue};
	}
	
	/**
	 * Return an Object array containing two specific labels.
	 * @param aLabel1 the first label
	 * @param aLabel2 the second label
	 * @return the Object array
	 */
	protected Object[] getObjectArrayForTwoLabels(String aLabel1, String aLabel2) {
		return new Object[] {new DefaultMessageSourceResolvable(new String[] {Constants.GBL_CMD_DATA + "." + aLabel1}), new DefaultMessageSourceResolvable(new String[] {Constants.GBL_CMD_DATA + "." + aLabel2})};
	}

	/**
	 * Validate Email address is valid. Will only validate a single email address at a time
	 * @param email an email address to validate as a String
	 * @return true if the email is valid
	 */
	public boolean validateEmail(String email) {
		return Pattern.matches(EMAIL_REGEX, email);
	}
}
