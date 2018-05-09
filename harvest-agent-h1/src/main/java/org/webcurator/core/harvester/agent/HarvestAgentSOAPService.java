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
package org.webcurator.core.harvester.agent;

import org.springframework.remoting.jaxrpc.ServletEndpointSupport;
import org.webcurator.core.common.Constants;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;

import java.util.List;

/**
 * The <code>HarvestAgentSOAPService</code> is the class that is accessed by 
 * the remote Web Service client.  This class uses spring to lookup the 
 * class to handle the <code>HarvestAgent</code> calls from the client. 
 * 
 * @author nwaight
 */
public class HarvestAgentSOAPService extends ServletEndpointSupport implements HarvestAgent {

    /** The instance of the HarvestAgent to use. */
    private HarvestAgent ha = null;
                
    /** Initialisation for the SOAP Service. */
    protected void onInit() {
        ha = (HarvestAgent) getWebApplicationContext().getBean(Constants.BEAN_HARVEST_AGENT);
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#getName()
     */
    public String getName() {
		return ha.getName();
	}
    
    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#initiateHarvest(java.lang.String, java.lang.String, java.lang.String)
     */
    public void initiateHarvest(String aJob, String aProfile, String aSeeds) {
        ha.initiateHarvest(aJob, aProfile, aSeeds);
    }

    @Override
    public void recoverHarvests(List<String> activeJobs) {
        // Placeholder - not used with Heritrix 1x
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#restrictBandwidth(java.lang.String, int)
     */
    public void restrictBandwidth(String aJob, int aBandwidthLimit) {
        ha.restrictBandwidth(aJob, aBandwidthLimit);
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#pause(java.lang.String)
     */
    public void pause(String aJob) {
        ha.pause(aJob);
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#resume(java.lang.String)
     */
    public void resume(String aJob) {
        ha.resume(aJob);
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#abort(java.lang.String)
     */
    public void abort(String aJob) {
        ha.abort(aJob);
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#stop(java.lang.String)
     */
    public void stop(String aJob) {
        ha.stop(aJob);
    }
    
    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#completeHarvest(String, int). 
     */
    public int completeHarvest(String aJob, int aFailureStep) {
        ha.stop(aJob);  
        return NO_FAILURES;
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#pauseAll()
     */
    public void pauseAll() {
        ha.pauseAll();
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#resumeAll()
     */
    public void resumeAll() {
        ha.resumeAll();
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#getStatus()
     */
    public HarvestAgentStatusDTO getStatus() {        
        return ha.getStatus();
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#loadSettings(String)
     */
    public void loadSettings(String aJob) {
        ha.loadSettings(aJob);        
    }

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#updateProfileOverrides(String, String)
     */
	public void updateProfileOverrides(String aJob, String aProfile) {
		ha.updateProfileOverrides(aJob, aProfile);		
	}

    /**
     * @see org.webcurator.core.harvester.agent.HarvestAgent#purgeAbortedTargetInstances(String[])
     */
	public void purgeAbortedTargetInstances(String [] targetInstanceNames) {
		ha.purgeAbortedTargetInstances(targetInstanceNames);		
	}
	
 	/**
	 * @return the memoryWarning flag
	 */
	public boolean getMemoryWarning() {
		return ha.getMemoryWarning();
	}

	/**
	 * @param memoryWarning the memoryWarning flag to set
	 */
	public void setMemoryWarning(boolean memoryWarning) {
		ha.setMemoryWarning(memoryWarning);
	}


	public boolean isValidProfile(String profile) {
	    return ha.isValidProfile(profile);
    }
	
}
