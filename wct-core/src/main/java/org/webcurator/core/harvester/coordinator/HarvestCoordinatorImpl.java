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
package org.webcurator.core.harvester.coordinator;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webcurator.core.archive.SipBuilder;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.harvester.HarvesterType;
import org.webcurator.core.harvester.agent.HarvestAgentConfig;
import org.webcurator.core.notification.InTrayManager;
import org.webcurator.core.notification.MessageType;
import org.webcurator.core.profiles.HeritrixProfile;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.store.DigitalAssetStore;
import org.webcurator.core.store.DigitalAssetStoreFactory;
import org.webcurator.core.targets.TargetManager;
import org.webcurator.domain.TargetInstanceCriteria;
import org.webcurator.domain.TargetInstanceDAO;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.core.*;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.domain.model.dto.QueuedTargetInstanceDTO;

/**
 * 
 * @author nwaight
 */
public class HarvestCoordinatorImpl implements HarvestCoordinator {

	private static final long HOUR_MILLISECONDS = 60 * 60 * 1000;

	private TargetInstanceManager targetInstanceManager;

	// Functionality segregated to reduce complexity and increase testability
	private HarvestAgentManager harvestAgentManager;
	private HarvestLogManager harvestLogManager;
	private HarvestBandwidthManager harvestBandwidthManager;
	private HarvestQaManager harvestQaManager;

	private TargetInstanceDAO targetInstanceDao;

	private DigitalAssetStoreFactory digitalAssetStoreFactory;

	/** The Target Manager. */
	private TargetManager targetManager;

	/** The InTrayManager. */
	private InTrayManager inTrayManager;

	/**
	 * the number of days before a target instance's digital assets are purged
	 * from the DAS.
	 */
	private int daysBeforeDASPurge = 14;

	/**
	 * the number of days before an aborted target instance's remnant data are
	 * purged from the system.
	 */
	private int daysBeforeAbortedTargetInstancePurge = 7;

	private SipBuilder sipBuilder = null;

	private Logger log = LoggerFactory.getLogger(getClass());
	private boolean queuePaused = false;

	private int harvestOptimizationLookAheadHours;
	private int numHarvestersExcludedFromOptimisation;
	private boolean harvestOptimizationEnabled;

