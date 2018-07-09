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
package org.webcurator.core.profiles;

/**
 * Thrown if an element added to the profile has a name that clashes
 * with an existing element.
 * 
 * @author bbeaumont
 */
public class DuplicateNameException extends Exception {
	/** Serial version id. */
	private static final long serialVersionUID = 1L;
	
	/** The duplicate field name */
	private String dupeName = null;

	/**
	 * Construct a new exception.
	 * @param aDupeName The name of the field that clashed.
	 */
	public DuplicateNameException(String aDupeName) {
		super();
		dupeName = aDupeName;
	}

	/**
	 * Construct a new exception.
	 * @param message	The message of the exception.
	 * @param cause		The root exception.
	 * @param aDupeName The name of the field that clashed.
	 */	
	public DuplicateNameException(String message, Throwable cause, String aDupeName) {
		super(message, cause);
		dupeName = aDupeName;
	}

	/**
	 * Construct a new exception.
	 * @param message	The message of the exception.
	 * @param aDupeName The name of the field that clashed.
	 */		
	public DuplicateNameException(String message, String aDupeName) {
		super(message);
		dupeName = aDupeName;
	}

	
	/**
	 * Construct a new exception.
	 * @param cause		The root exception.
	 * @param aDupeName The name of the field that clashed.
	 */		
	public DuplicateNameException(Throwable cause, String aDupeName) {
		super(cause);
		dupeName = aDupeName;
	}

	/**
	 * Get the name of the field that caused the clash.
	 * @return Returns the dupeName.
	 */
	public String getDupeName() {
		return dupeName;
	}

}
