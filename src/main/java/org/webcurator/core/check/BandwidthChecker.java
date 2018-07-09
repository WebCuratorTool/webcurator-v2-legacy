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
package org.webcurator.core.check;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;
import org.webcurator.domain.model.core.harvester.agent.HarvestAgentStatusDTO;

/**
 * A Checker for the current bandwidth usage.
 * 
 * @author nwaight
 */
public class BandwidthChecker extends AbstractChecker {
	/** The warning bandwidth threshold. */
	private long warnThreshold;
	/** The error bandwidth threshold. */
	private long errorThreshold;
	/** The bandwidth is above the warning threshold. */
	private boolean aboveWarnThreshold = false;
	/** The bandwidth is above the error threshold. */
	private boolean aboveErrorThreshold = false;
	/** The harvest coordinator to use. */
	private HarvestCoordinator harvestCoordinator;
	/** the logger. */
	private static Logger log = LoggerFactory.getLogger(MemoryChecker.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.webcurator.core.check.AbstractChecker#check()
	 */
	@Override
	public void check() {
		long maxBandwidth = harvestCoordinator.getCurrentGlobalMaxBandwidth();
		long usedBandwidth = calculateTotalBandwidthUsed();

		double percentage = (usedBandwidth / maxBandwidth) * 100;
		log.debug(percentage + "% of the max bandwidth is being used. " + usedBandwidth + " of " + maxBandwidth);

		if (percentage >= warnThreshold && !aboveWarnThreshold) {
			aboveWarnThreshold = true;
			log.warn("The used bandwidth is above the warning threshold " + warnThreshold + "% and is " + percentage + "%");
			notify(LEVEL_WARNING, "The used bandwidth is above the warning threshold " + warnThreshold + "% and is " + percentage
					+ "%");
		} else if (percentage >= errorThreshold && !aboveErrorThreshold) {
			aboveErrorThreshold = true;
			log.error("The used bandwidth is above the error threshold " + errorThreshold + "% and is " + percentage + "%");
			notify(LEVEL_ERROR, "The used bandwidth is above the error threshold " + errorThreshold + "% and is " + percentage
					+ "%");
		} else if (percentage < warnThreshold && aboveWarnThreshold) {
			aboveWarnThreshold = false;
			log.info("The used bandwidth has recovered below the warning threshold {}% and is {}%", warnThreshold, percentage);
		} else if (percentage < errorThreshold && aboveErrorThreshold) {
			aboveErrorThreshold = false;
			log.info("The used bandwidth has recovered below the error threshold {}% and is {}%",errorThreshold, percentage);
		}
	}

	/**
	 * @return the amount of bandwidth being used based on the current agents status's
	 */
	private long calculateTotalBandwidthUsed() {
		HashMap<String, HarvestAgentStatusDTO> agents = harvestCoordinator.getHarvestAgents();
		if (agents == null || agents.isEmpty()) {
			return 0;
		}

		double tot = 0;
		for (HarvestAgentStatusDTO agent : agents.values()) {
			tot += agent.getCurrentKBs();
		}

		Double total = new Double(tot);
		return total.longValue();
	}

	/**
	 * @param errorThreshold
	 *            the errorThreshold to set
	 */
	public void setErrorThreshold(long errorThreshold) {
		this.errorThreshold = errorThreshold;
	}

	/**
	 * @param warnThreshold
	 *            the warnThreshold to set
	 */
	public void setWarnThreshold(long warnThreshold) {
		this.warnThreshold = warnThreshold;
	}

	/**
	 * @param harvestCoordinator
	 *            the harvestCoordinator to set
	 */
	public void setHarvestCoordinator(HarvestCoordinator harvestCoordinator) {
		this.harvestCoordinator = harvestCoordinator;
	}
}
