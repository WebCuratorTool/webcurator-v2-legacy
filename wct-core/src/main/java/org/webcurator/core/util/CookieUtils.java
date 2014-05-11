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

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.webcurator.ui.common.Constants;


/**
 * A set of cookie utilities.
 * 
 * @author oakleigh_sk
 */
public class CookieUtils {

	/**
	 * Gets the value stored in the specified cookie.
	 * @param request The HttpServletRequest to search.
	 * @param cookieName The name of the cookie.
	 * @param defaultValue The value to return if the cookie does not exist.
	 * @return The value of the named cookie if found, otherwise the specified default.
	 */
	public static String getCookieValue(HttpServletRequest request, String cookieName, String defaultValue) {
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for(int i=0; i<cookies.length; i++) {
				Cookie c = cookies[i];
				if (c.getName().equals(cookieName)) {
					return c.getValue();
				}
			}
		}
		return defaultValue;
	}

	/**
	 * Sets the value of the specified cookie.
	 * @param response The HttpServletResponse to store the cookie in.
	 * @param cookieName The name of the cookie to update.
	 * @param cookieValue The value to store in the cookie.
	 * @param maxAge The age of the cookie in seconds, 0 means session cookie.
	 */
	public static void setCookieValue(HttpServletResponse response, String cookieName, String cookieValue, int maxAge) {
		
		Cookie aCookie = new Cookie(cookieName, cookieValue);
		aCookie.setMaxAge(maxAge);
		response.addCookie(aCookie);			
	}
	
	/**
	 * Gets the value stored in the page size cookie.
	 * @param request The HttpServletRequest to search.
	 * @return The value of the page size cookie if found, otherwise the page size default.
	 */
	public static String getPageSize(HttpServletRequest request) {
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for(int i=0; i<cookies.length; i++) {
				Cookie c = cookies[i];
				if ( c.getName().equals(Constants.PAGE_SIZE_COOKIE_NAME) ) {
					return c.getValue();
				}
			}
		}
		return Constants.GBL_DEFAULT_PAGE_SIZE;
	}

	/**
	 * Sets the value of the page size cookie.
	 * @param response The HttpServletResponse to store the cookie in.
	 * @param pageSize The value to store in the page size cookie.
	 */
	public static void setPageSize(HttpServletResponse response, String pageSize) {
		
		Cookie aCookie = new Cookie(Constants.PAGE_SIZE_COOKIE_NAME, pageSize);
		aCookie.setMaxAge(Constants.DEFAULT_PAGE_SIZE_COOKIE_AGE);
		aCookie.setPath(Constants.PAGE_SIZE_COOKIE_DOMAIN_PATH);
		response.addCookie(aCookie);			
	}
}
