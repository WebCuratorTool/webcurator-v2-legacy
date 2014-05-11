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
package org.webcurator.core.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;

/**
 * The Scheduled job for prompting the harvest coordinator to process the 
 * current jobs in the queue.
 * @author nwaight
 */
public class ScheduleJob extends QuartzJobBean {	
	/** the logger. */
	private static final Log log = LogFactory.getLog(ScheduleJob.class);
	/** The harvest Coordinator to use to schedule jobs. */	
	private HarvestCoordinator harvestCoordinator;
	
	/* (non-Javadoc)
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.quartz.JobExecutionContext)
	 */
	@Override
	protected void executeInternal(JobExecutionContext aContext) throws JobExecutionException {
		try {
			aContext.getScheduler().pauseTriggerGroup("ProcessScheduleTriggerGroup");

			harvestCoordinator.processSchedule();					
		} 
		catch (Exception e) {
			// Dont throw an exception here as this will stop the scheduling job running.
			if (log.isErrorEnabled()) {
				log.error("process schedule failed controlling the scheduler: " + e.getMessage(), e);
			}
		}
		finally {
			try {
				aContext.getScheduler().resumeTriggerGroup("ProcessScheduleTriggerGroup");
			} 
			catch (SchedulerException e) {
				if (log.isErrorEnabled()) {
					log.error("Failed to resume the trigger for processing the schedule.");
				}
			}
		}
	}

	/**  
	 * @param harvestCoordinator the harvest coordinator to use.
	 */
	public void setHarvestCoordinator(HarvestCoordinator harvestCoordinator) {
		this.harvestCoordinator = harvestCoordinator;
	}
}
