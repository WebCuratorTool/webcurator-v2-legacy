package org.webcurator.core.harvester.coordinator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webcurator.core.common.Environment;
import org.webcurator.core.common.EnvironmentFactory;
import org.webcurator.core.harvester.agent.HarvestAgent;
import org.webcurator.core.harvester.agent.HarvestAgentConfig;
import org.webcurator.core.harvester.agent.HarvestAgentFactory;
import org.webcurator.core.harvester.agent.HarvestAgentSOAPClient;
import org.webcurator.core.reader.LogReader;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.domain.TargetInstanceDAO;
import org.webcurator.domain.model.core.HarvesterStatus;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

public class HarvestAgentManagerImpl implements HarvestAgentManager {

	static Set<Long> targetInstanceLocks = Collections.synchronizedSet(new HashSet<Long>());

	HashMap<String, HarvestAgentStatusDTO> harvestAgents = new HashMap<String, HarvestAgentStatusDTO>();;
	private Logger log = LoggerFactory.getLogger(getClass());
	private TargetInstanceDAO targetInstanceDao;
	private TargetInstanceManager targetInstanceManager;
	private HarvestAgentFactory harvestAgentFactory;

	@Override
	public void heartbeat(HarvestAgentStatusDTO aStatus) {
		if (harvestAgents.containsKey(aStatus.getName())) {
			log.debug("Updating status for {}", aStatus.getName());
		} else {
			log.info("Registering harvest agent " + aStatus.getName());
		}

		aStatus.setLastUpdated(new Date());
		HarvestAgentStatusDTO currentStatus = harvestAgents.get(aStatus.getName());
		if (currentStatus != null) {
			aStatus.setAcceptTasks(currentStatus.isAcceptTasks());
		}
		harvestAgents.put(aStatus.getName(), aStatus);

		HashMap<String, HarvesterStatusDTO> harvesterStatusMap = aStatus.getHarvesterStatus();
		for (String key : harvesterStatusMap.keySet()) {
			long tiOid = Long.parseLong(key.substring(key.lastIndexOf("-") + 1));

			// lock the ti for update
			if (!lock(tiOid))
				break;
			log.debug("Obtained lock for ti " + tiOid);

			TargetInstance ti = targetInstanceDao.load(tiOid);
			HarvesterStatusDTO harvesterStatusDto = (HarvesterStatusDTO) harvesterStatusMap.get(key);

			updateStatusWithEnvironment(harvesterStatusDto);
			HarvesterStatus harvesterStatus = createHarvesterStatus(ti, harvesterStatusDto);

			String harvesterStatusValue = harvesterStatus.getStatus();
			if (harvesterStatusValue.startsWith("Paused")) {
				doHeartbeatPaused(ti);
			}

			// We have seen cases where a running Harvest is showing as Queued
			// in the UI. Once in this state, the user has no control over the
			// harvest and cannot use it. This work around means that any
			// TIs in the wrong state will be corrected on the next heartbeat
			if (harvesterStatusValue.startsWith("Running")) {
				doHeartbeatRunning(aStatus, ti, harvesterStatus);
			}

			if (harvesterStatusValue.startsWith("Finished")) {
				doHeartbeatFinished(ti);
			}

			// This is a required because when a
			// "Could not launch job - Fatal InitializationException" job occurs
			// We do not get a notification that causes the job to stop nicely
			if (harvesterStatusValue.startsWith("Could not launch job - Fatal InitializationException")) {
				doHeartbeatLaunchFailed(ti);
			}

			targetInstanceManager.save(ti);
			unLock(tiOid);
			log.debug("Released lock for ti " + tiOid);
		}
	}

	private HarvesterStatus createHarvesterStatus(TargetInstance ti, HarvesterStatusDTO harvesterStatusDto) {
		HarvesterStatus harvesterStatus = null;
		if (ti.getStatus() == null) {
			harvesterStatus = new HarvesterStatus(harvesterStatusDto);
			ti.setStatus(harvesterStatus);
			harvesterStatus.setOid(ti.getOid());
		} else {
			harvesterStatus = ti.getStatus();
			harvesterStatus.update(harvesterStatusDto);
		}
		return harvesterStatus;
	}

	private void updateStatusWithEnvironment(HarvesterStatusDTO harvesterStatusDto) {
		// Update the harvesterStatus with current versions
		Environment env = EnvironmentFactory.getEnv();
		harvesterStatusDto.setApplicationVersion(env.getApplicationVersion());
		if(harvesterStatusDto.getHeritrixVersion() == null){
			harvesterStatusDto.setHeritrixVersion(env.getHeritrixVersion());
		}
	}

