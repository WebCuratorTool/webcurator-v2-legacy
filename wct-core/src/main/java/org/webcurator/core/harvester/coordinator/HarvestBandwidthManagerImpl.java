package org.webcurator.core.harvester.coordinator;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webcurator.core.exceptions.WCTRuntimeException;
import org.webcurator.core.util.Auditor;
import org.webcurator.domain.HarvestCoordinatorDAO;
import org.webcurator.domain.TargetInstanceCriteria;
import org.webcurator.domain.TargetInstanceDAO;
import org.webcurator.domain.model.core.BandwidthRestriction;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.domain.model.dto.QueuedTargetInstanceDTO;

public class HarvestBandwidthManagerImpl implements HarvestBandwidthManager {

	private HarvestCoordinatorDAO harvestCoordinatorDao;
	private HarvestAgentManager harvestAgentManager;
	private TargetInstanceDAO targetInstanceDao;

	private Logger log = LoggerFactory.getLogger(getClass());
	private Auditor auditor = null;

	private int minimumBandwidth = 1;
	private int maxBandwidthPercent = 80;
	private long previousMaxGlobalBandwidth = 0;
	private BandwidthCalculator bandwidthCalculator = new BandwidthCalculatorImpl();

	@Override
	public void sendBandWidthRestrictions() {
		// Allocate the bandwidth
		HashMap<Long, TargetInstance> running = calculateBandwidthAllocation();
		for (TargetInstance ti : running.values()) {
			harvestAgentManager.restrictBandwidthFor(ti);
		}
	}

	@Override
	public HashMap<Long, TargetInstance> calculateBandwidthAllocation(TargetInstance aTargetInstance) {
		// Check to see if there are other running target instances with a
		// percentage allocation.
		TargetInstanceCriteria tic = new TargetInstanceCriteria();
		Set<String> states = new HashSet<String>();
		states.add(TargetInstance.STATE_RUNNING);
		states.add(TargetInstance.STATE_PAUSED);
		tic.setStates(states);

		List<TargetInstance> runningTIs = targetInstanceDao.findTargetInstances(tic);
		runningTIs.add(aTargetInstance);

		return calculateBandwidthAllocation(runningTIs);
	}

	@Override
	public HashMap<Long, TargetInstance> calculateBandwidthAllocation() {
		// Check to see if there are other running target instances with a
		// percentage allocation.
		TargetInstanceCriteria tic = new TargetInstanceCriteria();
		Set<String> states = new HashSet<String>();
		states.add(TargetInstance.STATE_RUNNING);
		states.add(TargetInstance.STATE_PAUSED);
		tic.setStates(states);

		List<TargetInstance> runningTIs = targetInstanceDao.findTargetInstances(tic);

		return calculateBandwidthAllocation(runningTIs);
	}

	/** @see HarvestCoordinator#calculateBandwidthAllocation(TargetInstance). */
	private HashMap<Long, TargetInstance> calculateBandwidthAllocation(List<TargetInstance> aRunningTargetInstances) {
		// Get the global max bandwidth setting for the current period.
		long maxBandwidth = getCurrentGlobalMaxBandwidth();
		return bandwidthCalculator.calculateBandwidthAllocation(aRunningTargetInstances, maxBandwidth, maxBandwidthPercent);
	}

	@Override
	public long getCurrentGlobalMaxBandwidth() {
		return getGlobalMaxBandwidth(0);
	}

	private long getGlobalMaxBandwidth(int aMillisBeforeNow) {
		try {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MILLISECOND, (aMillisBeforeNow * -1));
			String time = BandwidthRestriction.TIMEONLY_FORMAT.format(now.getTime());
			Date date = BandwidthRestriction.FULLDATE_FORMAT.parse(BandwidthRestriction.DEFAULT_DATE + time);
			String day = BandwidthRestriction.FULLDAY_FORMAT.format(now.getTime()).toUpperCase();

			BandwidthRestriction br = harvestCoordinatorDao.getBandwidthRestriction(day, date);
			if (br != null) {
				return br.getBandwidth();
			}
		} catch (ParseException e) {
			log.error("Failed to parse the date for the bandwith restriction : " + e.getMessage(), e);
		}

