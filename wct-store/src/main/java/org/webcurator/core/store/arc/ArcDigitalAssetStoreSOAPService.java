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
package org.webcurator.core.store.arc;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.remoting.jaxrpc.ServletEndpointSupport;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.core.store.DigitalAssetStoreSOAP;
import org.webcurator.core.store.TempFileDataSource;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResultDTO;

/**
 * The ArcDigitalAssetStoreSOAPService is server implementation of the SOAP Service.
 * The SOAP Service will connvert DataHandler objects to files and delegates the actual
 * processing to the ArcDigitalAssetStoreService.
 * @author bbeaumont
 */
public class ArcDigitalAssetStoreSOAPService extends ServletEndpointSupport implements DigitalAssetStoreSOAP   {
	/** The logger. */
	private static Log log = LogFactory.getLog(ArcDigitalAssetStoreSOAPService.class);
	/** The service to delegate the actual processing to. */
	private ArcDigitalAssetStoreService service;
	/** Initilisation. */
	protected void onInit() {
        service = (ArcDigitalAssetStoreService) getWebApplicationContext().getBean("arcDigitalAssetStoreService");
    }
	/** @see DigitalAssetStoreSOAP#save(String, String[], DataHandler[]). */
	public void save(String targetInstanceName, String[] filenames, DataHandler[] files) throws DigitalAssetStoreException {
		File[] realFiles = new File[files.length];
		// Rename the files to have the right names.
		for(int i=0; i<files.length;i++) {
			File oldFile = new File(files[i].getName());
			realFiles[i] = new File(oldFile.getParentFile(), filenames[i]);
			
            if (log.isDebugEnabled()) {
                log.debug("SOAPService renaming " + oldFile.getAbsolutePath() + " to " + realFiles[i].getAbsolutePath());
            }
			oldFile.renameTo(realFiles[i]);
		}
		
		service.save(targetInstanceName, realFiles);
	}

	
	public void save(String targetInstanceName, String directory, String filename, DataHandler file) throws DigitalAssetStoreException {
		save(targetInstanceName, directory, new String[] {filename}, new DataHandler[] { file });
	}
	
	public void save(String targetInstanceName, String filename, DataHandler file) throws DigitalAssetStoreException {
		save(targetInstanceName, new String[] {filename}, new DataHandler[] { file });
	}	
	
	/** @see DigitalAssetStoreSOAP#save(String, String, String[], DataHandler[]). */
	public void save(String targetInstanceName, String directory, String[] filenames, DataHandler[] files) throws DigitalAssetStoreException {
		File[] realFiles = new File[files.length];
		// Rename the files to have the right names.
		for(int i=0; i<files.length;i++) {
			File oldFile = new File(files[i].getName());
			realFiles[i] = new File(oldFile.getParentFile(), filenames[i]);
			
            if (log.isDebugEnabled()) {
                log.debug("SOAPService renaming " + oldFile.getAbsolutePath() + " to " + realFiles[i].getAbsolutePath());
            }
			oldFile.renameTo(realFiles[i]);
		}
		
		service.save(targetInstanceName, directory, realFiles);
	}
	
	/** @see DigitalAssetStoreSOAP#getResource(String, int, HarvestResourceDTO). */
	public DataHandler getResource(String targetInstanceName, int harvestResultNumber, HarvestResourceDTO resource) {
		File file = null;
		try {
			file = service.getResource(targetInstanceName, harvestResultNumber, resource);
			}
		catch(DigitalAssetStoreException e) {
			if (log.isWarnEnabled()) {
				log.warn("SOAP Service Failed to get resource : " + e.getMessage());
			}
		}	
		return new DataHandler(new TempFileDataSource(file));	

	}
	
