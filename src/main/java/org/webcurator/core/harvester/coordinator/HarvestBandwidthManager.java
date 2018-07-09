package org.webcurator.core.harvester.coordinator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.webcurator.domain.model.core.BandwidthRestriction;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.QueuedTargetInstanceDTO;

public interface HarvestBandwidthManager {

	void sendBandWidthRestrictions();

	long getCurrentGlobalMaxBandwidth();

	HashMap<Long, TargetInstance> calculateBandwidthAllocation();

	HashMap<Long, TargetInstance> calculateBandwidthAllocation(TargetInstance ti);
	
	void setMinimumBandwidth(int aMinimumBandwidth);

	void setMaxBandwidthPercent(int maxBandwidthPercent);

	int getMaxBandwidthPercent();

	int getMinimumBandwidth();

	boolean isMiniumBandwidthAvailable(QueuedTargetInstanceDTO aTargetInstance);

	boolean isMiniumBandwidthAvailable(TargetInstance aTargetInstance);

	void checkForBandwidthTransition();

	boolean isHarvestOptimizationAllowed();

	HashMap<String, List<BandwidthRestriction>> getBandwidthRestrictions();

	BandwidthRestriction getBandwidthRestriction(Long oid);

	void saveOrUpdate(BandwidthRestriction br);

	void delete(BandwidthRestriction br);

	BandwidthRestriction getBandwidthRestriction(String aDay, Date aTime);
}
