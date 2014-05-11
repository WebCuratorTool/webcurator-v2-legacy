package org.webcurator.core.store;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResultDTO;

public class MockDigitalAssetStore implements DigitalAssetStore {

	private static Log log = LogFactory.getLog(MockDigitalAssetStore.class);
	private Boolean checkIndexing = false;
	private List<ArcHarvestResultDTO> removedIndexes = new ArrayList<ArcHarvestResultDTO>();
	
	public MockDigitalAssetStore() {
		// TODO Auto-generated constructor stub
	}

	public HarvestResultDTO copyAndPrune(String targetInstanceName,
			int orgHarvestResultNum, int newHarvestResultNum,
			List<String> urisToDelete, List<HarvestResourceDTO> hrsToDelete) throws DigitalAssetStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public Header[] getHeaders(String targetInstanceName,
			int harvestResultNumber, HarvestResourceDTO resource)
			throws DigitalAssetStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public File getResource(String targetInstanceName, int harvestResultNumber,
			HarvestResourceDTO resource) throws DigitalAssetStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getSmallResource(String targetInstanceName,
			int harvestResultNumber, HarvestResourceDTO resource)
			throws DigitalAssetStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public void initiateIndexing(ArcHarvestResultDTO harvestResult)
			throws DigitalAssetStoreException {
		// TODO Auto-generated method stub

	}
	
	public void initiateRemoveIndexes(ArcHarvestResultDTO harvestResult)
			throws DigitalAssetStoreException {
		log.info("Removing indexes for TargetInstance "+harvestResult.getTargetInstanceOid()+ " HarvestNumber "+harvestResult.getHarvestNumber());
		removedIndexes.add(harvestResult);
	}

	public void purge(String[] targetInstanceNames)
			throws DigitalAssetStoreException {
		// TODO Auto-generated method stub

	}

	public void purgeAbortedTargetInstances(String[] targetInstanceNames)
	throws DigitalAssetStoreException {
		// TODO Auto-generated method stub

}

	public void save(String targetInstanceName, File[] files)
			throws DigitalAssetStoreException {
		// TODO Auto-generated method stub

	}

	public void save(String targetInstanceName, File file)
			throws DigitalAssetStoreException {
		// TODO Auto-generated method stub

	}

	public void save(String targetInstanceName, String directory, File[] files)
			throws DigitalAssetStoreException {
		// TODO Auto-generated method stub

	}

	public void save(String targetInstanceName, String directory, File file)
			throws DigitalAssetStoreException {
		// TODO Auto-generated method stub

	}

	public void submitToArchive(String targetInstanceOid, String SIP,
			Map attributes, int harvestNumber)
			throws DigitalAssetStoreException {
		// TODO Auto-generated method stub

	}

	public Boolean checkIndexing(Long harvestResultOid) throws DigitalAssetStoreException {
		return checkIndexing;
	}
	
	public void setCheckIndexingReturn(Boolean checkIndexing)
	{
		this.checkIndexing = checkIndexing;
	}
	
	public List<ArcHarvestResultDTO> getRemovedIndexes() {
		return removedIndexes;
	}
	
	public CustomDepositFormResultDTO getCustomDepositFormDetails(CustomDepositFormCriteriaDTO criteria) throws DigitalAssetStoreException {
		return null;
	}

}
