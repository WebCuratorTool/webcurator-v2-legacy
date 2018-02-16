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
package org.webcurator.core.harvester.agent.exception;

/**
 * A HarvesterException is thrown when a problem occurs in a hervester process.  
 * @author nwaight
 */
public class HarvesterException extends RuntimeException {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 5907702744245475368L;

    /**
     * @param message
     */
    public HarvesterException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public HarvesterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public HarvesterException(Throwable cause) {
        super(cause);
    }
}
