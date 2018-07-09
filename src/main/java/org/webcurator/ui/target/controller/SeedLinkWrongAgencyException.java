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

package org.webcurator.ui.target.controller;

/**
 * Thrown when you attempt to link a seed to a
 * permission that belongs to an agency other than that
 * which owns the target.
 * 
 * @author bbeaumont
 */
public class SeedLinkWrongAgencyException extends Exception {

	/** For serialisation */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new generic seed link exception.
	 */
	public SeedLinkWrongAgencyException() {
		super();
	}

	/**
	 * Create a seed link exception with a sepcific message.
	 * @param message
	 */
	public SeedLinkWrongAgencyException(String message) {
		super(message);
	}

}
