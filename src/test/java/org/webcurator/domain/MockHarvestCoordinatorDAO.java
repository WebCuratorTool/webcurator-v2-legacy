package org.webcurator.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.domain.model.core.BandwidthRestriction;

public class MockHarvestCoordinatorDAO implements HarvestCoordinatorDAO {

	private static Log log = LogFactory.getLog(MockHarvestCoordinatorDAO.class);

	public MockHarvestCoordinatorDAO(String filename)
	{
		// TODO: implement file based data store
	}
	
	public void delete(BandwidthRestriction bandwidthRestriction) 
	{
		log.debug("delete - " + bandwidthRestriction.getOid());
	}

	public BandwidthRestriction getBandwidthRestriction(Long oid) {
		
		BandwidthRestriction res = new BandwidthRestriction();
		res.setOid(oid);
		res.setBandwidth(500);
		res.setDayOfWeek("Monday");
		res.setStartTime(new Date());
		res.setEndTime(new Date());

		return res;
	}

	public BandwidthRestriction getBandwidthRestriction(String day, Date time) {

		return getBandwidthRestriction(12345L);
	}

	public HashMap<String, List<BandwidthRestriction>> getBandwidthRestrictions() {
		HashMap<String, List<BandwidthRestriction>> map = new HashMap<String, List<BandwidthRestriction>>(); 

		return map;
	}

	public void saveOrUpdate(BandwidthRestriction bandwidthRestriction) {
		log.debug("saveOrUpdate - " + bandwidthRestriction.getOid());
	}

}
