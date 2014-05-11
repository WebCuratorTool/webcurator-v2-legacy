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
package org.webcurator.core.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.webcurator.domain.Pagination;
import org.webcurator.domain.TargetInstanceCriteria;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.Indicator;
import org.webcurator.domain.model.core.IndicatorCriteria;
import org.webcurator.domain.model.core.IndicatorReportLine;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.HarvestHistoryDTO;
import org.webcurator.domain.model.dto.QueuedTargetInstanceDTO;

/**
 * The interface for managing target instances.
 * @author nwaight
 */
public interface TargetInstanceManager {
	
	/** 
	 * Store the seed history in the SeedHistory table during prepareHarvest.
	 * @return true to store seed history 
	 */
	public boolean getStoreSeedHistory();

	/** 
	 * Return the first page of TargetInstances that meet the specified criteria.
	 * @param aCriteria the criteria to return instances for
	 * @return the first page of TargetInstances 
	 */
    Pagination search(final TargetInstanceCriteria aCriteria);
    
    /**
     * Return the specified page of TargetInstances that meet the specified criteria.
     * @param aCriteria the criteria to return instances for
     * @param aPage the page number to return
     * @param aPageSize the page size to use
     * @return the page of TargetInstances
     */
    Pagination search(final TargetInstanceCriteria aCriteria, final int aPage, final int aPageSize);
    
    /**
     * Return the TargetInstance with the specified primary key.
     * @param aOid the unique id of the TargetInsrtance
     * @return the TargetInstance
     */
    TargetInstance getTargetInstance(Long aOid);
    
    /**
     * Return the TargetInstance with the specified primary key, 
     * if the fully populate flag is set then load its related objects
     * @param aOid the unique id of the TargetInsrtance
     * @param aLoadFully flag to indicate that related objects should be loaded
     * @return the TargetInstance
     */
    TargetInstance getTargetInstance(Long aOid, boolean aLoadFully);
    
    /**
     * Return the the TargetInstance that is the next one off the queue.
     * @return the TargetIntance to be harvested next.
     */
    TargetInstance getNextTargetInstanceToHarvest();
    
    /**
     * Return the queue of target instances
     * @return a <code>List</code> of queued <code>QueuedTargetInstanceDTO</code>s
     */
    List<QueuedTargetInstanceDTO> getQueue();
    
	/**
	 * Return all future scheduled <code>TargetInstance</code>s for the specific <code>Target</code>
	 * @param targetOid the oid of the <code>Target</code>
	 * @return a <code>List</code> of <code>TargetInstances</code>s
	 */
	List<QueuedTargetInstanceDTO> getQueueForTarget(Long targetOid);
	
	/**
	 * Return the count of all future scheduled <code>TargetInstance</code>s for the specific <code>Target</code>
	 * @param targetOid the oid of the <code>Target</code>
	 * @return a <code>List</code> of <code>TargetInstances</code>s
	 */	
	Long countQueueLengthForTarget(final Long targetOid);
	
    /**
     * Return a list of annotations for the specified TargetInstance
     * @param aTargetInstance the target instance to return annotations for
     * @return the list of annotations
     */
    List<Annotation> getAnnotations(TargetInstance aTargetInstance);
    
    /**
     * Fetch the <code>List</code> of <code>IndicatorCriteria</code> currently defined
     * @return The <code>List</code> of <code>IndicatorCriteria</code>s
     */
    List<IndicatorCriteria> getIndicatorCriterias();
    
    /**
     * Fetch the <code>List</code> of <code>IndicatorCriteria</code> currently defined for the specified <code>Agency</code>
     * @param agencyOid the <code>Agency</code>'s unique identifier
     * @return The <code>List</code> of <code>IndicatorCriteria</code>s
     */
	List<IndicatorCriteria> getIndicatorCriteriasByAgencyOid(Long agencyOid);
    
    /**
     * Save the specified target instance.
     * @param aTargetInstance the TargetInstance to save
     */
    void save(TargetInstance aTargetInstance);

    /**
     * Save the specified HarvestResult.
     * @param aHarvestResult the HarvestResult to save
     */
    void save(HarvestResult aHarvestResult);    
    
    /**
     * Save the specified <code>Indicator</code>.
     * @param indicator the <code>Indicator</code> to save
     */
    void saveOrUpdate(Indicator indicator);   
    
    /**
     * Save the specified <code>IndicatorReportLine</code>.
     * @param indicatorReportLine the <code>IndicatorReportLine</code> to save
     */
    void saveOrUpdate(IndicatorReportLine indicatorReportLine);
    
    /**
     * Delete the specified target instance.
     * @param aTargetInstance the TargetInstance to delete
     */
    void delete(TargetInstance aTargetInstance);    
    
    /**
     * Save all of the target instances in the collection.
     * @param aCollection A collection of target instances to be saved.
     */
    void saveAll(Collection<TargetInstance> aCollection);
    
    /**
     * Delete TargetInstances for the specified Target and Schedule
     * @param aTargetOid The target OID.
     * @param aScheduleOid The schedule OID.
     */
    void deleteTargetInstances(Long aTargetOid, Long aScheduleOid);
    
    /**
     * Delete all <code>IndicatorReportLine</code>s for the specified <code>Indicator</code> 
     * @param indicator
     */
    void deleteIndicatorReportLines(Indicator indicator);
     
    /**
     * Return a count of target instances owned by the specified user
     * where the target instance is in one of the specified states
     * @param aUser the owner of the target instances to count
     * @param aStates the list of states to count
     * @return the count of target instances
     */
    int countTargetInstances(User aUser, ArrayList<String> aStates);    
    
    /**
     * Return a count of target instances 'owned' by the specified Target
     * @param Oid the oid of the target record from which the TI was derived
     * @return the count of target instances 'owned'
     */
    int countTargetInstancesByTarget(Long Oid);    

    /** 
     * Get the HarvestHistory of a taget.
     * @param targetOid The OID of the Target.
     * @return A list of HarvestHistory objects.
     */
    List<HarvestHistoryDTO> getHarvestHistory(Long targetOid);
    
    /**
     * Retrieve the list of HarvestResults associated with the Target Instance.
     * @param targetInstanceOid The OID of the Target Instance
     * @return A list of the HarvestResults.
     */
    List<HarvestResult> getHarvestResults(Long targetInstanceOid);
    
	/**
	 * Return the Harvest Resource DTO specified from the persistent data store.
	 * @param harvestResultOid the id of the harvest result the resource belongs to
	 * @param resource the name of the resource
	 * @return the HarvestResourceDTO
	 */
	HarvestResourceDTO getHarvestResourceDTO(final long harvestResultOid, final String resource);
    
    /**
     * Set the purged flag and delete any harvest resources.
     * @param aTargetInstance The Target Instance
     */
    void purgeTargetInstance(TargetInstance aTargetInstance);
}
