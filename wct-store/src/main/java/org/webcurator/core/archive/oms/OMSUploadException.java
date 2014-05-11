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
package org.webcurator.core.archive.oms;

import org.webcurator.core.exceptions.DigitalAssetStoreException;

/**
 * Exception object for OMS archiving problems
 * @author AParker
 */
public class OMSUploadException extends DigitalAssetStoreException{
	
	private static final long serialVersionUID = 6569597045529864533L;

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public OMSUploadException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 * @param message
	 */
	public OMSUploadException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public OMSUploadException(Throwable cause) {
		super(cause);
	}
}