	/** @see DigitalAssetStoreSOAP#getSmallResource(String, int, HarvestResourceDTO). */
	public byte[] getSmallResource(String targetInstanceName, int harvestResultNumber, HarvestResourceDTO resourcex) throws DigitalAssetStoreException {
		try {
			return service.getSmallResource(targetInstanceName, harvestResultNumber, resourcex);
		}
		catch(Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("SOAP Service Failed to get resource : " + e.getMessage());
			}
		}
		return new byte[0];
	}
	
	/** @see DigitalAssetStoreSOAP#getHeaders(String, int, HarvestResourceDTO). */
	public Header[] getHeaders(String targetInstanceName, int harvestResultNumber, HarvestResourceDTO resourcex) throws DigitalAssetStoreException {
		Header[] header = null;
		try {
		    header = service.getHeaders(targetInstanceName, harvestResultNumber, resourcex);
        }
        catch(Exception e) {
        	if (log.isWarnEnabled()) {
        		log.warn("SOAP Service Failed to get resource : " + e.getMessage());
        	}
        }	
		return header;
	}
	
	/** @see DigitalAssetStoreSOAP#copyAndPrune(String, int, int, List). */
	public HarvestResultDTO copyAndPrune(String targetInstanceName, int orgHarvestResultNum, int newHarvestResultNum, List<String> urisToDelete, List<HarvestResourceDTO> hrsToImport) throws DigitalAssetStoreException {
        return service.copyAndPrune(targetInstanceName, orgHarvestResultNum, newHarvestResultNum, urisToDelete, hrsToImport);
	}

	/** @see DigitalAssetStoreSOAP#purge(String[]). */
	public void purge(String[] targetInstanceNames) throws DigitalAssetStoreException {
		service.purge(targetInstanceNames);
	}
	
	/** @see DigitalAssetStoreSOAP#purgeAbortedTargetInstances(String[]). */
	public void purgeAbortedTargetInstances(String[] targetInstanceNames) throws DigitalAssetStoreException {
		service.purgeAbortedTargetInstances(targetInstanceNames);
	}
	
	/** @see DigitalAssetStoreSOAP#submitToArchive(String, String, Map, int). */
	public void submitToArchive(String targetInstanceOid, String SIP, Map xAttributes, int harvestNumber) throws DigitalAssetStoreException{
		log.info("ArcDigitalAssetStoreSOAPService: submitToArchive");
		service.submitToArchive(targetInstanceOid, SIP, xAttributes, harvestNumber);
	}
	
	/** @see DigitalAssetStoreSOAP#initiateIndexing(ArcHarvestResultDTO). */
	public void initiateIndexing(ArcHarvestResultDTO harvestResult) throws DigitalAssetStoreException {
		log.info("ArcDigitalAssetStoreSOAPService: initiateIndexing");
		service.initiateIndexing(harvestResult);
	}

	/** @see DigitalAssetStoreSOAP#initiateRemoveIndexes(ArcHarvestResultDTO). */
	public void initiateRemoveIndexes(ArcHarvestResultDTO harvestResult) throws DigitalAssetStoreException {
		log.info("ArcDigitalAssetStoreSOAPService: initiateRemoveIndexes");
		service.initiateRemoveIndexes(harvestResult);
	}
		
	/** @see DigitalAssetStoreSOAP#abortIndexing(Long). */
	public Boolean checkIndexing(Long harvestResultOid) throws DigitalAssetStoreException {
		log.info("ArcDigitalAssetStoreSOAPService: checkIndexing");
		return service.checkIndexing(harvestResultOid);
	}

	/** @see DigitalAssetStoreSOAP#getCustomDepositFormDetails(CustomDepositFormCriteriaDTO). */
	public CustomDepositFormResultDTO getCustomDepositFormDetails(CustomDepositFormCriteriaDTO criteria) throws DigitalAssetStoreException {
		log.info("ArcDigitalAssetStoreSOAPService: getCustomDepositFormDetails() invoked with " + criteria);
		return service.getCustomDepositFormDetails(criteria);
	}
}
