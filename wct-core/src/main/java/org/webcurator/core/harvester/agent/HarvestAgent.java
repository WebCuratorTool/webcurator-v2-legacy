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

import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;

import java.util.List;

/** 
 * The <code>HarvestAgent</code> is the interface used by the <code>HarvestCoordinator</code> to 
 * control the <code>HarvestAgent</code> and its individual Harvesters.
 * This interface assumes that the profile sent is for a Heritrix Web Crawler. 
 * @author nwaight
 */
public interface HarvestAgent {
	/** Harvest complete processing was successful. */
	int NO_FAILURES = 0;
	/** Harvest complete processing failed to create the harvest index. */
	int FAILED_ON_CREATE_INDEX = 1;
	/** Harvest complete processing failed to send arc files to the digital asset store. */
	int FAILED_ON_SEND_ARCS = 2;
	/** Harvest complete processing failed to send log files to the digital asset store. */
	int FAILED_ON_SEND_LOGS = 3;
	/** Harvest complete processing failed to send report files to the digital asset store. */
	int FAILED_ON_SEND_RPTS = 4;
	/** Harvest complete processing failed to send the harvest result to the core. */
	int FAILED_ON_SEND_RESULT = 5;
	
	/**
	 * Return the name of the harvest agent.
	 * @return the name of the harvest agent
	 */
	String getName();
	
    /**
     * initiate a new Harvest.
     * @param aJob the unique id of the harvest job
     * @param aProfile the profile to use for the new Harvest.
     * @param aSeeds the list of seeds to harvest
     */
    void initiateHarvest(String aJob, String aProfile, String aSeeds);

    /**
     * recover Harvests on launch.
     * @param activeJobs a list of active jobs within WCT Core
     */
    void recoverHarvests(List<String> activeJobs);

    /**
     * Update the profile with any new overrides.
     * @param aJob the unique id of the harvest job
     * @param aProfile the profile to use for the new Harvest.
     */
    void updateProfileOverrides(String aJob, String aProfile);
    
    /**
	 * Purge all the data from the digital asset store for the target instances
	 * specified in the list of **aborted** target instance names.
	 * @param targetInstanceNames the target instances to purge
     */
    void purgeAbortedTargetInstances(String [] targetInstanceNames);

    /**
     * Set the bandwidth limit for the specified Harvest job.
     * @param aJob the name of the harvest job
     * @param aBandwidthLimit the bandwidth limit
     */
    void restrictBandwidth(String aJob, int aBandwidthLimit);
        
    /**
     * Pause the specified running harvest job.
     * @param aJob the name of the harvest job to pause
     */
    void pause(String aJob);
    
    /**
     * Resume the specified paused harvest job.
     * @param aJob the name of the harvest job to resume.
     */
    void resume(String aJob);
    
    /**
     * Abort the specified running harvest job.
     * @param aJob the name of the harvest job to abort
     */
    void abort(String aJob);
    
    /** 
     * Stop and complete the specified running harvest job.
     * @param aJob the name of the harvest job to stop
     */
    void stop(String aJob);
    
    /**
     * Complete the specified job by sending the harvested data to the 
     * digital asset store and WCT as approriate.
     * @param aJob the job to complete
     * @param aFailureStep the step that the last harvest complete failed on
     */
    int completeHarvest(String aJob, int aFailureStep);
    
    /** 
     * Load the settings for the job.
     * @param aJob the job to load the settings for
     */
    void loadSettings(String aJob);
    
    /** 
     * Pause all running Harvest jobs on this <code>HarvestAgent</code>.
     */
    void pauseAll();
    
    /**
     * Resume all paused Harvest jobs on this <code>HarvestAgent</code>.
     */
    void resumeAll();
    
    
	/**
	 * @return the memoryWarning flag
	 */
	boolean getMemoryWarning();

	/**
	 * @param memoryWarning the memoryWarning flag to set
	 */
	void setMemoryWarning(boolean memoryWarning);
    
    /** 
     * Return the status of the HarvestAgent.
     * @return the status of the HarvestAgent
     */
    HarvestAgentStatusDTO getStatus();


    /**
     * 
     * @param profile A profile
     * @return true iff the submitted profile is valid (according to the agent's idea of "valid" - YMMV).
     */
    boolean isValidProfile(String profile);

    /**
     * Execute the shell script in the Heritrix3 server for the job.
     * @param jobName the job
     * @param engine the script engine: beanshell, groovy, or nashorn (ECMAScript)
     * @param shellScript the script to execute
     * @return the script result
     */
    HarvestAgentScriptResult executeShellScript(String jobName, String engine, String shellScript);
}
