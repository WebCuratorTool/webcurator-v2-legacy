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

import org.webcurator.domain.model.auth.User;

/**
 * The Auditor Interface defines the audit methods that an audit implementation must support.
 * @author bprice
 */
public interface Auditor {
    /**
     * Audit action used to mark a successful user login
     */
    public static final String ACTION_LOGIN_SUCCESS = "LOGIN_SUCCESS";
    /**
     * Audit action used to mark a successful user login
     */
    public static final String ACTION_LOGIN_FAILURE = "LOGIN_FAILURE";
    /**
     * Audit action used to mark a unsuccessful user login due to missing user credentials in the WCT
     */
    public static final String ACTION_LOGIN_FAILURE_NO_USER = "LOGIN_FAILURE_NO_USER";
    
    /**
     * Audit action used to mark a redirect of the user to the forced password change page
     */
    public static final String ACTION_FORCE_PWD_CHANGE = "FORCE_PWD_CHANGE";
    /**
     * Audit action used to identify the creation of a new WCT User in the system
     */
    public static final String ACTION_NEW_USER = "NEW_USER";
    /**
     * Audit action used to identify the update of an existing WCT User in the system
     */
    public static final String ACTION_UPDATE_USER = "UPDATE_USER";
    
    /**
     * Audit action used to identify the re-activation of a User within the WCT.
     * This is when a user is taken from the inactive state and put into the active state
     */
    public static final String ACTION_ACTIVATE_USER = "ACTIVATE_USER";
    
    /**
     * Audit action used to identify the deactivation of a User within the WCT.
     * This is when a user is taken from the active state and put into the inactive state
     */
    public static final String ACTION_DEACTIVATE_USER = "DEACTIVATE_USER";
    
    /**
     * Audit action used to identify that a User has been deleted from
     * the system.
     */
    public static final String ACTION_DELETE_USER = "DELETE_USER";
    
    /**
     * Audit action used to identify the modification of a Users Roles within the
     * WCT system.
     */
    public static final String ACTION_ASSOCIATE_ROLES = "ASSOCIATE_ROLES";
    
    /**
     * Audit action used to identify the addition of a Role to the WCT system.
     */
    public static final String ACTION_NEW_ROLE = "NEW_ROLE";
    
    /**
     * Audit action used to identify the deletion of a Role from the WCT.
     */
    public static final String ACTION_DELETE_ROLE = "DELETE_ROLE";
    
    /**
     * Audit action used to idenitfy the update of a Role.
     */
    public static final String ACTION_UPDATE_ROLE = "UPDATE_ROLE";
    
    /**
     * Audit action used to identify the create agency function. 
     */
    public static final String ACTION_NEW_AGENCY = "NEW_AGENCY";    
    /**
     * Audit action used to identify the update of the agency fields.
     */
    public static final String ACTION_UPDATE_AGENCY = "UPDATE_AGENCY";    
    /**
     * Audit action used to mark a User Logout
     */
    public static final String ACTION_LOGOUT = "LOGOUT";
    /** Audit action used to identify the creation of a target instance. */
    public static final String ACTION_NEW_TARGET_INSTANCE = "NEW_TARGET_INSTANCE";
    /** Audit action used to identify the update of a target instance. */
    public static final String ACTION_UPDATE_TARGET_INSTANCE = "UPDATE_TARGET_INSTANCE";    
    /** Audit action used to identify the state change of a target instance. */
    public static final String ACTION_STATE_CHANGE_TARGET_INSTANCE = "STATE_CHANGE_TARGET_INSTANCE";
    /** Audit action used to identify the owner change of a target instance. */
    public static final String ACTION_OWNER_CHANGE_TARGET_INSTANCE = "OWNER_CHANGE_TARGET_INSTANCE";
    /** Audit action used to identify the delete of a target instance. */
    public static final String ACTION_DELETE_TARGET_INSTANCE = "DELETE_TARGET_INSTANCE";
    /** Audit action used to identify the copy and prune of a harvest result. */
    public static final String ACTION_COPY_AND_PRUNE_HARVEST_RESULT = "COPY_AND_PRUNE_HARVEST_RESULT";
    
    /** Audit action used to identify the creation of a new permission. */
    public static final String ACTION_NEW_PERMISSION = "NEW_PERMISSION";
    /** Audit action used to identify the deletion of a permission. */
    public static final String ACTION_DELETE_PERMISSION = "DELETE_PERMISSION";
    /** Audit action used to identify the update permission action. */
    public static final String ACTION_UPDATE_PERMISSION = "UPDATE_PERMISSION";   
    /** Audit action used to identify the approve permission action. */
    public static final String ACTION_APPROVE_PERMISSION = "APPROVE_PERMISSION";  
    /** Audit action used to identify the permission requested action. */
    public static final String ACTION_REQUESTED_PERMISSION = "REQUESTED_PERMISSION";
    /** Audit action used to identify the seek permission action. */
    public static final String ACTION_SEEK_PERMISSION = "SEEK_PERMISSION";
    
    /** Audit action used to identify the claim task action. */
    public static final String ACTION_CLAIM_TASK = "CLAIM_TASK";
    /** Audit action used to identify the un-claim task action. */
    public static final String ACTION_UNCLAIM_TASK = "UNCLAIM_TASK";
    