	private void doHeartbeatLaunchFailed(TargetInstance ti) {
		String state = ti.getState();
		if (state.equals(TargetInstance.STATE_RUNNING)) {
			ti.setState(TargetInstance.STATE_ABORTED);
			HarvestAgentStatusDTO hs = getHarvestAgentStatusFor(ti.getJobName());
			if (hs == null) {
				log.warn("Forced Abort Failed. Failed to find the Harvest Agent for the Job {}.", ti.getJobName());
			} else {
				HarvestAgent agent = harvestAgentFactory.getHarvestAgent(hs.getHost(), hs.getPort(), hs.getService());
				agent.abort(ti.getJobName());
			}
		}
	}

	private void doHeartbeatFinished(TargetInstance ti) {
		String state = ti.getState();
		if (state.equals(TargetInstance.STATE_RUNNING)) {
			ti.setState(TargetInstance.STATE_STOPPING);
		}
	}

	private void doHeartbeatRunning(HarvestAgentStatusDTO aStatus, TargetInstance ti, HarvesterStatus harvesterStatus) {
		String state = ti.getState();
		if (state.equals(TargetInstance.STATE_PAUSED) || state.equals(TargetInstance.STATE_QUEUED)) {

			if (state.equals(TargetInstance.STATE_QUEUED)) {
				log.info("HarvestCoordinator: Target Instance state changed from Queued to Running for target instance {}", ti
						.getOid().toString());
			}
			if (ti.getActualStartTime() == null) {
				// This was not set up correctly when harvest was initiated
				Date now = new Date();
				Date startTime = new Date(now.getTime() - harvesterStatus.getElapsedTime());
				ti.setActualStartTime(startTime);
				ti.setHarvestServer(aStatus.getName());

				log.info("HarvestCoordinator: Target Instance start time set for target instance " + ti.getOid().toString());
			}
			ti.setState(TargetInstance.STATE_RUNNING);
		}
	}

	private void doHeartbeatPaused(TargetInstance ti) {
		String state = ti.getState();
		if (state.equals(TargetInstance.STATE_RUNNING)) {
			ti.setState(TargetInstance.STATE_PAUSED);
		}
	}

	/**
	 * @return a harvest agent status for the specified job name
	 */
	HarvestAgentStatusDTO getHarvestAgentStatusFor(String aJobName) {
		for (HarvestAgentStatusDTO agentStatus : harvestAgents.values()) {
			if (agentStatus.getHarvesterStatus() != null) {
				if (agentHasJob(aJobName, agentStatus))
					return agentStatus;
			}
		}
		return null;
	}

