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

import org.webcurator.core.notification.InTrayManager;
import org.webcurator.core.notification.MessageType;
import org.webcurator.core.util.Auditor;
import org.webcurator.domain.AnnotationDAO;
import org.webcurator.domain.IndicatorCriteriaDAO;
import org.webcurator.domain.IndicatorDAO;
import org.webcurator.domain.IndicatorReportLineDAO;
import org.webcurator.domain.Pagination;
import org.webcurator.domain.ProfileDAO;
import org.webcurator.domain.TargetInstanceCriteria;
import org.webcurator.domain.TargetInstanceDAO;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Annotation;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.Indicator;
import org.webcurator.domain.model.core.IndicatorCriteria;
import org.webcurator.domain.model.core.IndicatorReportLine;
import org.webcurator.domain.model.core.Profile;
import org.webcurator.domain.model.core.ProfileOverrides;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.HarvestHistoryDTO;
import org.webcurator.domain.model.dto.ProfileDTO;
import org.webcurator.domain.model.dto.QueuedTargetInstanceDTO;
import org.webcurator.domain.model.dto.TargetInstanceDTO;

/**
 * The implementation of the Target Instance Manager interface.
 * @author nwaight
 */
public class TargetInstanceManagerImpl implements TargetInstanceManager {
	/** The Data access object for target instances. */
    private TargetInstanceDAO targetInstanceDao;
    /** The intray manager. */
    private InTrayManager inTrayManager;
    /** The object for createing audit entries. */
    private Auditor auditor;
    /** The Data access object for annotations. */
    private AnnotationDAO annotationDAO;
    /** The Data access object for indicators. */
    private IndicatorDAO indicatorDAO;
    /** The Data access object for indicator criterias. */
    private IndicatorCriteriaDAO indicatorCriteriaDAO;
    /** The Data access object for indicator report lines. */
    private IndicatorReportLineDAO indicatorReportLineDAO;
    /** The Data access object for profiles. */
    private ProfileDAO profileDAO;
    /** Save seed history to the seed_history table during prepareHarvest */
	private boolean storeSeedHistory = true;

	public void setStoreSeedHistory(boolean storeSeedHistory)
	{
		this.storeSeedHistory = storeSeedHistory;
	}
	
    /** @see TargetInstanceManager#getStoreSeedHistory(). */
	public boolean getStoreSeedHistory()
	{
		return storeSeedHistory;
	}
	
    /** @see TargetInstanceManager#search(TargetInstanceCriteria). */
    public Pagination search(final TargetInstanceCriteria aCriteria) {
    	return search(aCriteria, 0, 10);
    }
    
    /** @see TargetInstanceManager#search(TargetInstanceCriteria, int). */
    public Pagination search(final TargetInstanceCriteria aCriteria, final int aPage, final int aPageSize) {
    	return targetInstanceDao.search(aCriteria, aPage, aPageSize);
    }
        
    /** @see TargetInstanceManager#getNextTargetInstanceToHarvest(). */
    public TargetInstance getNextTargetInstanceToHarvest() {                
    	List queue = targetInstanceDao.getQueue();
    	if (queue != null && !queue.isEmpty()) {
    		return (TargetInstance) queue.iterator().next();
    	}
    	
    	return null;
    }
    
    public List<QueuedTargetInstanceDTO> getQueue() {                
    	List<QueuedTargetInstanceDTO> queue = targetInstanceDao.getQueue();
    	
    	return queue;
    }
    
    public List<QueuedTargetInstanceDTO> getQueueForTarget(Long targetOid) {                
    	List<QueuedTargetInstanceDTO> queue = targetInstanceDao.getQueueForTarget(targetOid);
    	
    	return queue;
    }
    
    public Long countQueueLengthForTarget(final Long targetOid) {
    	return targetInstanceDao.countQueueLengthForTarget(targetOid);
    }

    /** @see TargetInstanceManager#getTargetInstance(Long). */
    public TargetInstance getTargetInstance(Long aOid) {        
        return getTargetInstance(aOid, false);
    }
    
    /** @see TargetInstanceManager#getTargetInstance(Long, boolean). */
    public TargetInstance getTargetInstance(Long aOid, boolean aLoadFully) {
    	TargetInstance ti = targetInstanceDao.load(aOid);
    	if (aLoadFully) {
    		ti = targetInstanceDao.populate(ti);
    	}
    	
        return ti;
    }
    
