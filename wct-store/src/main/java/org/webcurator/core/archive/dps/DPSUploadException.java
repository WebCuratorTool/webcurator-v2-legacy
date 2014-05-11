/**
 * org.webcurator.core.archive.dps - Software License
 *
 * Copyright 2007/2009 National Library of New Zealand.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * or the file "LICENSE.txt" included with the software.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package org.webcurator.core.archive.dps;

import org.webcurator.core.exceptions.DigitalAssetStoreException;

/**
 * Exception object for OMS archiving problems
 *
 * @author Nicolai Moles-Benfell
 */
public class DPSUploadException extends DigitalAssetStoreException {

    private static final long serialVersionUID = 6569597045529864533L;

    /**
     * @param message
     * @param cause
     */
    public DPSUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public DPSUploadException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public DPSUploadException(Throwable cause) {
        super(cause);
	}

}
