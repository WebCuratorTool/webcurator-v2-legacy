
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

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.webcurator.core.harvester.agent.HarvestAgent;
import org.webcurator.core.harvester.coordinator.HarvestCoordinatorNotifier;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;

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

            HarvestAgentStatusDTO status = harvestAgent.getStatus();
            notifier.heartbeat(status);

            aJobContext.getScheduler().resumeTriggerGroup("HeartBeatTriggerGroup");
        }
        catch (SchedulerException e) {
            e.printStackTrace();
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            throw new JobExecutionException("Heartbeat failed controlling the scheduler. (triggerState is: " + triggerState + ")");
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