	boolean agentHasJob(String aJobName, HarvestAgentStatusDTO agentStatus) {
		for (Object hsObject : agentStatus.getHarvesterStatus().values()) {
			HarvesterStatusDTO harvesterStatus = (HarvesterStatusDTO) hsObject;
			if (harvesterStatus.getJobName().equals(aJobName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateProfileOverrides(TargetInstance aTargetInstance, String profileString) {
		HarvestAgentStatusDTO status = getHarvestAgentStatusFor(aTargetInstance.getJobName());
		if (status == null) {
			log.warn("Update Profile Overrides Failed. Failed to find the Harvest Agent for the Job {}.",
					aTargetInstance.getJobName());
			return;
		}
		HarvestAgent agent = harvestAgentFactory.getHarvestAgent(status.getHost(), status.getPort(), status.getService());
		agent.updateProfileOverrides(aTargetInstance.getJobName(), profileString);
	}

	@Override
	public void pause(TargetInstance aTargetInstance) {
		String jobName = aTargetInstance.getJobName();
		HarvestAgentStatusDTO status = getHarvestAgentStatusFor(jobName);
		if (status == null) {
			log.warn("PAUSE Failed. Failed to find the Harvest Agent for the Job {}.", jobName);
			return;
		}

		HarvestAgent agent = harvestAgentFactory.getHarvestAgent(status.getHost(), status.getPort(), status.getService());

		// Update the state of the allocated Target Instance
		aTargetInstance.setState(TargetInstance.STATE_PAUSED);
		// Note that resume uses a different method to save the target instance!
		targetInstanceDao.save(aTargetInstance);

		agent.pause(jobName);
	}

	@Override
	public void resume(TargetInstance aTargetInstance) {
		HarvestAgentStatusDTO status = getHarvestAgentStatusFor(aTargetInstance.getJobName());
		if (status == null) {
			log.warn("RESUME Failed. Failed to find the Harvest Agent for the Job {}.", aTargetInstance.getJobName());
			return;
		}
		HarvestAgent agent = harvestAgentFactory.getHarvestAgent(status.getHost(), status.getPort(), status.getService());

		// Update the state of the allocated Target Instance
		aTargetInstance.setState(TargetInstance.STATE_RUNNING);
		// Note that pause uses a different method to save the target instance!
		targetInstanceManager.save(aTargetInstance);

		agent.resume(aTargetInstance.getJobName());
	}

	@Override
	public void abort(TargetInstance aTargetInstance) {
		// Update the state of the allocated Target Instance
		aTargetInstance.setState(TargetInstance.STATE_ABORTED);
		targetInstanceDao.save(aTargetInstance);

		HarvestAgentStatusDTO status = getHarvestAgentStatusFor(aTargetInstance.getJobName());
		if (status == null) {
			log.warn("ABORT Failed. Failed to find the Harvest Agent for the Job {}.", aTargetInstance.getJobName());
		} else {
			HarvestAgent agent = harvestAgentFactory.getHarvestAgent(status.getHost(), status.getPort(), status.getService());
			try {
				agent.abort(aTargetInstance.getJobName());
			} catch (RuntimeException e) {
				log.warn("ABORT Failed. Failed Abort the Job {} on the Harvest Agent {}.", aTargetInstance.getJobName(),
						agent.getName());
			}
		}
	}

	@Override
	public void stop(TargetInstance aTargetInstance) {
		HarvestAgentStatusDTO status = getHarvestAgentStatusFor(aTargetInstance.getJobName());
		if (status == null) {
			log.warn("STOP Failed. Failed to find the Harvest Agent for the Job {}.", aTargetInstance.getJobName());
			return;
		}
		HarvestAgent agent = harvestAgentFactory.getHarvestAgent(status.getHost(), status.getPort(), status.getService());

		agent.stop(aTargetInstance.getJobName());
	}

	@Override
	public void pauseAll() {
		for (HarvestAgentStatusDTO agentDTO : harvestAgents.values()) {
			if (agentDTO.getHarvesterStatus() != null && !agentDTO.getHarvesterStatus().isEmpty()) {
				HarvestAgent agent = harvestAgentFactory.getHarvestAgent(agentDTO.getHost(), agentDTO.getPort(), agentDTO.getService());
				agent.pauseAll();
			}
		}
	}

	@Override
	public void resumeAll() {
		for (HarvestAgentStatusDTO agentDTO : harvestAgents.values()) {
			if (agentDTO.getHarvesterStatus() != null && !agentDTO.getHarvesterStatus().isEmpty()) {
				HarvestAgent agent = harvestAgentFactory.getHarvestAgent(agentDTO.getHost(), agentDTO.getPort(), agentDTO.getService());
				agent.resumeAll();
			}
		}
	}

	@Override
	public LogReader getLogReader(TargetInstance aTargetInstance) {
		// If we are harvesting then get the log files from the harvester
		HarvestAgentStatusDTO status = getHarvestAgentStatusFor(aTargetInstance.getJobName());
		if (status == null) {
			log.warn("list Log Files Failed. Failed to find the Log Reader for the Job {}.", aTargetInstance.getJobName());
			return null;
		}

		LogReader logReader = harvestAgentFactory.getLogReader(status.getHost(), status.getPort(), status.getLogReaderService());
		return logReader;
	}

	@Override
	public void pauseAgent(String agentName) {
		HarvestAgentStatusDTO agent = harvestAgents.get(agentName);
		if (agent != null) {
			agent.setAcceptTasks(false);
		}
	}

	@Override
	public void resumeAgent(String agentName) {
		HarvestAgentStatusDTO agent = harvestAgents.get(agentName);
		if (agent != null) {
			agent.setAcceptTasks(true);
		}
	}

	@Override
	public boolean runningOrPaused(TargetInstance aTargetInstance) {
		String state = aTargetInstance.getState();
		return state.equals(TargetInstance.STATE_RUNNING) || aTargetInstance.getState().equals(TargetInstance.STATE_PAUSED);
	}

	@Override
	public void restrictBandwidthFor(TargetInstance targetInstance) {
		HarvestAgentStatusDTO ha = getHarvestAgentStatusFor(targetInstance.getJobName());
		if (ha != null) {
			HarvestAgent agent = harvestAgentFactory.getHarvestAgent(ha.getHost(), ha.getPort(), ha.getService());
			Long allocated = targetInstance.getAllocatedBandwidth();
			if (allocated == null || targetInstance.getAllocatedBandwidth().intValue() <= 0) {
				// zero signifies unlimited bandwidth, prevent this
				targetInstance.setAllocatedBandwidth(new Long(1));
			}
			agent.restrictBandwidth(targetInstance.getJobName(), targetInstance.getAllocatedBandwidth().intValue());
			targetInstanceDao.save(targetInstance);
		}
	}

	@Override
	public List<HarvestAgentStatusDTO> getHarvestersForAgency(String agencyName) {
		List<HarvestAgentStatusDTO> result = new ArrayList<HarvestAgentStatusDTO>();
		for (HarvestAgentStatusDTO agent : harvestAgents.values()) {
			ArrayList<String> allowedAgencies = agent.getAllowedAgencies();
			if (allowedAgencies == null || allowedAgencies.isEmpty() || allowedAgencies.contains(agencyName)) {
				result.add(agent);
			}
		}
		return result;
	}

	@Override
	public void markDead(HarvestAgentStatusDTO agent) {
		harvestAgents.remove(agent.getName());
	}

	@Override
	public void initiateHarvest(HarvestAgentStatusDTO aHarvestAgent, TargetInstance aTargetInstance, String profile,
			String seedsString) {
		HarvestAgent agent = harvestAgentFactory.getHarvestAgent(aHarvestAgent.getHost(), aHarvestAgent.getPort(), aHarvestAgent.getService());
		agent.initiateHarvest(aTargetInstance.getJobName(), profile, seedsString);
	}

	@Override
	public void recoverHarvests(String haHost, int haPort, String haService, List<String> activeJobs){
		HarvestAgent agent  = harvestAgentFactory.getHarvestAgent(haHost, haPort, haService);
		agent.recoverHarvests(activeJobs);
	}

	@Override
	public boolean lock(Long tiOid) {
		return targetInstanceLocks.add(tiOid);
	}

	@Override
	public void unLock(Long tiOid) {
		targetInstanceLocks.remove(tiOid);
	}

	@Override
	public HashMap<String, HarvestAgentStatusDTO> getHarvestAgents() {
		return new HashMap<String, HarvestAgentStatusDTO>(harvestAgents);
	}

	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}

	public void setHarvestAgentFactory(HarvestAgentFactory harvestAgentFactory) {
		this.harvestAgentFactory = harvestAgentFactory;
	}

	public void setTargetInstanceDao(TargetInstanceDAO targetInstanceDao) {
		this.targetInstanceDao = targetInstanceDao;
	}

	@Override
	public void purgeAbortedTargetInstances(String[] tiNames) {
		for(HarvestAgentStatusDTO statusDto:harvestAgents.values()) {
			HarvestAgent ha = harvestAgentFactory.getHarvestAgent(statusDto.getHost(), statusDto.getPort(), statusDto.getService());
			try {
				ha.purgeAbortedTargetInstances(tiNames);
			} catch (Exception e) {
				log.error(MessageFormat.format("Failed to complete the purge of aborted ti data via HA: {0}", e.getMessage()), e);
			}
		}
	}

	@Override
	public List<HarvestAgentStatusDTO> getAvailableHarvesters(String agencyName) {
		List<HarvestAgentStatusDTO> result = new ArrayList<HarvestAgentStatusDTO>();
		List<HarvestAgentStatusDTO> harvestersForAgency = getHarvestersForAgency(agencyName);
		for (HarvestAgentStatusDTO agent : harvestersForAgency) {
			if (harvesterCanHarvestNow(agent)) {
				result.add(agent);
			}
		}
		Collections.sort(result, new Comparator<HarvestAgentStatusDTO>() {
			@Override
			public int compare(HarvestAgentStatusDTO o1, HarvestAgentStatusDTO o2) {
				// Result is negated to get a descending sort order
				return -(o1.getHarvesterStatusCount() - o2.getHarvesterStatusCount());
			}
		});
		return result;
	}

	/**
	 * Return the next harvest agent to allocate a target instance to.
	 * 
	 * @param agencyName
	 *            the agency to get harvesters for
	 * @return the harvest agent
	 */
	@Override
	public HarvestAgentStatusDTO getHarvester(String agencyName) {
		HarvestAgentStatusDTO selectedAgent = null;

		List<HarvestAgentStatusDTO> harvestersForAgency = getHarvestersForAgency(agencyName);
		for (HarvestAgentStatusDTO agent : harvestersForAgency) {
			if (selectedAgent == null || agent.getHarvesterStatusCount() < selectedAgent.getHarvesterStatusCount()) {
				if (harvesterCanHarvestNow(agent)) {
					selectedAgent = agent;
				}
			}
		}
		return selectedAgent;
	}

	private boolean harvesterCanHarvestNow(HarvestAgentStatusDTO agent) {
		return !agent.getMemoryWarning() && !agent.isInTransition() && agent.getHarvesterStatusCount() < agent.getMaxHarvests();
	}

}
