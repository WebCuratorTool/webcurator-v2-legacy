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
package org.webcurator.core.admin;

import java.util.List;

import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.PermissionTemplate;

/**
 * Provides the helper methods required to manage PermissionTemplates.
 * @author bprice
 */
public interface PermissionTemplateManager {

    /**
     * gets the PermissionTemplate based on the Templates name
     * @param templateOid the Primary key of the Template to load
     * @return the PermissionTemplate
     */
    PermissionTemplate getTemplate(Long templateOid);
    
    /**
     * gets the Permission object based on its primary key
     * @param permissionOid the permission objects primary key
     * @return the Permission object
     */
    Permission getPermission(Long permissionOid);
    
    /**
     * gets all Permission Request Templates for the logged in user.
     * If the logged in user has a scope of ALL on the appropriate privilge,
     * they will see all Templates in the system. If the use only has a scope 
     * of AGENCY, they will only see the Agency specific Templates.
     * @param user the User to generate the Template List for
     * @return the List of PermissionTemplate objects
     */
    List getTemplates(User user);
    
    /**
     * saves the Permission Template object to the database
     * @param permissionTemplate the permission template to persist
     */
    void saveTemplate(PermissionTemplate permissionTemplate);
    
    /**
     * gets the defined Template types within the WCT system
     * @return a List of Template types
     */
    List getTemplateTypes();
    
    /**
     * takes an existing template and the specified user and replaces
     * all the variables in the template with the correct values based on the User
     * and permissionOid
     * @param templateOid the oid of the selected template to generate
     * @param user the User associated with the template request
     * @param permissionOid the permission oid that this template request is related to 
     * @return the populated PermissionTemplate with all its varaiables replaced
     */
    PermissionTemplate completeTemplate(Long templateOid, User user, Long permissionOid);
    
    /**
     * deletes the specified template based on the primary key of the template
     * @param templateOid the template primary key to delete
     */
    void delete(Long templateOid);

}
