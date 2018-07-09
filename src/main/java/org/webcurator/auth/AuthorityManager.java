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
package org.webcurator.auth;

import java.util.HashMap;

import org.webcurator.core.exceptions.NoPrivilegeException;
import org.webcurator.domain.AgencyOwnable;
import org.webcurator.domain.UserOwnable;
import org.webcurator.domain.model.auth.User;

/**
 * The AuthorityManager Interface provides all the methods required
 * to determine if Users in the system have the Authority to carry out 
 * certain actions based on their privileges.
 * @author bprice
 */
public interface AuthorityManager {

    /**
     * checks whether the logged in user has the appropriate privilege on the ownable
     * object. 
     * @param object the WCT object to verify ownership of. The object must
     * implement the AgencyOwnable Interface
     * @param privilege the privilege to check for
     * @return true if the scoped privilege is found, otherwise false
     */
    boolean hasPrivilege(AgencyOwnable object, String privilege);
    
    /**
     * checks whether a user has the appropriate privilege on the ownable
     * object. 
     * @param object the WCT object to verify ownership of. The object must
     * implement the AgencyOwnable Interface
     * @param privilege the privilege to check for
     * @return true if the scoped privilege is found, otherwise false
     */
    boolean hasPrivilege(User user, AgencyOwnable object, String privilege);
    
    
    /**
     * checks whether the logged in user has the appropriate privilege on the ownable
     * object. 
     * @param object the WCT object to verify ownership of. The object must
     * implement the UserOwnable Interface
     * @param privilege the privilege to check for
     * @return true if the scoped privilege is found, otherwise false
     */
    boolean hasPrivilege(UserOwnable object, String privilege);
    
    /**
     * checks whether a user has the appropriate privilege on the ownable
     * object. 
     * @param object the WCT object to verify ownership of. The object must
     * implement the UserOwnable Interface
     * @param privilege the privilege to check for
     * @return true if the scoped privilege is found, otherwise false
     */
    boolean hasPrivilege(User user, UserOwnable object, String privilege);
    
    /**
     * determines if the specified user has the
     * correct privilege. For a Privilege to match it must
     * be of the same scope.
     * @param user the User object
     * @param privilege the privilege to check for
     * @param scope the scope of the Privilege to check for
     * @return true if this user has the correctly scoped privilege
     */
    boolean hasPrivilege(User user, String privilege, int scope);
    
    /**
     * determines if the logged in user has the
     * correct privilege. For a Privilege to match it must
     * be of the same scope.
     * @param privilege the privilege to check for
     * @param scope the scope of the Privilege to check for
     * @return true if this user has the correctly scoped privilege
     */
    boolean hasPrivilege(String privilege, int scope);
    
    /**
     * gets a single Map of Privileges that this User has
     * based on their Role membership. The HashMap will contain
     * the Privilege the user has with the broadest scope.
     * @param user the User object
     * @return a HashMap of all Privileges this user has keyed by the Privilege code
     */
    HashMap getPrivilegesForUser(User user);
    
    /**
     * checks if the user has one of the specified privileges
     * independant of the privilege scope.
     * @param privilege a String array of Privilege codes to look for
     * @return true if the User has at least one privilege in the set
     */
    boolean hasAtLeastOnePrivilege(String[] privilege);
    
    
    /**
     * checks if the user has one of the specified privileges
     * against the specified object.
     * @param privileges a String array of Privilege codes to look for
     * @param subject   The subject to check permissions against.
     * @return true if the User has at least one privilege in the set
     */
    boolean hasAtLeastOnePrivilege(UserOwnable subject, String... privileges);  
    
    /**
     * checks if the user has one of the specified privileges
     * against the specified object.
     * @param privileges a String array of Privilege codes to look for
     * @param subject   The subject to check permissions against.
     * @return true if the User has at least one privilege in the set
     */
    boolean hasAtLeastOnePrivilege(AgencyOwnable subject, String... privileges);        
    
    /**
     * gets the Logged In Users scope for the specified Privilege
     * @param privilege the Privilege
     * @return the Scope of the specified privilege
     * @throws NoPrivilegeException thrown if the user does not have the specified privilege
     */
    int getPrivilegeScope(String privilege) throws NoPrivilegeException;
    
    /**
     * gets the Logged In Users scope for the specified Privilege without throwing
     * exceptions.
     * @param privilege the Privilege
     * @return the Scope of the specified privilege, none if the user does not
     *         have the privilege.
     */
    int getPrivilegeScopeNE(String privilege);    
    
    /**
     * gets the Users scope for the specified Privilege
     * @param user the User object
     * @param privilege the Privilege
     * @return the Scope of the specified privilege
     * @throws NoPrivilegeException thrown if the user does not have the specified privilege
     */
    int getPrivilegeScope(User user, String privilege) throws NoPrivilegeException;
    
    
    /**
     * Retrieves the profile level for the currently logged in user.
     * @return The maximum level that the logged in user has access to.
     */
    int getProfileLevel();

}
