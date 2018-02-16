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
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.webcurator.core.common.Constants;
import org.webcurator.core.harvester.agent.HarvestAgent;
import org.webcurator.core.harvester.agent.exception.HarvestAgentException;
import org.webcurator.core.harvester.coordinator.HarvestCoordinatorNotifier;
import org.webcurator.core.notification.MessageType;
import org.webcurator.core.util.ApplicationContextFactory;

/**
 * Quartz job to process the completion of a Harvest Job.
 * @author nwaight
 */
public class HarvestCompleteJob implements Job {
    /** The name of the job name data parameter. */
    public static final String PARAM_JOB_NAME = "JobName";
    /** The name of the failure step data parameter. */
    public static final String PARAM_FAILURE_STEP = "FailureStep";
    /** The name of the message sent data parameter. */
    public static final String PARAM_MSG_SENT = "MessageSent";
    /** The name of the retries data parameter. */
    public static final String PARAM_RETRIES = "Retries";

    /** the logger. */
    private Log log;
    
    /** Default Constructor. */
    public HarvestCompleteJob() {
        super();
        log = LogFactory.getLog(getClass());
    }

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext aJobContext) throws JobExecutionException {        
        if (aJobContext.getJobDetail().getJobDataMap() != null) {
            JobDataMap jdm = aJobContext.getJobDetail().getJobDataMap();
            String jobName = (String) jdm.get(PARAM_JOB_NAME);
            Integer failureStep = (Integer) jdm.get(PARAM_FAILURE_STEP);
            Boolean msgSent = (Boolean) jdm.get(PARAM_MSG_SENT);
            Integer retries = (Integer) jdm.get(PARAM_RETRIES);
                                    
            if (log.isInfoEnabled()) {
                log.info("Processing job completion for " + jobName);
            }
            
            ApplicationContext context = ApplicationContextFactory.getWebApplicationContext();
            HarvestAgent ha = (HarvestAgent) context.getBean(Constants.BEAN_HARVEST_AGENT);                               
            
            int failedOn = ha.completeHarvest(jobName, failureStep);
            if (failedOn != HarvestAgent.NO_FAILURES) {
            	if (!msgSent.booleanValue()) {
            		// Send the failure notification.
            		HarvestCoordinatorNotifier harvestCoordinatorNotifier = (HarvestCoordinatorNotifier) context.getBean(Constants.BEAN_NOTIFIER);
            		harvestCoordinatorNotifier.notification(new Long(jobName), MessageType.CATEGORY_MISC, MessageType.TARGET_INSTANCE_PROCESSING_ERROR);
            		msgSent = new Boolean(true);
            	}            	            
            	
            	try {
            		SchedulerUtil.scheduleHarvestCompleteJob(jobName, failedOn, msgSent, retries.intValue()+ 1);
                }
                catch (Exception e) {
                    throw new HarvestAgentException("Failed to start harvest complete job : " + e.getMessage(), e);
                }
            }            
        }
    }
}
