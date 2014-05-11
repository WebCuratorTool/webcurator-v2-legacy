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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.webcurator.domain.model.core.AbstractTarget;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.Schedule;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.HarvestHistoryDTO;
import org.webcurator.domain.model.dto.QueuedTargetInstanceDTO;
import org.webcurator.domain.model.dto.TargetInstanceDTO;

/**
 * The object for accessing TargetInstances from the persistent store.
 * @author nwaight
 */
public interface TargetInstanceDAO {
	/**
	 * Retrurn a list of TargetInstances that match the specified criteria.
	 * As this method does not return a pagination it should not be used when 
	 * rendering a ui search result.
	 * @see #search(TargetInstanceCriteria, int)
	 * @param aCriteria the search criteria
	 * @return the list of TargetInstances
	 */
    List<TargetInstance> findTargetInstances(TargetInstanceCriteria aCriteria);
    
    /**
     * Return a page of TargetInstances that match the specified search criteria.
     * @param aCriteria the search criteria
     * @param aPage the page number to return
     * @return the page of TargetInstances
     */
    Pagination search(final TargetInstanceCriteria aCriteria, final int aPage, final int aPageSize);
    
    /**
     * Save or update the specified object to the persistent data store.
     * @param aObject the object to save or update
     */
	void save(Object aObject);
	
	/**
	 * Save or update all the objects in the collection to the 
	 * persistent data store.
	 * @param collection the collection of objects to save
	 */
	void saveAll(Collection collection);
	
	/**
	 * Load the specified target instance from the persistent data store.
	 * @param targetInstanceOid the id of the target instance to load
	 * @return the TargetInstance
	 */
    TargetInstance load(final long targetInstanceOid);
    
    /**
     * Load the specified harvest result from the persistent data store.
     * @param harvestResultOid the id of the harvest result to load.
     * @return the HarvestResult
     */
	HarvestResult getHarvestResult(final Long harvestResultOid);
	
    /**
     * Load the specified harvest result from the persistent data store.
     * @param harvestResultOid the id of the harvest result to load.
     * @param loadFully true to load the target instance and resources.
     * @return the HarvestResult
     */
	HarvestResult getHarvestResult(final Long harvestResultOid, boolean loadFully);	
	
	/**
	 * Return the Harvest Resource DTO specified from the persistent data store.
	 * @param harvestResultOid the id of the harvest result the resource belongs to
	 * @param resource the name of the resource
	 * @return the HarvestResourceDTO
	 */
	HarvestResourceDTO getHarvestResourceDTO(final long harvestResultOid, final String resource);
	
	/**
	 * Return the Harvest Resource DTOs specified from the persistent data store.
	 * @param harvestResultOid the id of the harvest result the resource belongs to
	 * @return a <code>List</code> of <code>HarvestResourceDTO</code>
	 */
	List<HarvestResourceDTO> getHarvestResourceDTOs(final long harvestResultOid);

	
	/**
	 * Return a list of HarvestResults for the specified target instance.
	 * @param targetInstanceId the target instance to return the harvest results for
	 * @return the list of HarvestResults
	 */
	List<HarvestResult> getHarvestResults(final long targetInstanceId);
	
	/**
	 * Return the ordered Queue of scheduled or queued TargetInstances.
	 * @return the TaregtInstance Queue
	 */
	List<QueuedTargetInstanceDTO> getQueue();
	
	/**
	 * Return the ordered Queue of scheduled or queued TargetInstances scheduled
	 * to run before the given number of milliseconds in the future   
	 * @param futureMs milliseconds in the future to use for inclusion of target instances
	 * @return the TaregtInstance Queue
	 */
	public List<QueuedTargetInstanceDTO> getUpcomingJobs(final long futureMs);
	
	/**
	 * Return the Queue length for scheduled and queued TargetInstances.
	 * @param targetOid
	 * @return
	 */
	public Long countQueueLengthForTarget(final Long targetOid);
	