	/** Default Constructor. */
	public HarvestCoordinatorImpl() {
		super();
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestAgentListener#heartbeat(org.webcurator.core.harvester.agent.HarvestAgentStatus)
	 */
	public void heartbeat(HarvestAgentStatusDTO aStatus) {
		harvestAgentManager.heartbeat(aStatus);
	}

	public void requestRecovery(String haHost, int port, String haService) {}

	/**
	 * Execute search of Target Instances in 'Running' or 'Paused' states. Add any
	 * active job names to List for Harvest Agent attempting recovery.
	 *
	 * @see org.webcurator.core.harvester.coordinator.HarvestAgentListener#recoverHarvests(java.lang.String, int, java.lang.String)
	 * @param haHost harvest agent host requesting attempting recovery
	 * @param haPort harvest agent port requesting attempting recovery
	 * @param haService harvest agent service requesting attempting recovery
	 */
	public void recoverHarvests(String haHost, int haPort, String haService) {
		TargetInstanceCriteria criteria = new TargetInstanceCriteria();
		Set<String> states = new HashSet<String>();
		states.add("Running");
		states.add("Paused");
		criteria.setStates(states);
		List<TargetInstance> results = targetInstanceDao.findTargetInstances(criteria);
		List<String> activeJobs = new ArrayList<String>();
		for(TargetInstance ti : results){
//			log.info("RecoverHarvests: sending data back for TI: " + ti.getJobName());
			activeJobs.add(ti.getJobName());
		}
		harvestAgentManager.recoverHarvests(haHost, haPort, haService, activeJobs);
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestAgentListener#harvestComplete(org.webcurator.core.model.HarvestResult)
	 */
	public void harvestComplete(HarvestResultDTO aResult) {
		TargetInstance ti = targetInstanceDao.load(aResult.getTargetInstanceOid());
		if (ti == null) {
			throw new WCTRuntimeException("Unknown TargetInstance oid recieved " + aResult.getTargetInstanceOid()
					+ " failed to save HarvestResult.");
		}

		// The result is for the original harvest, but the TI already has one or
		// more results
		if (aResult.getHarvestNumber() == 1 && !ti.getHarvestResults().isEmpty()) {
			// This is a repeat message probably due to a timeout. Leaving this
			// to run
			// would generate a second 'Original Harvest' which will
			// subsequently fail in indexing
			// due to a duplicate file name constraint in the arc_harvest_file
			// table
			log.warn("Duplicate 'Harvest Complete' message received for job: " + ti.getOid() + ". Message ignored.");
			return;
		}

		log.info("'Harvest Complete' message received for job: " + ti.getOid() + ".");

		HarvestResult harvestResult = null;
		if (aResult instanceof ArcHarvestResultDTO) {
			harvestResult = new ArcHarvestResult((ArcHarvestResultDTO) aResult, ti);
		} else {
			harvestResult = new HarvestResult(aResult, ti);
		}

		harvestResult.setState(HarvestResult.STATE_INDEXING);

		List<HarvestResult> hrs = ti.getHarvestResults();
		hrs.add(harvestResult);
		ti.setHarvestResults(hrs);

		ti.setState(TargetInstance.STATE_HARVESTED);

		targetInstanceDao.save(harvestResult);
		targetInstanceDao.save(ti);
		harvestBandwidthManager.sendBandWidthRestrictions();

		// IF the associated target record for this TI has
		// no active TIs remaining (scheduled, queued, running,
		// paused, stopping)
		// AND
		// the target's schedule is not active (i.e we're past
		// the schedule end date),
		// THEN set the status of the associated target to 'complete'.
		//
		boolean bActiveSchedules = false;
		AbstractTarget tiTarget = ti.getTarget();
		Date now = new Date();
		for (Schedule schedule : tiTarget.getSchedules()) {
			if (schedule.getEndDate() == null) {
				bActiveSchedules = true;
			} else {
				if (schedule.getEndDate().after(now)) {
					bActiveSchedules = true;
				}
			}
		}

		if (targetInstanceDao.countActiveTIsForTarget(tiTarget.getOid()) == 0 && !bActiveSchedules) {
			Target t = targetManager.load(tiTarget.getOid(), true);
			t.changeState(Target.STATE_COMPLETED);
			targetManager.save(t);
		}

		// Ask the DigitalAssetStore to index the ARC
		try {
			digitalAssetStoreFactory.getDAS().initiateIndexing(
					new ArcHarvestResultDTO(harvestResult.getOid(), harvestResult.getTargetInstance().getOid(), harvestResult
							.getCreationDate(), harvestResult.getHarvestNumber(), harvestResult.getProvenanceNote()));
		} catch (DigitalAssetStoreException ex) {
			log.error("Could not send initiateIndexing message to the DAS", ex);
		}

		inTrayManager.generateNotification(ti.getOwner().getOid(), MessageType.CATEGORY_MISC, MessageType.TARGET_INSTANCE_COMPLETE,
				ti);
		inTrayManager.generateTask(Privilege.ENDORSE_HARVEST, MessageType.TARGET_INSTANCE_ENDORSE, ti);

		log.info("'Harvest Complete' message processed for job: " + ti.getOid() + ".");

		//TODO WARNING - the auto prune process initiates it's own indexing, but it potentially does this
		//while the indexing initiated above is STILL RUNNING.  The fact that it works is likely attributable
		//to the fact that the second indexing is likely to finish after the first, but this may not always be
		//the case.
		runAutoPrune(ti);

	}

	/**
	 * @param ti
	 */
	private void runAutoPrune(TargetInstance ti) {
		// auto-prune if this option is enabled on the target and the ti has
		// only one harvest result
		try {
			List<HarvestResult> harvestResults = targetInstanceManager.getHarvestResults(ti.getOid());
			if (harvestResults.size() == 1) {
				harvestQaManager.autoPrune(ti);
			}
		} catch (DigitalAssetStoreException ex) {
			log.error("Could not auto-prune the target instance with oid " + ti.getOid(), ex);
		}
	}

	private void cleanHarvestResult(HarvestResult harvestResult) {
		if (harvestResult != null) {
			if (harvestResult.getResources() != null) {
				targetInstanceDao.deleteHarvestResultResources(harvestResult.getOid());
			}

			if (harvestResult instanceof ArcHarvestResult) {
				targetInstanceDao.deleteHarvestResultFiles(harvestResult.getOid());
			}
		}
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestCoordinator#reIndexHarvestResult(HarvestResult)
	 */
	public Boolean reIndexHarvestResult(HarvestResult origHarvestResult) {
		TargetInstance ti = origHarvestResult.getTargetInstance();

		// Assume we are already indexing
		Boolean reIndex = false;

		try {
			reIndex = !digitalAssetStoreFactory.getDAS().checkIndexing(origHarvestResult.getOid());
		} catch (DigitalAssetStoreException ex) {
			log.error("Could not send checkIndexing message to the DAS", ex);
		}

		if (reIndex) {
			// Save any unsaved changes
			targetInstanceDao.save(ti);

			// remove any HarvestResources and ArcHarvestFiles
			cleanHarvestResult(origHarvestResult);

			// reload the targetInstance
			ti = targetInstanceDao.load(ti.getOid());

			HarvestResult newHarvestResult = null;
			if (origHarvestResult instanceof ArcHarvestResult) {
				ArcHarvestResultDTO ahr = new ArcHarvestResultDTO();
				ahr.setCreationDate(new Date());
				ahr.setTargetInstanceOid(ti.getOid());
				ahr.setProvenanceNote(origHarvestResult.getProvenanceNote());
				ahr.setHarvestNumber(origHarvestResult.getHarvestNumber());
				newHarvestResult = new ArcHarvestResult(ahr, ti);
			} else {
				HarvestResultDTO hr = new HarvestResultDTO();
				hr.setCreationDate(new Date());
				hr.setTargetInstanceOid(ti.getOid());
				hr.setProvenanceNote(origHarvestResult.getProvenanceNote());
				hr.setHarvestNumber(origHarvestResult.getHarvestNumber());
				newHarvestResult = new HarvestResult(hr, ti);
			}

			origHarvestResult.setState(HarvestResult.STATE_ABORTED);
			newHarvestResult.setState(HarvestResult.STATE_INDEXING);

			List<HarvestResult> hrs = ti.getHarvestResults();
			hrs.add(newHarvestResult);
			ti.setHarvestResults(hrs);

			ti.setState(TargetInstance.STATE_HARVESTED);

			targetInstanceDao.save(newHarvestResult);
			targetInstanceDao.save(ti);

			try {
				digitalAssetStoreFactory.getDAS().initiateIndexing(
						new ArcHarvestResultDTO(newHarvestResult.getOid(), newHarvestResult.getTargetInstance().getOid(),
								newHarvestResult.getCreationDate(), newHarvestResult.getHarvestNumber(), newHarvestResult
										.getProvenanceNote()));

				inTrayManager.generateNotification(ti.getOwner().getOid(), MessageType.CATEGORY_MISC,
						MessageType.TARGET_INSTANCE_COMPLETE, ti);
				inTrayManager.generateTask(Privilege.ENDORSE_HARVEST, MessageType.TARGET_INSTANCE_ENDORSE, ti);
			} catch (DigitalAssetStoreException ex) {
				log.error("Could not send initiateIndexing message to the DAS", ex);
			}
		}

		return reIndex;
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestAgentListener#notification(Long,
	 *      String, String)
	 */
	public void notification(Long aTargetInstanceOid, int notificationCategory, String aMessageType) {
		TargetInstance ti = targetInstanceDao.load(aTargetInstanceOid);
		inTrayManager.generateNotification(ti.getOwner().getOid(), notificationCategory, aMessageType, ti);
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestAgentListener#notification(String,
	 *      String)
	 */
	public void notification(String aSubject, int notificationCategory, String aMessage) {
		List<String> privs = new ArrayList<String>();
		privs.add(Privilege.MANAGE_WEB_HARVESTER);
		inTrayManager.generateNotification(privs, notificationCategory, aSubject, aMessage);
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestAgentCoordinator#harvest(TargetInstance)
	 */
	public void harvest(TargetInstance aTargetInstance, HarvestAgentStatusDTO aHarvestAgent) {
		if (aTargetInstance == null) {
			throw new WCTRuntimeException("A null target instance was provided to the harvest command.");
		}

		if (aHarvestAgent == null) {
			throw new WCTRuntimeException("A null harvest agent status was provided to the harvest command.");
		}

		// if the target is not approved to be harvested then do not harvest
		if (queuePaused || !isTargetApproved(aTargetInstance) || aHarvestAgent.getMemoryWarning()) {
			return;
		}

		// Prepare the instance for harvesting by storing its current
		// information.
		prepareHarvest(aTargetInstance);

		// Run the actual harvest.
		_harvest(aTargetInstance, aHarvestAgent);
	}

	private void prepareHarvest(TargetInstance aTargetInstance) {
		BusinessObjectFactory factory = new BusinessObjectFactory();
		Set<String> originalSeeds = new HashSet<String>();
		Set<SeedHistory> seedHistory = new HashSet<SeedHistory>();
		for (Seed seed : targetManager.getSeeds(aTargetInstance)) {
			originalSeeds.add(seed.getSeed());

			if (targetInstanceManager.getStoreSeedHistory()) {
				SeedHistory history = factory.newSeedHistory(aTargetInstance, seed);
				seedHistory.add(history);
			}
		}

		// Note that seed history should eventually supplant original seeds.
		// Original seeds
		// has been implemented as a Hibernate collection of String and thus the
		// target_instance_orig_seeds table has no id column, preventing further
		// expansion
		// of the collection. The seed history needs to include the primary
		// column. The
		// originalSeeds are used by the quality review (prune) tool to generate
		// the to level
		// tree view. The original seeds has been left in for this purpose to
		// support legacy
		// target instances created prior to this release. Future releases may
		// see the removal
		// of this functionality in favour of the SeedHistory; meanwhile this
		// can be turned off
		// via the targetInstanceManager bean in wct_core.xml
		aTargetInstance.setOriginalSeeds(originalSeeds);
		if (targetInstanceManager.getStoreSeedHistory()) {
			aTargetInstance.setSeedHistory(seedHistory);
		}
		// Generate some of the SIP.
		Map<String, String> sipParts = sipBuilder.buildSipSections(aTargetInstance);
		aTargetInstance.setSipParts(sipParts);

		// Save the sip parts and seeds to the database.
		targetInstanceDao.save(aTargetInstance);
	}

	/**
	 * Internal harvest method.
	 * 
	 * @param aTargetInstance
	 *            The instance to harvest.
	 * @param aHarvestAgent
	 *            The agent to harvest on.
	 */
	private void _harvest(TargetInstance aTargetInstance, HarvestAgentStatusDTO aHarvestAgent) {
		if (aTargetInstance == null) {
			throw new WCTRuntimeException("A null target instance was provided to the harvest command.");
		}

		if (aHarvestAgent == null) {
			throw new WCTRuntimeException("A null harvest agent status was provided to the harvest command.");
		}

		// if the target is not approved to be harvested then do not harvest
		if (!isTargetApproved(aTargetInstance) || aHarvestAgent.getMemoryWarning()) {
			return;
		}

		// Create the seeds file contents.
		StringBuffer seeds = new StringBuffer();
		Set<String> originalSeeds = aTargetInstance.getOriginalSeeds();
		for (String seed : originalSeeds) {
			seeds.append(seed);
			seeds.append("\n");
		}

		// Get the profile.
		String profile = getHarvestProfileString(aTargetInstance);

		// Initiate harvest on the remote harvest agent
		harvestAgentManager.initiateHarvest(aHarvestAgent, aTargetInstance, profile, seeds.toString());

		// Update the state of the allocated Target Instance
		aTargetInstance.setActualStartTime(new Date());
		aTargetInstance.setState(TargetInstance.STATE_RUNNING);
		aTargetInstance.setHarvestServer(aHarvestAgent.getName());

		// Save the updated information.
		targetInstanceManager.save(aTargetInstance);

		log.info("HarvestCoordinator: Harvest initiated successfully for target instance " + aTargetInstance.getOid().toString());

		// Run the bandwidth calculations.
		harvestBandwidthManager.sendBandWidthRestrictions();
	}

	/** @see HarvestCoordinator#checkForBandwidthTransition(). */
	public synchronized void checkForBandwidthTransition() {
		harvestBandwidthManager.checkForBandwidthTransition();
	}

	/**
	 * Get the profile string with the overrides applied.
	 * 
	 * @return
	 */
	private String getHarvestProfileString(TargetInstance aTargetInstance) {

		Profile profile = aTargetInstance.getTarget().getProfile();

		if (profile.getHarvesterType().equals(HarvesterType.HERITRIX1.name())) {
			String profileString = profile.getProfile();

			// replace any ${TI_OID} tokens
			profileString = profileString.replace("${TI_OID}", aTargetInstance.getOid().toString());

			HeritrixProfile heritrixProfile = HeritrixProfile.fromString(profileString);

			if (aTargetInstance.getProfileOverrides().hasOverrides()) {
				log.info("Applying Profile Overrides for " + aTargetInstance.getOid());
				aTargetInstance.getProfileOverrides().apply(heritrixProfile);
			}

			heritrixProfile.setToeThreads(targetManager.getSeeds(aTargetInstance).size() * 2);
			return heritrixProfile.toString();
		}
		if (profile.getHarvesterType().equals(HarvesterType.HERITRIX3.name())) {
			String profileString = profile.getProfile();
			// TODO - Heritrix3 overrides
			return profileString;
		}
		return profile.getProfile();
	}

	public long getCurrentGlobalMaxBandwidth() {
		return harvestBandwidthManager.getCurrentGlobalMaxBandwidth();
	}

	/**
	 * Checks if harvest optimization is permitted within the current bandwidth
	 * restriction. Note that this is different to the
	 * "isHarvestOptimizationEnabled" check.
	 * 
	 * @return
	 */
	boolean isHarvestOptimizationAllowed() {
		return harvestBandwidthManager.isHarvestOptimizationAllowed();
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestCoordinator#getBandwidthRestrictions()
	 *      .
	 */
	public HashMap<String, List<BandwidthRestriction>> getBandwidthRestrictions() {
		return harvestBandwidthManager.getBandwidthRestrictions();
	}

	/**
	 * @see org.webcurator.domain.HarvestCoordinatorDAO#getBandwidthRestriction(Long)
	 *      .
	 */
	public BandwidthRestriction getBandwidthRestriction(Long aOid) {
		return harvestBandwidthManager.getBandwidthRestriction(aOid);
	}

	/**
	 * @see org.webcurator.domain.HarvestCoordinatorDAO#getBandwidthRestriction(String,
	 *      Date).
	 */
	public BandwidthRestriction getBandwidthRestriction(String aDay, Date aTime) {
		return harvestBandwidthManager.getBandwidthRestriction(aDay, aTime);
	}

	/**
	 * @see org.webcurator.domain.HarvestCoordinatorDAO#saveOrUpdate(BandwidthRestriction)
	 *      .
	 */
	public void saveOrUpdate(BandwidthRestriction bandwidthRestriction) {
		harvestBandwidthManager.saveOrUpdate(bandwidthRestriction);
	}

	/** @see org.webcurator.domain.HarvestCoordinatorDAO#delete(Object). */
	public void delete(BandwidthRestriction bandwidthRestriction) {
		harvestBandwidthManager.delete(bandwidthRestriction);
	}

	/**
	 * @param aMinimumBandwidth
	 *            The minimumBandwidth to set.
	 */
	public void setMinimumBandwidth(int aMinimumBandwidth) {
		harvestBandwidthManager.setMinimumBandwidth(aMinimumBandwidth);
	}

	/**
	 * @param targetInstanceDao
	 *            The targetInstanceDao to set.
	 */
	public void setTargetInstanceDao(TargetInstanceDAO targetInstanceDao) {
		this.targetInstanceDao = targetInstanceDao;
	}

	/**
	 * @return Returns the maxBandwidthPercent.
	 */
	public int getMaxBandwidthPercent() {
		return harvestBandwidthManager.getMaxBandwidthPercent();
	}

	/**
	 * @param maxBandwidthPercent
	 *            The maxBandwidthPercent to set.
	 */
	public void setMaxBandwidthPercent(int maxBandwidthPercent) {
		harvestBandwidthManager.setMaxBandwidthPercent(maxBandwidthPercent);
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestCoordinator#updateProfileOverrides(TargetInstance)
	 */
	public void updateProfileOverrides(TargetInstance aTargetInstance) {
		if (aTargetInstance == null) {
			throw new WCTRuntimeException("A null target instance was provided.");
		}
		String profile = getHarvestProfileString(aTargetInstance);
		harvestAgentManager.updateProfileOverrides(aTargetInstance, profile);
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestCoordinator#pause(TargetInstance)
	 */
	public void pause(TargetInstance aTargetInstance) {
		if (aTargetInstance == null) {
			throw new WCTRuntimeException("A null target instance was provided to the harvest command.");
		}
		harvestAgentManager.pause(aTargetInstance);
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestCoordinator#resume(TargetInstance)
	 */
	public void resume(TargetInstance aTargetInstance) {
		harvestAgentManager.resume(aTargetInstance);
		// When profile overrides need updating we should also reset the
		// bandwidth restrictions
		harvestBandwidthManager.sendBandWidthRestrictions();
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestCoordinator#abort(TargetInstance)
	 */
	public void abort(TargetInstance aTargetInstance) {
		if (aTargetInstance == null) {
			throw new WCTRuntimeException("A null target instance was provided to the harvest command.");
		}
		harvestAgentManager.abort(aTargetInstance);
		harvestBandwidthManager.sendBandWidthRestrictions();
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestCoordinator#stop(TargetInstance)
	 */
	public void stop(TargetInstance aTargetInstance) {
		if (aTargetInstance == null) {
			throw new WCTRuntimeException("A null target instance was provided to the harvest command.");
		}
		harvestAgentManager.stop(aTargetInstance);
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestCoordinator#pauseAll()
	 */
	public void pauseAll() {
		harvestAgentManager.pauseAll();
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestCoordinator#resumeAll()
	 */
	public void resumeAll() {
		harvestAgentManager.resumeAll();
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestCoordinator#pauseQueue()
	 */
	public void pauseQueue() {
		queuePaused = true;
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestCoordinator#resumeQueue()
	 */
	public void resumeQueue() {
		queuePaused = false;
	}

	/**
	 * @see org.webcurator.core.harvester.coordinator.HarvestCoordinator#isQueuePaused()
	 */
	public boolean isQueuePaused() {
		return queuePaused;
	}

	/** @see HarvestCoordinator#processSchedule(). */
	public void processSchedule() {
		long now = System.currentTimeMillis();
		queueScheduledInstances();
		queueOptimisableInstances();

		log.debug("Processing schedules took {} ms", System.currentTimeMillis() - now);
	}

	private void queueScheduledInstances() {
		List<QueuedTargetInstanceDTO> theQueue = targetInstanceDao.getQueue();
		log.info("Start: Processing " + theQueue.size() + " entries from the queue.");

		QueuedTargetInstanceDTO ti = null;
		Iterator<QueuedTargetInstanceDTO> it = theQueue.iterator();
		while (it.hasNext()) {
			ti = it.next();
			log.info("Processing queue entry: " + ti.toString());
			harvestOrQueue(ti);
		}
		log.info("Finished: Processing {} entries from the queue.", theQueue.size());
	}

	void queueOptimisableInstances() {
		if (harvestOptimizationEnabled && isHarvestOptimizationAllowed()) {
			int optimizedJobCount = 0;
			int optimizationUnavailableCount = 0;
			List<QueuedTargetInstanceDTO> upcomingJobs = targetInstanceDao.getUpcomingJobs(harvestOptimizationLookAheadHours
					* HOUR_MILLISECONDS);
			int upcomingJobCount = upcomingJobs.size();
			log.info("Start: Attempting to optimize {} entries from the queue.", upcomingJobCount);
			for (QueuedTargetInstanceDTO qti : upcomingJobs) {
				if(loadAndStartOptimizable(qti)) {
					optimizedJobCount++;
				} else {
					optimizationUnavailableCount++;
				}
			}
			log.info(MessageFormat.format(
					"Finished: Processed {0} of {1} upcoming jobs, of which {2} were not eligible/configured for optimization",
					(optimizedJobCount + optimizationUnavailableCount), upcomingJobCount, optimizationUnavailableCount));
		}
	}

	private boolean loadAndStartOptimizable(QueuedTargetInstanceDTO qti) {
		TargetInstance targetInstance = loadTargetInstance(qti.getOid());
		AbstractTarget abstractTarget = targetInstance.getTarget();
		if (abstractTarget.getObjectType() == AbstractTarget.TYPE_TARGET) {
			Target target = targetManager.load(abstractTarget.getOid());
			if (target.isAllowOptimize()) {
				boolean harvesterWasAvailableForOptimize = startOptimisableInstance(targetInstance, qti.getAgencyName());
				return harvesterWasAvailableForOptimize;
			}
		}
		return false;
	}
	
	private boolean startOptimisableInstance(TargetInstance targetInstance, String agencyName) {
		List<HarvestAgentStatusDTO> harvesters = harvestAgentManager.getAvailableHarvesters(agencyName);
		if (harvesters.size() > numHarvestersExcludedFromOptimisation) {
			return startOptimizedHarvest(targetInstance, harvesters);
		} else {
			log.trace("No available harvesters to optimize target instance id {}", targetInstance.getOid());
			return false;
		}
	}

	private boolean startOptimizedHarvest(TargetInstance targetInstance, List<HarvestAgentStatusDTO> harvesters) {
		for (HarvestAgentStatusDTO agent : harvesters) {
			log.trace("Harvester {} can optimize scheduled of target instance id {}", agent.getName(),
					targetInstance.getOid());
			if (harvestTargetInstance(agent, targetInstance)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Run the checks to see if the target instance can be harvested or if it
	 * must be queued. If harvest is possible and there is a harvester available
	 * then allocate it.
	 * 
	 * @param aTargetInstance
	 *            the target instance to harvest
	 */
	public synchronized void harvestOrQueue(QueuedTargetInstanceDTO aTargetInstance) {
		TargetInstance ti = null;
		boolean approved = true;

		// lock the ti
		Long tiOid = aTargetInstance.getOid();
		if (!harvestAgentManager.lock(tiOid))
			return;
		log.info("Obtained lock for ti " + tiOid);

		if (TargetInstance.STATE_SCHEDULED.equals(aTargetInstance.getState())) {
			ti = loadTargetInstance(tiOid);
			approved = isTargetApproved(ti);
		}

		if (approved) {
			queueApprovedHarvest(aTargetInstance, ti, tiOid);
		}
		// release the lock
		harvestAgentManager.unLock(tiOid);
		log.info("Released lock for ti " + tiOid);
	}

	private void queueApprovedHarvest(QueuedTargetInstanceDTO queuedTargetInstance, TargetInstance ti, Long tiOid) {
		boolean processed = false;
		while (!processed) {
			// Check to see what harvester resource is available
			HarvestAgentStatusDTO agent = harvestAgentManager.getHarvester(queuedTargetInstance.getAgencyName());

			if (harvestAgentCanHarvest(agent, queuedTargetInstance)) {
				synchronized (agent) {
					// allocate the target instance to the agent
					if (ti == null) {
						ti = loadTargetInstance(tiOid);
					}
					log.info("Allocating TI " + tiOid + " to agent " + agent.getName());
					processed = harvestTargetInstance(agent, ti);
				}
			} else {
				processed = true;
				log.info("Re-queueing TI " + tiOid);
				// if not already queued set the target instance to the
				// queued state.
				if (!queuedTargetInstance.getState().equals(TargetInstance.STATE_QUEUED)) {
					if (ti == null) {
						ti = loadTargetInstance(tiOid);
					}

					// Prepare the harvest for the queue.
					prepareHarvest(ti);

					ti.setState(TargetInstance.STATE_QUEUED);
					targetInstanceDao.save(ti);
					inTrayManager.generateNotification(ti.getOwner().getOid(), MessageType.CATEGORY_MISC,
							MessageType.TARGET_INSTANCE_QUEUED, ti);
				}
			}
		}
	}

	private TargetInstance loadTargetInstance(Long tiOid) {
		TargetInstance ti = targetInstanceDao.load(tiOid);
		ti = targetInstanceDao.populate(ti);
		return ti;
	}

	private boolean harvestTargetInstance(HarvestAgentStatusDTO agent, TargetInstance ti) {
		boolean processed = false;
		try {
			if (!TargetInstance.STATE_QUEUED.equals(ti.getState())) {
				prepareHarvest(ti);
			}
			_harvest(ti, agent);
			agent.setInTransition(true);
			processed = true;
		} catch (Throwable e) {
			log.warn(MessageFormat.format("Failed to allocate harvest to agent {0}: {1}", agent.getName(), e.getMessage()), e);
			harvestAgentManager.markDead(agent);
		}
		return processed;
	}

	private boolean harvestAgentCanHarvest(HarvestAgentStatusDTO agent, QueuedTargetInstanceDTO aTargetInstance) {
		return !queuePaused && agent != null && agent.isAcceptTasks()
				&& harvestBandwidthManager.isMiniumBandwidthAvailable(aTargetInstance);
	}

	/**
	 * Check that the target that the instance belongs to is approved and if not
	 * don't harvest.
	 * 
	 * @param aTargetInstance
	 *            the target instance whos target should be checked.
	 * @return flag to indicat approval
	 */
	private boolean isTargetApproved(TargetInstance aTargetInstance) {
		// Check permissions if none defer the target instance and send and
		// notification
		if (!targetManager.isTargetHarvestable(aTargetInstance)) {
			// Defer the schedule 24 hours and notifiy the owner.
			Calendar cal = Calendar.getInstance();
			cal.setTime(aTargetInstance.getScheduledTime());
			cal.add(Calendar.DATE, 1);

			aTargetInstance.setScheduledTime(cal.getTime());
			targetInstanceDao.save(aTargetInstance);

			log.info("The target {} is not apporoved for harvest and has been defered 24 hours.", aTargetInstance.getTarget()
					.getName());
			inTrayManager.generateNotification(aTargetInstance.getOwner().getOid(), MessageType.CATEGORY_MISC,
					MessageType.TARGET_INSTANCE_RESCHEDULED, aTargetInstance);

			return false;
		}

		return true;
	}

	/** @see HarvestCoordinator#isMiniumBandwidthAvailable(TargetInstance) . */
	public boolean isMiniumBandwidthAvailable(TargetInstance aTargetInstance) {
		return harvestBandwidthManager.isMiniumBandwidthAvailable(aTargetInstance);
	}

	/**
	 * @see HarvestCoordinator#listLogFiles(TargetInstance)
	 */
	public List<String> listLogFiles(TargetInstance aTargetInstance) {
		if (aTargetInstance == null) {
			throw new WCTRuntimeException("A null target instance was provided to the listLogFiles command.");
		}
		return harvestLogManager.listLogFiles(aTargetInstance);
	}

	/**
	 * @see HarvestCoordinator#listLogFileAttributes(TargetInstance)
	 */
	public LogFilePropertiesDTO[] listLogFileAttributes(TargetInstance aTargetInstance) {
		return harvestLogManager.listLogFileAttributes(aTargetInstance);
	}

	/**
	 * @see HarvestCoordinator#tailLog(TargetInstance, String, int)
	 */
	public String[] tailLog(TargetInstance aTargetInstance, String aFileName, int aNoOfLines) {
		return harvestLogManager.tailLog(aTargetInstance, aFileName, aNoOfLines);
	}

	/**
	 * @see HarvestCoordinator#countLogLines(TargetInstance, String)
	 */
	public Integer countLogLines(TargetInstance aTargetInstance, String aFileName) {
		return harvestLogManager.countLogLines(aTargetInstance, aFileName);
	}

	/**
	 * @see HarvestCoordinator#headLog(TargetInstance, String, int)
	 */
	public String[] headLog(TargetInstance aTargetInstance, String aFileName, int aNoOfLines) {
		return harvestLogManager.headLog(aTargetInstance, aFileName, aNoOfLines);
	}

	/**
	 * @see HarvestCoordinator#getLog(TargetInstance, String, int, int)
	 */
	public String[] getLog(TargetInstance aTargetInstance, String aFileName, int aStartLine, int aNoOfLines) {
		return harvestLogManager.getLog(aTargetInstance, aFileName, aStartLine, aNoOfLines);
	}

	/**
	 * @see HarvestCoordinator#getFirstLogLineBeginning(TargetInstance, String,
	 *      String)
	 */
	public Integer getFirstLogLineBeginning(TargetInstance aTargetInstance, String aFileName, String match) {
		return harvestLogManager.getFirstLogLineBeginning(aTargetInstance, aFileName, match);
	}

	/**
	 * @see HarvestCoordinator#getFirstLogLineContaining(TargetInstance, String,
	 *      String)
	 */
	public Integer getFirstLogLineContaining(TargetInstance aTargetInstance, String aFileName, String match) {
		return harvestLogManager.getFirstLogLineContaining(aTargetInstance, aFileName, match);
	}

	/**
	 * @see HarvestCoordinator#getFirstLogLineAfterTimeStamp(TargetInstance,
	 *      String, Long)
	 */
	public Integer getFirstLogLineAfterTimeStamp(TargetInstance aTargetInstance, String aFileName, Long timestamp) {
		return harvestLogManager.getFirstLogLineAfterTimeStamp(aTargetInstance, aFileName, timestamp);
	}

	/**
	 * @see HarvestCoordinator#getLogLinesByRegex(TargetInstance, String, int,
	 *      String)
	 */
	public String[] getLogLinesByRegex(TargetInstance aTargetInstance, String aFileName, int aNoOfLines, String aRegex,
			boolean prependLineNumbers) {
		return harvestLogManager.getLogLinesByRegex(aTargetInstance, aFileName, aNoOfLines, aRegex, prependLineNumbers);
	}

	/**
	 * @see HarvestCoordinator#getHopPath(TargetInstance, String, String)
	 */
	public String[] getHopPath(TargetInstance aTargetInstance, String aFileName, String aUrl) {
		return harvestLogManager.getHopPath(aTargetInstance, aFileName, aUrl);
	}

	public File getLogfile(TargetInstance aTargetInstance, String aFilename) {
		return harvestLogManager.getLogfile(aTargetInstance, aFilename);
	}

	/**
	 * @see HarvestCoordinator#purgeDigitalAssets().
	 */
	public void purgeDigitalAssets() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, daysBeforeDASPurge * -1);

		List<TargetInstance> tis = targetInstanceDao.findPurgeableTargetInstances(cal.getTime());
		log.debug("Attempting to purge {} harvests from the digital asset store.", tis.size());

		if (tis != null && !tis.isEmpty()) {
			int index = 0;
			String[] tiNames = new String[tis.size()];
			for (TargetInstance ti : tis) {
				tiNames[index++] = ti.getJobName();
			}

			try {
				digitalAssetStoreFactory.getDAS().purge(tiNames);
				for (TargetInstance ti : tis) {
					targetInstanceManager.purgeTargetInstance(ti);
				}
			} catch (DigitalAssetStoreException e) {
				log.error(MessageFormat.format("Failed to complete the purge {0}", e.getMessage()), e);
			}
		}
	}

	/**
	 * @see HarvestCoordinator#purgeAbortedTargetInstances().
	 */
	public void purgeAbortedTargetInstances() {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, daysBeforeAbortedTargetInstancePurge * -1);

		List<TargetInstance> tis = targetInstanceDao.findPurgeableAbortedTargetInstances(cal.getTime());
		log.debug("Attempting to purge {} aborted harvests from the system.", tis.size());

		if (tis != null && !tis.isEmpty()) {
			int index = 0;
			String[] tiNames = new String[tis.size()];
			for (TargetInstance ti : tis) {
				tiNames[index++] = ti.getJobName();
			}

			harvestAgentManager.purgeAbortedTargetInstances(tiNames);

			// call the same web-method on the DAS, to delete folders which
			// may have been created in error while a running harvest was in
			// transition from running to stopping to harvested.
			try {
				digitalAssetStoreFactory.getDAS().purgeAbortedTargetInstances(tiNames);
			} catch (DigitalAssetStoreException e) {
				log.error(MessageFormat.format("Failed to complete the purge of aborted ti data via DAS: {0}", e.getMessage()), e);
			}

			try {
				for (TargetInstance ti : tis) {
					targetInstanceManager.purgeTargetInstance(ti);
				}
			} catch (Exception e) {
				log.error(
						MessageFormat.format("Failed to set the purged flag on all of the eligible aborted TIs: {0}",
								e.getMessage()), e);
			}

		}
	}

	/**
	 * @param digitalAssetStoreFactory
	 *            the digitalAssetStoreFactory to set
	 */
	public void setDigitalAssetStoreFactory(DigitalAssetStoreFactory digitalAssetStoreFactory) {
		this.digitalAssetStoreFactory = digitalAssetStoreFactory;
	}

	/**
	 * @param targetManager
	 *            the targetManager to set
	 */
	public void setTargetManager(TargetManager targetManager) {
		this.targetManager = targetManager;
	}

	public void setInTrayManager(InTrayManager inTrayManager) {
		this.inTrayManager = inTrayManager;
	}

	/**
	 * @param daysBeforeDASPurge
	 *            the daysBeforeDASPurge to set
	 */
	public void setDaysBeforeDASPurge(int daysBeforeDASPurge) {
		this.daysBeforeDASPurge = daysBeforeDASPurge;
	}

	/**
	 * @param daysBeforeAbortedTargetInstancePurge
	 *            the daysBeforeAbortedTargetInstancePurge to set
	 */
	public void setDaysBeforeAbortedTargetInstancePurge(int daysBeforeAbortedTargetInstancePurge) {
		this.daysBeforeAbortedTargetInstancePurge = daysBeforeAbortedTargetInstancePurge;
	}

	/**
	 * @param sipBuilder
	 *            the sipBuilder to set
	 */
	public void setSipBuilder(SipBuilder sipBuilder) {
		this.sipBuilder = sipBuilder;
	}

	public void addToHarvestResult(Long harvestResultOid, ArcHarvestFileDTO ahf) {
		ArcHarvestResult ahr = (ArcHarvestResult) targetInstanceDao.getHarvestResult(harvestResultOid, false);
		ArcHarvestFile f = new ArcHarvestFile(ahf, ahr);
		targetInstanceDao.save(f);
	}

	public void addHarvestResources(Long harvestResultOid, Collection<HarvestResourceDTO> dtos) {
		ArcHarvestResult ahr = (ArcHarvestResult) targetInstanceDao.getHarvestResult(harvestResultOid, false);
		Collection<ArcHarvestResource> resources = new ArrayList<ArcHarvestResource>(dtos.size());
		for (HarvestResourceDTO dto : dtos) {
			resources.add(new ArcHarvestResource((ArcHarvestResourceDTO) dto, ahr));
		}
		targetInstanceDao.saveAll(resources);
	}

	public Long createHarvestResult(HarvestResultDTO harvestResultDTO) {
		if (harvestResultDTO instanceof ArcHarvestResultDTO) {
			TargetInstance ti = targetInstanceDao.load(harvestResultDTO.getTargetInstanceOid());
			ArcHarvestResult result = new ArcHarvestResult((ArcHarvestResultDTO) harvestResultDTO, ti);
			ti.getHarvestResults().add(result);
			result.setState(HarvestResult.STATE_INDEXING);

			targetInstanceDao.save(result);
			return result.getOid();
		} else {
			throw new IllegalArgumentException("Only supports ArcHarvestResults");
		}
	}

	public void finaliseIndex(Long harvestResultOid) {
		ArcHarvestResult ahr = (ArcHarvestResult) targetInstanceDao.getHarvestResult(harvestResultOid, false);
		ahr.setState(0);
		harvestQaManager.triggerAutoQA(ahr);
		targetInstanceDao.save(ahr);
		// run the QA recommendation service to derive the Quality Indicators
		harvestQaManager.initialiseQaRecommentationService(harvestResultOid);

	}

	public void runQaRecommentationService(TargetInstance ti) {
		harvestQaManager.runQaRecommentationService(ti);
	}

	public void notifyAQAComplete(String aqaId) {
		log.debug("Received notifyAQAComplete for job(" + aqaId + ").");

		try {
			String[] ids = aqaId.split("-");
			long tiOid = Long.parseLong(ids[0]);
			TargetInstance ti = targetInstanceDao.load(tiOid);
			int harvestNumber = Integer.parseInt(ids[1]);
			HarvestResult result = ti.getHarvestResult(harvestNumber);

			// Send a message.
			inTrayManager.generateNotification(ti.getOwner().getOid(), MessageType.CATEGORY_MISC,
					MessageType.NOTIFICATION_AQA_COMPLETE, result);
		} catch (Exception e) {
			log.error("Unable to notify AQA Id: " + aqaId, e);
		}
	}

	public void removeIndexes(TargetInstance ti) {
		List<HarvestResult> results = ti.getHarvestResults();
		if (results != null) {
			Iterator<HarvestResult> it = results.iterator();
			while (it.hasNext()) {
				HarvestResult hr = it.next();
				if (hr.getState() != HarvestResult.STATE_REJECTED) {
					// Rejected HRs have already had their indexes removed
					// The endorsing process should mean there is only one none
					// rejected HR
					removeIndexes(hr);
				}
			}
		}
	}

	public void removeIndexes(HarvestResult hr) {
		DigitalAssetStore das = digitalAssetStoreFactory.getDAS();
		try {
			log.info("Attempting to remove indexes for TargetInstance " + hr.getTargetInstance().getOid() + " HarvestNumber "
					+ hr.getHarvestNumber());
			das.initiateRemoveIndexes(new ArcHarvestResultDTO(hr.getOid(), hr.getTargetInstance().getOid(), hr.getCreationDate(),
					hr.getHarvestNumber(), hr.getProvenanceNote()));
		} catch (DigitalAssetStoreException e) {
			log.error(MessageFormat.format(
					"Could not send initiateRemoveIndexes message to the DAS for TargetInstance {0} HarvestNumber {1}: {2}", hr
							.getTargetInstance().getOid(), hr.getHarvestNumber(), e.getMessage()), e);
		}
	}

	public void completeArchiving(Long targetInstanceOid, String archiveIID) {
		// Update the state.
		TargetInstance ti = targetInstanceDao.load(targetInstanceOid);
		ti.setArchiveIdentifier(archiveIID);
		ti.setArchivedTime(new Date());
		ti.setState(TargetInstance.STATE_ARCHIVED);
		targetInstanceManager.save(ti);

		// Remove any indexes
		removeIndexes(ti);

		// Send a message.
		inTrayManager.generateNotification(ti.getOwner().getOid(), MessageType.CATEGORY_MISC,
				MessageType.NOTIFICATION_ARCHIVE_SUCCESS, ti);

	}

	public void failedArchiving(Long targetInstanceOid, String message) {
		// Update the state.
		TargetInstance ti = targetInstanceDao.load(targetInstanceOid);
		ti.setState(TargetInstance.STATE_ENDORSED);
		targetInstanceManager.save(ti);

		log.error("Failed to archive - trying to send message");

		// Send a message.
		inTrayManager.generateNotification(ti.getOwner().getOid(), MessageType.CATEGORY_MISC, "subject.archived.failed",
				new Object[] { ti.getTarget().getName(), ti.getResourceName() }, "message.archived.failed", new Object[] {
						ti.getTarget().getName(), ti.getResourceName(), message }, ti, true);

	}

	/**
	 * @param targetInstanceManager
	 *            the targetInstanceManager to set
	 */
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}

	@Override
	public void pauseAgent(String agentName) {
		harvestAgentManager.pauseAgent(agentName);
	}

	@Override
	public void resumeAgent(String agentName) {
		harvestAgentManager.resumeAgent(agentName);
	}

	@Override
	public int getHarvestOptimizationLookAheadHours() {
		return harvestOptimizationLookAheadHours;
	}

	public void setHarvestOptimizationLookAheadHours(int harvestOptimizeLookAheadHours) {
		this.harvestOptimizationLookAheadHours = harvestOptimizeLookAheadHours;
	}

	@Override
	public void setHarvestOptimizationEnabled(boolean state) {
		this.harvestOptimizationEnabled = state;
	}

	@Override
	public boolean isHarvestOptimizationEnabled() {
		return harvestOptimizationEnabled;
	}

	public int getNumHarvestersExcludedFromOptimisation() {
		return numHarvestersExcludedFromOptimisation;
	}

	public void setNumHarvestersExcludedFromOptimisation(int numHarvestersExcludedFromOptimisation) {
		this.numHarvestersExcludedFromOptimisation = numHarvestersExcludedFromOptimisation;
	}

	@Override
	public HashMap<String, HarvestAgentStatusDTO> getHarvestAgents() {
		return harvestAgentManager.getHarvestAgents();
	}

	public void setHarvestAgentManager(HarvestAgentManager harvestAgentManager) {
		this.harvestAgentManager = harvestAgentManager;
	}

	public void setHarvestLogManager(HarvestLogManager harvestLogManager) {
		this.harvestLogManager = harvestLogManager;
	}

	public void setHarvestBandwidthManager(HarvestBandwidthManager harvestBandwidthManager) {
		this.harvestBandwidthManager = harvestBandwidthManager;
	}

	public void setHarvestQaManager(HarvestQaManager harvestQaManager) {
		this.harvestQaManager = harvestQaManager;
	}

}