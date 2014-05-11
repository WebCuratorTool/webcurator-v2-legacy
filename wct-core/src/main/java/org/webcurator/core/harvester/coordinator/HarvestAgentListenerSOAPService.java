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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.jaxrpc.ServletEndpointSupport;
import org.webcurator.core.check.CheckNotifier;
import org.webcurator.core.common.Constants;
import org.webcurator.domain.model.core.ArcHarvestFileDTO;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResultDTO;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;

/**
 * The Server side implmentation of the HarvestAgentListener. This Service is deployed on the core and is used by the agents to send
 * messages to the core.
 * 
 * @author nwaight
 */
public class HarvestAgentListenerSOAPService extends ServletEndpointSupport implements HarvestAgentListener, CheckNotifier,
		IndexerService, DasCallback {
	/** the harvest coordinator to delegate to. */
	HarvestCoordinator hc = null;

	/** the logger. */
	private static Logger log = LoggerFactory.getLogger(HarvestAgentListenerSOAPService.class);

	protected void onInit() {
		hc = (HarvestCoordinator) getWebApplicationContext().getBean(Constants.BEAN_HARVEST_COORDINATOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.webcurator.core.harvester.coordinator.HarvestAgentListener#heartbeat(org.webcurator.core.harvester.agent.HarvestAgentStatus
	 * )
	 */
	public void heartbeat(HarvestAgentStatusDTO aStatus) {
		log.info("Received heartbeat from {}:{}", aStatus.getHost(), aStatus.getPort());
		hc.heartbeat(aStatus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.harvester.coordinator.HarvestAgentListener#harvestComplete(org.webcurator.core.model.HarvestResult)
	 */
	public void harvestComplete(HarvestResultDTO aResult) {
		log.info("Received harvest complete for {} {}", aResult.getTargetInstanceOid(), aResult.getHarvestNumber());
		hc.harvestComplete(aResult);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.harvester.coordinator.HarvestAgentListener#notification(Long, String)
	 */
	public void notification(Long aTargetInstanceOid, int notificationCategory, String aMessageType) {
		log.info("Received Notification TargetInstanceOid {} with MessageType of {}", aTargetInstanceOid, aMessageType);
		hc.notification(aTargetInstanceOid, notificationCategory, aMessageType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.harvester.coordinator.HarvestAgentListener#notification(String, String)
	 */
	public void notification(String aSubject, int notificationCategory, String aMessage) {
		log.info("Received Notification {} {}", aSubject, aMessage);
		hc.notification(aSubject, notificationCategory, aMessage);
	}

	public void addToHarvestResult(Long harvestResultOid, ArcHarvestFileDTO ahf) {
		try {
			log.info("Received addToHarvestResult({},{})", harvestResultOid, ahf.getName());
			hc.addToHarvestResult(harvestResultOid, ahf);
		} catch (RuntimeException ex) {
			log.error("Exception in createHarvestResult", ex);
			throw ex;
		} catch (Error er) {
			log.error("Error in createHarvestResult", er);
			throw er;
		}

	}

	public Long createHarvestResult(HarvestResultDTO harvestResultDTO) {
		try {
			log.info("Received createHarvestResult for Target Instance {}", harvestResultDTO.getTargetInstanceOid());
			return hc.createHarvestResult(harvestResultDTO);
		} catch (RuntimeException ex) {
			log.error("Exception in createHarvestResult", ex);
			throw ex;
		} catch (Error er) {
			log.error("Error in createHarvestResult", er);
			throw er;
		}
	}

	public void finaliseIndex(Long harvestResultOid) {
		try {
			log.info("Received finaliseIndex for Harvest Result {}", harvestResultOid);
			hc.finaliseIndex(harvestResultOid);
		} catch (RuntimeException ex) {
			log.error("Exception in finaliseIndex", ex);
			throw ex;
		} catch (Error er) {
			log.error("Error in finaliseIndex", er);
			throw er;
		}
	}

	public void notifyAQAComplete(String aqaId) {
		try {
			log.info("Received notifyAQAComplete for AQA Job {}", aqaId);
			hc.notifyAQAComplete(aqaId);
		} catch (RuntimeException ex) {
			log.error("Exception in notifyAQAComplete", ex);
			throw ex;
		} catch (Error er) {
			log.error("Error in notifyAQAComplete", er);
			throw er;
		}
	}

	public void addHarvestResources(Long harvestResultOid, Collection<HarvestResourceDTO> harvestResources) {
		try {
			log.info("Received addHarvestResources for Harvest Result {}", harvestResultOid);
			hc.addHarvestResources(harvestResultOid, harvestResources);
		} catch (RuntimeException ex) {
			log.error("Exception in createHarvestResult", ex);
			throw ex;
		} catch (Error er) {
			log.error("Error in createHarvestResult", er);
			throw er;
		}

	}

	public void completeArchiving(Long targetInstanceOid, String archiveIID) {
		try {
			log.info("Received completeArchiving for Target Instance {}  with archive IID of {}", targetInstanceOid, archiveIID);
			hc.completeArchiving(targetInstanceOid, archiveIID);
		} catch (RuntimeException ex) {
			log.error("Exception in completeArchiving", ex);
			throw ex;
		} catch (Error er) {
			log.error("Error in completeArchiving", er);
			throw er;
		}

	}

	public void failedArchiving(Long targetInstanceOid, String message) {
		try {
			log.info("Received failedArchiving for Target Instance " + targetInstanceOid + " with message " + message);
			hc.failedArchiving(targetInstanceOid, message);
		} catch (RuntimeException ex) {
			log.error("Exception in failedArchiving", ex);
			throw ex;
		} catch (Error er) {
			log.error("Error in failedArchiving", er);
			throw er;
		}

	}
}
