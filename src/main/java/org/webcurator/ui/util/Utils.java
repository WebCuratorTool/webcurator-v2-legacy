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
package org.webcurator.ui.util;

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;

import org.springframework.beans.propertyeditors.CustomDateEditor;

/**
 * A String utility method
 * @author bbeaumont
 */
public class Utils {
	/**
	 * Check to see if the string is empty or null.
	 * @param aString the string to check
	 * @return true if the string is null or empty.
	 */
	public static boolean isEmpty(String aString) {
		return aString == null || "".equals(aString.trim());
	}
	
	public static PropertyEditor getCustomDateEditor(String dateFormat, boolean allowEmpty) {
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		df.setLenient(false);
		return new CustomDateEditor(df, allowEmpty, dateFormat.length());
	}
}
