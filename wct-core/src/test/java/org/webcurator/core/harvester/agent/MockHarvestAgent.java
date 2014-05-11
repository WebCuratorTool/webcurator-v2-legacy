package org.webcurator.core.harvester.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;

public class MockHarvestAgent implements HarvestAgent {

	private static Log log = LogFactory.getLog(MockHarvestAgent.class);
	private boolean memoryWarning = false;
	private String _profile = "";
	
	public void abort(String job) 
	{
		log.debug("abort - "+job);
	}

	public int completeHarvest(String job, int failureStep) {
		log.debug("complete harvest - "+job);
		return 0;
	}

	public String getName() {
		return "Test Agent";
	}

	public HarvestAgentStatusDTO getStatus() {
		HarvestAgentStatusDTO sdto = new HarvestAgentStatusDTO();
		sdto.setMemoryWarning(memoryWarning);
		return sdto;
	}

	public void initiateHarvest(String job, String profile, String seeds) 
	{
		_profile = profile;
		log.debug("initiate harvest - "+job);
	}

	public void loadSettings(String job) 
	{
		log.debug("load settings - "+job);
	}

	public void pause(String job) 
	{
		log.debug("pause - "+job);
	}

	public void pauseAll() {
		log.debug("pause all");
	}

	public void purgeAbortedTargetInstances(String[] targetInstanceNames) {
		for(int i = 0; i < targetInstanceNames.length; i++)
		{
			log.debug("purge aboted ti - "+targetInstanceNames[i]);
		}
	}

	public void restrictBandwidth(String job, int bandwidthLimit) 
	{
		log.debug("restrict bandwidth of "+job+" to "+bandwidthLimit);
	}

	public void resume(String job) {
		log.debug("resume - "+job);
	}

	public void resumeAll() {
		log.debug("resume all");
	}

	public void stop(String job) {
		log.debug("stop - "+job);
	}

	public void updateProfileOverrides(String job, String profile) {
		log.debug("update profile overrides - "+job);
	}

	public boolean getMemoryWarning() {
		return memoryWarning;
	}

	public void setMemoryWarning(boolean memoryWarning) {
		this.memoryWarning = memoryWarning;
	}
	
	public String getProfileString()
	{
		return _profile;
	}

}
