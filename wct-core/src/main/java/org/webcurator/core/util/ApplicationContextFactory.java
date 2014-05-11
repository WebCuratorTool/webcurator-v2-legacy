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

import org.springframework.web.context.WebApplicationContext;
import org.webcurator.core.exceptions.WCTRuntimeException;

/**
 * The ApplicationContextFactory holds a reference to the WebApplicationContext so that
 * the Spring WebApplicationContext can be accessed by objects that do not have access 
 * to the ServletContext.
 * @author nwaight
 */
public class ApplicationContextFactory {
    /** the mutex for this singleton. */
    private static final Object mutex = new Object();
    /** the singleton instance of the ApplicationContextFactory. */
    private static ApplicationContextFactory instance = null;
    /** the spring application context. */
    private WebApplicationContext wac = null;
    
    /**
     * private constructor taking the WebApplicationContext
     * @param aWebApplicationContext the applications WebApplicationContext
     */ 
    private ApplicationContextFactory(WebApplicationContext aWebApplicationContext) {
        super();
        wac = aWebApplicationContext;
    }
    
    /**
     * Set the WebApplicationContext.
     * @param aWebApplicationContext the WebApplicationContext to set
     */
    public static void setWebApplicationContext(WebApplicationContext aWebApplicationContext) {
        synchronized (mutex) {
            if (instance == null) {
                instance = new ApplicationContextFactory(aWebApplicationContext);
            }
            else {
                instance.wac = aWebApplicationContext;
            }
        }
    }

    /**
     * @return the WebApplicationContext.
     */
    public static WebApplicationContext getWebApplicationContext() {
       return instance.getContext();
    }
    
    /** 
     * Return the WebApplicationContext stored by this instance.
     * @return the WebApplicationContext
     */
    private WebApplicationContext getContext() {
        if (wac != null) {
            return wac;
        }
        
        throw new WCTRuntimeException("The ApplicationContextFactory has not been intitalised.");
    }    
}
