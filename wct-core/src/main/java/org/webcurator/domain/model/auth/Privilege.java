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
package org.webcurator.domain.model.auth;

/**
 * holds all the know privileges of the WCT. A role will 
 * group together a set of privileges to allow a distinct set of 
 * actions/functions within the WCT. 
 * @author bprice
 */
public class Privilege {

    // The scope of each privilege can be one of these values
	
	/** The privilege can be applied to all objects in the system */
    public static final int SCOPE_ALL = 0;
    /** The privilege can be applied to all objects that belong to the same agency as the user */
    public static final int SCOPE_AGENCY = 100;
    /** The privilege can be applied only against objects owned by the user. */
    public static final int SCOPE_OWNER = 200;
    /** This is a special scope for privileges where ownership is either non-existent or not important. */
    public static final int SCOPE_NONE = 500;
    
    //Authentication Privileges
    public static final String LOGIN = "LOGIN";
    public static final String MODIFY_OWN_CREDENTIALS = "MODIFY_OWN_CREDENTIALS";
    
    //Manage Copying Permissions and Access Rights privileges
    public static final String CREATE_SITE = "CREATE_SITE";
    public static final String MODIFY_SITE = "MODIFY_SITE";
    public static final String CONFIRM_PERMISSION = "CONFIRM_PERMISSION";
    public static final String MODIFY_PERMISSION = "MODIFY_PERMISSION";
    public static final String TRANSFER_LINKED_TARGETS = "TRANSFER_LINKED_TARGETS";
    public static final String ENABLE_DISABLE_SITE = "ENABLE_DISABLE_SITE";
    public static final String GENERATE_TEMPLATE = "GENERATE_TEMPLATE";
    
    //Manage Target related privileges
    public static final String CREATE_TARGET = "CREATE_TARGET";
    public static final String MODIFY_TARGET = "MODIFY_TARGET";
    public static final String APPROVE_TARGET = "APPROVE_TARGET";
    public static final String CANCEL_TARGET = "CANCEL_TARGET";
    public static final String DELETE_TARGET = "DELETE_TARGET";
    public static final String REINSTATE_TARGET = "REINSTATE_TARGET";
    public static final String ADD_SCHEDULE_TO_TARGET = "ADD_SCHEDULE_TO_TARGET";
    public static final String SET_HARVEST_PROFILE_LV1 = "SET_HARVEST_PROFILE_LV1";
    public static final String SET_HARVEST_PROFILE_LV2 = "SET_HARVEST_PROFILE_LV2";
    public static final String SET_HARVEST_PROFILE_LV3 = "SET_HARVEST_PROFILE_LV3";
    
    //Target Instance related privileges
    public static final String MANAGE_TARGET_INSTANCES = "MANAGE_TARGET_INSTANCES";
    public static final String LAUNCH_TARGET_INSTANCE_IMMEDIATE = "LAUNCH_TARGET_INSTANCE_IMMEDIATE";
    public static final String MANAGE_WEB_HARVESTER = "MANAGE_WEB_HARVESTER";
    public static final String ENDORSE_HARVEST = "ENDORSE_HARVEST";
    public static final String ARCHIVE_HARVEST = "ARCHIVE_HARVEST";
    public static final String UNENDORSE_HARVEST = "UNENDORSE_HARVEST";
    
    //Target Group related privileges
    public static final String CREATE_GROUP = "CREATE_GROUP";
    public static final String MANAGE_GROUP = "MANAGE_GROUP";
    public static final String ADD_TARGET_TO_GROUP = "ADD_TARGET_TO_GROUP";
    public static final String MANAGE_GROUP_SCHEDULE = "MANAGE_GROUP_SCHEDULE";
    public static final String MANAGE_GROUP_OVERRIDES = "MANAGE_GROUP_OVERRIDES";
    
    //Manage Profiles
    public static final String VIEW_PROFILES = "VIEW_PROFILES";
    public static final String MANAGE_PROFILES  = "MANAGE_PROFILES";
    
    //Manage Users, Roles, Agencies and Rejection Reasons related privileges
    public static final String MANAGE_REASONS = "MANAGE_REASONS";
    public static final String MANAGE_AGENCIES = "MANAGE_AGENCIES";
    public static final String MANAGE_USERS = "MANAGE_USERS";
    public static final String MANAGE_ROLES = "MANAGE_ROLES";
    public static final String MANAGE_INDICATORS = "MANAGE_INDICATORS";
    public static final String MANAGE_FLAGS = "MANAGE_FLAGS";
    public static final String GRANT_CROSS_AGENCY_USER_ADMIN = "GRANT_CROSS_AGENCY_USER_ADMIN";
    
    //System Administration related privileges
    public static final String CONFIGURE_PARAMETERS = "CONFIGURE_PARAMETERS";
    public static final String PERMISSION_REQUEST_TEMPLATE = "PERMISSION_REQUEST_TEMPLATE";
    
    //Reporting privileges
    public static final String SYSTEM_REPORT_LEVEL_1 = "SYSTEM_REPORT_LEVEL_1";
    
    //In-Tray privilege
    public static final String DELETE_TASK = "DELETE_TASK";
    
    //Ownership Transfer
    public static final String TAKE_OWNERSHIP = "TAKE_OWNERSHIP";
    public static final String GIVE_OWNERSHIP = "GIVE_OWNERSHIP";
    
}
