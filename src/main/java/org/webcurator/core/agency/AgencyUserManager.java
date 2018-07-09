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
package org.webcurator.core.agency;

import java.util.List;
import java.util.Map;

import org.webcurator.core.util.Auditor;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.UserOwnable;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.Role;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Flag;
import org.webcurator.domain.model.core.IndicatorCriteria;
import org.webcurator.domain.model.core.RejReason;
import org.webcurator.domain.model.dto.UserDTO;

/**
 * Provides the mechanism for accessing user, role and agency data.
 * @author bprice
 */
public interface AgencyUserManager{

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
     * gets a user based on its primary key
     * @param userOid the users primary key
     * @return the populated User object
     */
    public User getUserByOid(Long userOid);
    
    /**
     * gets a user based on its unique user name
     * @param username the users name
     * @return the populated User object
     */
    public User getUserByUserName(String username);

    /**
     * gets the WCT Users in the system, but only as Data Transfer Objects
     * @return a List of UserDTO objects
     */
    public List getUserDTOs();
    
    /**
     * gets the WCT Users for a given agency, but only as Data Transfer Objects
     * @param agencyOid a List of UserDTO objects
     * @return a List of UserDTO objects for the agency
     */
    public List getUserDTOs(Long agencyOid);
    
    /**
     * gets the WCT Users that belong to the logged in users agency
     * @return the list of Users
     */
    public List getUserDTOsForLoggedInUser();
    
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
     * gets all the agencies defined for the logged in user,
     * based on the users privilege levels
     * @return a List of Agencies
     */
    public List getAgenciesForLoggedInUser();
    
    /**
     * gets all the agencies defined for the logged in user
     * based on their MANAGE_TEMPLATE privilege
     * @return a List of Agencies
     */
    public List getAgenciesForTemplatePriv();
    
    /**
     * gets an agency using its oid (primary key)
     * @param oid the Agency Oid
     * @return the populated Agency Object
     */
    public Agency getAgencyByOid(Long oid);
    
    /**
     * gets all the viewable roles for the Logged in user
     * @return a List of Roles
     */
    public List getRolesForLoggedInUser();
    
    /**
     * gets all the Roles that this user can be assigned.
     * This will always be limited to the User Agency Roles.
     * @param user the user object
     * @return the List of Roles
     */
    public List getRolesForUser(User user);
    
    /**
     * gets a Role base on the Roles primary key
     * @param oid the Roles primary key
     * @return the fully populated Role object
     */
    public Role getRoleByOid(Long oid);
    
    /**
     * gets the set of Roles that are currently assigned to this user
     * @param oid the primary key of the User
     * @return a List of Roles
     */
    public List getAssociatedRolesForUser(Long oid);
    
    // rejection reason related methods..
    public List getRejReasonsForLoggedInUser();
    
    public List getIndicatorCriteriaForLoggedInUser();
    public IndicatorCriteria getIndicatorCriteriaByOid(Long oid);
    public void deleteIndicatorCriteria(IndicatorCriteria indicatorCriteria);
    
    public List getFlagForLoggedInUser();
    public Flag getFlagByOid(Long oid);
    public void deleteFlag(Flag flag);
    
    public void deleteRejReason(RejReason reason);

    public RejReason getRejReasonByOid(Long oid);

    public void updateRejReason(RejReason reason, boolean update);
    
    public void updateIndicatorCriteria(IndicatorCriteria indicator, boolean update);
  
    public void updateFlag(Flag flag, boolean update);

	public List getValidRejReasonsForTargets(Long agencyOid);    

	public List getValidRejReasonsForTIs(Long agencyOid);    

	/**
     * persists the agency into the database
     * @param agency the Agency domain object to persist
     * @param update set this to true if the agency should be updated. Set
     * this to false if this is anew Agency
     * 
     */
    public void updateAgency(Agency agency, boolean update);
    
    /**
     * creates or updates a User object in the database
     * @param user the User object 
     * @param update true if this is an update action rather than a create action
     */
    public void updateUser(User user, boolean update);
    
    /**
     * allows the users active/inactive flag to be altered
     * and the action to be audited. If the user passed to the method
     * is currently active, the user will be made inactive.
     * If the user passed to the method is inactive, the user
     * will be made active.
     * @param user the User object to operate on
     */
    public void modifyUserStatus(User user);
    
    /**
     * deletes the User object and its role associations
     * @param user the fully populated user object for deletion
     */
    public void deleteUser(User user);
    
    /**
     * Creates or updates a Role
     * @param role the Role object to persist
     * @param update true if the action is an update, false if the action is a create
     */
    public void updateRole(Role role, boolean update);
    
    /**
     * Deletes a Role from the System
     * @param role the Role object to remove
     */
    public void deleteRole(Role role);
    
    /**
     * Takes a populated User object and updates all the associated Roles
     * of the user.
     * @param user the fully populated user with the new role associations defined
     */
    public void updateUserRoles(User user);
    
    
    /**
     * Gets a list of users that the current owner of an object can 
     * give ownership to.
     * @param owner The current owner.
     * @return A list of possible owners.
     */
    public List<UserDTO> getAllowedOwners(User owner);

    
    public List<UserDTO> getPossibleOwners(UserOwnable subject);
    
    public boolean canGiveTo(UserOwnable subject, User newOwner);
    
    /**
     * gets a Map of UserDTO's that have the all the specified privileges
     * @param privileges the list of privileges to check
     * @return the Map of UserDTO objects keyed by User Oid
     */
    Map getUsersWithAllPrivilege(List privileges);
    
    /**
     * gets a Map of UserDTO's that have the at least one of the specified privileges
     * @param privileges the list of privileges to check
     * @return the Map of UserDTO objects keyed by User Oid
     */
    Map getUsersWithAtLeastOnePrivilege(List privileges);
    
    /**
     * Get all the User DTOs who own targets associated with a given permission.
     * @param permissionOid The OID of the permission.
     * @return A list of UserDTOs.
     */
    public List<UserDTO> getUserDTOsByTargetPrivilege(Long permissionOid);

}


