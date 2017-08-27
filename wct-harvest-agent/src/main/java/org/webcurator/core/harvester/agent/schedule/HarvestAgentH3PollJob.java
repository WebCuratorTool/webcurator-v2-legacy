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
package org.webcurator.core.harvester.agent.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netarchivesuite.heritrix3wrapper.Heritrix3Wrapper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.webcurator.core.harvester.agent.HarvestAgent;
import org.webcurator.core.harvester.agent.Harvester;
import org.webcurator.core.harvester.agent.exception.HarvestAgentException;
import org.webcurator.core.harvester.coordinator.HarvestCoordinatorNotifier;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

import java.util.Iterator;
import java.util.Map;

/**
 * The HarvestAgentHeartBeatJob is a scheduled job that triggers the sending of 
 * the Harvest Agents status information to the Web Curator Tools Core. 
 * @author nwaight
 */
public class HarvestAgentH3PollJob extends QuartzJobBean {
    /** The harvest agent to use to get status information. */
    HarvestAgent harvestAgent;
    /** The notifier to use to send data to the WCT. */
    HarvestCoordinatorNotifier notifier;

    /** the logger. */
    private Log log = LogFactory.getLog(getClass());

    /** Default Constructor. */
    public HarvestAgentH3PollJob() {
        super();
    }

    @Override
    protected void executeInternal(JobExecutionContext aJobContext) throws JobExecutionException {
    	int triggerState = -2;
    	try {
            log.info("HarvestAgentH3PollJob executing");
			triggerState = aJobContext.getScheduler().getTriggerState(null, "H3PollTriggerGroup");
			aJobContext.getScheduler().pauseTriggerGroup("H3PollTriggerGroup");
			
			HarvestAgentStatusDTO status = harvestAgent.getStatus();

            Map<String, HarvesterStatusDTO> harvestStatus = status.getHarvesterStatus();

            for(Map.Entry<String, HarvesterStatusDTO> entry : harvestStatus.entrySet()) {
                HarvesterStatusDTO job = entry.getValue();
                String jobStatus = job.getStatus();

                if(jobStatus != null) {

                    // When the agent moves into the running state grab the settings we need at
                    // Job completion as some of these may no longer be available when the job is finished.
                    if (jobStatus.equals(Heritrix3Wrapper.CrawlControllerState.RUNNING.toString())) {
                        //TODO - unsure if needed
//                    agent.loadSettings((String) handback);
                        log.info("HarvestAgentH3PollJob - job RUNNING: " + job.getJobName() + ". status: " + jobStatus);
                    }

                    // Schedule the job completion process to be run.
                    else if (jobStatus.equals("Finished") || jobStatus.equals("Could not launch job - Fatal InitializationException")) {
                        if(log.isDebugEnabled()){
                            log.debug("HarvestAgentH3PollJob - job FINISHED: " + job.getJobName() + ". status: " + jobStatus);
                        }
                        SchedulerUtil.scheduleHarvestCompleteJob(job.getJobName());
                        log.info("Scheduling Harvest Complete Job for: " + job.getJobName());
                    } else {
                        log.info("HarvestAgentH3PollJob - job: " + job.getJobName() + ". status: " + jobStatus);
                    }
                }

            }

			aJobContext.getScheduler().resumeTriggerGroup("H3PollTriggerGroup");

		} catch (ObjectAlreadyExistsException ex){
            log.error("Failed to start harvest complete job: " + ex.getMessage());
            // Resume trigger group, other thread will suspend forever
            try {
                aJobContext.getScheduler().resumeTriggerGroup("H3PollTriggerGroup");
            } catch (SchedulerException e) {
                e.printStackTrace();
                throw new JobExecutionException("Failed to resume Trigger Group - H3PollTriggerGroup: " + e.getMessage());
            }
        }
        catch (SchedulerException e) {
    		e.printStackTrace();
    		if (e.getCause() != null)
    			e.getCause().printStackTrace();
			throw new JobExecutionException("H3Poll failed controlling the scheduler. (triggerState is: " + triggerState + ")");
		}
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * @param harvestAgent The harvestAgent to set.
     */
    public void setHarvestAgent(HarvestAgent harvestAgent) {
        this.harvestAgent = harvestAgent;
    }

    /**
     * @param notifier The notifier to set.
     */
    public void setNotifier(HarvestCoordinatorNotifier notifier) {
        this.notifier = notifier;
    }
}
