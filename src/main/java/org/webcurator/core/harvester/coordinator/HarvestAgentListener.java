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

import org.webcurator.domain.model.core.HarvestResultDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;

/**
 * The <code>HarvestAgentListener</code> is a service that 
 * listens for messages from a <code>HarvestAgent</code>.
 * The messages may either be the current status of the agent
 * or a <code>HarvestResult</code> for a completed harvest.
 * @author nwaight
 */
public interface HarvestAgentListener {
    /**
     * Gets the current status of a <code>HarvestAgent</code>. 
     * @param aStatus the current agent status
     */
    void heartbeat(HarvestAgentStatusDTO aStatus);

    /**
     *
     * @param haHost
     * @param haPort
     * @param haService
     */
    void requestRecovery(String haHost, int haPort, String haService);
    
    /** 
     * Gets the result of a complete harvest.
     * @param aResult the harvest result
     */
    void harvestComplete(HarvestResultDTO aResult);
    
    /**
     * Notify the WCT that an event has occurred in a harvest that may 
     * be of interest.
     * @param aTargetInstanceOid the Oid of the TragetInstance effected.
     * @param notificationCategory The category of the notification.
     * @param aMessageType the MessageType to build for this Notification event.
     */
    void notification(Long aTargetInstanceOid, int notificationCategory, String aMessageType);       
}