    /** @see TargetInstanceManager#delete(TargetInstance). */
    public void delete(TargetInstance aTargetInstance) {
    	aTargetInstance.setTarget(null);
    	if(aTargetInstance.getSchedule() != null)
    	{
    		//remove this target instance from any schedules it is associated with
    		aTargetInstance.getSchedule().getTargetInstances().remove(aTargetInstance);
    	}
    	
    	targetInstanceDao.delete(aTargetInstance);
    	auditor.audit(TargetInstance.class.getName(), aTargetInstance.getOid(), Auditor.ACTION_DELETE_TARGET_INSTANCE, "The TargetInstance '"+ aTargetInstance.getOid() +"' has been deleted");
    }
    
    /** @see TargetInstanceManager#save(TargetInstance). */
	public void save(TargetInstance aTargetInstance) {
		TargetInstanceDTO origTi = targetInstanceDao.getTargetInstanceDTO(aTargetInstance.getOid());
		
		// we are moving to the running state
		if (TargetInstance.STATE_RUNNING.equals(aTargetInstance.getState())) {
			
			Profile currentProfile = aTargetInstance.getProfile();
			if(!currentProfile.isLocked())
			{
				//Lock the profile
				Profile newProfile = currentProfile.clone();
				newProfile.setOrigOid(currentProfile.getOid());
				newProfile.setName(newProfile.getName()+" Locked(v"+newProfile.getVersion()+")");
				
				//Look for a locked profile with this oid and version
				ProfileDTO dto = profileDAO.getLockedDTO(newProfile.getOrigOid(),
						newProfile.getVersion());
				if(dto == null)
				{
					//Save the new profile
					profileDAO.saveOrUpdate(newProfile);
					
					//Now load it in again - with an associated OID
					dto = profileDAO.getLockedDTO(newProfile.getOrigOid(),
							newProfile.getVersion());
				}
				
				aTargetInstance.setLockedProfile(profileDAO.load(dto.getOid()));

				if(aTargetInstance.getOverrides() == null &&
						aTargetInstance.getTarget() != null &&
						aTargetInstance.getTarget().getOverrides() != null) 
				{
					ProfileOverrides overrides = aTargetInstance.getTarget().getOverrides().copy();
					aTargetInstance.setOverrides(overrides);
				}
			}
		}

		
		
		targetInstanceDao.save(aTargetInstance);

		if (aTargetInstance.getAnnotations() != null && !aTargetInstance.getAnnotations().isEmpty()) {
			annotationDAO.saveAnnotations(aTargetInstance.getAnnotations());
		}		
		if (aTargetInstance.getDeletedAnnotations() != null && !aTargetInstance.getDeletedAnnotations().isEmpty()) {
			annotationDAO.deleteAnnotations(aTargetInstance.getDeletedAnnotations());
		}		
		
		if (TargetInstance.STATE_ENDORSED.equals(aTargetInstance.getState()) 
			|| TargetInstance.STATE_REJECTED.equals(aTargetInstance.getState())
			|| TargetInstance.STATE_ARCHIVED.equals(aTargetInstance.getState())) {
			inTrayManager.deleteTasks(aTargetInstance.getOid(), aTargetInstance.getResourceType(), MessageType.TARGET_INSTANCE_ENDORSE);
		}
		
		if (TargetInstance.STATE_HARVESTED.equals(origTi.getState()) && TargetInstance.STATE_ENDORSED.equals(aTargetInstance.getState())) {
			inTrayManager.generateTask(Privilege.ARCHIVE_HARVEST, MessageType.TARGET_INSTANCE_ARCHIVE, aTargetInstance);
		}
		
		if (TargetInstance.STATE_ARCHIVED.equals(aTargetInstance.getState())) {
			inTrayManager.deleteTasks(aTargetInstance.getOid(), aTargetInstance.getResourceType(), MessageType.TARGET_INSTANCE_ARCHIVE);
		}
	}    
	
	public void save(HarvestResult aHarvestResult) {
		targetInstanceDao.save(aHarvestResult);
	}
	
	public void saveOrUpdate(Indicator indicator) {
		indicatorDAO.saveOrUpdate(indicator);
	}
	
	public void saveOrUpdate(IndicatorReportLine indicatorReportLine) {
		indicatorReportLineDAO.saveOrUpdate(indicatorReportLine);
	}
    
