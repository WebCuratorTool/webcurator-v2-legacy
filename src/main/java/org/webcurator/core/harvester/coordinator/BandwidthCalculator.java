package org.webcurator.core.harvester.coordinator;

import java.util.HashMap;
import java.util.List;

import org.webcurator.domain.model.core.TargetInstance;

public interface BandwidthCalculator {

	HashMap<Long, TargetInstance> calculateBandwidthAllocation(List<TargetInstance> aRunningTargetInstances, long aMaxBandwidth,
			int aMaxBandwidthPercent);

}
