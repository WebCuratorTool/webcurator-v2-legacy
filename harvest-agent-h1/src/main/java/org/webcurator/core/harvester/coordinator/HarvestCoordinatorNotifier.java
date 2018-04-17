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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.check.CheckNotifier;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.harvester.agent.HarvestAgent;
import org.webcurator.core.util.WCTSoapCall;
import org.webcurator.domain.model.core.ArcHarvestFileDTO;
import org.webcurator.domain.model.core.ArcHarvestResourceDTO;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.domain.model.core.HarvestResultDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvesterStatusDTO;

/**
 * The HarvestCoordinatorNotifier uses SOAP to send messages back to the core.
 * These include notifications, heartbeats and job completions messages.
 * @author nwaight
 */
public class HarvestCoordinatorNotifier implements HarvestAgentListener, CheckNotifier {
	/** The harvest agent that the this notifier is running on. */
	HarvestAgent agent;	
    /** the host name or ip-address for the wct. */
    private String host = "localhost";
    /** the port number for the wct. */
    private int port = 8080;
    /** the name of the soap call for the harvest agent listener. */
    private String service = WCTSoapCall.WCT_HARVEST_LISTENER;
    /** the logger. */
    private static Log log = LogFactory.getLog(HarvestCoordinatorNotifier.class);
    
    /* (non-Javadoc)
     * @see org.webcurator.core.harvester.coordinator.HarvestAgentListener#heartbeat(org.webcurator.core.harvester.agent.HarvestAgentStatus)
     */
    public void heartbeat(HarvestAgentStatusDTO aStatus) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("WCT: Start of heartbeat");
            }
            log.info("WCT: Start of heartbeat");
            
            WCTSoapCall call = new WCTSoapCall(host, port, service, "heartbeat");
            call.regTypes(HarvestAgentStatusDTO.class, HarvesterStatusDTO.class);
            call.invoke(aStatus);
            
            if (log.isDebugEnabled()) {
                log.debug("WCT: End of heartbeat");
            }
            log.info("WCT: End of heartbeat");
        }
        catch(Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Heartbeat Notification failed : " + ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void requestRecovery(String haHost, int haPort, String haService) {
        // Placeholder - not used with Heritrix 1x
    }

    /* (non-Javadoc)
     * @see org.webcurator.core.harvester.coordinator.HarvestAgentListener#harvestComplete(org.webcurator.core.model.HarvestResult)
     */
    public void harvestComplete(HarvestResultDTO aResult) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("WCT: Start of harvestComplete");
            }
            
            WCTSoapCall call = new WCTSoapCall(host, port, service, "harvestComplete");
            call.regTypes(ArcHarvestResultDTO.class, ArcHarvestResourceDTO.class, ArcHarvestFileDTO.class);
            call.invoke(aResult);
            
            if (log.isDebugEnabled()) {
                log.debug("WCT: End of HarvestComplete");
            }
        }
        catch(Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Harvest Complete Notification failed : " + ex.getMessage(), ex);
            } 
            throw new WCTRuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.webcurator.core.harvester.coordinator.HarvestAgentListener#notification(Long, String)
     */
    public void notification(Long aTargetInstanceOid, int notificationCategory, String aMessageType) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("WCT: Start of notification");
            }
            
            WCTSoapCall call = new WCTSoapCall(host, port, service, "notification");
            call.invoke(aTargetInstanceOid, notificationCategory, aMessageType);
            
            if (log.isDebugEnabled()) {
                log.debug("WCT: End of notification");
            }
        }
        catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Notification failed : " + ex.getMessage(), ex);
            }  
        }        
    }
    
    /* (non-Javadoc)
     * @see org.webcurator.core.harvester.coordinator.HarvestAgentListener#notification(String, String)
     */
    public void notification(String aSubject, int notificationCategory, String aMessage) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("WCT: Start of notification");
            }
            
            WCTSoapCall call = new WCTSoapCall(host, port, service, "notification");
            call.invoke(agent.getName() + " " + aSubject, notificationCategory, aMessage);

            if (log.isDebugEnabled()) {
                log.debug("WCT: End of notification");
            }
        }
        catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Notification failed : " + ex.getMessage(), ex);
            }  
        }        
    }
    
    /**
     * @param aHost The host to set.
     */
    public void setHost(String aHost) {
        this.host = aHost;
    }

    /**
     * @param aPort The port to set.
     */
    public void setPort(int aPort) {
        this.port = aPort;
    }

    /**
     * @param aService The service to set.
     */
    public void setService(String aService) {
        this.service = aService;
    }

	/**
	 * @param agent the agent to set
	 */
	public void setAgent(HarvestAgent agent) {
		this.agent = agent;
	}   
}
