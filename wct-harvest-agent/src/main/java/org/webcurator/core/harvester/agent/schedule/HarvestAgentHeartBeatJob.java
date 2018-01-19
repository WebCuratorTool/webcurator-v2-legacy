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
import org.webcurator.core.harvester.coordinator.HarvestCoordinatorNotifier;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

import java.util.Map;

/**
 * The HarvestAgentHeartBeatJob is a scheduled job that triggers the sending of 
 * the Harvest Agents status information to the Web Curator Tools Core. 
 * @author nwaight
 */
public class HarvestAgentHeartBeatJob extends QuartzJobBean {
    /** The harvest agent to use to get status information. */
    HarvestAgent harvestAgent;
    /** The notifier to use to send data to the WCT. */
    HarvestCoordinatorNotifier notifier;

    /** the logger. */
    private Log log = LogFactory.getLog(getClass());
    
    /** Default Constructor. */
    public HarvestAgentHeartBeatJob() {
        super();
    }

    @Override
    protected void executeInternal(JobExecutionContext aJobContext) throws JobExecutionException {
    	int triggerState = -2;
    	try {
			triggerState = aJobContext.getScheduler().getTriggerState(null, "HeartBeatTriggerGroup");
			aJobContext.getScheduler().pauseTriggerGroup("HeartBeatTriggerGroup");

            log.info("HarvestAgentHeartBeatJob executing");
			HarvestAgentStatusDTO status = harvestAgent.getStatus();        
			notifier.heartbeat(status);

            /* H3 polling begin*/

            Map<String, HarvesterStatusDTO> harvestStatus = status.getHarvesterStatus();

            for(Map.Entry<String, HarvesterStatusDTO> entry : harvestStatus.entrySet()) {
                HarvesterStatusDTO job = entry.getValue();
                String jobStatus = job.getStatus();

                if(jobStatus != null) {
                    // When the agent moves into the running state grab the settings we need at
                    // Job completion as some of these may no longer be available when the job is finished.
                    if (jobStatus.equals(Heritrix3Wrapper.CrawlControllerState.RUNNING.toString())) {
                        log.info("HeartBeatJob-H3Poll - job RUNNING: " + job.getJobName() + ". status: " + jobStatus);
                    }

                    // Schedule the job completion process to be run.
                    else if (jobStatus.equals("Finished") || jobStatus.equals("Could not launch job - Fatal InitializationException")) {
                        if(log.isDebugEnabled()){
                            log.debug("HeartBeatJob:H3Poll - job FINISHED: " + job.getJobName() + ". status: " + jobStatus);
                        }
                        SchedulerUtil.scheduleHarvestCompleteJob(job.getJobName());
                        log.info("HeartBeatJob-H3Poll - Scheduling Harvest Complete Job for: " + job.getJobName());
                    } else {
                        log.info("HeartBeatJob-H3Poll - job: " + job.getJobName() + ". status: " + jobStatus);
                    }
                }
            }

            /* H3 polling end*/
			
			aJobContext.getScheduler().resumeTriggerGroup("HeartBeatTriggerGroup");
		}
        catch (ObjectAlreadyExistsException ex){
            log.error("Failed to start harvest complete job: " + ex.getMessage());
            // Resume trigger group, other thread will suspend forever
            try {
                aJobContext.getScheduler().resumeTriggerGroup("HeartBeatTriggerGroup");
            } catch (SchedulerException e) {
                e.printStackTrace();
                log.error("Failed to resume Trigger Group - HeartBeatTriggerGroup: " + e.getMessage());
                throw new JobExecutionException("Failed to resume Trigger Group - HeartBeatTriggerGroup: " + e.getMessage());
            }
        }
    	catch (SchedulerException e) {
    		e.printStackTrace();
    		if (e.getCause() != null)
    			e.getCause().printStackTrace();
            log.error("Heartbeat failed controlling the scheduler. (triggerState is: " + triggerState + ")");
			throw new JobExecutionException("Heartbeat failed controlling the scheduler. (triggerState is: " + triggerState + ")");
		}
        catch (Exception e){
            e.printStackTrace();
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            log.error("Heartbeat job failed", e);
            throw new JobExecutionException(e);
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
