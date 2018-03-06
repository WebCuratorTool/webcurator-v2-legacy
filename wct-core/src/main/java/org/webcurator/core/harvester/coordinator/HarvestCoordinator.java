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
import java.util.HashMap;
import java.util.List;

import org.webcurator.core.check.CheckNotifier;
import org.webcurator.domain.HarvestCoordinatorDAO;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.domain.model.core.LogFilePropertiesDTO;
import org.webcurator.domain.model.dto.QueuedTargetInstanceDTO;
import org.webcurator.domain.model.core.HarvestResult;

/**
 * The HarvestCoordinator is responsible for managing the scheduling, monitoring and completion
 * of harvests. 
 * The HarvestCoordinator allocates jobs and sets bandwidth for the harvest agents and listens 
 * for heartbeats, harvest completion data and notifications.
 * @author nwaight
 */
public interface HarvestCoordinator extends HarvestAgentListener, HarvestCoordinatorDAO, CheckNotifier, IndexerService {
	/** 
	 * Process any TargetInstances that are ready to be processed.
	 */
	void processSchedule();
	
	/** 
	 * Purge any Target instances digital assets that have been in the system 
	 * longer than the specified period.
	 */
	void purgeDigitalAssets();
	
	/** 
	 * Purge any aborted Target instances harvest data that have been in the system 
	 * longer than the specified period.
	 */
	void purgeAbortedTargetInstances();
	
	/**
	 * Run the checks to see if the target instance can be harvested or if it must be queued.
	 * If harvest is possible and there is a harvester available then allocate it.
	 * @param aTargetInstance the target instance to harvest
	 */
	void harvestOrQueue(QueuedTargetInstanceDTO aTargetInstance);
	
	/** 
	 * Check for a change in the bandwidth. If a change is detected then 
	 * send the new bandwidth settings to the running harvests
	 */
	void checkForBandwidthTransition();	
	
	/**
	 * Return the current maximum bandwidth setting. 
	 * @return the current maximum bandwidth
	 */
	long getCurrentGlobalMaxBandwidth();
	
    /**
     * Return a list of harvest agents with their last reported status. 
     * @return a list of harvest agent status's
     */
    HashMap<String, HarvestAgentStatusDTO> getHarvestAgents();

    /**
     * Send recover harvest information to Harvest Agent
     * @param host
     * @param port
     * @param service
     */
    void recoverHarvests(String host, int port, String service);

    /**
     * Allocate the target instance to the specified harvest agent.
     * @param aTargetInstance the target instance to harvest.
     * @param aHarvestAgent the harvest agent.
     */
    void harvest(TargetInstance aTargetInstance, HarvestAgentStatusDTO aHarvestAgent);
        
    /**
     * Pause a TargetInstance that is in the process or being harvested
     * @param aTargetInstance the TargetInstance being harvested
     */
    void pause(TargetInstance aTargetInstance);
    
    /** 
     * Resume the harvesting of a paused TargetInstance
     * @param aTargetInstance a paused TargetInstance
     */
    void resume(TargetInstance aTargetInstance);
    
    /**
     * Stop a inprogress harvest and discard all result data.
     * @param aTargetInstance the TargetInstance to abort
     */
    void abort(TargetInstance aTargetInstance);
    
    /**
     * Stop an inprogress harvest but retain any data that has been collected.
     * @param aTargetInstance the TargetInstance to stop
     */
    void stop(TargetInstance aTargetInstance);    
    
    /**
     * Pause all TargetInstances that are in the process or being harvested.
     */
    void pauseAll();
    
    /** 
     * Resume the harvesting of all paused TargetInstances.
     */
    void resumeAll();

    /**
     * Stop harvesting Scheduled or Queued TargetInstances
     * Scheduled items will still be added to the Queue
     */
    void pauseQueue();
    
    /** 
     * Resume the harvesting of Scheduled or Queued TargetInstances
     */
    void resumeQueue();
    
    /** 
     * @return Returns true if the Queue is paused.
     */
    public boolean isQueuePaused();
    
    /**
     * @return Returns the maxBandwidthPercent.
     */
    int getMaxBandwidthPercent();
    
    /**
     * Check to see if adding this target instance to be harvested will mean that 
     * the the minimum bandwidth will not be availabile to this or other running jobs
     * @param aTargetInstance the target instance to check
     * @return true if the minimum bandwidth will be available
     */
    boolean isMiniumBandwidthAvailable(TargetInstance aTargetInstance);
    
    /**
     * Send the latest profile to the Harvest Agent for a specified target instance
     * only if the TargetInstance is in the Paused state.
     * @param aTargetInstance the target instance to update the profile overrides for
     */
    void updateProfileOverrides(TargetInstance aTargetInstance);
        
    /**
     * Return a list of all the log and status files names available for the 
     * specified TargetInstance.
     * @param aTargetInstance the TargetInstance to return the file list for 
     * @return the list of log file names.
     */
    List<String> listLogFiles(TargetInstance aTargetInstance);
    