	/** @see TargetInstanceManager#getAnnotations(TargetInstance). */
	public List<Annotation> getAnnotations(TargetInstance aTargetInstance) {
		List<Annotation> annotations = null;
		if (aTargetInstance.getOid() != null) {
			annotations = annotationDAO.loadAnnotations(TargetInstance.class.getName(), aTargetInstance.getOid());
		}
		
		if (annotations == null) {
			annotations = new ArrayList<Annotation>();
		}
		
		return annotations;
	}
		
	/** @see TargetInstanceManager#countTargetInstances(User, ArrayList). */
	public int countTargetInstances(User aUser, ArrayList<String> aStates) {
		return targetInstanceDao.countTargetInstances(aUser.getUsername(), aStates);
	}
	
	/** @see TargetInstanceManager#countTargetInstancesByTarget(Long Oid). */
	public int countTargetInstancesByTarget(Long Oid) {
		return targetInstanceDao.countTargetInstancesByTarget(Oid);
	}

	/**
     * @param targetInstanceDao The targetInstanceDao to set.
     */
    public void setTargetInstanceDao(TargetInstanceDAO targetInstanceDao) {
        this.targetInstanceDao = targetInstanceDao;
    }

	/**
	 * @param auditor the auditor to set
	 */
	public void setAuditor(Auditor auditor) {
		this.auditor = auditor;
	}

	/**
	 * @param annotationDAO the annotationDAO to set
	 */
	public void setAnnotationDAO(AnnotationDAO annotationDAO) {
		this.annotationDAO = annotationDAO;
	}

	/**
	 * @param profileDAO the profileDAO to set
	 */
	public void setProfileDAO(ProfileDAO profileDAO) {
		this.profileDAO = profileDAO;
	}

	/**
	 * @param inTrayManager the inTrayManager to set
	 */
	public void setInTrayManager(InTrayManager inTrayManager) {
		this.inTrayManager = inTrayManager;
	}
	
    /**
     * Save all of the target instances in the collection.
     * @param aCollection A collection of target instances to be saved.
     */
    public void saveAll(Collection<TargetInstance> aCollection) {
    	targetInstanceDao.saveAll(aCollection);
    }
    
    /**
     * Delete TargetInstances for the specified Target and Schedule
     * @param aTargetOid The target OID.
     * @param aScheduleOid The schedule OID.
     */
    public void deleteTargetInstances(Long aTargetOid, Long aScheduleOid) {
    	targetInstanceDao.deleteScheduledInstances(aTargetOid, aScheduleOid);
    }
    
    public List<HarvestHistoryDTO> getHarvestHistory(Long targetOid) {
    	return targetInstanceDao.getHarvestHistory(targetOid);
    }
    
    public List<HarvestResult> getHarvestResults(Long targetInstanceOid) {
    	return targetInstanceDao.getHarvestResults(targetInstanceOid);
    }
    
	public HarvestResourceDTO getHarvestResourceDTO(final long harvestResultOid, final String resource) {
		return targetInstanceDao.getHarvestResourceDTO(harvestResultOid, resource);
	}

    
    public void purgeTargetInstance(TargetInstance aTargetInstance)
    {
    	aTargetInstance.setPurged(true);
		targetInstanceDao.save(aTargetInstance);
		targetInstanceDao.deleteHarvestResources(aTargetInstance.getOid());
    }

	public void setIndicatorDAO(IndicatorDAO indicatorDAO) {
		this.indicatorDAO = indicatorDAO;
	}
	
	public IndicatorDAO getIndicatorDAO() {
		return indicatorDAO;
	}

	public IndicatorCriteriaDAO getIndicatorCriteriaDAO() {
		return indicatorCriteriaDAO;
	}
	
	public List<IndicatorCriteria> getIndicatorCriterias() {
		return indicatorCriteriaDAO.getIndicatorCriterias();
	}
	
	public List<IndicatorCriteria> getIndicatorCriteriasByAgencyOid(Long agencyOid) {
		return indicatorCriteriaDAO.getIndicatorCriteriasByAgencyOid(agencyOid);
	}
	
	public void setIndicatorCriteriaDAO(IndicatorCriteriaDAO indicatorCriteriaDAO) {
		this.indicatorCriteriaDAO = indicatorCriteriaDAO;
	}
	
	public void setIndicatorReportLineDAO(IndicatorReportLineDAO indicatorReportLineDAO) {
		this.indicatorReportLineDAO = indicatorReportLineDAO;
	}
	
	public void deleteIndicatorReportLines(Indicator indicator) {
		while (indicator.getIndicatorReportLines().size() > 0) {
			indicatorReportLineDAO.delete(indicator.getIndicatorReportLines().get(0));
		}
	}
}