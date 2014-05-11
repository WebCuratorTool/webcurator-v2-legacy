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
import org.webcurator.domain.model.core.RejReason;

/**
 * The Rejection Reason DAO provides access to rejection reason
 * data from the persistent data store. 
 * @author oakleigh_sk
 */
public interface RejReasonDAO {
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
     * gets the specific reason based on the provided oid
     * @param oid the Rejection Reason's Oid 
     * @return the populated RejReason object
     */
    public RejReason getRejReasonByOid(Long oid);
    
    /**
     * gets all reasons in the system
     * @return a List of fully populated RejReason objects
     */
    public List getRejReasons();
    
    /**
     * gets the reasons for a selected Agency
     * @param agencyOid the Oid of the Agency in which to search for reasons
     * @return a List of fully populated RejReason objects
     */
    public List getRejReasons(Long agencyOid);
    
}