    /**
     * Return an array of all the log and status file objects available for the 
     * specified TargetInstance.
     * @param aTargetInstance the TargetInstance to return the file list for 
     * @return the array of log file objects.
     */
    LogFilePropertiesDTO[] listLogFileAttributes(TargetInstance aTargetInstance);

    /**
     * Return a count of lines from the specified file for the specified TargetInstance.
     * @param aTargetInstance the TargetInstance to return the log lines for
     * @param aFileName the name of the file to return the lines from 
      * @return the count of lines for the log file
     */
	Integer countLogLines(TargetInstance aTargetInstance, String aFileName);
    
    /**
     * Return a the first x lines from the specified file for the specified TargetInstance.
     * @param aTargetInstance the TargetInstance to return the log lines for
     * @param aFileName the name of the file to return the lines from 
     * @param aNoOfLines the number of lines to return
     * @return the lines for the log file
     */
    String[] headLog(TargetInstance aTargetInstance, String aFileName, int aNoOfLines);

    /**
     * Return a the first x lines from the specified file for the specified TargetInstance.
     * @param aTargetInstance the TargetInstance to return the log lines for
     * @param aFileName the name of the file to return the lines from 
     * @param aStartLine the line to start from
     * @param aNoOfLines the number of lines to return
     * @return the lines for the log file
     */
    String[] getLog(TargetInstance aTargetInstance, String aFileName, int aStartLine, int aNoOfLines);

    /**
     * Return a the last x lines from the specified file for the specified TargetInstance.
     * @param aTargetInstance the TargetInstance to return the log lines for
     * @param aFileName the name of the file to return the lines from 
     * @param aNoOfLines the number of lines to return
     * @return the lines for the log file
     */
    String[] tailLog(TargetInstance aTargetInstance, String aFileName, int aNoOfLines);
    
    /**
     * Return the line number from the specified log file starting with the specified match
     * param aTargetInstance the TargetInstance to return the log line number for
     * @param aFileName the name of the file to return the line number from 
     * @param match the regex to use
     * @return the line number in the log file
     */
    Integer getFirstLogLineBeginning(TargetInstance aTargetInstance, String aFileName, String match);
    
    /**
     * Return the line number from the specified log file containing the specified match
     * param aTargetInstance the TargetInstance to return the log line number for
     * @param aFileName the name of the file to return the line number from 
     * @param match the regex to use
     * @return the line number in the log file
     */
    Integer getFirstLogLineContaining(TargetInstance aTargetInstance, String aFileName, String match);
    
    /**
     * Return the line number from the specified log file starting from the specified match
     * param aTargetInstance the TargetInstance to return the log line number for
     * @param aFileName the name of the file to return the line number from 
     * @param timestamp the 14 digit timestamp to use
     * @return the line number in the log file
     */
    Integer getFirstLogLineAfterTimeStamp(TargetInstance aTargetInstance, String aFileName, Long timestamp);
    
    /**
     * Return x lines from the specified log file where those lines match the specified regex
     * param aTargetInstance the TargetInstance to return the log lines for
     * @param aFileName the name of the file to return the lines from 
     * @param aNoOfLines the number of lines to return 
     * @param aRegex the regular expression to use to filter the lines
     * @return the lines for the log file
     */
    String[] getLogLinesByRegex(TargetInstance aTargetInstance, String aFileName, int aNoOfLines, String aRegex, boolean prependLineNumbers);
    
    /**
     * Retrieves a log file from the server.
     * @param aTargetInstance The instance to retrieve the log file for.
     * @param aFilename The filename of the logfile.
     * @return The log file.
     */
    File getLogfile(TargetInstance aTargetInstance, String aFilename);

    /**
     * Complete the Archiving process
     */
    void completeArchiving(Long targetInstanceOid, String archiveIID);
    
    /**
     * Failed to complete the Archiving process
     */
    void failedArchiving(Long targetInstanceOid, String message);
     
    /**
     * Force re-indexing of the specified HarvestResult
    * @param aHarvestResult The result to re-index.
    * @return true if a reIndex was initialised
     */
    Boolean reIndexHarvestResult(HarvestResult aHarvestResult);
    
    /**
     * Remove indexes for the target instance. Most indexers do no action, however 
     * a wayback indexer needs to remove arc files from the local wayback BDB index 
    * @param ti The target instance to remove the indexes for
    * @return void
     */
    void removeIndexes(TargetInstance ti);
    void removeIndexes(HarvestResult hr);

    /**
     * Return the hop path for the specified Url by parsing the sorted crawl.log
     * for the specified TargetInstance.
     * @param aTargetInstance the TargetInstance to return the hop path lines for
     * @param aFileName the name of the sorted crawl.log file to derive the hop path from 
     * @param aUrl the Url to search for the hop path with
     * @return the lines for the hop path
     */
    String[] getHopPath(TargetInstance aTargetInstance, String aFileName, String aUrl);
    
    public void runQaRecommentationService(TargetInstance ti);

	void pauseAgent(String agentName);

	void resumeAgent(String agentName);

	void setHarvestOptimizationEnabled(boolean optimizeEnabled);

	boolean isHarvestOptimizationEnabled();

	int getHarvestOptimizationLookAheadHours();
}