	/**
	 * Return all future scheduled <code>TargetInstance</code>s for the specific <code>Target</code>
	 * @param targetOid the oid of the <code>Target</code>
	 * @return a <code>List</code> of <code>TargetInstances</code>s
	 */
	List<QueuedTargetInstanceDTO> getQueueForTarget(Long targetOid);
	
	/**
	 * Load all the related objects for the specified TargetInstance.
	 * @param aTargetInstance the TargetInstance to populate
	 * @return the fully populated TargetInstance
	 */
	TargetInstance populate(final TargetInstance aTargetInstance);
	
	/**
	 * List all the TargetInstances that were archived before 
	 * the specified purge date
	 * @param aPurgeDate the date to purge target instances created before
	 * @return the list of target instances that can be purged
	 */
	List<TargetInstance> findPurgeableTargetInstances(final Date aPurgeDate);
	
	/**
	 * List all the **Aborted** TargetInstances that have been in the system since before 
	 * the specified purge date
	 * @param aPurgeDate the date to purge aborted target instances created before
	 * @return the list of aborted target instances that can be purged
	 */
	List<TargetInstance> findPurgeableAbortedTargetInstances(final Date aPurgeDate);

	/**
	 * Remove the specified object from the persistent data store.
	 * @param aObject the object to remove
	 */
	void delete(Object aObject);
	
	/**
	 * Remove the harvest resources for a specified target instance.
	 * @param targetInstanceId the target instance for which to delete resources
	 */
	void deleteHarvestResources(Long targetInstanceId);
	
	/**
	 * Remove the harvest resources for a specified harvest result.
	 * @param harvestResultId the harvest result for which to delete resources
	 */
	void deleteHarvestResultResources(final Long harvestResultId); 
	
	/**
	 * Remove the harvest files for a specified ArcHarvestResult.
	 * @param harvestResultId the ArcHarvestResult for which to delete files
	 */
	void deleteHarvestResultFiles(final Long harvestResultId); 
	
	/**
	 * Remove any target instances that have been created for the specified schedule.
	 * @param aSchedule the schedule to delete the target instances for
	 */
	void deleteScheduledInstances(Schedule aSchedule);	
	
	/**
     * Delete TargetInstances for the specified Target and Schedule
     * @param targetOid The target OID.
     * @param scheduleOid The schedule OID.
     */    
    void deleteScheduledInstances(Long targetOid, Long scheduleOid);
    
	/**
	 * Deletes all scheduled instances for the Target.
	 * @param anAbstractTarget The abstract target to delete scheduled
	 *        instances for.
	 */
	void deleteScheduledInstances(AbstractTarget anAbstractTarget);
	
	/**
	 * For any groups whos end date has arrived will have their state set to inactive.
	 */
	void endDateGroups();
	
	/**
	 * Counts the number of target instances in the specified states for the user.
	 * @param aUsername the username to count target instances for
	 * @param aStates the states to count target instances for
	 * @return the count of target instances
	 */
	int countTargetInstances(final String aUsername, final ArrayList<String> aStates);
	
	/**
	 * Counts the number of active target instances for a give target oid.
	 * @param targetOid the target's oid to count target instances for
	 * @return the count of active target instances
	 */
    int countActiveTIsForTarget(final Long targetOid);

	/**
	 * Counts the number of target instances for a give target oid.
	 * @param targetOid the target's oid to count target instances for
	 * @return the count of active target instances
	 */
    int countTargetInstancesByTarget(final Long targetOid);

    /**
	 * Return the DTO for the specified Target Instance.
	 * @param aOid the oid of the target instance DTO to return
	 * @return the target instance DTO
	 */
	TargetInstanceDTO getTargetInstanceDTO(final Long aOid);
	
	/**
	 * Get the HarvestHistory for a Target.
	 * @param targetOid the OID of the Target to retrieve the history for.
	 * @return A List of HarvestHistoryDTO objects.
	 */
	List<HarvestHistoryDTO> getHarvestHistory(Long targetOid);
}
