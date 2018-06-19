package org.webcurator.core.harvester.coordinator;

import java.util.HashMap;
import java.util.List;

import org.webcurator.core.reader.LogReader;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;

public interface HarvestAgentManager {

	void heartbeat(HarvestAgentStatusDTO aStatus);

	void updateProfileOverrides(TargetInstance aTargetInstance, String profileString);

	void pause(TargetInstance aTargetInstance);

	void resume(TargetInstance aTargetInstance);

	void abort(TargetInstance aTargetInstance);

	void stop(TargetInstance aTargetInstance);

	void pauseAll();

	void resumeAll();
	
	public void pauseAgent(String agentName);
	public void resumeAgent(String agentName);

	LogReader getLogReader(TargetInstance aTargetInstance);

	boolean runningOrPaused(TargetInstance aTargetInstance);

	void restrictBandwidthFor(TargetInstance targetInstance);

	List<HarvestAgentStatusDTO> getHarvestersForAgency(String agencyName);

	void markDead(HarvestAgentStatusDTO agent);

	void initiateHarvest(HarvestAgentStatusDTO aHarvestAgent, TargetInstance aTargetInstance, String profile, String seedsString);

	void recoverHarvests(String host, int port, String Service, List<String> activeJobs);

	boolean lock(Long tiOid);

	void unLock(Long tiOid);

	HashMap<String, HarvestAgentStatusDTO> getHarvestAgents();

	void purgeAbortedTargetInstances(String[] tiNames);

	List<HarvestAgentStatusDTO> getAvailableHarvesters(String agencyName);

	HarvestAgentStatusDTO getHarvester(String agencyName, String harvesterType);


}
