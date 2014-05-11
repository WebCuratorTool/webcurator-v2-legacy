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
package org.webcurator.domain;

import java.util.List;

import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Role;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.dto.UserDTO;

/**
 * The User Role DAO provides access to User, Role and Agency
 * data from the persistent data store. 
 * @author bprice
 */
public interface UserRoleDAO {
	/**
	 * Save or update the specified object to the persistent data store.
	 * @param aObject the object to save or update
	 */
    public void saveOrUpdate(Object aObject);
    
    /**
     * Remove the specified object from the persistent data store.
     * @param aObject the object to remove
     */
    public void delete(Object aObject);
    
    /**
     * gets a List of RolePrivilege objects. The RolePrivilege contains both 
     * the Privilege and the scope of the Privilege
     * @param username the username
     * @return a List of RolePrivileges
     */
    public List getUserPrivileges(String username);
    
    /**
     * gets a specific User based on the users login name
     * @param username the login username
     * @return a populated User object
     */
    public User getUserByName(String username);
    
    /**
     * gets the specific user based on the provided oid
     * @param oid the Users Oid 
     * @return the populated User object
     */
    public User getUserByOid(Long oid);
    
    /**
     * gets the Defined roles in the system
     * @return a List of Roles
     */
    public List getRoles();
    
    /**
     * gets a Defined list of roles for the specified agency
     * @param agencyOid the oid of the agency
     * @return a List of Roles
     */
    public List getRoles(Long agencyOid);
    
    /**
     * gets the Roles associated with this user
     * @param  userOid the users primary key
     * @return a List of associated Roles for this user
     */
    public List getAssociatedRolesForUser(Long userOid);
    
    /**
     * gets all users of the WTC system
     * @return a List of full populated User objects
     */
    public List getUsers();
    
    /**
     * gets the users of the WTC system for a selected Agency
     * @param agencyOid the Oid of the AGency in which to search for users
     * @return a List of full populated User objects
     */
    public List getUsers(Long agencyOid);
    
    /**
     * gets a the WCT Users in the system, but only as Data Transfer Objects
     * @return a List of UserDTO objects
     */
    public List getUserDTOs();
    
    /**
     * gets a the WCT Users for a given agency, but only as Data Transfer Objects
     * @param agencyOid a List of UserDTO objects
     * @return a List of UserDTO objects for the agency
     */
    public List getUserDTOs(Long agencyOid);
    
    /**
     * gets a UserDTO object based on the Users oid
     * @param userOid the oid to load
     * @return a populated UserDTO object
     */
    public UserDTO getUserDTOByOid(Long userOid);
    
    
    /**
     * gets all the Agencies defined within the WCT system
     * @return a List of Agencies
     */
    public List getAgencies();
    
    /**
     * gets an agency using its oid (primary key)
     * @param oid the Agency Oid
     * @return the populated Agency Object
     */
    public Agency getAgencyByOid(Long oid);
    
    /**
     * gets a Role object based on its primary key
     * @param oid the roles primary key
     * @return the populated Role object
     */
    public Role getRoleByOid(Long oid);
    
    /**
     * gets a List of UserDTO's that have this privilege across
     * all Agencies
     * @param privilege the Privilge code to look for
     * @return  a List of UserDTO objects
     */
    public List<UserDTO> getUserDTOsByPrivilege(String privilege);
    
    /**
     * gets a List of UserDTO's that have this privilege within the 
     * specified Agency
     * @param privilege the Privilge code to look for
     * @param agencyOid the AGency to limit the search to
     * @return a List of UserDTO objects
     */
    public List<UserDTO> getUserDTOsByPrivilege(String privilege, Long agencyOid);
    
    /**
     * Get all the User DTOs who own targets associated with a given permission.
     * @param permissionOid The OID of the permission.
     * @return A list of UserDTOs.
     */
    public List<UserDTO> getUserDTOsByTargetPrivilege(Long permissionOid);
}
