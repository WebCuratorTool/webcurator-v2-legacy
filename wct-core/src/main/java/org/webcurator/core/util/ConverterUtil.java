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
package org.webcurator.core.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ConverterUtil {
	
	public static final DecimalFormat GIGABYTE_FORMAT = new DecimalFormat("#,###.00GB");
	public static final DecimalFormat MEGABYTE_FORMAT = new DecimalFormat("#,###.00MB");
	public static final DecimalFormat KILOBYTE_FORMAT = new DecimalFormat("#,###.00KB");
	public static final NumberFormat SECOND_FORMAT = new DecimalFormat("00s");
	
	
	/**
	 * Format a number of bytes into Megabytes/Kilobytes depending on the quantity.
	 * @param bytes The number of bytes.
	 * @return A human readable string.
	 */
	public static String formatBytes(long bytes) {
		if (bytes > 1024 * 1024 * 1024) {
			return GIGABYTE_FORMAT.format(((double)bytes) / (1024*1024));
		}
		else if (bytes > 1024 * 1024) {
			return MEGABYTE_FORMAT.format(((double)bytes) / (1024*1024));
		}
		else if(bytes > 1024) {
			return KILOBYTE_FORMAT.format(((double)bytes) / (1024));
		}
		else {
			return Long.toString(bytes)+"B";
		}
	}
	
	/**
	 * Format milliseconds into a more readable format.
	 * @param millis The number of milliseconds.
	 * @return a human readable string.
	 */
	public static String formatMilliseconds(long milliseconds) {
		// First round to the closest second.
		long millis = Math.round(milliseconds / (double)1000);
		StringBuffer niceString = new StringBuffer();
		
		// Now calculate the hours/minutes/seconds string.
		if(millis > 60 * 60) {
			niceString.append(millis / (60*60) + "h");
			millis %= (60*60);
		}
		
		if(millis > 60) {
			niceString.append(millis / (60) + "m");
			millis %= (60);
		}
		
		niceString.append(SECOND_FORMAT.format(millis));
		
		return niceString.toString();
	}
}
