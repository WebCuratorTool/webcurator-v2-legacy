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
package org.webcurator.ui.common.editor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date Utility Methods.
 * @author nwaight
 */
public final class DateUtil {

	/** list of acceptable date formats. */
	private static final String[] ACCEPTABLE_DATE_FORMATS = new String[] {
			"dd-MM-yyyy HH:mm", "dd/MM/yyyy HH:mm",
			"dd MM yyyy HH:mm", "dd.MM.yyyy HH:mm",

			"yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm",
			"yyyy MM dd HH:mm", "yyyy.MM.dd HH:mm",

			"yyyy-MMM-dd HH:mm", "yyyy/MMM/dd HH:mm",
			"yyyy MMM dd HH:mm", "yyyy.MMM.dd HH:mm",

			"dd-MMM-yyyy HH:mm", "dd/MMM/yyyy HH:mm",
			"dd MMM yyyy HH:mm", "dd.MMM.yyyy HH:mm",

			"yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss",
			"yyyy MM dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss",

			"dd-MM-yyyy HH:mm:ss", "dd/MM/yyyy HH:mm:ss",
			"dd MM yyyy HH:mm:ss", "dd.MM.yyyy HH:mm:ss",

			"yyyy-MMM-dd HH:mm:ss", "yyyy/MMM/dd HH:mm:ss",
			"yyyy MMM dd HH:mm:ss", "yyyy.MMM.dd HH:mm:ss",

			"dd-MMM-yyyy HH:mm:ss", "dd/MMM/yyyy HH:mm:ss",
			"dd MMM yyyy HH:mm:ss", "dd.MMM.yyyy HH:mm:ss",

			"dd-MM-yyyy", "dd/MM/yyyy", "dd\\MM\\yyyy", "dd.MM.yyyy",
			"dd MM yyyy", "yyyy-MM-dd",

			"dd-MMM-yyyy", "dd/MMM/yyyy", "dd\\MMM\\yyyy", "dd.MMM.yyyy",
			"dd MMM yyyy", "yyyy-MMM-dd", };

	/**
	 * Takes a string representing a date and converts it to a real date. The
	 * format of the string is "yyyy-MM-dd HH:mm:ss". This method makes use of
	 * the translateToDate(date,format) method
	 *
	 * @param aDate
	 *            the date string to be converted
	 * @return Date the converted date
	 */
	public static Date translateToDate(String aDate) {
		// run through every combination of acceptable date/time to find a
		// value...
		Date candidate = null;
		for (int d = 0; d < ACCEPTABLE_DATE_FORMATS.length; d++) {
			try {
				candidate = translateToDate(aDate, ACCEPTABLE_DATE_FORMATS[d]);
				return candidate;
			}
			catch (Exception ex) {
				// didn't work keep trying...
				candidate = null;
			}
		}

		throw new IllegalArgumentException("Date " + aDate + " not formatted correctly");
	}

	/**
	 * Takes a string representing a date and converts it to a real date
	 * according to a given format.
	 *
	 * @param aDate
	 *            the date string to be converted
	 * @param aFormat
	 *            the pattern that the date is formatted in.
	 * @return Date the converted date
	 * @throws Exception
	 *             if the format is not supported or the date is incorrectly
	 *             formatted
	 */
	public static Date translateToDate(String aDate, String aFormat) throws Exception {
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		dateFormatter.applyPattern(aFormat);
		Date returnDate = dateFormatter.parse(aDate);
		return returnDate;
	}

	/**
	 * Return the date formated with the default formatter dd/MM/yyyy.
	 * @param aDate trhe date to format
	 * @return the formatted date
	 */
	public static String getDateAsText(Date aDate) {
		return getDateAsText(aDate, "dd/MM/yyyy");
	}

	/**
	 * Return the date formated with the specified format String.
	 * @param aDate the date to format
	 * @param aFormat the format
	 * @return the formatted date string
	 */
	public static String getDateAsText(Date aDate, String aFormat) {
		String value = "";
		try {
			if (aDate != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(aFormat);
				value = sdf.format(aDate);
			}
		}
		catch (Exception e) {
			value = "";
		}

		return value;
	}

    /** 
     * truncates a String to the specified length
     * @param input the String to truncate
     * @param size the number of characters the string should be
     * @return the truncated String
     */
    public static String truncate(String input,int size) {
        if (input != null) {
            int len = input.length();
            if (len <= size) {
                return input;
            } else {
                return input.substring(0,size)+"...";
            }
        } else {
            return "";
        }
    }
	/** private constructor. */
	private DateUtil() {
		super();
	}
}