		return 0;
	}

	/**
	 * Check to see that at least the minimum amount of bandwith can be allocated to all the running target instances assuming that
	 * this target instance is allocated to a harvest agent
	 * 
	 * @param aTargetInstance
	 *            the target instances that may be allocated
	 * @return true if the minimum bandwidth will be available.
	 */
	@Override
	public boolean isMiniumBandwidthAvailable(QueuedTargetInstanceDTO aTargetInstance) {
		if (null == aTargetInstance) {
			throw new WCTRuntimeException("The Target Instance passed in was null.");
		}

		if (getCurrentGlobalMaxBandwidth() < getMinimumBandwidth()) {
			return false;
		}

		TargetInstance ti = targetInstanceDao.load(aTargetInstance.getOid());
		ti = targetInstanceDao.populate(ti);

		return isMiniumBandwidthAvailable(ti);
	}

	@Override
	public boolean isMiniumBandwidthAvailable(TargetInstance aTargetInstance) {
		if (null == aTargetInstance) {
			throw new WCTRuntimeException("The Target Instance passed in was null.");
		}

		if (getCurrentGlobalMaxBandwidth() < getMinimumBandwidth()) {
			return false;
		}

		TargetInstance ti = targetInstanceDao.load(aTargetInstance.getOid());
		ti = targetInstanceDao.populate(ti);

		HashMap<Long, TargetInstance> targetInstances = null;
		if (TargetInstance.STATE_PAUSED.equals(ti.getState())) {
			targetInstances = calculateBandwidthAllocation();
		} else {
			targetInstances = calculateBandwidthAllocation(ti);
		}

		Integer bandwidthPercent = ti.getBandwidthPercent();
		if (bandwidthPercent == null) {
			if (ti.getAllocatedBandwidth() < getMinimumBandwidth()) {
				// failure bandwidth setting is too low.
				return false;
			}
		} else {
			for (TargetInstance ati : targetInstances.values()) {
				if (ati.getBandwidthPercent() == null) {
					if (ati.getAllocatedBandwidth() < getMinimumBandwidth()) {
						// failure bandwidth setting is too low.
						return false;
					}
				}
			}
		}

		return true;
	}

	@Override
	public synchronized void checkForBandwidthTransition() {

		long currBW = getCurrentGlobalMaxBandwidth();
		log.debug("Checking bandwidth. prev = {} curr = {}", previousMaxGlobalBandwidth, currBW);

		if (currBW != previousMaxGlobalBandwidth) {
			log.info("Found bandwidth transition from {} to {} re-calulating bandwidth settings.", previousMaxGlobalBandwidth,
					currBW);
			sendBandWidthRestrictions();
		}

		previousMaxGlobalBandwidth = currBW;
	}

	@Override
	public boolean isHarvestOptimizationAllowed() {
		try {
			Calendar now = Calendar.getInstance();
			String time = BandwidthRestriction.TIMEONLY_FORMAT.format(now.getTime());
			Date date = BandwidthRestriction.FULLDATE_FORMAT.parse(BandwidthRestriction.DEFAULT_DATE + time);
			String day = BandwidthRestriction.FULLDAY_FORMAT.format(now.getTime()).toUpperCase();

			BandwidthRestriction br = harvestCoordinatorDao.getBandwidthRestriction(day, date);
			if (br == null)
				return false;
			return br.isAllowOptimize();
		} catch (ParseException e) {
			log.error("Failed to parse the date for the bandwith restriction : " + e.getMessage(), e);
		}
		return false;
	}

	@Override
	public BandwidthRestriction getBandwidthRestriction(Long oid) {
		return harvestCoordinatorDao.getBandwidthRestriction(oid);
	}

	@Override
	public void saveOrUpdate(BandwidthRestriction bandwidthRestriction) {
		boolean isNew = bandwidthRestriction.getOid() == null;
		harvestCoordinatorDao.saveOrUpdate(bandwidthRestriction);

		if (isNew) {
			auditor.audit(BandwidthRestriction.class.getName(), bandwidthRestriction.getOid(),
					Auditor.ACTION_NEW_BANDWIDTH_RESTRICTION, "New bandwidth restriction: " + bandwidthRestriction.toString());
		} else {
			auditor.audit(BandwidthRestriction.class.getName(), bandwidthRestriction.getOid(),
					Auditor.ACTION_CHANGE_BANDWIDTH_RESTRICTION, "Bandwidth setting changed to: " + bandwidthRestriction.toString());
		}
	}

	@Override
	public void delete(BandwidthRestriction bandwidthRestriction) {
		auditor.audit(BandwidthRestriction.class.getName(), null, Auditor.ACTION_DELETE_BANDWIDTH_RESTRICTION,
				"Deleted bandwidth restriction: " + bandwidthRestriction.toString());
		harvestCoordinatorDao.delete(bandwidthRestriction);
	}

	@Override
	public BandwidthRestriction getBandwidthRestriction(String aDay, Date aTime) {
		return harvestCoordinatorDao.getBandwidthRestriction(aDay, aTime);
	}

	@Override
	public HashMap<String, List<BandwidthRestriction>> getBandwidthRestrictions() {
		return harvestCoordinatorDao.getBandwidthRestrictions();
	}

	@Override
	public int getMinimumBandwidth() {
		return minimumBandwidth;
	}

	@Override
	public void setMinimumBandwidth(int minimumBandwidth) {
		this.minimumBandwidth = minimumBandwidth;
	}

	@Override
	public int getMaxBandwidthPercent() {
		return maxBandwidthPercent;
	}

	@Override
	public void setMaxBandwidthPercent(int maxBandwidthPercent) {
		this.maxBandwidthPercent = maxBandwidthPercent;
	}

	public void setHarvestCoordinatorDao(HarvestCoordinatorDAO harvestCoordinatorDao) {
		this.harvestCoordinatorDao = harvestCoordinatorDao;
	}

	public void setHarvestAgentManager(HarvestAgentManager harvestAgentManager) {
		this.harvestAgentManager = harvestAgentManager;
	}

	public void setTargetInstanceDao(TargetInstanceDAO targetInstanceDao) {
		this.targetInstanceDao = targetInstanceDao;
	}

	public void setAuditor(Auditor auditor) {
		this.auditor = auditor;
	}

	public void setBandwidthCalculator(BandwidthCalculator bandwidthCalculator) {
		this.bandwidthCalculator = bandwidthCalculator;
	}

}
