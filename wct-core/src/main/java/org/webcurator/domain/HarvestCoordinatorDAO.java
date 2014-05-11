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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.webcurator.domain.model.core.BandwidthRestriction;

/**
 * The interface for accessing data from the persistent store used 
 * by the HarvestCoordinator. 
 * @author nwaight
 */
public interface HarvestCoordinatorDAO {
    /** 
     * Return the map of bandwith restrictions by day of the week.
     * @return bandwith restrictions by day of the week
     */
    HashMap<String, List<BandwidthRestriction>> getBandwidthRestrictions();    
    
    /** 
     * Return the bandwidth restricion for the specified oid
     * @param aOid the restriction oid
     * @return the bandwidth restriction
     */
    BandwidthRestriction getBandwidthRestriction(Long aOid);

    /**
     * Return the bandwidth restricion for the specified day and time.
     * @param aDay the day to return the bandwidth restriction for.
     * @param aTime the time to return the bandwidth restriction for.
     * @return the bandwidth restriction
     */
    BandwidthRestriction getBandwidthRestriction(String aDay, Date aTime);
    
    /**
     * Save or Update the object passed in. 
     * @param aBandwidthRestriction the bandwidth restriciton to save
     */
    void saveOrUpdate(BandwidthRestriction aBandwidthRestriction);
    
    
    /**
     * Delete the persistent object from the repository
     * @param aBandwidthRestriction the bandwidth restriction to delete
     */
    void delete(BandwidthRestriction aBandwidthRestriction);
}
