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
package org.webcurator.core.store;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResultDTO;

/**
 * The <code>DigitalAssetStore</code> interface is used by the WCT Core and
 * WCT Harvest Agent to interact with the digital asset store component. 
 * 
 * @author bbeaumont
 */
public interface DigitalAssetStore {	
	
	/**
	 * Retrieve a resource from the Digital Asset Store. The resource is
	 * returned as a SOAP attachment and written to disk for use. This is ideal
	 * for streaming large resources.
	 * 
	 * @param targetInstanceName  The OID of the target instance that the 
	 * 							  resource belongs to.
	 * @param harvestResultNumber The index of the harvest result, within the 
	 * 							  target instance, that contains the resource.
	 * @param resource			  The resource to retrieve.
	 * @return					  The resource, as a file.
	 * @throws DigitalAssetStoreException if there are any errors.
	 */
	File getResource(String targetInstanceName, int harvestResultNumber, HarvestResourceDTO resource) throws DigitalAssetStoreException;
	
	
	/**
	 * Retrieves a resource transferring it as a byte array, rather than as 
	 * a SOAP attachment. This method is appropriate for small resources where
	 * streaming is not required. It has additional memory requirements, but
	 * has no file I/O.
	 * 
	 * @param targetInstanceName  The OID of the target instance that the 
	 * 							  resource belongs to.
	 * @param harvestResultNumber The index of the harvest result, within the 
	 * 							  target instance, that contains the resource.
	 * @param resource			  The resource to retrieve.
	 * @return					  The resource, as a file.
	 * @throws DigitalAssetStoreException if there are any errors.
	 */
	byte[] getSmallResource(String targetInstanceName, int harvestResultNumber, HarvestResourceDTO resource) throws DigitalAssetStoreException;
	
	
	/**
	 * Retrieve the HTTP headers for a given resource.
	 * 
	 * @param targetInstanceName  The OID of the target instance that the 
	 * 							  resource belongs to.
	 * @param harvestResultNumber The index of the harvest result, within the 
	 * 							  target instance, that contains the resource.
	 * @param resource			  The resource for which to retrieve the headers.
	 * @return					  An array of HTTP Headers.
	 * @throws DigitalAssetStoreException if there are any errors.
	 */
	Header[] getHeaders(String targetInstanceName, int harvestResultNumber, HarvestResourceDTO resource) throws DigitalAssetStoreException;
	
	
	/**
	 * Save an array of files to the digital asset store. The files are
	 * saved to the first harvest result, so this method should be used
	 * only for files coming directly from the Harvest Agent.
	 * 
	 * @param targetInstanceName The OID of the target instance to save the files to.
	 * @param files				 An array of files to send to the asset store.
	 * @throws DigitalAssetStoreException if there are any errors.
	 */
	void save(String targetInstanceName, File[] files) throws DigitalAssetStoreException;
	
	void save(String targetInstanceName, File file) throws DigitalAssetStoreException;

	
	/**
	 * Save an array of files to the digital asset store. The files are
	 * saved to the first harvest result, so this method should be used
	 * only for files coming directly from the Harvest Agent.
	 * 
	 * @param targetInstanceName The OID of the target instance to save the files to.
	 * @param directory			 The subdirectory under the target instance
	 * 							 directory to which to save the files. 
	 * @param files				 An array of files to send to the asset store.
	 * @throws DigitalAssetStoreException if there are any errors.
	 */
	void save(String targetInstanceName, String directory, File[] files) throws DigitalAssetStoreException;
	
	void save(String targetInstanceName, String directory, File file) throws DigitalAssetStoreException;
	
	
	/**
	 * Create a new Harvest Result by pruning resources out of an existing
	 * harvest result.
	 * 
	 * @param targetInstanceName  The OID of the target instance to which the
	 * 							  harvest results belong.
	 * @param orgHarvestResultNum The original harvest result number to prune.
	 * @param newHarvestResultNum The number for the new harvest result.
	 * @param urisToDelete		  A List of resource URIs that should be pruned.
	 * @param urisToImport		  A List of HarvestResource that should be imported.
	 * @return					  The HarvestResultDTO of the pruned harvest
	 * 							  result.
	 * @throws DigitalAssetStoreException if any errors occur.
	 */
	HarvestResultDTO copyAndPrune(String targetInstanceName, int orgHarvestResultNum, int newHarvestResultNum, List<String> urisToDelete, List<HarvestResourceDTO> hrsToImport) throws DigitalAssetStoreException;
	
	/**
	 * Initiate the indexing of a Harvest Result.
	 * 
	 * @param harvestResult The DTO of the Harvest Result to be indexed.
	 */
	void initiateIndexing(ArcHarvestResultDTO harvestResult) throws DigitalAssetStoreException;
	/**
	 * Initiate the removal of indexes of the given HarvestResult.
	 * @param harvestResult The HarvestResult to remove indexes for.
	 * @throws DigitalAssetStoreException if any errors occur.
	 */
	void initiateRemoveIndexes(ArcHarvestResultDTO harvestResult) throws DigitalAssetStoreException;

	/**
	 * Check the indexing of a Harvest Result.
	 * 
	 * @param Long harvestResultOid the harvest result to check
	 * @return true if the harvestResult is currently indexing.
	 */
	Boolean checkIndexing(Long harvestResultOid) throws DigitalAssetStoreException;
	
	/**
	 * Submits a harvest result to the archive. This method will use a callback to 
	 * send the unique identifier returned from the archive. 
	 * 
	 * @param targetInstanceOid	The OID of the target instance.
	 * @param SIP				The XML SIP file.		
	 * @param xAttributes		A Map of indexed attributes. This map may or 
	 * 							may not be used by the archive adapter 
	 * 							implementation.
	 * @param harvestNumber     The number of the harvest result to submit.
	 * @throws DigitalAssetStoreException if any errors occur.
	 */
	void submitToArchive(String targetInstanceOid, String SIP, Map xAttributes, int harvestNumber)throws DigitalAssetStoreException;
	

	
	/**
	 * Purge all the data from the digital asset store for the target instances
	 * specified in the list of target instance names.
	 * @param targetInstanceNames the target instances to purge
	 * @throws DigitalAssetStoreException if any errors occur.
	 */
	void purge(String[] targetInstanceNames) throws DigitalAssetStoreException;
	
	/**
	 * Purge all the data from the digital asset store for the target instances
	 * specified in the list of **aborted** target instance names.
	 * @param targetInstanceNames the target instances to purge
	 * @throws DigitalAssetStoreException if any errors occur.
	 */
	void purgeAbortedTargetInstances(String[] targetInstanceNames) throws DigitalAssetStoreException;
	
	/**
	 * Determine whether a custom deposit form is required to be shown before
	 * submitting a harvest to a specific digital asset store.
	 * 
	 * @param criteria Provides parameters that are required for DAS to make this decision.
	 * @return The response DTO that indicates whether a custom form is required, 
	 * and if so, the location/content of the custom form.
	 * @throws Exception thrown if there is an error
	 */
	public CustomDepositFormResultDTO getCustomDepositFormDetails(CustomDepositFormCriteriaDTO criteria)throws DigitalAssetStoreException;

}
