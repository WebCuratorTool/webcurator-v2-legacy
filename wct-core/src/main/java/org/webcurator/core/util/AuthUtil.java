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

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.webcurator.domain.model.auth.User;

/**
 * authUtil can be used to query the logged in users credentials
 * and also obtain the fully populated WCT User object. 
 * @author bprice
 */
public class AuthUtil {

    private static User user;

	/**
     * obtains the logged in Username as populated by the acegi security framework
     * @return the logged in username
     */
    public static String getRemoteUser() {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();      
        if (auth != null) {            
            return auth.getName();
        }
        return null;
    }
    
    /**
     * obtains the fully populated User object and its relationship to
     * Roles and privileges.
     * @return a fully populated wct User object, null is returned if no object found
     */
    public static User getRemoteUserObject() {
    	if(user!=null) {
    		return user;
    	}
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return (User)auth.getDetails();
        }
        return null;
    }

    /**
     * Used only for testing, do not use this in non-testing code
     * @param user
     */
	public static void setUser(User user) {
		AuthUtil.user = user;
	}

    
}
