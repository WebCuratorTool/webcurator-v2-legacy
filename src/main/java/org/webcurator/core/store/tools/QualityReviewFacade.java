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
package org.webcurator.core.store.tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.core.store.DigitalAssetStore;
import org.webcurator.core.util.Auditor;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.TargetInstanceDAO;
import org.webcurator.domain.model.core.ArcHarvestResult;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.domain.model.core.HarvestResource;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResult;
import org.webcurator.domain.model.core.TargetInstance;

/**
 * This facade provides the methods required for the Quality Review Tools.
 * @author bbeaumont
 *
 */
public class QualityReviewFacade {

	/** A reference to the digital asset store. */
	private DigitalAssetStore digialAssetStore = null;
	/** The Target Instance Dao */
	private TargetInstanceDAO targetInstanceDao = null;
	/** the auditor. */
	private Auditor auditor = null;
	
	/**
	 * Get the digital asset store. 
	 * @return The digital asset store.
	 */
	public DigitalAssetStore getDigialAssetStore() {
		return digialAssetStore;
	}
	
	/**
	 * Spring setter for the digital asset store.
	 * @param store The digital asset store.
	 */
	public void setDigialAssetStore(DigitalAssetStore store) {
		this.digialAssetStore = store;
	}
	
	/**
	 * Get the target instance dao.
	 * @return The target instance dao.
	 */
	public TargetInstanceDAO getTargetInstanceDao() {
		return targetInstanceDao;
	}
	
	/** 
	 * Spring setter method for the Target Instance DAO.
	 * @param targetInstanceDao The target instance DAO.
	 */
	public void setTargetInstanceDao(TargetInstanceDAO targetInstanceDao) {
		this.targetInstanceDao = targetInstanceDao;
	}
	
	/**
	 * Get a harvested resource from the digital asset store.
	 * @param targetInstanceName The OID of the target instance (as a string).
	 * @param harvestResultNumber The number of the harvest result to get the resource from.
	 * @param resource The HarvestResource to get.
	 * @return The resource.
	 * @throws DigitalAssetStoreException if there are any errors.
	 */
	public File getResource(String targetInstanceName, int harvestResultNumber, HarvestResource resource) throws DigitalAssetStoreException {
		return digialAssetStore.getResource(targetInstanceName, harvestResultNumber, resource.buildDTO());
	}
	
	/**
	 * Get a harvested resource from the digital asset store.
	 * @param dto The DTO HarvestResource to retrieve.
	 * @return The resource.
	 * @throws DigitalAssetStoreException if there are any errors.
	 */
	public File getResource(HarvestResourceDTO dto) throws DigitalAssetStoreException {
		return digialAssetStore.getResource(dto.buildJobName(), dto.getHarvestResultNumber(), dto);
	}	
	
	/**
	 * Get a harvested resource from the digital asset store as a byte array. 
	 * This tends to be faster, but has higher memory requirements so should
	 * only be used for smaller resources.
	 * @param dto The DTO HarvestResource to retrieve.
	 * @return The resource as a byte array.
	 * @throws DigitalAssetStoreException if there are any errors.
	 */	
	public byte[] getSmallResource(HarvestResourceDTO dto) throws DigitalAssetStoreException {
		return digialAssetStore.getSmallResource(dto.buildJobName(), dto.getHarvestResultNumber(), dto);
	}		
	
	/**
	 * Retrieve a HarvestResult from the database.
	 * @param harvestResultOid The OID of the harvest result to get.
	 * @return The harvest result.
	 */
	public HarvestResult getHarvestResult(Long harvestResultOid) {
		return targetInstanceDao.getHarvestResult(harvestResultOid);
	}
	
	/**
	 * Get a tree of the harvest result.
	 * @param harvestResultOid The OID of the harvest result to build the tree for.
	 * @return A tree of the harvest result.
	 */
	public HarvestResourceNodeTreeBuilder getHarvestResultTree(Long harvestResultOid) {
		HarvestResult result = targetInstanceDao.getHarvestResult(harvestResultOid);
		Iterator<HarvestResource> it = result.getResources().values().iterator();
		
		HarvestResourceNodeTreeBuilder tree = new HarvestResourceNodeTreeBuilder();
		while(it.hasNext()) {
			HarvestResource res = it.next();
			
			try {
				if(res.getName().startsWith("http")) {
					tree.addNode(res);
				}
			}
			catch(MalformedURLException ex) {
				ex.printStackTrace();
			}
		}
		
		return tree;
	}	
	
