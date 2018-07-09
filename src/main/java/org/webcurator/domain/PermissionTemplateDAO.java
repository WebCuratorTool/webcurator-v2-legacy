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

import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.PermissionTemplate;
/**
 * Persistance Interface for the managing the Permission Template Request object
 * @author BPrice
 */
public interface PermissionTemplateDAO {

    /**
     * gets a single template based on its primary key
     * @param oid the primary key of the template
     * @return the PermissionTemplate object
     */
    PermissionTemplate getTemplate(Long oid);
    
    /**
     * gets all the Templates appropriate for the agency specified
     * @param agencyOid the primary key of the Agency
     * @return a List of PermissionTemplate objects
     */
    List getTemplates(Long agencyOid);
    
    /**
     * gets all the Templates in the system
     * @return a List of PermissionTemplate objects
     */
    List getAllTemplates();
    
    /**
     * saves or updates the PermissionTemplate object
     * @param aObject the PermissionTemplate object
     */
    void saveOrUpdate(Object aObject);
    
    /**
     * obtains the Permission object from the system. The permission object
     * holds all the information required by the template for rendering.
     * @param oid the primary key of the Permission object
     * @return the populated Permission object
     */
    Permission getPermission(Long oid);
    
    /**
     * deletes the specified PermissionTemplate object
     * @param aObject the template object to delete
     */
    void delete(Object aObject);
}
