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
package org.webcurator.core.exceptions;

/**
 * The WCTRuntimeException is thrown when an error occurs within 
 * one of the WCT components.
 * @author nwaight
 */
public class WCTRuntimeException extends RuntimeException {
	/** the serial version uid. */
    private static final long serialVersionUID = -8423037258814143174L;
    
    /**
     * @param message the error message.
     */
    public WCTRuntimeException(String message) {
        super(message);
    }

    /** 
     * @param message the error message
     * @param cause the cause of the exception
     */
    public WCTRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /** 
     * @param cause the cause of the exception
     */
    public WCTRuntimeException(Throwable cause) {
        super(cause);
    }
}