	/**
	 * Get the HTTP Headers for a given resource.
	 * @param targetInstanceName The OID of the target instance (as a string).
	 * @param harvestResultNumber The number of the harvest result to get the resource from.
	 * @param resource The HarvestResource to get.
	 * @return The headers.
	 * @throws DigitalAssetStoreException if there are any errors.
	 */
	public Header[] getHttpHeaders(String targetInstanceName, int harvestResultNumber, HarvestResource resource) throws DigitalAssetStoreException {
		return digialAssetStore.getHeaders(targetInstanceName, harvestResultNumber, resource.buildDTO());
	}
	
	/**
	 * Get the HTTP Headers for a given resource.
	 * @param dto The DTO HarvestResource to retrieve the headers for.
	 * @return The HTTP headers.
	 * @throws DigitalAssetStoreException if there are any errors.
	 */
	public Header[] getHttpHeaders(HarvestResourceDTO dto) throws DigitalAssetStoreException {
		return digialAssetStore.getHeaders(dto.buildJobName(), dto.getHarvestResultNumber(), dto);
	}
	
	/**
	 * Get the Harvest Resource DTO for a resource
	 * @param harvestResultNumber The OID of harvest result to get the resource from.
	 * @param resource The URL of the resource to get the harvest resource for.
	 * @return The HarvestResourceDTO.
	 * @throws IOException if there are any errors.
	 */
	public HarvestResourceDTO getHarvestResourceDTO(long harvestResultNumber, String resource) throws IOException {
		return targetInstanceDao.getHarvestResourceDTO(harvestResultNumber, resource);
	}	
	
	/**
	 * Get the Harvest Resource DTOs for a resource
	 * @param harvestResultNumber The OID of harvest result to get the resource from.
	 * @return a <code>List</code> of <code>HarvestResourceDTO</code>.
	 * @throws IOException if there are any errors.
	 */
	public List<HarvestResourceDTO> getHarvestResourceDTOs(long harvestResultOid) throws IOException {
		return targetInstanceDao.getHarvestResourceDTOs(harvestResultOid);
	}	
	
	/**
	 * Copy a Harvest Result and remove all the URLs in the urisToDelete list and import all the HarvestResourceDTOs in the hrsToImport list.
	 * @param harvestResultOid The OID of the harvest result to copy.
	 * @param urisToDelete     The List of URLs to delete from the copy.
	 * @param hrsToImport      The list of HarvestResourceDTO to import to the copy.
	 * @param provenanceNote   A note to explain why the contents were pruned and/or added to.
	 * @return 				   The HarvestResult for the pruned copy.
	 * @throws DigitalAssetStoreException if anything fails.
	 */
	public HarvestResult copyAndPrune(long harvestResultOid, List<String> urisToDelete, List<HarvestResourceDTO> hrsToImport, String provenanceNote, List<String> modificationNotes) throws DigitalAssetStoreException {
		HarvestResult res = targetInstanceDao.getHarvestResult(harvestResultOid);
		TargetInstance ti = res.getTargetInstance();
		
		// Perform the copy and prune.
		digialAssetStore.copyAndPrune(ti.getJobName(), res.getHarvestNumber(), ti.getHarvestResults().size()+1, urisToDelete, hrsToImport);
		
		// Create the base record.
        ArcHarvestResult hr = new ArcHarvestResult(ti, ti.getHarvestResults().size()+1);
		hr.setDerivedFrom(res.getHarvestNumber());
        hr.setProvenanceNote(provenanceNote);
        hr.addModificationNotes(modificationNotes);
		hr.setTargetInstance(ti);
		hr.setState(HarvestResult.STATE_INDEXING);
		if (AuthUtil.getRemoteUserObject() != null) {
			hr.setCreatedBy(AuthUtil.getRemoteUserObject());
		} else {
			hr.setCreatedBy(res.getCreatedBy());
		}
		
		ti.getHarvestResults().add(hr);

		// Save to the database.
		targetInstanceDao.save(hr);
		targetInstanceDao.save(ti);
		
		// Ask the Digital Asset Store to create the index.
		digialAssetStore.initiateIndexing(new ArcHarvestResultDTO( hr.getOid(), hr.getTargetInstance().getOid(), hr.getCreationDate(), hr.getHarvestNumber(), hr.getProvenanceNote() ));
		
		// Audit the completion of the copy and prune.
		auditor.audit(ArcHarvestResult.class.getName(), hr.getOid(), Auditor.ACTION_COPY_AND_PRUNE_HARVEST_RESULT, "Created pruned harvest result " + ti.getHarvestResults().size() + " from harvest result " + res.getHarvestNumber() + " for target instance " + ti.getOid());
		
		// Return the HarvestResult.
		return hr;        
	}

	/**
	 * @param auditor the auditor to set
	 */
	public void setAuditor(Auditor auditor) {
		this.auditor = auditor;
	}	
}
