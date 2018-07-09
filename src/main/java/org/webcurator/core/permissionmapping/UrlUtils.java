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
package org.webcurator.core.permissionmapping;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A set of URL parsing utilities.
 * @author bbeaumont
 *
 */
public class UrlUtils {
	/** Pattern for parsing URLs strings */
	private static 	Pattern PATTERN_HOST_PART = Pattern.compile("(http|https)://([^/]*)/(.*)");
	
	/**
	 * Check if the URL complies with the expected pattern.
	 * @param aUrl The URL string to test.
	 * @return true if the URL meets the pattern; otherwise false.
	 */
	public static boolean isUrl(String aUrl) {
		Matcher matcher = PATTERN_HOST_PART.matcher(aUrl);
		return matcher.matches();
	}
	
	/**
	 * Gets the host part of the URL.
	 * @param aUrl The URL.
	 * @return The host part of the URL.
	 */
	public static String getHost(String aUrl) {
		Matcher matcher = PATTERN_HOST_PART.matcher(aUrl);
		if(matcher.matches()) {
			return matcher.group(2);
		}
		else {
			return "";
		}
	}
	
	/**
	 * Gets the schema part of the URL.
	 * @param aUrl The URL.
	 * @return the schema part of the URL (e.g. http:// )
	 */
	public static String getSchema(String aUrl) {
		Matcher matcher = PATTERN_HOST_PART.matcher(aUrl);
		if(matcher.matches()) {
			return matcher.group(1);
		}
		else {
			return "";
		}
	}	
	
	/**
	 * Gets the resource part of the URL.
	 * @param aUrl The URL.
	 * @return The resuorce part of the URL.
	 */
	public static String getResource(String aUrl) {
		Matcher matcher = PATTERN_HOST_PART.matcher(aUrl);
		if(matcher.matches()) {
			return matcher.group(3);
		}
		else {
			return "";
		}
	}		
	
	/**
	 * Assume the http:// prefix and a trailing slash if they are not there.
	 * @param aUrl The URL to check.
	 * @return A fixed URL.
	 */
	public static String fixUrl(String aUrl) {
		// Make sure it starts with HTTP or HTTPS.
		if (aUrl != null && !aUrl.trim().equals("")) {
			String url = aUrl;
			if(aUrl.indexOf("://") < 0 ) {
				url = "http://" + aUrl;
			}
		
			if( url.indexOf('/', url.indexOf("://") + 3) < 0) {
				url = url + "/";
			}
			
			return url;
		}
		else {		
			return aUrl;
		}
	}
}
