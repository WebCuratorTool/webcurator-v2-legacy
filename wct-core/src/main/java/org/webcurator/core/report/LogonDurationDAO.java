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
package org.webcurator.core.report;

import java.util.Date;

/**
 * Interface for marking a User's login / logout, storing his/her associated session
 * @author MDubos
 *
 */
public interface LogonDurationDAO {

	
    /**
	 * User has logged in
	 * @param sessionId User's session id
	 * @param loggedInTime Time of login
	 * @param userId User's id
	 * @param username Username
	 * @param userRealName User real name
	 */
	public void setLoggedIn(String sessionId, Date loggedInTime, Long userId, String username, String userRealName);
	
	/**
	 * User has logged in
	 * @param sessionId User's session id
	 * @param loggedOutTime Time of User's logout
	 */
	public void setLoggedOut(String sessionId, Date loggedOutTime);
	
	
	/**
	 * Make sure that all durations of previous logins are 
	 * set for the current user. I.e previous session are 
	 * all closed even if Tomcat has been shut down e.g. 
	 * @param currentUserOid ID of the current User
	 * @param currentUserSessionId ID if the current User's session
	 */
	public void setProperLoggedoutForCurrentUser(Long currentUserOid, String currentUserSessionId);
}
