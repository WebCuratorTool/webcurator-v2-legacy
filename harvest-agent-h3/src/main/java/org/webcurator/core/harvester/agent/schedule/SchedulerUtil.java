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

import java.util.Calendar;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.webcurator.core.common.Constants;
import org.webcurator.core.util.ApplicationContextFactory;

/**
 * The SchedulerUtil is a static utility class for scheduling the completion tasks to 
 * be run for a harvest.
 * @author nwaight
 */
public final class SchedulerUtil {
	/** The name of the harvest agent complete job. */
    private static final String JOB_NAME_COMPLETE = "Complete";    
    /** The name of the harvest agent job complete group. */
    private static final String JOB_GROUP_COMPLETE = "CompleteGroup";    
    /** The name of the harvest agent complete trigger. */
    private static final String TRG_NAME_COMPLETE = "CompleteTrigger";    
    /** The name of the harvest agent complete trigger group. */
    private static final String TRG_GROUP_COMPLETE = "CompleteTriggerGroup";
    /** the serperator between the object name and the job name. */
    private static final String SEPERATOR = "-";	
	
    /**
     * Schedule the harvest completion to run after a specified delay to allow the 
     * harvester to release all its resources
     * @param aHarvestName the name of the harvest job
     * @throws SchedulerException thrown if there is an error
     */
	public static final void scheduleHarvestCompleteJob(String aHarvestName) throws SchedulerException {
		scheduleHarvestCompleteJob(aHarvestName, 0, false, 0);
	}
	
	/**
	 * Schedule the harvest completion to run after a specified delay to allow the 
     * harvester to release all its resources or after a failure to contace the 
     * core or the digital asset store
	 * @param aHarvestName the name of the harvest job
	 * @param aFailueStep the step that the completion failed at
	 * @param aMessageSent a flag to indicated that the failure notification has been sent
	 * @param aRetries the number of retries attempted
	 * @throws SchedulerException thrown if there is a problem scheduling the quartz job
	 */
	public static final void scheduleHarvestCompleteJob(String aHarvestName, int aFailueStep, boolean aMessageSent, int aRetries) throws SchedulerException {
		ApplicationContext context = ApplicationContextFactory.getWebApplicationContext();
		Scheduler scheduler = (Scheduler) context.getBean(Constants.BEAN_SCHEDULER_FACTORY);
		HarvestCompleteConfig hcc = (HarvestCompleteConfig) context.getBean(Constants.BEAN_HARVEST_COMPLETE_CONFIG);
	    
        JobDetail job = new JobDetail(JOB_NAME_COMPLETE + SEPERATOR + aHarvestName + SEPERATOR + aRetries, JOB_GROUP_COMPLETE + SEPERATOR + aHarvestName, HarvestCompleteJob.class);
        JobDataMap jdm = new JobDataMap();
        jdm.put(HarvestCompleteJob.PARAM_JOB_NAME, aHarvestName);
        jdm.put(HarvestCompleteJob.PARAM_FAILURE_STEP, new Integer(aFailueStep));
        jdm.put(HarvestCompleteJob.PARAM_MSG_SENT, new Boolean(aMessageSent));
        jdm.put(HarvestCompleteJob.PARAM_RETRIES, new Integer(aRetries));
        job.setJobDataMap(jdm);

        // Set the complete job to run xx seconds after we get the notification
        Calendar cal = Calendar.getInstance();
        if (aRetries == 0) {
        	cal.add(Calendar.SECOND, hcc.getWaitOnCompleteSeconds());
        }
        else if (aRetries < hcc.getLevelRetryBand()) {
        	cal.add(Calendar.SECOND, hcc.getWaitOnFailureLevelOneSecs());
        }
        else {
        	cal.add(Calendar.SECOND, hcc.getWaitOnFailureLevelTwoSecs());
        }
                
        Trigger trigger = new SimpleTrigger(TRG_NAME_COMPLETE + SEPERATOR + aHarvestName + SEPERATOR + aRetries, TRG_GROUP_COMPLETE + SEPERATOR + aHarvestName, cal.getTime());                       
        scheduler.scheduleJob(job, trigger);
	}	
}