    /** Audit action used to identify a change in a target groups's state */
    public static final String ACTION_TARGET_GROUP_STATE_CHANGE = "TARGET_GROUP_STATE_CHANGE";
    /** Audit action used to identify a change in ownership */
    public static final String ACTION_TARGET_GROUP_CHANGE_OWNER = "TARGET_GROUP_CHANGE_OWNER";
    /** Audit action used to identify a change in the profile */
    public static final String ACTION_TARGET_GROUP_CHANGE_PROFILE = "TARGET_GROUP_CHANGE_PROFILE";
    /** Audit action used to identify a new target group */
    public static final String ACTION_NEW_TARGET_GROUP = "NEW_TARGET_GROUP";
    /** Audit action used to identify a update to a target group */
    public static final String ACTION_UPDATE_TARGET_GROUP = "UPDATE_TARGET_GROUP";
    
    /** Audit action used to identify new schedule actions */
    public static final String ACTION_NEW_SCHEDULE = "NEW_SCHEDULE";
    /** Audit action used to identify a change in a target's state */
    public static final String ACTION_TARGET_STATE_CHANGE = "TARGET_STATE_CHANGE";
    /** Audit action used to identify a change in ownership */
    public static final String ACTION_TARGET_CHANGE_OWNER = "TARGET_CHANGE_OWNER";
    /** Audit action used to identify a change in the profile */
    public static final String ACTION_TARGET_CHANGE_PROFILE = "TARGET_CHANGE_PROFILE";
    /** Audit action used to identify a change to the Target */
    public static final String ACTION_UPDATE_TARGET = "UPDATE_TARGET";
    
    /** Audit action used to identify new profile action */
    public static final String ACTION_NEW_PROFILE = "NEW_PROFILE";
    /** Audit action used to identify update profile action */
    public static final String ACTION_UPDATE_PROFILE = "UPDATE_PROFILE";
    /** Audit action used to identify deactivate profile action */
    public static final String ACTION_DEACTIVATE_PROFILE = "DEACTIVATE_PROFILE";    
    /** Audit action used to identify activate profile action */
    public static final String ACTION_ACTIVATE_PROFILE = "ACTIVATE_PROFILE";
    /** Audit action used to identify delete profile action */
    public static final String ACTION_DELETE_PROFILE = "DELETE_PROFILE";
    /** Audit action used to identify set default profile action */
    public static final String ACTION_SET_DEFAULT_PROFILE = "SET_DEFAULT_PROFILE";
    
    /** Audit action used to identify changes to bandwidth */
    public static final String ACTION_CHANGE_BANDWIDTH_RESTRICTION = "CHANGE_BANDWIDTH_RESTRICTION";  
    /** Audit action used to identify a new bandwidth restriction */
    public static final String ACTION_NEW_BANDWIDTH_RESTRICTION = "NEW_BANDWIDTH_RESTRICTION";
    /** Audit action used to identify a deleted bandwidth restriction */
    public static final String ACTION_DELETE_BANDWIDTH_RESTRICTION = "DELETE_BANDWIDTH_RESTRICTION";    
    
    /** Audit action used to identify a new Site creation. */
    public static final String ACTION_NEW_SITE = "NEW_HARVEST_AUTHORISATION";    
    /** Audit action used to identify a update Site. */
    public static final String ACTION_UPDATE_SITE = "UPDATE_HARVEST_AUTHORISATION";
	
    public static final String ACTION_DELETE_REASON = "DELETE_REJREASON";
	public static final String ACTION_UPDATE_REASON = "UPDATE_REJREASON";
	public static final String ACTION_NEW_REASON = "NEW_REJREASON";
	
    /** Audit action used to identify the creation of an indicator. */
    public static final String ACTION_NEW_INDICATOR = "NEW_INDICATOR";
    
    /** Audit action used to identify the update of an indicator. */
    public static final String ACTION_UPDATE_INDICATOR = "NEW_INDICATOR";

    public static final String ACTION_DELETE_INDICATOR_CRITERIA = "DELETE_INDICATOR_CRITERIA";
	public static final String ACTION_UPDATE_INDICATOR_CRITERIA = "UPDATE_INDICATOR_CRITERIA";
	public static final String ACTION_NEW_INDICATOR_CRITERIA = "INDICATOR_CRITERIA";
	
    public static final String ACTION_DELETE_FLAG = "DELETE_FLAG";
	public static final String ACTION_UPDATE_FLAG = "UPDATE_FLAG";
	public static final String ACTION_NEW_FLAG = "FLAG";
	
    /**
     * creates an audit record of the specified action
     * @param subjectType the subject type that is effected by the action, e.g. TargetInstance
     * @param subjectOid the subject oid that is effected by this action
     * @param action the audit action 
     * @param message the text describing the action taken
     */
    public void audit(String subjectType, Long subjectOid, String action, String message);
    
    /**
     * create an audit record of the specified action, when no user information is available
     * @param subjectType the subject type that is effected by the action, e.g. TargetInstance
     * @param action the audit action 
     * @param message the text describing the action taken
     */
    public void audit(String subjectType, String action, String message);
    
    /**
     * creates an audit record of the specified action
     * @param aUser the user that issues the action
     * @param subjectType the subject type that is effected by the action, e.g. TargetInstance
     * @param subjectOid the subject oid that is effected by this action
     * @param action the audit action 
     * @param message the text describing the action taken
     */
    public void audit(User aUser, String subjectType, Long subjectOid, String action, String message);
}
